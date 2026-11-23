param([Parameter(ValueFromRemainingArguments = $true)][string[]]$JUnitArgs = @())
# Compiles tests\java against the rebuilt classes (run build.ps1 first) and runs them with JUnit 5.
# Extra arguments are passed to the JUnit console launcher.
. "$PSScriptRoot\env.ps1"

& "$PSScriptRoot\fetch-test-deps.ps1" | Out-Null
$junit = Get-ChildItem -LiteralPath (Join-Path $WORK 'test') -Filter 'junit-platform-console-standalone-*.jar' -File | Select-Object -First 1
if (-not $junit) { throw 'JUnit launcher missing: run fetch-test-deps.ps1' }

$out = Join-Path $BUILD 'test'
Remove-Item -LiteralPath $out -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $out -Force | Out-Null

$cp = (@((Join-Path $BUILD 'ov\17'), (Join-Path $BUILD 'ov\21'), (Join-Path $BUILD 'ov\25'),
        (Get-ClassPath $MAIN_PAPER), $junit.FullName)) -join ';'
$tests = @(Get-ChildItem -LiteralPath (Join-Path $ROOT 'tests\java') -Recurse -Filter '*.java' -File | ForEach-Object { $_.FullName })
$log = Join-Path $BUILD 'tests-compile.log'
$code = Invoke-Javac -Options @('-proc:none', '-nowarn', '-encoding', 'UTF-8', '-source', '17', '-target', '17', '-cp', $cp, '-d', $out) `
    -Sources $tests -ArgFile (Join-Path $BUILD 'tests.args') -LogFile $log
if ($code -ne 0) {
    Get-Content -LiteralPath $log -TotalCount 30 | Write-Host
    throw "test compilation failed, see $log"
}

$runArgs = @('-jar', $junit.FullName, 'execute', '-cp', "$out;$cp", '--scan-classpath', $out, '--disable-banner', '--details=summary') + $JUnitArgs
$code = Invoke-Native 'java' $runArgs
if ($code -ne 0) { throw "tests failed (exit $code)" }
