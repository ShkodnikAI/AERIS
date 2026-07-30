# AERIS v5.0 Project Generator
# Run: .\generate-aeris.ps1

$projectRoot = "D:\AI_projectS\AERIS"

function Write-File($path, $content) {
    $fullPath = Join-Path $projectRoot $path
    $dir = Split-Path $fullPath -Parent
    if (!(Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    Set-Content -Path $fullPath -Value $content -Encoding UTF8 -Force
}

Write-Host "AERIS v5.0 Generator" -ForegroundColor Cyan
Write-Host "Project root: $projectRoot" -ForegroundColor Gray

# Build files
Write-Host "Creating build files..." -ForegroundColor Green

# Note: This script assumes the repository has been cloned and build files exist.
# For a fresh clone, run: git clone https://github.com/ShkodnikAI/AERIS.git

Write-Host "Build project..." -ForegroundColor Green
Set-Location $projectRoot
.\gradlew.bat assembleDebug

if ($LASTEXITCODE -eq 0) {
    Write-Host "BUILD SUCCESSFUL" -ForegroundColor Green
    $apk = "$projectRoot\app\build\outputs\apk\debug\app-debug.apk"
    if (Test-Path $apk) {
        Write-Host "APK: $apk" -ForegroundColor Cyan
    }
} else {
    Write-Host "BUILD FAILED" -ForegroundColor Red
}

Write-Host "Done." -ForegroundColor Green
