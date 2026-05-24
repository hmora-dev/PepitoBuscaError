param(
	[ValidateRange(0, 65535)]
	[int]$Port = 0,
	[ValidateSet("auto", "cloudflare", "ngrok")]
	[string]$Provider = "auto"
)

$ErrorActionPreference = "Stop"

$ScriptRoot = if (-not [string]::IsNullOrWhiteSpace($PSScriptRoot)) {
	$PSScriptRoot
} else {
	Split-Path -Parent $MyInvocation.MyCommand.Path
}
$ProjectRoot = (Resolve-Path (Join-Path $ScriptRoot "..")).Path

function Test-TcpPortAvailable($CandidatePort) {
	$listener = $null
	try {
		$listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Any, $CandidatePort)
		$listener.Start()
		return $true
	} catch {
		return $false
	} finally {
		if ($null -ne $listener) {
			$listener.Stop()
		}
	}
}

function Get-AvailablePort {
	for ($candidate = 8081; $candidate -le 8199; $candidate++) {
		if (Test-TcpPortAvailable $candidate) {
			return $candidate
		}
	}

	$listener = $null
	try {
		$listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback, 0)
		$listener.Start()
		return ([System.Net.IPEndPoint]$listener.LocalEndpoint).Port
	} finally {
		if ($null -ne $listener) {
			$listener.Stop()
		}
	}
}

function Confirm-PortAvailable($CandidatePort, $Source) {
	if (-not (Test-TcpPortAvailable $CandidatePort)) {
		throw "$Source port $CandidatePort is already in use. Run .\scripts\free-port.ps1 -Port $CandidatePort or choose another port with -Port."
	}
	return $CandidatePort
}

function Resolve-AppPort {
	if ($Port -gt 0) {
		return Confirm-PortAvailable $Port "-Port"
	}
	if (-not [string]::IsNullOrWhiteSpace($env:SERVER_PORT)) {
		$parsedPort = 0
		if ([int]::TryParse($env:SERVER_PORT, [ref]$parsedPort) -and $parsedPort -gt 0 -and $parsedPort -le 65535) {
			return Confirm-PortAvailable $parsedPort "SERVER_PORT"
		}
		throw "SERVER_PORT is set to '$env:SERVER_PORT', but it must be a number between 1 and 65535."
	}
	if (Test-TcpPortAvailable 8080) {
		return 8080
	}
	$fallbackPort = Get-AvailablePort
	Write-Host "Port 8080 is busy; using port $fallbackPort instead." -ForegroundColor Yellow
	return $fallbackPort
}

$AppPort = Resolve-AppPort

function Test-Tool($Name) {
	return $null -ne (Get-Command $Name -ErrorAction SilentlyContinue)
}

function Stop-TunnelProcess($TunnelProcess) {
	if ($null -ne $TunnelProcess -and -not $TunnelProcess.HasExited) {
		Stop-Process -Id $TunnelProcess.Id -Force -ErrorAction SilentlyContinue
	}
}

function Start-CloudflareQuickTunnel {
	if (-not (Test-Tool "cloudflared")) {
		throw "cloudflared is not installed or is not available in PATH."
	}

	$logPrefix = Join-Path $env:TEMP ("pepito-cloudflared-" + [guid]::NewGuid())
	$outputLogPath = "$logPrefix.out.log"
	$errorLogPath = "$logPrefix.err.log"
	$arguments = @("tunnel", "--url", "http://localhost:$AppPort", "--no-autoupdate")
	$process = Start-Process -FilePath "cloudflared" -ArgumentList $arguments `
		-RedirectStandardOutput $outputLogPath -RedirectStandardError $errorLogPath -NoNewWindow -PassThru

	for ($attempt = 0; $attempt -lt 60; $attempt++) {
		Start-Sleep -Seconds 1
		$outputContent = if (Test-Path $outputLogPath) { Get-Content -Path $outputLogPath -Raw -ErrorAction SilentlyContinue } else { "" }
		$errorContent = if (Test-Path $errorLogPath) { Get-Content -Path $errorLogPath -Raw -ErrorAction SilentlyContinue } else { "" }
		$content = "$outputContent`n$errorContent"
		if ($content -match "https://[A-Za-z0-9-]+\.trycloudflare\.com") {
			return @{
				Url = $matches[0]
				Process = $process
				LogPath = $errorLogPath
				Provider = "Cloudflare Tunnel"
			}
		}
		if ($process.HasExited) {
			throw "cloudflared stopped before creating a public URL. Log: $errorLogPath"
		}
	}

	Stop-TunnelProcess $process
	throw "Timed out waiting for Cloudflare Tunnel to print a public URL. Log: $errorLogPath"
}

