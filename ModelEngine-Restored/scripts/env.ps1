# Shared settings for the Windows (PowerShell) build scripts. Dot-source this file; do not run it:
#   . "$PSScriptRoot\env.ps1"
# Settings come from environment variables. Only JAVA_HOME is required.
#   JAVA_HOME         JDK 25 folder (must contain bin\javac.exe)
#   GRADLE_USER_HOME  Gradle user home (not used by the build steps)
#   GRADLE            path to gradle.bat (not used by the build steps)
#   TEST_SERVER       test server folder (not used by the build steps)
#   ORIG_JAR          original UltimateModelEngine.jar (default: ..\originals\ModelEngine\UltimateModelEngine.jar)
#   WORK, BUILD, DIST work, build and output folders (default: .work, build, dist in the repo root)
#   MAIN_PAPER        Paper version for api/core/core21/core25/v26_3 (default: 26.3)

$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'
[Net.ServicePointManager]::SecurityProtocol = [Net.ServicePointManager]::SecurityProtocol -bor [Net.SecurityProtocolType]::Tls12

$ROOT = Split-Path -Parent $PSScriptRoot
$WORK = if ($env:WORK) { [IO.Path]::GetFullPath($env:WORK) } else { Join-Path $ROOT '.work' }
$BUILD = if ($env:BUILD) { [IO.Path]::GetFullPath($env:BUILD) } else { Join-Path $ROOT 'build' }
$DIST = if ($env:DIST) { [IO.Path]::GetFullPath($env:DIST) } else { Join-Path $ROOT 'dist' }
$DEPS = Join-Path $WORK 'deps'
$LOMBOK = Join-Path $DEPS 'lombok-1.18.42.jar'
$ORIG_JAR = if ($env:ORIG_JAR) { [IO.Path]::GetFullPath($env:ORIG_JAR) } else { Join-Path (Split-Path -Parent $ROOT) 'originals\ModelEngine\UltimateModelEngine.jar' }
$MAIN_PAPER = if ($env:MAIN_PAPER) { $env:MAIN_PAPER } else { '26.3' }

# module -> Paper version, Java target
$ADAPTERS = [ordered]@{
    'v1_19_R3' = @('1.19.4', '17')
    'v1_20_R1' = @('1.20.1', '17')
    'v1_20_R2' = @('1.20.2', '17')
    'v1_20_R3' = @('1.20.4', '17')
    'v1_20_R4' = @('1.20.6', '21')
    'v1_21_R1' = @('1.21.1', '21')
    'v1_21_R2' = @('1.21.3', '21')
    'v1_21_R3' = @('1.21.4', '21')
    'v1_21_R4' = @('1.21.5', '21')
    'v1_21_R5' = @('1.21.8', '21')
    'v1_21_R6' = @('1.21.10', '21')
    'v1_21_R7' = @('1.21.11', '21')
    'v26_1'    = @('26.1.2', '25')
    'v26_2'    = @('26.2', '25')
    'v26_3'    = @('26.3', '25')
}

if (-not $env:JAVA_HOME -or -not (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin\javac.exe'))) {
    throw 'JAVA_HOME is not set or does not contain bin\javac.exe (JDK 25 required).'
}
$env:PATH = (Join-Path $env:JAVA_HOME 'bin') + ';' + $env:PATH

function Get-PaperDir([string]$Version) {
    Join-Path (Join-Path $WORK 'paper') $Version
}

# Classpath for a Paper version: its libraries and versions, the shared deps, then the original jar.
function Get-ClassPath([string]$PaperVersion) {
    $paper = Get-PaperDir $PaperVersion
    $jars = foreach ($dir in @((Join-Path $paper 'libraries'), (Join-Path $paper 'versions'), $DEPS)) {
        if (Test-Path -LiteralPath $dir) {
            Get-ChildItem -LiteralPath $dir -Filter '*.jar' -Recurse -File | ForEach-Object { $_.FullName }
        }
    }
    $jars = @($jars | Where-Object { (Split-Path -Leaf $_) -ne 'orig-UME.jar' } | Sort-Object)
    # classes already built in this run: api (ov\17) and the NMS adapters (ov-v*), used by later groups
    $built = @(Join-Path $BUILD 'ov\17') + @(Get-ChildItem -LiteralPath $BUILD -Directory -Filter 'ov-*' -ErrorAction SilentlyContinue | ForEach-Object { $_.FullName }) | Where-Object { Test-Path -LiteralPath $_ }
    $jars = @($jars) + @($built)
    $orig = Join-Path $DEPS 'orig-UME.jar'   # optional: only present when the original jar was given
    if (Test-Path -LiteralPath $orig) { $jars = @($jars) + $orig }
    return ($jars -join ';')
}

function Write-Utf8Lines([string]$Path, [string[]]$Lines) {
    New-Item -ItemType Directory -Path (Split-Path -Parent $Path) -Force | Out-Null
    [IO.File]::WriteAllLines($Path, $Lines, (New-Object System.Text.UTF8Encoding($false)))
}

# Runs a native tool and returns its exit code. Output goes to $LogFile, or to the host, or is dropped with -Quiet.
function Invoke-Native {
    param([string]$Exe, [string[]]$Arguments, [string]$LogFile, [switch]$Quiet)
    $previous = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        if ($LogFile) { & $Exe @Arguments *> $LogFile }
        elseif ($Quiet) { & $Exe @Arguments *> $null }
        else { & $Exe @Arguments | Out-Host }
    }
    finally { $ErrorActionPreference = $previous }
    return $LASTEXITCODE
}

# javac through an argument file (keeps long classpaths off the command line). Paths use forward slashes.
function Invoke-Javac {
    param([string[]]$Options, [string[]]$Sources, [string]$ArgFile, [string]$LogFile)
    $tokens = @($Options) + @($Sources)
    $lines = foreach ($t in $tokens) { '"' + ($t -replace '\\', '/') + '"' }
    Write-Utf8Lines $ArgFile @($lines)
    return Invoke-Native 'javac' @("@$ArgFile") $LogFile
}

function Save-Download([string]$Url, [string]$Target, [int]$TimeoutSec = 300) {
    New-Item -ItemType Directory -Path (Split-Path -Parent $Target) -Force | Out-Null
    try {
        Invoke-WebRequest -Uri $Url -OutFile $Target -UseBasicParsing -TimeoutSec $TimeoutSec
    }
    catch {
        Remove-Item -LiteralPath $Target -Force -ErrorAction SilentlyContinue
        throw "download failed: $Url ($($_.Exception.Message))"
    }
}

function Assert-File([string]$Path, [string]$Hint) {
    if (-not (Test-Path -LiteralPath $Path -PathType Leaf)) { throw "Missing: $Path ($Hint)" }
}
