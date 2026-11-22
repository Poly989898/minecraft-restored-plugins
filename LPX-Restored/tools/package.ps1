# build\libs\LPX-3.9.1-restored.jar = original jar + classes recompiled from src\. Run tools\build.ps1 first.
# Same steps as package.sh.
$ErrorActionPreference = 'Stop'
. "$PSScriptRoot\common.ps1"
$P = Get-PluginRoot; Set-Location $P
$orig = if ($env:ORIG) { $env:ORIG } else { Join-Path (Get-OriginalsDir) 'LPX\LPX-3.9.1.jar' }
$java = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin\java.exe' } else { 'java' }
$w = 'build\pkg'; $out = Join-Path $P 'build\libs\LPX-3.9.1-restored.jar'
Remove-Item $w -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force $w, 'build\libs' | Out-Null
Add-Type -AssemblyName System.IO.Compression.FileSystem
[IO.Compression.ZipFile]::ExtractToDirectory($orig, (Join-Path $P $w))
# drop original classes that were renamed or removed in src\ (stale obfuscated names), except the switch-map holders in keep-original.txt
$rel = { param($root) Get-ChildItem $root -Recurse -Filter *.class | ForEach-Object { $_.FullName.Substring($root.Length + 1) -replace '\\', '/' -replace '\.class$', '' } | Sort-Object }
$origClasses = & $rel (Join-Path $P "$w\it\ytnoos")
$origClasses = $origClasses | ForEach-Object { "it/ytnoos/$_" }
$newClasses = & $rel (Join-Path $P 'build\classes')
$keep = @(Get-Content tools\keep-original.txt | Where-Object { $_ })
$stale = $origClasses | Where-Object { $newClasses -notcontains $_ } | Where-Object { $keep -notcontains $_ }
foreach ($c in $stale) { Remove-Item (Join-Path $P "$w\$c.class") -Force -ErrorAction SilentlyContinue }
Copy-Item build\classes\* $w -Recurse -Force
$asm = Get-AsmJar 'asm-9*.jar'
$asmc = Get-AsmJar 'asm-commons-9*.jar'
& $java -cp "$asm;$asmc" tools\RemapClasses.java tools\mappings.txt $w
& $java -cp "$asm;$asmc" tools\RelocateKyori.java $w
Remove-Item $out -Force -ErrorAction SilentlyContinue
& jar --create --file $out --manifest "$w\META-INF\MANIFEST.MF" -C $w .
Write-Output "built $out"