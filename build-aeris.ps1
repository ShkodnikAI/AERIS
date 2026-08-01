# AERIS v5.0 Build Script for Windows PowerShell
# Run: .\build-aeris.ps1
# Requirements: JDK 17, Android SDK

$projectPath = "D:\AI_projectS\AERIS"
$gradleVersion = "8.4"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  AERIS v5.0 Windows Build Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Check JDK 17
Write-Host "[1/6] Checking JDK 17..." -ForegroundColor Green
$javaVersion = & java -version 2>&1 | Select-String -Pattern '"(\d+\.)' | ForEach-Object { $_.Matches.Groups[1].Value }
if ($javaVersion -notmatch "17") {
    Write-Host "ERROR: JDK 17 not found. Current: $javaVersion" -ForegroundColor Red
    Write-Host "Install Eclipse Temurin JDK 17 from: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}
Write-Host "  JDK 17 found: $javaVersion" -ForegroundColor Gray

# 2. Check Android SDK
Write-Host "[2/6] Checking Android SDK..." -ForegroundColor Green
$sdkPaths = @(
    "$env:LOCALAPPDATA\Android\Sdk",
    "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk",
    "D:\Android\Sdk",
    "$env:ANDROID_SDK_ROOT",
    "$env:ANDROID_HOME"
)
$androidSdk = $null
foreach ($path in $sdkPaths) {
    if (Test-Path "$path\platforms") {
        $androidSdk = $path
        break
    }
}
if (-not $androidSdk) {
    Write-Host "ERROR: Android SDK not found in standard locations." -ForegroundColor Red
    Write-Host "Standard paths checked:" -ForegroundColor Yellow
    foreach ($path in $sdkPaths) { Write-Host "  $path" -ForegroundColor DarkGray }
    Write-Host ""
    $customSdk = Read-Host "Enter your Android SDK path (or press Enter to exit)"
    if (-not $customSdk) { exit 1 }
    $androidSdk = $customSdk
}
Write-Host "  Android SDK: $androidSdk" -ForegroundColor Gray

# 3. Clone or update repository
Write-Host "[3/6] Setting up repository..." -ForegroundColor Green
if (Test-Path "$projectPath\.git") {
    Write-Host "  Repository exists. Pulling latest changes..." -ForegroundColor Gray
    Set-Location $projectPath
    git pull origin main
} else {
    Write-Host "  Cloning repository..." -ForegroundColor Gray
    $parentDir = Split-Path $projectPath -Parent
    if (-not (Test-Path $parentDir)) { New-Item -ItemType Directory -Path $parentDir -Force | Out-Null }
    Set-Location $parentDir
    git clone https://github.com/ShkodnikAI/AERIS.git
    Set-Location $projectPath
}

# 4. Create local.properties
Write-Host "[4/6] Creating local.properties..." -ForegroundColor Green
$localProps = "sdk.dir=$androidSdk"
Set-Content -Path "$projectPath\local.properties" -Value $localProps -Force
Write-Host "  local.properties created" -ForegroundColor Gray

# 5. Download gradle-wrapper.jar if missing
Write-Host "[5/6] Checking Gradle Wrapper..." -ForegroundColor Green
$wrapperJar = "$projectPath\gradle\wrapper\gradle-wrapper.jar"
if (-not (Test-Path $wrapperJar)) {
    Write-Host "  Downloading gradle-wrapper.jar..." -ForegroundColor Gray
    $wrapperUrl = "https://raw.githubusercontent.com/gradle/gradle/v$gradleVersion.0/gradle/wrapper/gradle-wrapper.jar"
    $wrapperDir = "$projectPath\gradle\wrapper"
    if (-not (Test-Path $wrapperDir)) { New-Item -ItemType Directory -Path $wrapperDir -Force | Out-Null }
    try {
        Invoke-WebRequest -Uri $wrapperUrl -OutFile $wrapperJar -UseBasicParsing
        Write-Host "  gradle-wrapper.jar downloaded" -ForegroundColor Gray
    } catch {
        Write-Host "  WARNING: Could not download wrapper jar. Trying alternative..." -ForegroundColor Yellow
        # Alternative: use gradle directly
        $gradleInstalled = Get-Command gradle -ErrorAction SilentlyContinue
        if (-not $gradleInstalled) {
            Write-Host "  Downloading Gradle $gradleVersion..." -ForegroundColor Gray
            $gradleZip = "https://services.gradle.org/distributions/gradle-$gradleVersion-bin.zip"
            $gradleZipPath = "$env:TEMP\gradle-$gradleVersion-bin.zip"
            Invoke-WebRequest -Uri $gradleZip -OutFile $gradleZipPath
            Expand-Archive -Path $gradleZipPath -DestinationPath "$env:TEMP\gradle" -Force
            $env:PATH = "$env:TEMP\gradle\gradle-$gradleVersion\bin;$env:PATH"
        }
        Write-Host "  Generating wrapper..." -ForegroundColor Gray
        Set-Location $projectPath
        gradle wrapper --gradle-version $gradleVersion
    }
} else {
    Write-Host "  gradle-wrapper.jar found" -ForegroundColor Gray
}

# 6. Build APK
Write-Host "[6/6] Building Debug APK..." -ForegroundColor Green
Write-Host "  This may take 5-15 minutes on first run..." -ForegroundColor DarkGray
Set-Location $projectPath

$buildResult = & .\gradlew.bat assembleDebug 2>&1
$buildResult | ForEach-Object { Write-Host $_ }

# Check result
$apkPath = "$projectPath\app\build\outputs\apk\debug\app-debug.apk"
if (Test-Path $apkPath) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  BUILD SUCCESSFUL!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "APK location:" -ForegroundColor Cyan
    Write-Host "  $apkPath" -ForegroundColor White
    Write-Host ""
    Write-Host "Install on device:" -ForegroundColor Cyan
    Write-Host "  adb install `"$apkPath`"" -ForegroundColor White
    Write-Host ""
    $fileInfo = Get-Item $apkPath
    Write-Host "File size: $([math]::Round($fileInfo.Length / 1MB, 2)) MB" -ForegroundColor Gray
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "  BUILD FAILED" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Check errors above. Common fixes:" -ForegroundColor Yellow
    Write-Host "  1. Ensure Android SDK API 28-34 is installed" -ForegroundColor Yellow
    Write-Host "  2. Run: sdkmanager.bat 'platforms;android-34'" -ForegroundColor Yellow
    Write-Host "  3. Check JAVA_HOME points to JDK 17" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
