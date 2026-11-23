# Builds dist\UltimateModelEngine-fixed.jar = original jar + every recompiled class.
. "$PSScriptRoot\env.ps1"
Add-Type -AssemblyName System.IO.Compression.FileSystem


$M = Join-Path $BUILD 'merge'
Remove-Item -LiteralPath $M -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $M, $DIST -Force | Out-Null
if (Test-Path -LiteralPath $ORIG_JAR -PathType Leaf) { [System.IO.Compression.ZipFile]::ExtractToDirectory($ORIG_JAR, $M) }
# without the original jar, resources come from resources\ (copied below)
if (Test-Path -LiteralPath (Join-Path $ROOT 'resources')) { Copy-Item -Path (Join-Path $ROOT 'resources\*') -Destination $M -Recurse -Force }

$dirs = @((Join-Path $BUILD 'ov\17'), (Join-Path $BUILD 'ov\21'), (Join-Path $BUILD 'ov\25'))
$dirs += @(Get-ChildItem -LiteralPath $BUILD -Directory -Filter 'ov-v*' | Sort-Object Name | ForEach-Object { $_.FullName })
foreach ($d in $dirs) {
    if (Test-Path -LiteralPath $d -PathType Container) {
        Copy-Item -Path (Join-Path $d '*') -Destination $M -Recurse -Force
    }
}

# manifest: from the original jar when it was extracted, otherwise a minimal one
$mf = Join-Path $M 'META-INF\MANIFEST.MF'
if (-not (Test-Path -LiteralPath $mf)) {
    New-Item -ItemType Directory -Path (Split-Path -Parent $mf) -Force | Out-Null
    [IO.File]::WriteAllText($mf, "Manifest-Version: 1.0
Implementation-Title: ModelEngine (rebuilt)

", (New-Object System.Text.UTF8Encoding($false)))
}
$out = Join-Path $DIST 'UltimateModelEngine-fixed.jar'
Remove-Item -LiteralPath $out -Force -ErrorAction SilentlyContinue
Push-Location $M
try {
    $code = Invoke-Native 'jar' @('--create', '--file', $out, '--manifest', 'META-INF/MANIFEST.MF', '.')
}
finally {
    Pop-Location
}
if ($code -ne 0) { throw "jar failed (exit $code)" }

$hash = (Get-FileHash -LiteralPath $out -Algorithm SHA256).Hash.ToLowerInvariant()
Write-Output "$hash  $out"
