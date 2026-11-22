# build\libs\nexo-1.26-custom.jar = original jar + classes recompiled from src\ (names fixed back to Kotlin's hyphenated form).
# Usage: tools\package.ps1   (run `gradle compileJava` first). Same steps as package.sh.
$ErrorActionPreference = 'Stop'
$P = Split-Path -Parent $PSScriptRoot; Set-Location $P
$orig = Join-Path $P 'libs\nexo-1.26-patched.jar'
$out = Join-Path $P 'build\libs\nexo-1.26-custom.jar'
$w = Join-Path $P 'build\pkg'
$py = if ($env:PY) { $env:PY } else { 'python' }
Remove-Item $w -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force "$w\orig", "$w\new", (Join-Path $P 'build\libs') | Out-Null
Add-Type -AssemblyName System.IO.Compression.FileSystem
[IO.Compression.ZipFile]::ExtractToDirectory($orig, "$w\orig")
Copy-Item "$P\build\classes\java\main\*" "$w\new" -Recurse -Force
# compile-time Duration stub, must not be packaged
Remove-Item "$w\new\kotlin" -Recurse -Force -ErrorAction SilentlyContinue
# optional-dependency integrations: keep original bytecode (javac-built versions fail class verification when the plugin is absent)
foreach ($d in 'com\nexomc\nexo\compatibilities', 'com\nexomc\nexo\mechanics\furniture\compatibility', 'nl', 'gs', 'ai', 'me', 'com\moulberry') {
    Remove-Item "$w\new\$d" -Recurse -Force -ErrorAction SilentlyContinue
}
& $py "$P\tools\fixnames.py" "$w\orig" "$w\new"
Copy-Item "$w\new\*" "$w\orig" -Recurse -Force
Remove-Item $out -Force -ErrorAction SilentlyContinue
& jar --create --file $out --manifest "$w\orig\META-INF\MANIFEST.MF" -C "$w\orig" .
Write-Output "built $out"