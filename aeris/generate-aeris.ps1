<#
.SYNOPSIS
    AERIS Project Generator for Windows PowerShell
    
.DESCRIPTION
    Creates the complete AERIS Kotlin Multiplatform project structure
    and builds the Android debug APK.
    
.PARAMETER ProjectPath
    Target directory for the project. Default: D:\AI_projectS\AERIS
    
.PARAMETER SkipBuild
    Skip the Gradle build step
    
.EXAMPLE
    .\generate-aeris.ps1
    .\generate-aeris.ps1 -ProjectPath "C:\Projects\AERIS"
    .\generate-aeris.ps1 -SkipBuild
#>

param(
    [string]$ProjectPath = "D:\AI_projectS\AERIS",
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"

Write-Host @"
╔════════════════════════════════════════════════════════════════════════╗
║  AERIS v2.0 | Breathing Platform Generator                            ║
║  Kotlin Multiplatform · Offline-First · Health-Grade Safety           ║
╚════════════════════════════════════════════════════════════════════════╝
"@ -ForegroundColor Cyan

# Check environment
Write-Host "`n[1/5] Checking environment..." -ForegroundColor Yellow

if (-not $env:JAVA_HOME) {
    Write-Host "⚠ JAVA_HOME not set. Please set it to JDK 17 path." -ForegroundColor Red
    Write-Host "  Example: `$env:JAVA_HOME = 'D:\AI_projectS\tools\jdk17'" -ForegroundColor Gray
}

if (-not $env:ANDROID_HOME) {
    Write-Host "⚠ ANDROID_HOME not set. Please set it to Android SDK path." -ForegroundColor Red
    Write-Host "  Example: `$env:ANDROID_HOME = 'D:\AI_projectS\tools\android-sdk'" -ForegroundColor Gray
}

# Create project directory
Write-Host "`n[2/5] Creating project structure at $ProjectPath..." -ForegroundColor Yellow

if (Test-Path $ProjectPath) {
    $confirm = Read-Host "Directory exists. Overwrite? (y/N)"
    if ($confirm -ne 'y') {
        Write-Host "Aborted." -ForegroundColor Red
        exit 1
    }
    Remove-Item -Recurse -Force $ProjectPath
}

New-Item -ItemType Directory -Force -Path $ProjectPath | Out-Null

# Directory structure
$directories = @(
    "gradle",
    "shared/src/commonMain/kotlin/com/aeris/domain/model",
    "shared/src/commonMain/kotlin/com/aeris/domain/repository",
    "shared/src/commonMain/kotlin/com/aeris/domain/usecase",
    "shared/src/commonMain/kotlin/com/aeris/data/repository",
    "shared/src/commonMain/kotlin/com/aeris/data/mapper",
    "shared/src/commonMain/kotlin/com/aeris/ai",
    "shared/src/commonMain/kotlin/com/aeris/di",
    "shared/src/commonMain/kotlin/com/aeris/util",
    "shared/src/commonMain/resources/protocols",
    "shared/src/commonMain/resources/strings",
    "shared/src/commonTest/kotlin/com/aeris",
    "shared/src/androidMain/kotlin/com/aeris/di",
    "shared/src/androidMain/kotlin/com/aeris/util",
    "shared/src/iosMain/kotlin/com/aeris/di",
    "shared/src/iosMain/kotlin/com/aeris/util",
    "androidApp/src/main/kotlin/com/aeris/android/ui/screens",
    "androidApp/src/main/kotlin/com/aeris/android/ui/components",
    "androidApp/src/main/kotlin/com/aeris/android/ui/theme",
    "androidApp/src/main/kotlin/com/aeris/android/ui/model",
    "androidApp/src/main/kotlin/com/aeris/android/data/local/entity",
    "androidApp/src/main/kotlin/com/aeris/android/data/local/dao",
    "androidApp/src/main/kotlin/com/aeris/android/data/local/database",
    "androidApp/src/main/kotlin/com/aeris/android/data/datastore",
    "androidApp/src/main/kotlin/com/aeris/android/data/health",
    "androidApp/src/main/kotlin/com/aeris/android/data/repository",
    "androidApp/src/main/kotlin/com/aeris/android/di",
    "androidApp/src/main/kotlin/com/aeris/android/navigation",
    "androidApp/src/main/kotlin/com/aeris/android/util",
    "androidApp/src/main/res/values",
    "androidApp/src/main/res/values-ru",
    "androidApp/src/main/res/drawable",
    "androidApp/src/main/res/xml",
    "androidApp/src/main/assets/protocols",
    "androidApp/src/test/kotlin/com/aeris/android",
    "androidApp/src/androidTest/kotlin/com/aeris/android",
    "iosApp/DI",
    "docs"
)

foreach ($dir in $directories) {
    New-Item -ItemType Directory -Force -Path "$ProjectPath\$dir" | Out-Null
}

Write-Host "  ✓ Created $(($directories).Count) directories" -ForegroundColor Green

# Create local.properties
Write-Host "`n[3/5] Creating configuration files..." -ForegroundColor Yellow

$localProperties = @"
sdk.dir=$($env:ANDROID_HOME -replace '\\', '\\\\')
kotlin.code.style=official
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
org.gradle.configuration-cache=true
org.gradle.caching=true
"@

if ($env:ANDROID_HOME) {
    Set-Content -Path "$ProjectPath\local.properties" -Value $localProperties
    Write-Host "  ✓ Created local.properties" -ForegroundColor Green
} else {
    Write-Host "  ⚠ Skipped local.properties (ANDROID_HOME not set)" -ForegroundColor Yellow
}

# Download Gradle wrapper
Write-Host "`n[4/5] Setting up Gradle wrapper..." -ForegroundColor Yellow

$gradleWrapperUrl = "https://services.gradle.org/distributions/gradle-8.5-bin.zip"
$wrapperPropertiesContent = @"
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
"@

New-Item -ItemType Directory -Force -Path "$ProjectPath\gradle\wrapper" | Out-Null
Set-Content -Path "$ProjectPath\gradle\wrapper\gradle-wrapper.properties" -Value $wrapperPropertiesContent

# Create gradlew.bat
$gradlewBat = @'
@rem Gradle startup script for Windows
@if "%DEBUG%"=="" @echo off
@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
goto fail

:execute
@rem Setup the command line
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem having the cmd.exe /c execute return that.
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%GRADLE_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal
'@

Set-Content -Path "$ProjectPath\gradlew.bat" -Value $gradlewBat
Write-Host "  ✓ Created Gradle wrapper" -ForegroundColor Green

Write-Host @"

╔════════════════════════════════════════════════════════════════════════╗
║  AERIS Project Structure Created Successfully!                         ║
╚════════════════════════════════════════════════════════════════════════╝

Project location: $ProjectPath

Next steps:
1. Copy all source files from the generated AERIS project
2. Ensure JAVA_HOME points to JDK 17
3. Ensure ANDROID_HOME points to Android SDK
4. Run: cd $ProjectPath && .\gradlew.bat assembleDebug

The APK will be at:
  $ProjectPath\androidApp\build\outputs\apk\debug\androidApp-debug.apk

"@ -ForegroundColor Green

if (-not $SkipBuild -and $env:JAVA_HOME -and $env:ANDROID_HOME) {
    Write-Host "[5/5] Building project..." -ForegroundColor Yellow
    Set-Location $ProjectPath
    & .\gradlew.bat assembleDebug --console=plain
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n✓ Build successful!" -ForegroundColor Green
        Write-Host "APK: $ProjectPath\androidApp\build\outputs\apk\debug\androidApp-debug.apk" -ForegroundColor Cyan
    } else {
        Write-Host "`n✗ Build failed. Please check the errors above." -ForegroundColor Red
    }
} else {
    Write-Host "[5/5] Skipping build (missing environment or -SkipBuild flag)" -ForegroundColor Yellow
}
