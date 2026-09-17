$backupDir = "f:\proyecto-catalogo-digital\backups"
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$backupFile = Join-Path $backupDir "catalogo-$timestamp.sql"

New-Item -ItemType Directory -Force -Path $backupDir | Out-Null

docker exec catalogo-mysql mysqldump `
  -u root `
  -p"alumno2008" `
  --databases catalogo_digital `
  --single-transaction `
  --routines `
  --triggers > $backupFile

if ($LASTEXITCODE -eq 0) {
    Write-Host "Backup creado: $backupFile"
} else {
    Remove-Item $backupFile -ErrorAction SilentlyContinue
    Write-Error "El backup fallo"
}

# Elimina backups con más de 7 días
Get-ChildItem $backupDir -Filter "*.sql" |
    Where-Object { $_.LastWriteTime -lt (Get-Date).AddDays(-7) } |
    Remove-Item