@echo off

setlocal enabledelayedexpansion
set SRC_DIR=src
set DOC_DIR=doc

if not exist "%DOC_DIR%" mkdir "%DOC_DIR%"
echo Generating JavaDoc from '%SRC_DIR%' to '%DOC_DIR%'...

javadoc -d "%DOC_DIR%" ^
    -sourcepath "%SRC_DIR%" ^
    -subpackages .

if %errorlevel% equ 0 (
    echo Documentation generated successfully in '%DOC_DIR%' folder!
) else (
    echo Error generating documentation!
)
