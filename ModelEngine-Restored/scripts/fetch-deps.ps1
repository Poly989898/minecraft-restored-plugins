# Downloads compile-time dependencies into .work\deps and copies the original jar there.
# Downloads that fail are reported together at the end; the others are still fetched.
. "$PSScriptRoot\env.ps1"

New-Item -ItemType Directory -Path $DEPS -Force | Out-Null
if (Test-Path -LiteralPath $ORIG_JAR -PathType Leaf) {
    Copy-Item -LiteralPath $ORIG_JAR -Destination (Join-Path $DEPS 'orig-UME.jar') -Force
}

$script:failed = @()
function Get-Dep([string]$Url) {
    $name = $Url.Substring($Url.LastIndexOf('/') + 1)
    $target = Join-Path $DEPS $name
    if (Test-Path -LiteralPath $target) { return }
    try {
        Save-Download $Url $target 300
        Write-Output "downloaded $name"
    }
    catch {
        Write-Warning $_.Exception.Message
        $script:failed += $name
    }
}

$MC = 'https://repo1.maven.org/maven2'
Get-Dep "$MC/org/projectlombok/lombok/1.18.42/lombok-1.18.42.jar"
Get-Dep "$MC/org/jetbrains/annotations/24.1.0/annotations-24.1.0.jar"
Get-Dep "$MC/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar"
Get-Dep "$MC/net/kyori/adventure-text-serializer-bungeecord/4.3.0/adventure-text-serializer-bungeecord-4.3.0.jar"
Get-Dep 'https://repo.viaversion.com/com/viaversion/viaversion-api/5.5.1/viaversion-api-5.5.1.jar'   # optional integration (compile only)
Get-Dep 'https://mvn.lumine.io/repository/maven-public/io/lumine/Mythic-Dist/5.14.0/Mythic-Dist-5.14.0.jar'   # MythicMobs API
Get-Dep 'https://repo.md-5.net/content/repositories/releases/LibsDisguises/LibsDisguises/10.0.44/LibsDisguises-10.0.44.jar'

# Citizens: take the newest build listed in the snapshot folder.
$CIT = 'https://maven.citizensnpcs.co/repo/net/citizensnpcs/citizens-main/2.0.44-SNAPSHOT'
try {
    $listing = (Invoke-WebRequest -Uri "$CIT/" -UseBasicParsing -TimeoutSec 60).Content
    $names = @([regex]::Matches($listing, 'citizens-main-2\.0\.44-[0-9.]+-[0-9]+\.jar') | ForEach-Object { $_.Value } | Sort-Object -Unique)
    if ($names.Count -eq 0) { throw 'no Citizens jar found in the snapshot listing' }
    Get-Dep "$CIT/$($names[-1])"
}
catch {
    Write-Warning "Citizens: $($_.Exception.Message)"
    $script:failed += 'citizens'
}

Get-ChildItem -LiteralPath $DEPS | ForEach-Object { Write-Output $_.Name }
if ($script:failed.Count -gt 0) {
    throw "Failed to fetch: $($script:failed -join ', ')"
}
