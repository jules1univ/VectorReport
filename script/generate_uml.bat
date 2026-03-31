@echo off

setlocal enabledelayedexpansion

set "UML_FILE=uml\src.puml"
set "PLANTUML_JAR=script\plantuml.jar"

if not exist "%PLANTUML_JAR%" (
    echo plantuml.jar not found
    exit /b 1
)

if not exist "%UML_FILE%" (
    echo diagram .puml not found
    exit /b 1
)

java -jar "%PLANTUML_JAR%" -tsvg "%UML_FILE%"
