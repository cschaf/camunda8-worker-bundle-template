#!/bin/bash

# ===========================================
# Docker Image Build und Export Script
# ===========================================

# Konfiguration - Hier kannst du die Werte anpassen
IMAGE_NAME="worker-bundle"
IMAGE_TAG="latest"
TAR_OUTPUT_PATH="/home/$(whoami)"
DOCKERFILE_PATH="."
DOCKERFILE_NAME="Dockerfile"
USE_TIMESTAMP=true  # true = Timestamp im Dateinamen, false = ohne Timestamp

# Vollständiger Image-Name
FULL_IMAGE_NAME="${IMAGE_NAME}:${IMAGE_TAG}"

# TAR-Dateiname mit oder ohne Timestamp
if [ "$USE_TIMESTAMP" = true ]; then
    TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
    TAR_FILENAME="${IMAGE_NAME}_${IMAGE_TAG}_${TIMESTAMP}.tar"
else
    TAR_FILENAME="${IMAGE_NAME}_${IMAGE_TAG}.tar"
fi
FULL_TAR_PATH="${TAR_OUTPUT_PATH}/${TAR_FILENAME}"

echo "===========================================";
echo "Docker Image Build und Export Script"
echo "===========================================";
echo "Image Name: ${FULL_IMAGE_NAME}"
echo "TAR Datei: ${TAR_FILENAME}"
echo "TAR Pfad: ${FULL_TAR_PATH}"
echo "Dockerfile: ${DOCKERFILE_PATH}/${DOCKERFILE_NAME}"
echo "Timestamp: $(if [ "$USE_TIMESTAMP" = true ]; then echo "aktiviert"; else echo "deaktiviert"; fi)"
echo "===========================================";

# Prüfen ob Dockerfile existiert
if [ ! -f "${DOCKERFILE_PATH}/${DOCKERFILE_NAME}" ]; then
    echo "❌ FEHLER: Dockerfile nicht gefunden: ${DOCKERFILE_PATH}/${DOCKERFILE_NAME}"
    exit 1
fi

# Prüfen ob Docker läuft
if ! docker info > /dev/null 2>&1; then
    echo "❌ FEHLER: Docker ist nicht verfügbar oder läuft nicht"
    exit 1
fi

# Ziel-Ordner erstellen falls nicht vorhanden
mkdir -p "${TAR_OUTPUT_PATH}"

echo "📦 Baue Docker Image..."
if docker build -f "${DOCKERFILE_PATH}/${DOCKERFILE_NAME}" -t "${FULL_IMAGE_NAME}" "${DOCKERFILE_PATH}"; then
    echo "✅ Docker Image erfolgreich gebaut: ${FULL_IMAGE_NAME}"
else
    echo "❌ FEHLER: Docker Build fehlgeschlagen"
    exit 1
fi

echo "💾 Exportiere Docker Image zu TAR-Datei..."
if docker save -o "${FULL_TAR_PATH}" "${FULL_IMAGE_NAME}"; then
    echo "✅ Docker Image erfolgreich exportiert: ${FULL_TAR_PATH}"
else
    echo "❌ FEHLER: Docker Export fehlgeschlagen"
    exit 1
fi

echo "===========================================";
echo "✅ ERFOLGREICH ABGESCHLOSSEN!"
echo "Image: ${FULL_IMAGE_NAME}"
echo "TAR-Datei: ${FULL_TAR_PATH}"
echo "Dateigröße: $(du -h "${FULL_TAR_PATH}" | cut -f1)"
echo "===========================================";

# Optional: Fragen ob das Docker Image lokal behalten werden soll
read -p "Möchtest du das lokale Docker Image ${FULL_IMAGE_NAME} löschen? (j/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[JjYy]$ ]]; then
    docker rmi "${FULL_IMAGE_NAME}"
    echo "🗑️ Lokales Docker Image gelöscht"
fi

echo "Script beendet."