param(
	[Parameter(Mandatory = $true)]
	[int]$Port
)

$ErrorActionPreference = "Stop"

if ($Port -lt 1 -or $Port -gt 65535) {
	Write-Host "Port must be between 1 and 65535." -ForegroundColor Red
	exit 1
}

$env:SERVER_PORT = "$Port"

Write-Host ""
Write-Host "Starting PepitoBuscaError on port $Port..." -ForegroundColor Cyan
Write-Host "SERVER_PORT=$env:SERVER_PORT"
Write-Host "Local URL: http://localhost:$Port"
Write-Host ""

.\mvnw.cmd spring-boot:run
