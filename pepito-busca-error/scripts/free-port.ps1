param(
	[int]$Port = 8080
)

$ErrorActionPreference = "Stop"

if ($Port -lt 1 -or $Port -gt 65535) {
	Write-Host "Port must be between 1 and 65535." -ForegroundColor Red
	exit 1
}

Write-Host ""
Write-Host "Checking port $Port..." -ForegroundColor Cyan

$connections = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue

if (-not $connections) {
	Write-Host "Port $Port is free." -ForegroundColor Green
	exit 0
}

$processIds = $connections | Select-Object -ExpandProperty OwningProcess -Unique

foreach ($processId in $processIds) {
	$process = Get-Process -Id $processId -ErrorAction SilentlyContinue
	if ($null -eq $process) {
		Write-Host "Port $Port is used by PID $processId, but the process details could not be read." -ForegroundColor Yellow
		continue
	}
	$processPath = try { $process.Path } catch { "(path unavailable)" }
	if ([string]::IsNullOrWhiteSpace($processPath)) {
		$processPath = "(path unavailable)"
	}

	Write-Host ""
	Write-Host "Port $Port is being used by:" -ForegroundColor Yellow
	Write-Host "PID:  $($process.Id)"
	Write-Host "Name: $($process.ProcessName)"
	Write-Host "Path: $processPath"

	$answer = Read-Host "Stop this process? Type Y to confirm"
	if ($answer -eq "Y" -or $answer -eq "y") {
		try {
			Stop-Process -Id $process.Id -Force -ErrorAction Stop
			Write-Host "Stopped PID $($process.Id). Port $Port should now be free." -ForegroundColor Green
		} catch {
			Write-Host "Could not stop PID $($process.Id): $($_.Exception.Message)" -ForegroundColor Red
			exit 1
		}
	} else {
		Write-Host "Skipped PID $($process.Id). Port $Port is still in use." -ForegroundColor Yellow
	}
}
