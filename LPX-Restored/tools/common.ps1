# Shared lookups for the PowerShell tools (Windows). Mirrors the bash tools; paths can be overridden by env vars.
function Get-PluginRoot { Split-Path -Parent $PSScriptRoot }
function Get-GradleHome { if ($env:GRADLE_USER_HOME) { $env:GRADLE_USER_HOME } else { Join-Path $env:USERPROFILE '.gradle' } }
function Find-Jar([string]$Root, [string]$Pattern) {
    $hit = Get-ChildItem -Path $Root -Recurse -Filter $Pattern -File -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -notmatch 'sources' } | Select-Object -First 1
    if ($hit) { $hit.FullName } else { throw "jar not found: $Pattern under $Root" }
}
function Get-AsmJar([string]$Name) { Find-Jar (Join-Path (Get-GradleHome) 'caches\modules-2\files-2.1\org.ow2.asm') $Name }
function Get-TestServerDir { if ($env:TEST_SERVER) { $env:TEST_SERVER } else { Join-Path (Split-Path -Parent (Get-PluginRoot)) 'test-server' } }
function Get-OriginalsDir { if ($env:ORIGINALS) { $env:ORIGINALS } else { Join-Path (Split-Path -Parent (Get-PluginRoot)) 'originals' } }