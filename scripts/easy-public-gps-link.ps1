param(
	[ValidateRange(0, 65535)]
	[int]$Port = 0,
	[ValidateSet("auto", "cloudflare", "ngrok")]
	[string]$Provider = "auto"
)

$ErrorActionPreference = "Stop"

$projectScript = Join-Path $PSScriptRoot "..\pepito-busca-error\scripts\easy-public-gps-link.ps1"
if (-not (Test-Path $projectScript)) {
	throw "Could not find project helper script at $projectScript."
}

& $projectScript -Port $Port -Provider $Provider
exit $LASTEXITCODE
