param(
    [int]$BackendPort = 8080,
    [int]$MySqlPort = 3306
)

$ErrorActionPreference = "Stop"
$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$webAdminDir = Join-Path $projectRoot "web-admin"
$logDir = Join-Path $projectRoot "runtime-logs"
$stdoutLog = Join-Path $logDir "web-admin.out.log"
$stderrLog = Join-Path $logDir "web-admin.err.log"

function Get-PortListener([int]$Port) {
    Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
        Select-Object -First 1
}

if (Get-PortListener $BackendPort) {
    Write-Host "Backend is already running on port $BackendPort."
    exit 0
}

if (-not (Get-PortListener $MySqlPort)) {
    throw "MySQL is not running on port $MySqlPort. Start MySQL in XAMPP first."
}

$databaseName = "appshopbanhang"
$schemaFile = Join-Path $projectRoot "database\mysql\appshopbanhang_mysql.sql"
$xamppMySql = "C:\xampp\mysql\bin\mysql.exe"
$mysql = if (Test-Path $xamppMySql) {
    $xamppMySql
} else {
    (Get-Command mysql.exe -ErrorAction Stop).Source
}

$database = & $mysql -u root -N -B -e "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME='$databaseName';"
if ($LASTEXITCODE -ne 0) {
    throw "Cannot connect to MySQL with the configured root account."
}

if ([string]::IsNullOrWhiteSpace(($database | Out-String))) {
    if (-not (Test-Path $schemaFile)) {
        throw "Database '$databaseName' is missing and schema file was not found."
    }

    Write-Host "Database '$databaseName' is missing. Importing schema..."
    $importCommand = "`"$mysql`" -u root < `"$schemaFile`""
    & cmd.exe /d /c $importCommand
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to import database '$databaseName'."
    }
}

New-Item -ItemType Directory -Force -Path $logDir | Out-Null
Remove-Item -LiteralPath $stdoutLog, $stderrLog -Force -ErrorAction SilentlyContinue

$null = Get-Command mvn.cmd -ErrorAction Stop
$backendRunner = Join-Path $PSScriptRoot "run-backend.cmd"
$backendProcess = Start-Process `
    -FilePath $backendRunner `
    -WorkingDirectory $webAdminDir `
    -WindowStyle Hidden `
    -PassThru

for ($attempt = 0; $attempt -lt 45; $attempt++) {
    Start-Sleep -Seconds 1

    if (Get-PortListener $BackendPort) {
        $response = Invoke-WebRequest `
            -UseBasicParsing `
            -Uri "http://localhost:$BackendPort/api/products" `
            -TimeoutSec 10

        if ($response.StatusCode -eq 200) {
            Write-Host "Backend started successfully on port $BackendPort."
            exit 0
        }
    }

    if ($backendProcess.HasExited) {
        break
    }
}

$stdout = Get-Content -LiteralPath $stdoutLog -Tail 80 -ErrorAction SilentlyContinue
$stderr = Get-Content -LiteralPath $stderrLog -Tail 80 -ErrorAction SilentlyContinue
Write-Host $stdout
Write-Host $stderr
throw "Backend failed to start on port $BackendPort."
