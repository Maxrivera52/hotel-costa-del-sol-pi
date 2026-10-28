<#
.SYNOPSIS
    Recrea la base de datos PostgreSQL y ejecuta el script schema_postgresql.sql para hotel-costa-del-sol-pi.
.DESCRIPTION
    Recrea la base de datos hotel_costa_sol_pi (y espejo opcional hotel_costa_del_sol_pi),
    ejecuta el DDL y carga de datos iniciales (seed data).
#>

[CmdletBinding()]
param (
    [string]$DbName = "hotel_costa_sol_pi",
    [string]$HostName = "localhost",
    [int]$Port = 5432,
    [string]$User = "postgres",
    [string]$Password = "1234",
    [switch]$IncludeMirror = $true
)

$ErrorActionPreference = "Stop"

# Ubicar psql
$psql = (Get-Command -ErrorAction SilentlyContinue psql | Select-Object -ExpandProperty Source)
if (-not $psql) {
    $psqlCandidates = Get-ChildItem -Path "C:\Program Files\PostgreSQL", "C:\Program Files (x86)\PostgreSQL" -Recurse -Filter "psql.exe" -Depth 3 -ErrorAction SilentlyContinue
    if ($psqlCandidates) {
        $psql = $psqlCandidates[0].FullName
    }
}

if (-not $psql) {
    Write-Error "No se encontro psql.exe en el sistema."
    exit 1
}

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent $scriptDir
$sqlFile = Join-Path $projectRoot "src\main\resources\schema\schema_postgresql.sql"

if (-not (Test-Path $sqlFile)) {
    Write-Error "No se encontro el archivo de esquema: $sqlFile"
    exit 1
}

$env:PGPASSWORD = $Password

function Execute-DbScript {
    param([string]$targetDb)

    Write-Host "==> Preparando base de datos '$targetDb'..." -ForegroundColor Cyan

    # Terminar conexiones activas y recrear BD
    & $psql -U $User -h $HostName -p $Port -d postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = '$targetDb' AND pid <> pg_backend_pid();" | Out-Null
    & $psql -U $User -h $HostName -p $Port -d postgres -c "DROP DATABASE IF EXISTS $targetDb;" | Out-Null
    & $psql -U $User -h $HostName -p $Port -d postgres -c "CREATE DATABASE $targetDb ENCODING 'UTF8';" | Out-Null

    Write-Host "==> Ejecutando script DDL/DML ($sqlFile)..." -ForegroundColor Yellow
    & $psql -U $User -h $HostName -p $Port -d $targetDb -f $sqlFile | Out-Null

    # Validar tablas
    $tableCount = & $psql -U $User -h $HostName -p $Port -d $targetDb -t -c "SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public';"
    $tableCount = $tableCount.Trim()
    Write-Host "==> BD '$targetDb' lista con $tableCount tablas en esquema public." -ForegroundColor Green
}

Execute-DbScript -targetDb $DbName

if ($IncludeMirror -and $DbName -ne "hotel_costa_del_sol_pi") {
    Execute-DbScript -targetDb "hotel_costa_del_sol_pi"
}

Remove-Item env:\PGPASSWORD
Write-Host "Operacion completada con exito." -ForegroundColor Green
