# Builds + packages LPX and starts a throw-away Paper 26.3 server (scratch copy, port 25603, only LPX) until startup finishes.
# usage: tools\testserver.ps1 [-Commands "lpx;lpx reload"] [-Jar path\to\other.jar]   (-Jar with the original jar gives a baseline run)
# Same steps as testserver.sh. The scratch copy uses junctions for libraries, versions and cache.
param(
    [string]$Commands = 'lpx',
    [string]$Jar
)
$ErrorActionPreference = 'Stop'
. "$PSScriptRoot\common.ps1"
$P = Get-PluginRoot; Set-Location $P
if (-not $Jar) { & "$PSScriptRoot\build.ps1"; & "$PSScriptRoot\package.ps1" | Out-Null; $Jar = Join-Path $P 'build\libs\LPX-3.9.1-restored.jar' }
$T = Get-TestServerDir
$S = Join-Path ([IO.Path]::GetTempPath()) 'lpxtest'
Remove-Item $S -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force (Join-Path $S 'plugins') | Out-Null
foreach ($d in 'libraries', 'versions', 'cache') { New-Item -ItemType Junction -Path (Join-Path $S $d) -Target (Join-Path $T $d) | Out-Null }
$paper = (Get-ChildItem $T -Filter 'paper-26.3-*.jar' | Select-Object -First 1).Name
Copy-Item (Join-Path $T $paper) $S; Copy-Item (Join-Path $T 'eula.txt') $S
Set-Content -Path (Join-Path $S 'server.properties') -Value "online-mode=false`nserver-port=25603`nview-distance=4`nlevel-type=flat`ngenerate-structures=false" -Encoding ascii
Copy-Item $Jar (Join-Path $S 'plugins')
$java = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin\java.exe' } else { 'java' }
$log = Join-Path $S 'server.log'
$psi = New-Object Diagnostics.ProcessStartInfo
$psi.FileName = $java; $psi.Arguments = "-Xmx1G -jar $paper nogui"; $psi.WorkingDirectory = $S
$psi.RedirectStandardInput = $true; $psi.RedirectStandardOutput = $true; $psi.RedirectStandardError = $true
$psi.UseShellExecute = $false; $psi.CreateNoWindow = $true
$proc = [Diagnostics.Process]::Start($psi)
foreach ($ev in 'OutputDataReceived', 'ErrorDataReceived') {
    Register-ObjectEvent -InputObject $proc -EventName $ev -MessageData $log -Action { if ($EventArgs.Data -ne $null) { Add-Content -Path $Event.MessageData -Value $EventArgs.Data -Encoding UTF8 } } | Out-Null
}
$proc.BeginOutputReadLine(); $proc.BeginErrorReadLine()
for ($i = 0; $i -lt 90; $i++) {
    Start-Sleep -Seconds 2
    if ((Test-Path $log) -and (Select-String -Path $log -Pattern 'Done \(|Error occurred while enabling' -Quiet)) { break }
}
Start-Sleep -Seconds 4
foreach ($c in ($Commands -split ';')) { $proc.StandardInput.WriteLine($c.Trim()); $proc.StandardInput.Flush(); Start-Sleep -Seconds 6 }
$proc.StandardInput.WriteLine('stop'); $proc.StandardInput.Flush()
if (-not $proc.WaitForExit(60000)) { $proc.Kill() }
Start-Sleep -Seconds 1
$lines = if (Test-Path $log) { Get-Content $log } else { @() }
Write-Output "exceptions/errors: $(@($lines | Select-String -Pattern 'exception|caused by|NoClassDef|VerifyError|LinkageError|AbstractMethodError' -CaseSensitive:$false).Count)"
$lines | Select-String -Pattern 'LPX|Done \(|Exception|Caused by|VerifyError|Error' | Select-Object -First 30 | ForEach-Object { $_.Line.Substring(0, [math]::Min(200, $_.Line.Length)) }