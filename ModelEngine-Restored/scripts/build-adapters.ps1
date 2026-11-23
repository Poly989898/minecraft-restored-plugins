param([string[]]$Modules = @())
# usage: build-adapters.ps1 [module ...]   (default: all NMS adapters, including v26_1 and v26_2)
# Each adapter is compiled against its own Paper version, downloaded on demand.
. "$PSScriptRoot\env.ps1"

Assert-File $LOMBOK 'run fetch-deps.ps1 first'
$selected = if ($Modules.Count -gt 0) { @($Modules) } else { @($ADAPTERS.Keys) }
$failed = @()

foreach ($m in ($selected | Sort-Object)) {
    if (-not $ADAPTERS.Contains($m)) { throw "unknown module $m" }
    $ver = $ADAPTERS[$m][0]
    $tgt = $ADAPTERS[$m][1]
    & "$PSScriptRoot\fetch-paper.ps1" -Version $ver | Out-Null

    $src = Join-Path $ROOT "src\com\ticxo\modelengine\$m"
    if (-not (Test-Path -LiteralPath $src)) { throw "missing source folder $src" }
    $files = @(Get-ChildItem -LiteralPath $src -Recurse -Filter '*.java' -File | ForEach-Object { $_.FullName })

    $out = Join-Path $BUILD "ov-$m"
    Remove-Item -LiteralPath $out -Recurse -Force -ErrorAction SilentlyContinue
    New-Item -ItemType Directory -Path $out -Force | Out-Null
    $log = Join-Path $BUILD "$m.log"
    $options = @('-processorpath', $LOMBOK, '-nowarn', '-encoding', 'UTF-8',
        '-source', $tgt, '-target', $tgt,
        '-cp', (Get-ClassPath $ver), '-d', $out)
    $code = Invoke-Javac -Options $options -Sources $files -ArgFile (Join-Path $BUILD "$m.args") -LogFile $log
    if ($code -eq 0) {
        Write-Output "$m (Paper $ver, java ${tgt}): $($files.Count) files OK"
    }
    else {
        Write-Warning "${m}: FAILED, see $log"
        $failed += $m
    }
}

if ($failed.Count -gt 0) { throw "failed adapters: $($failed -join ', ')" }
