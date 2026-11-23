# Builds MythicMobs-Restored on Windows. Same steps as the README "Building" section.
#
# Environment variables (no paths are hardcoded):
#   JAVA_HOME         JDK 25 directory (Gradle runs on it, bytecode is Java 21)
#   GRADLE            full path to gradle.bat (falls back to "gradle" on PATH)
#   GRADLE_USER_HOME  Gradle home directory (optional, Gradle default if unset)
#
# Usage:
#   powershell -ExecutionPolicy Bypass -File tools\build.ps1 [-Offline]

param(
    [switch]$Offline
)

$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
$nmsDir = Join-Path $root 'nms'
$pluginDir = Join-Path $root 'plugin'

if (-not $env:JAVA_HOME) {
    throw 'JAVA_HOME is not set. Point it to a JDK 25 directory.'
}
if (-not (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))) {
    throw "JAVA_HOME does not contain bin\java.exe: $env:JAVA_HOME"
}

if ($env:GRADLE) {
    $gradle = $env:GRADLE
} else {
    $cmd = Get-Command gradle -ErrorAction SilentlyContinue
    if (-not $cmd) {
        throw 'GRADLE is not set and gradle was not found on PATH.'
    }
    $gradle = $cmd.Source
}
if (-not (Test-Path $gradle)) {
    throw "Gradle not found: $gradle"
}

# Only the NMS modules that build from public Maven artifacts (Paper devBundle). The Spigot-mapped modules
# (w1_*, v1_*) are not built: their org.spigotmc:spigot snapshots are not public. See README.

$gradleOptions = @()
if ($Offline) {
    $gradleOptions += '--offline'
}

$nmsTasks = @(
    ':v26_1:compileJava', ':v26_2:compileJava', ':v26_3:compileJava'
)

function Invoke-Gradle {
    param(
        [string]$Directory,
        [string[]]$Tasks
    )
    Push-Location $Directory
    try {
        & $gradle @Tasks @gradleOptions
        if ($LASTEXITCODE -ne 0) {
            throw "Gradle failed in $Directory (exit code $LASTEXITCODE)"
        }
    } finally {
        Pop-Location
    }
}

# The NMS modules compile against plugin\build\classes\java\main, so the plugin
# sources are compiled first (the README order fails on a clean tree).
Write-Host "Step 1: plugin compileJava in $pluginDir"
Invoke-Gradle -Directory $pluginDir -Tasks @('compileJava')

Write-Host "Step 2: NMS modules in $nmsDir"
Invoke-Gradle -Directory $nmsDir -Tasks $nmsTasks

Write-Host "Step 3: plugin jar in $pluginDir"
Invoke-Gradle -Directory $pluginDir -Tasks @('jar')

$jar = Join-Path $pluginDir 'build\libs\MythicMobs-5.14.0-rebuilt.jar'
if (-not (Test-Path $jar)) {
    throw "Build finished but the jar was not found: $jar"
}
Write-Host "Step 3: output $jar"
