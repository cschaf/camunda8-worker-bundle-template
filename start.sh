#!/bin/sh
# start.sh

# Pfad als erstes Argument, Standard auf /app wenn nicht angegeben
BASE_PATH=${1:-/app}

# Alle JAR-Dateien im Pfad finden und starten
for jar in $BASE_PATH/*.jar; do
    if [ -f "$jar" ]; then
        echo "Starting $jar"
        java -jar "$jar" --spring.config.location="$BASE_PATH/application.yaml" &
    fi
done

# Warten, bis alle Prozesse beendet sind
wait