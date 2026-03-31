#!/bin/bash

# Accorder les droit au script bash
# Ne pas oublier de faire: chmod +x generate_uml.bash

UML_FILE="uml/src.puml"
PLANTUML_JAR="script/plantuml.jar"

if [ ! -f "$PLANTUML_JAR" ]; then
    echo "plantuml.jar introuvable"
    exit 1
fi

if [ ! -f "$UML_FILE" ]; then
    echo "diagram .puml introuvable"
    exit 1
fi

if ! command -v dot &> /dev/null; then
    echo "Graphviz non trouvé, installation..."
    sudo apt update
    sudo apt install -y graphviz
fi

java -jar "$PLANTUML_JAR" -tsvg "$UML_FILE"

