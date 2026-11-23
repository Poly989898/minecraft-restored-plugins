# Downloads the JUnit 5 console launcher (Maven Central) into .work\test and verifies its sha1.
. "$PSScriptRoot\env.ps1"

$V = '1.11.4'
$jarName = "junit-platform-console-standalone-$V.jar"
$dir = Join-Path $WORK 'test'
$target = Join-Path $dir $jarName
if (Test-Path -LiteralPath $target -PathType Leaf) {
    Write-Output "JUnit $V already present"
    return
}

$url = "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$V/$jarName"
Save-Download $url $target 300
$expected = (Invoke-WebRequest -Uri "${url}.sha1" -UseBasicParsing -TimeoutSec 60).Content.Trim().Split()[0]
$actual = (Get-FileHash -LiteralPath $target -Algorithm SHA1).Hash
if ($actual -ine $expected) {
    Remove-Item -LiteralPath $target -Force
    throw "sha1 mismatch for $jarName"
}
Write-Output "JUnit $V ready: $target"
