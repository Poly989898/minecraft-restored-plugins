# Compiles api, core, core21, core25 and v26_3 into build\ov\{17,21,25}.
# Targets match the original jar: api/core = Java 17, core21 = Java 21, core25/v26_3 = Java 25.
. "$PSScriptRoot\env.ps1"

Assert-File $LOMBOK 'run fetch-deps.ps1 first'
if (-not (Test-Path -LiteralPath (Get-PaperDir $MAIN_PAPER))) {
    throw "Paper $MAIN_PAPER is missing: run fetch-paper.ps1 $MAIN_PAPER first"
}

# Sources matching the module pattern. A file in patches\java replaces the src file with the same package path.
function Get-GroupSources([string]$Pattern) {
    $roots = @((Join-Path $ROOT 'src'), (Join-Path $ROOT 'patches\java')) | Where-Object { Test-Path -LiteralPath $_ }
    $all = @(Get-ChildItem -LiteralPath $roots -Recurse -Filter '*.java' -File | ForEach-Object {
            [pscustomobject]@{
                Full = $_.FullName
                Rel  = $_.FullName.Substring($ROOT.Length + 1).Replace('\', '/')
            }
        } | Where-Object { $_.Rel -match $Pattern })

    $prefix = 'patches/java/'
    $replaced = @($all | Where-Object { $_.Rel.StartsWith($prefix) } | ForEach-Object { $_.Rel.Substring($prefix.Length) })
    $selected = foreach ($e in $all) {
        if ($e.Rel.StartsWith($prefix)) { $e.Full; continue }
        $shadowed = $false
        foreach ($p in $replaced) {
            if ($e.Rel.EndsWith('src/' + $p)) { $shadowed = $true; break }
        }
        if (-not $shadowed) { $e.Full }
    }
    return @($selected)
}

function Build-Group([string]$Target, [string]$Pattern) {
    $files = Get-GroupSources $Pattern
    $out = Join-Path $BUILD "ov\$Target"
    New-Item -ItemType Directory -Path $out -Force | Out-Null
    $log = Join-Path $BUILD "group$Target.log"
    $options = @('-processorpath', $LOMBOK, '-nowarn', '-encoding', 'UTF-8',
        '-source', $Target, '-target', $Target,
        '-cp', (Get-ClassPath $MAIN_PAPER), '-d', $out)
    $code = Invoke-Javac -Options $options -Sources $files -ArgFile (Join-Path $BUILD "group$Target.args") -LogFile $log
    if ($code -ne 0) {
        Get-Content -LiteralPath $log -TotalCount 30 | Write-Host
        throw "FAILED (java $Target), see $log"
    }
    Write-Output "java ${Target}: $($files.Count) files OK"
}

Remove-Item -LiteralPath (Join-Path $BUILD 'ov') -Recurse -Force -ErrorAction SilentlyContinue
# Order: api (no dependencies) -> NMS adapters (depend on api only) -> core (depends on api and adapters).
Build-Group '17' 'modelengine/api/'
& "$PSScriptRoot\build-adapters.ps1"
Build-Group '17' 'modelengine/core/'
Build-Group '21' 'modelengine/core21/'
Build-Group '25' 'modelengine/core25/'
