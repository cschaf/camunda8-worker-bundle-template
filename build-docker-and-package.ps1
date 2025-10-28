# ===========================================
# Docker Image Build und Export Script (PowerShell)
# ===========================================

# Konfiguration - Hier kannst du die Werte anpassen
$IMAGE_NAME = "worker-bundle"
$IMAGE_TAG = "latest"
$TAR_OUTPUT_PATH = "c:\temp"
$DOCKERFILE_PATH = "."
$DOCKERFILE_NAME = "Dockerfile"
$USE_TIMESTAMP = $false  # $true = Timestamp im Dateinamen, $false = ohne Timestamp

# Vollständiger Image-Name
$FULL_IMAGE_NAME = "${IMAGE_NAME}:${IMAGE_TAG}"

# TAR-Dateiname mit oder ohne Timestamp
if ($USE_TIMESTAMP) {
    $TIMESTAMP = Get-Date -Format "yyyyMMdd_HHmmss"
    $TAR_FILENAME = "${IMAGE_NAME}_${IMAGE_TAG}_${TIMESTAMP}.tar"
} else {
    $TAR_FILENAME = "${IMAGE_NAME}_${IMAGE_TAG}.tar"
}
$FULL_TAR_PATH = Join-Path $TAR_OUTPUT_PATH $TAR_FILENAME

Write-Host "==========================================="
Write-Host "Docker Image Build und Export Script"
Write-Host "==========================================="
Write-Host "Image Name: $FULL_IMAGE_NAME"
Write-Host "TAR Datei: $TAR_FILENAME"
Write-Host "TAR Pfad: $FULL_TAR_PATH"
Write-Host "Dockerfile: $DOCKERFILE_PATH\$DOCKERFILE_NAME"
Write-Host "Timestamp: $(if ($USE_TIMESTAMP) { 'aktiviert' } else { 'deaktiviert' })"
Write-Host "==========================================="

# Prüfen ob Dockerfile existiert
$dockerfilePath = Join-Path $DOCKERFILE_PATH $DOCKERFILE_NAME
if (-not (Test-Path $dockerfilePath)) {
    Write-Host "❌ FEHLER: Dockerfile nicht gefunden: $dockerfilePath" -ForegroundColor Red
    exit 1
}

# Prüfen ob Docker läuft
try {
    $null = docker info 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "Docker nicht verfügbar"
    }
} catch {
    Write-Host "❌ FEHLER: Docker ist nicht verfügbar oder läuft nicht" -ForegroundColor Red
    exit 1
}

# Ziel-Ordner erstellen falls nicht vorhanden
if (-not (Test-Path $TAR_OUTPUT_PATH)) {
    New-Item -ItemType Directory -Path $TAR_OUTPUT_PATH -Force | Out-Null
}

Write-Host "📦 Baue Docker Image..." -ForegroundColor Yellow
try {
    $buildResult = docker build -f "$DOCKERFILE_PATH\$DOCKERFILE_NAME" -t $FULL_IMAGE_NAME $DOCKERFILE_PATH
    if ($LASTEXITCODE -ne 0) {
        throw "Docker Build fehlgeschlagen"
    }
    Write-Host "✅ Docker Image erfolgreich gebaut: $FULL_IMAGE_NAME" -ForegroundColor Green
} catch {
    Write-Host "❌ FEHLER: Docker Build fehlgeschlagen" -ForegroundColor Red
    exit 1
}

Write-Host "💾 Exportiere Docker Image zu TAR-Datei..." -ForegroundColor Yellow
try {
    $saveResult = docker save -o $FULL_TAR_PATH $FULL_IMAGE_NAME
    if ($LASTEXITCODE -ne 0) {
        throw "Docker Save fehlgeschlagen"
    }
    Write-Host "✅ Docker Image erfolgreich exportiert: $FULL_TAR_PATH" -ForegroundColor Green
} catch {
    Write-Host "❌ FEHLER: Docker Export fehlgeschlagen" -ForegroundColor Red
    exit 1
}

# Dateigröße ermitteln
$fileSize = if (Test-Path $FULL_TAR_PATH) {
    $sizeBytes = (Get-Item $FULL_TAR_PATH).Length
    if ($sizeBytes -gt 1GB) {
        "{0:N2} GB" -f ($sizeBytes / 1GB)
    } elseif ($sizeBytes -gt 1MB) {
        "{0:N2} MB" -f ($sizeBytes / 1MB)
    } elseif ($sizeBytes -gt 1KB) {
        "{0:N2} KB" -f ($sizeBytes / 1KB)
    } else {
        "$sizeBytes Bytes"
    }
} else {
    "Unbekannt"
}

Write-Host "==========================================="
Write-Host "✅ ERFOLGREICH ABGESCHLOSSEN!" -ForegroundColor Green
Write-Host "Image: $FULL_IMAGE_NAME"
Write-Host "TAR-Datei: $FULL_TAR_PATH"
Write-Host "Dateigröße: $fileSize"
Write-Host "==========================================="

# Optional: Fragen ob das Docker Image lokal behalten werden soll
$response = Read-Host "Möchtest du das lokale Docker Image $FULL_IMAGE_NAME löschen? (j/N)"
if ($response -match '^[JjYy]') {
    try {
        docker rmi $FULL_IMAGE_NAME
        if ($LASTEXITCODE -eq 0) {
            Write-Host "🗑️ Lokales Docker Image gelöscht" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "⚠️ Warnung: Konnte Docker Image nicht löschen" -ForegroundColor Yellow
    }
}

Write-Host "Script beendet." -ForegroundColor Cyan