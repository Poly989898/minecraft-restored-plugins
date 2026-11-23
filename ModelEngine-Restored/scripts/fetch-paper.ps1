param([Parameter(Mandatory = $true)][string]$Version)
# usage: fetch-paper.ps1 <minecraft-version>
# Downloads the latest Paper build from the official PaperMC API, verifies its sha256,
# and unpacks the server libraries with -Dpaperclip.patchonly=true (the server is not started).
. "$PSScriptRoot\env.ps1"

function Test-PaperReady([string]$Dir) {
    $versions = Join-Path $Dir 'versions'
    if (-not (Test-Path -LiteralPath $versions)) { return $false }
    $jar = Get-ChildItem -LiteralPath $versions -Directory |
        ForEach-Object { Get-ChildItem -LiteralPath $_.FullName -Filter 'paper-*.jar' -File } |
        Select-Object -First 1
    return [bool]$jar
}

$dir = Get-PaperDir $Version
if (Test-PaperReady $dir) {
    Write-Output "Paper $Version already prepared: $dir"
    return
}
New-Item -ItemType Directory -Path $dir -Force | Out-Null

$info = Invoke-RestMethod -Uri "https://fill.papermc.io/v3/projects/paper/versions/$Version/builds/latest" -UseBasicParsing -TimeoutSec 60
$server = $info.downloads.'server:default'
$url = [string]$server.url
$expected = [string]$server.checksums.sha256
$name = [string]$server.name
$target = Join-Path $dir $name

Save-Download $url $target 900
$actual = (Get-FileHash -LiteralPath $target -Algorithm SHA256).Hash
if ($actual -ine $expected) {
    Remove-Item -LiteralPath $target -Force
    throw "Paper ${Version}: sha256 mismatch for $name"
}

Push-Location $dir
try {
    $null = Invoke-Native 'java' @('-Dpaperclip.patchonly=true', '-jar', $name) -Quiet
}
finally {
    Pop-Location
}

if (-not (Test-PaperReady $dir)) { throw "Paper ${Version}: library extraction failed" }
Write-Output "Paper $Version ready: $dir"
