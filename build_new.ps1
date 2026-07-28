$ErrorActionPreference = "Stop"
Set-Location "C:\study duchieu\Do An 1\doan1\HDKManagement_New" 2>$null
if (-not (Test-Path "lib")) { Write-Error "Cannot find lib folder"; exit 1 }
New-Item -ItemType Directory -Path "build\classes" -Force | Out-Null
$cp = "lib\mysql-connector-j-8.0.33.jar;lib\flatlaf-3.5.1.jar;lib\miglayout-swing-4.2.jar;lib\emoji-java-5.1.1.jar"
$files = (Get-ChildItem -Recurse -Filter "*.java" "src").FullName
javac -encoding UTF-8 -classpath $cp -d "build\classes" $files
Write-Host ("Exit code: " + $LASTEXITCODE)