function Start-NgrokTunnel {
	if (-not (Test-Tool "ngrok")) {
		throw "ngrok is not installed or is not available in PATH."
	}

	$process = Start-Process -FilePath "ngrok" -ArgumentList @("http", "$AppPort") -NoNewWindow -PassThru

	for ($attempt = 0; $attempt -lt 45; $attempt++) {
		Start-Sleep -Seconds 1
		try {
			$tunnels = Invoke-RestMethod -Uri "http://127.0.0.1:4040/api/tunnels" -TimeoutSec 2
			$publicUrl = $tunnels.tunnels |
				Where-Object { $_.public_url -like "https://*" } |
				Select-Object -ExpandProperty public_url -First 1
			if (-not [string]::IsNullOrWhiteSpace($publicUrl)) {
				return @{
					Url = $publicUrl
					Process = $process
					LogPath = ""
					Provider = "ngrok"
				}
			}
		} catch {
			if ($process.HasExited) {
				throw "ngrok stopped before creating a public URL."
			}
		}
	}

	Stop-TunnelProcess $process
	throw "Timed out waiting for ngrok to create a public HTTPS URL."
}

function Start-SelectedTunnel {
	if ($Provider -eq "cloudflare") {
		return Start-CloudflareQuickTunnel
	}
	if ($Provider -eq "ngrok") {
		return Start-NgrokTunnel
	}
	if (Test-Tool "cloudflared") {
		return Start-CloudflareQuickTunnel
	}
	if (Test-Tool "ngrok") {
		return Start-NgrokTunnel
	}
	throw "No supported tunnel tool was found. Install cloudflared or ngrok, then run this script again."
}

function Build-AppJar {
	$mavenWrapper = Join-Path $ProjectRoot "mvnw.cmd"
	if (-not (Test-Path $mavenWrapper)) {
		throw "Could not find Maven wrapper at $mavenWrapper."
	}

	Write-Host "Building runnable jar with Maven..." -ForegroundColor Cyan
	& $mavenWrapper "-DskipTests" "package" | Out-Host
	$mavenExitCode = $LASTEXITCODE
	if ($mavenExitCode -ne 0) {
		throw "Maven package failed with exit code $mavenExitCode."
	}

	$expectedJar = Join-Path $ProjectRoot "target\pepito-busca-error-0.0.1-SNAPSHOT.jar"
	if (Test-Path $expectedJar) {
		return $expectedJar
	}

	$jar = Get-ChildItem -Path (Join-Path $ProjectRoot "target") -Filter "*.jar" |
		Where-Object { $_.Name -notlike "original-*" } |
		Sort-Object LastWriteTime -Descending |
		Select-Object -First 1
	if ($null -eq $jar) {
		throw "Maven package finished, but no runnable jar was found in target."
	}
	return $jar.FullName
}

