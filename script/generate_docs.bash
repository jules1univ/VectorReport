#!/bin/bash

# Accorder les droit au script bash
# Ne pas oublier de faire: chmod +x generate_docs.bash

SRC_DIR="src"
DOC_DIR="doc"
mkdir -p "$DOC_DIR"
echo "Génération de la JavaDoc depuis '$SRC_DIR' vers '$DOC_DIR'..."

javadoc -d "$DOC_DIR" -sourcepath "$SRC_DIR" -subpackages fr

if [ $? -eq 0 ]; then
    echo "Documentation générée avec succès dans le dossier '$DOC_DIR' !"
else
    echo "Erreur lors de la génération de la documentation !"
fi
