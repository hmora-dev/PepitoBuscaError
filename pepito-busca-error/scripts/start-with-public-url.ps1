param(
	[string]$PublicUrl,
	[int]$Port = 0
)

function Resolve-AppPort {
	if ($Port -gt 0) {
		return $Port
	}
	if (-not [string]::IsNullOrWhiteSpace($env:SERVER_PORT)) {
		$parsedPort = 0
		if ([int]::TryParse($env:SERVER_PORT, [ref]$parsedPort) -and $parsedPort -gt 0 -and $parsedPort -le 65535) {
			return $parsedPort
		}
		throw "SERVER_PORT is set to '$env:SERVER_PORT', but it must be a number between 1 and 65535."
	}
	return 8080
}

if ([string]::IsNullOrWhiteSpace($PublicUrl)) {
	$PublicUrl = Read-Host "Paste your public HTTPS tunnel URL"
}

$PublicUrl = $PublicUrl.Trim().TrimEnd("/")
$AppPort = Resolve-AppPort

if (-not $PublicUrl.StartsWith("https://")) {
	Write-Host "The public URL should start with https:// for mobile browser geolocation." -ForegroundColor Yellow
}

$env:APP_PUBLIC_BASE_URL = $PublicUrl
$env:SERVER_PORT = "$AppPort"
Write-Host "APP_PUBLIC_BASE_URL=$env:APP_PUBLIC_BASE_URL"
Write-Host "SERVER_PORT=$env:SERVER_PORT"
Write-Host "Local URL: http://localhost:$AppPort"
Write-Host "Starting Spring Boot with Maven Wrapper..."

.\mvnw.cmd spring-boot:run
