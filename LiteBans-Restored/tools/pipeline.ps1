# Regenerates src/main/java from the original jar: clean flags -> decrypt string tables (static) -> split local-variable webs -> deobfuscate (auto + mappings/manual.map) -> decompile -> mechanical source fixes.
# Windows counterpart of tools/pipeline.sh (same steps, same order).
# usage: tools\pipeline.ps1
# env:   TOOLS   folder holding asm\, vineflower\vineflower-1.12.0-slim.jar and python\python.exe
#        ASM, VF, PY   optional overrides of the single tool paths (take precedence over TOOLS)
#        ORIG_JAR      original jar (default: ..\originals\LiteBans\LiteBans-2.19.0-bm.jar)
#        JAVA_HOME     JDK 25; its bin folder is put first on PATH
$ErrorActionPreference = 'Stop'

function Invoke-Checked {
    param([string]$Exe, [string[]]$Arguments)
    & $Exe @Arguments
    if ($LASTEXITCODE -ne 0) { throw "failed ($LASTEXITCODE): $Exe $($Arguments -join ' ')" }
}

function Get-Setting {
    param([string]$Name, [string]$Default)
    $value = [Environment]::GetEnvironmentVariable($Name)
    if ($value) { return $value }
    return $Default
}

$tools = [Environment]::GetEnvironmentVariable('TOOLS')
if (-not $tools) { $tools = '' }
$asm = Get-Setting 'ASM' (Join-Path $tools 'asm')
$py = Get-Setting 'PY' (Join-Path $tools 'python\python.exe')
$vf = Get-Setting 'VF' (Join-Path $tools 'vineflower\vineflower-1.12.0-slim.jar')
$orig = Get-Setting 'ORIG_JAR' '..\originals\LiteBans\LiteBans-2.19.0-bm.jar'
$cp = (@('asm-9.11.1.jar', 'asm-commons-9.11.1.jar', 'asm-tree-9.11.1.jar', 'asm-analysis-9.11.1.jar') |
    ForEach-Object { Join-Path $asm $_ }) -join [IO.Path]::PathSeparator

if ($env:JAVA_HOME) { $env:PATH = (Join-Path $env:JAVA_HOME 'bin') + [IO.Path]::PathSeparator + $env:PATH }

$root = Split-Path -Parent $PSScriptRoot
Push-Location $root
try {
    New-Item -ItemType Directory -Force -Path '.work' | Out-Null

    Invoke-Checked 'java' @('-cp', $cp, 'tools/Cleaner.java', $orig, '.work/cleaned.jar')
    Invoke-Checked 'java' @('-cp', $cp, 'tools/Strings.java', '.work/cleaned.jar', '.work/strings.jar', '.work/strings.txt')
    Invoke-Checked 'java' @('-cp', $cp, 'tools/Lambdas.java', '.work/strings.jar', '.work/lambdas.jar')
    Invoke-Checked 'java' @('-cp', $cp, 'tools/Locals.java', '.work/lambdas.jar', '.work/locals.jar')
    Invoke-Checked 'java' @('-cp', $cp, 'tools/Deobf.java', '.work/locals.jar', '.work/deobf.jar', 'mappings/manual.map')

    # Vineflower failures are ignored, as in the shell version (|| true)
    if (Test-Path '.work\dec') { Remove-Item '.work\dec' -Recurse -Force }
    New-Item -ItemType Directory -Force -Path '.work\dec' | Out-Null
    & java -Xmx4g -jar $vf --silent -lac=1 '.work/deobf.jar' '.work/dec/' | Out-Null

    Invoke-Checked $py @('-I', 'tools/resugar.py', '.work/dec/litebans')
    Invoke-Checked $py @('-I', 'tools/postfix.py', '.work/dec', '.work/deobf.jar.bodies')

    if (Test-Path 'src\main\java\litebans') { Remove-Item 'src\main\java\litebans' -Recurse -Force }
    Copy-Item '.work\dec\litebans' 'src\main\java\' -Recurse -Force

    Invoke-Checked $py @('-I', 'tools/manual-fixes.py')

    if (Test-Path '.work\cp.txt') {
        & java 'tools/Fixer.java' '.work/cp.txt' 'src/main/java' 'stubs'
        if ($LASTEXITCODE -ne 0) { Write-Output 'Fixer failed' }
    } else {
        Write-Output '(.work\cp.txt missing: run gradle printCp)'
    }

    $count = @(Get-ChildItem 'src\main\java' -Recurse -Filter '*.java').Count
    Write-Output "sources regenerated: $count files"
} finally {
    Pop-Location
}
