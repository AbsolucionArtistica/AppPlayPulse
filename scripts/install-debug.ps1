# Script: scripts/install-debug.ps1
# Usage: run this from PowerShell. It will build the debug APK and install it on a connected device.

$ErrorActionPreference = 'Stop'

# Ensure we are in the repo root (script location)
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $scriptDir

Write-Host "Working directory: $(Get-Location)"

# Check gradlew
if (-not (Test-Path .\gradlew.bat)) {
    Write-Error "gradlew.bat not found in project root. Run this script from the project root where gradlew.bat is located."
    exit 1
}

# Build debug APK
Write-Host "Building debug APK (this can take a while)..."
& .\gradlew.bat clean assembleDebug --stacktrace
if ($LASTEXITCODE -ne 0) {
    Write-Error "Gradle build failed (exit code $LASTEXITCODE)"
    exit $LASTEXITCODE
}

$apkPath = Join-Path -Path ".\app\build\outputs\apk\debug" -ChildPath "app-debug.apk"
if (-not (Test-Path $apkPath)) {
    Write-Error "APK not found at $apkPath"
    Get-ChildItem -Path .\app\build\outputs\apk\debug -Force | Write-Host
    exit 1
}

Write-Host "Built APK: $apkPath"

# Find adb
function Find-Adb {
    # Try adb in PATH
    $adb = (Get-Command adb -ErrorAction SilentlyContinue)?.Source
    if ($adb) { return $adb }

    # Common locations
    $candidates = @(
        "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe",
        "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe",
        "$env:ANDROID_HOME\platform-tools\adb.exe",
        "C:\Android\platform-tools\adb.exe",
        "C:\Program Files (x86)\Android\android-sdk\platform-tools\adb.exe"
    )
    foreach ($c in $candidates) {
        if ($c -and (Test-Path $c)) { return $c }
    }
    return $null
}

$adb = Find-Adb
if (-not $adb) {
    Write-Error "adb executable not found. Please install Android platform-tools and ensure adb is in PATH or set ANDROID_SDK_ROOT/ANDROID_HOME.";
    exit 2
}

Write-Host "Using adb: $adb"

# Show devices
& $adb devices | Write-Host
$devices = (& $adb devices) -split "\r?\n" | Where-Object { 
    ($_ -and ($_ -notmatch "List of devices")) -and ($_ -match "device$")
}
if (-not $devices) {
    Write-Error "No devices detected. Ensure USB debugging is enabled and the device is authorized.";
    exit 3
}

# Install APK
Write-Host "Installing APK to device..."
& $adb install -r $apkPath
if ($LASTEXITCODE -ne 0) {
    Write-Error "adb install failed with exit code $LASTEXITCODE"
    exit $LASTEXITCODE
}

Write-Host "APK installed successfully."
exit 0
