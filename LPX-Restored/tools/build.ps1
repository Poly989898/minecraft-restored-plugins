# Compiles src/ + stubs/ against the access-flag-repaired original jar, Paper API 1.21.11 and the libraries of the shared test server.
# Result: build\classes (without the stubs), build\lpx-public.jar. usage: tools\build.ps1
# Same steps as build.sh. env: ORIG (original jar), ORIGINALS, TEST_SERVER, GRADLE_USER_HOME, JAVA_HOME
$ErrorActionPreference = 'Stop'
. "$PSScriptRoot\common.ps1"
$P = Get-PluginRoot; Set-Location $P
$orig = if ($env:ORIG) { $env:ORIG } else { Join-Path (Get-OriginalsDir) 'LPX\LPX-3.9.1.jar' }
$java = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin\java.exe' } else { 'java' }
$javac = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin\javac.exe' } else { 'javac' }
$asm = Get-AsmJar 'asm-9*.jar'
$api = Find-Jar (Get-GradleHome) 'paper-api-1.21.11*.jar'
$libs = (Get-ChildItem (Join-Path (Get-TestServerDir) 'libraries') -Recurse -Filter *.jar | ForEach-Object FullName) -join ';'
New-Item -ItemType Directory -Force build\classes, build\stubs-out | Out-Null
& $java -cp $asm tools\MakePublic.java $orig build\lpx-public.jar
Remove-Item build\classes, build\stubs-out -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force build\classes, build\stubs-out | Out-Null
$excluded = @(Get-Content excludes.txt -ErrorAction SilentlyContinue | Where-Object { $_ -and $_ -notmatch '^\s*#' } | ForEach-Object { $_.Trim() })
$src = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName.Substring($P.Length + 1) } | Where-Object { $excluded -notcontains $_ }
if (-not $src) { $src = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName.Substring($P.Length + 1) } }
$argsFile = 'build\sources.txt'; Set-Content -Path $argsFile -Value $src -Encoding ascii
& $javac -proc:none -nowarn -encoding UTF-8 -cp "build\lpx-public.jar;$api;$libs" -d build\classes "@$argsFile" 2>&1 | Where-Object { $_ -notmatch '^Note:' } | ForEach-Object { "$_" }
# stubs are compile-only: compile them separately so they never land in build\classes
$stubs = (Get-ChildItem stubs -Recurse -Filter *.java | ForEach-Object FullName)
Set-Content -Path 'build\stubs.txt' -Value $stubs -Encoding ascii
& $javac -proc:none -nowarn -encoding UTF-8 -cp "$api;$libs" -d build\stubs-out "@build\stubs.txt"
$cp2 = "build\lpx-public.jar;build\stubs-out;$api;$libs"
& $javac -proc:none -nowarn -encoding UTF-8 -cp $cp2 -d build\classes "@$argsFile" 2>&1 | Where-Object { $_ -notmatch '^Note:' } | ForEach-Object { "$_" }
$count = @(Get-ChildItem build\classes -Recurse -Filter *.class).Count
Write-Output "classes: $count"