function Start-SpringBootJar($JarPath) {
	if (-not (Test-Tool "java")) {
		throw "Java is not installed or is not available in PATH."
	}

	$process = Start-Process -FilePath "java" -ArgumentList @("-jar", $JarPath) -NoNewWindow -PassThru
	$started = $false
	for ($attempt = 0; $attempt -lt 90; $attempt++) {
		$process.Refresh()
		if ($process.HasExited) {
			break
		}
		try {
			$response = Invoke-WebRequest -UseBasicParsing -Uri "http://localhost:$AppPort/geolocation" -TimeoutSec 2
			if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) {
				$started = $true
				Write-Host "Spring Boot is reachable at http://localhost:$AppPort/geolocation" -ForegroundColor Green
				break
			}
			Start-Sleep -Seconds 1
		} catch {
			Start-Sleep -Seconds 1
		}
	}

	if (-not $started -and -not $process.HasExited) {
		Write-Host "Spring Boot is still starting; keeping the process attached." -ForegroundColor Yellow
	}

	while (-not $process.HasExited) {
		Start-Sleep -Seconds 1
		$process.Refresh()
	}

	$exitCode = $process.ExitCode
	if ($null -eq $exitCode) {
		if ($started) {
			Write-Host "Spring Boot stopped." -ForegroundColor Yellow
			return
		}
		throw "Spring Boot exited before becoming reachable."
	}
	if ($exitCode -ne 0) {
		if ($started) {
			Write-Host "Spring Boot stopped with exit code $exitCode." -ForegroundColor Yellow
			return
		}
		throw "Spring Boot exited before becoming reachable. Exit code: $exitCode."
	}
}

Write-Host ""
Write-Host "PepitoBuscaError easy public GPS link" -ForegroundColor Cyan
Write-Host "This builds the app, starts a public HTTPS tunnel, configures APP_PUBLIC_BASE_URL, and starts Spring Boot."
Write-Host "The client phone will still need to tap Allow in the browser location prompt."
Write-Host "Project folder: $ProjectRoot"
Write-Host "Local Spring Boot port: $AppPort"
Write-Host ""

$tunnel = $null
$locationPushed = $false
try {
	Push-Location $ProjectRoot
	$locationPushed = $true
	$jarPath = Build-AppJar
	$tunnel = Start-SelectedTunnel
	$publicUrl = $tunnel.Url.TrimEnd("/")
	$env:APP_PUBLIC_BASE_URL = $publicUrl
	$env:SERVER_PORT = "$AppPort"

	Write-Host ""
	Write-Host "Public HTTPS URL ready:" -ForegroundColor Green
	Write-Host $publicUrl -ForegroundColor Green
	Write-Host ""
	Write-Host "Tunnel target: http://localhost:$AppPort"
	Write-Host "Spring Boot will now start with:"
	Write-Host "SERVER_PORT=$env:SERVER_PORT"
	Write-Host "APP_PUBLIC_BASE_URL=$env:APP_PUBLIC_BASE_URL"
	Write-Host ""
	Write-Host "After the app starts, open http://localhost:$AppPort/geolocation, create or open a device, and copy the client GPS link."
	Write-Host "Keep this PowerShell window open while testing from another network."
	Write-Host ""

	Start-SpringBootJar $jarPath
} catch {
	Write-Host ""
	Write-Host "Could not start easy public link mode." -ForegroundColor Red
	Write-Host $_.Exception.Message -ForegroundColor Red
	Write-Host ""
	Write-Host "Simplest fix:" -ForegroundColor Yellow
	Write-Host "1. If port $AppPort is busy, run: .\scripts\free-port.ps1 -Port $AppPort"
	Write-Host "2. Or choose another port: `$env:SERVER_PORT='8081'; .\scripts\easy-public-gps-link.ps1"
	Write-Host "3. If no tunnel tool is installed, install Cloudflare Tunnel or ngrok, then run this script again."
	Write-Host ""
	exit 1
} finally {
	if ($locationPushed) {
		Pop-Location
	}
	if ($null -ne $tunnel) {
		Stop-TunnelProcess $tunnel.Process
	}
}
