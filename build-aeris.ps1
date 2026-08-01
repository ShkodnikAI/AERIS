# AERIS v5.0 Build Script for Windows PowerShell
# Run: .\build-aeris.ps1
# Requirements: JDK 17, Android SDK with API 34

$projectPath = "D:\AI_projectS\AERIS"
$gradleVersion = "8.4"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  AERIS v5.0 Windows Build Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Check JDK 17
Write-Host "[1/7] Checking JDK 17..." -ForegroundColor Green
$javaVersion = & java -version 2>&1 | Select-String -Pattern '"(\d+\.)' | ForEach-Object { $_.Matches.Groups[1].Value }
if ($javaVersion -notmatch "17") {
    Write-Host "ERROR: JDK 17 not found. Current: $javaVersion" -ForegroundColor Red
    Write-Host "Install Eclipse Temurin JDK 17 from: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}
Write-Host "  JDK 17 found: $javaVersion" -ForegroundColor Gray

# 2. Check Android SDK
Write-Host "[2/7] Checking Android SDK..." -ForegroundColor Green
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
    Write-Host "ERROR: Android SDK not found." -ForegroundColor Red
    exit 1
}
Write-Host "  Android SDK: $androidSdk" -ForegroundColor Gray

# 3. Check SDK platforms
Write-Host "[3/7] Checking SDK platforms..." -ForegroundColor Green
$requiredPlatforms = @("android-34", "android-33", "android-32", "android-31", "android-30", "android-29", "android-28")
$foundPlatform = $null
foreach ($platform in $requiredPlatforms) {
    if (Test-Path "$androidSdk\platforms\$platform") {
        $foundPlatform = $platform
        break
    }
}
if (-not $foundPlatform) {
    Write-Host "ERROR: No Android SDK platforms found (need API 28-34)." -ForegroundColor Red
    Write-Host "Install via Android Studio: SDK Manager -> SDK Platforms" -ForegroundColor Yellow
    Write-Host "Or run: sdkmanager.bat 'platforms;android-34'" -ForegroundColor Yellow
    exit 1
}
Write-Host "  Found platform: $foundPlatform" -ForegroundColor Gray

# 4. Clone or update repository
Write-Host "[4/7] Setting up repository..." -ForegroundColor Green
if (Test-Path "$projectPath\.git") {
    Write-Host "  Repository exists. Pulling latest changes..." -ForegroundColor Gray
    Set-Location $projectPath
    git pull origin main
} else {
    Write-Host "  ERROR: Repository not found at $projectPath" -ForegroundColor Red
    exit 1
}

# 5. Create local.properties with UTF-8
Write-Host "[5/7] Creating local.properties..." -ForegroundColor Green
$localPropsContent = "sdk.dir=$androidSdk"
[System.IO.File]::WriteAllText("$projectPath\local.properties", $localPropsContent, [System.Text.Encoding]::UTF8)
Write-Host "  local.properties created (UTF-8)" -ForegroundColor Gray

# 6. Check Gradle Wrapper
Write-Host "[6/7] Checking Gradle Wrapper..." -ForegroundColor Green
$wrapperJar = "$projectPath\gradle\wrapper\gradle-wrapper.jar"
if (-not (Test-Path $wrapperJar)) {
    Write-Host "  Downloading gradle-wrapper.jar..." -ForegroundColor Gray
    $wrapperDir = "$projectPath\gradle\wrapper"
    if (-not (Test-Path $wrapperDir)) { New-Item -ItemType Directory -Path $wrapperDir -Force | Out-Null }

    # Download from Maven Central
    $wrapperUrl = "https://repo1.maven.org/maven2/org/gradle/gradle-wrapper/8.4/gradle-wrapper-8.4.jar"
    try {
        Invoke-WebRequest -Uri $wrapperUrl -OutFile $wrapperJar -UseBasicParsing
        Write-Host "  gradle-wrapper.jar downloaded" -ForegroundColor Gray
    } catch {
        Write-Host "  WARNING: Could not download wrapper jar." -ForegroundColor Yellow
        Write-Host "  Trying to use system gradle..." -ForegroundColor Yellow
    }
} else {
    Write-Host "  gradle-wrapper.jar found" -ForegroundColor Gray
}

# 7. Build APK with UTF-8 encoding
Write-Host "[7/7] Building Debug APK..." -ForegroundColor Green
Write-Host "  This may take 5-15 minutes on first run..." -ForegroundColor DarkGray
Set-Location $projectPath

# Set UTF-8 for Gradle
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"
$env:GRADLE_OPTS = "-Dfile.encoding=UTF-8"

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
    Write-Host "Check errors above." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
