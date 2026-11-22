# Builds the jar (compileJava + package.ps1) and starts a throw-away Paper 26.3 server (scratch copy, port 25602, only Nexo) until startup finishes.
# usage: tools\testserver.ps1 [-Commands "nexo reload all;nexo list"]
# Same steps as testserver.sh. Needs gradle (GRADLE env or PATH), JAVA_HOME, and the server folder (TEST_SERVER).
param([string]$Commands = 'nexo reload all')
$ErrorActionPreference = 'Stop'
$P = Split-Path -Parent $PSScriptRoot; Set-Location $P
$gradle = if ($env:GRADLE) { $env:GRADLE } else { 'gradle' }
& $gradle --offline compileJava -q
& "$PSScriptRoot\package.ps1" | Out-Null
$T = if ($env:TEST_SERVER) { $env:TEST_SERVER } else { Join-Path (Split-Path -Parent $P) 'test-server' }
$S = Join-Path ([IO.Path]::GetTempPath()) 'nexotest'
Remove-Item $S -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force (Join-Path $S 'plugins') | Out-Null
foreach ($d in 'libraries', 'versions', 'cache') { New-Item -ItemType Junction -Path (Join-Path $S $d) -Target (Join-Path $T $d) | Out-Null }
$paper = (Get-ChildItem $T -Filter 'paper-26.3-*.jar' | Select-Object -First 1).Name
Copy-Item (Join-Path $T $paper) $S; Copy-Item (Join-Path $T 'eula.txt') $S
Set-Content -Path (Join-Path $S 'server.properties') -Value "online-mode=false`nserver-port=25602`nview-distance=4`nlevel-type=flat`ngenerate-structures=false" -Encoding ascii
Copy-Item (Join-Path $P 'build\libs\nexo-1.26-custom.jar') (Join-Path $S 'plugins')
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
foreach ($c in ($Commands -split ';')) { $proc.StandardInput.WriteLine($c.Trim()); $proc.StandardInput.Flush(); Start-Sleep -Seconds 8 }
$proc.StandardInput.WriteLine('stop'); $proc.StandardInput.Flush()
if (-not $proc.WaitForExit(60000)) { $proc.Kill() }
Start-Sleep -Seconds 1
$lines = if (Test-Path $log) { Get-Content $log } else { @() }
Write-Output "exceptions/errors: $(@($lines | Select-String -Pattern 'exception|caused by|NoClassDef|VerifyError|LinkageError' -CaseSensitive:$false).Count)"
$lines | Select-String -Pattern 'Nexo \| (ResourcePack|Successfully)|Done \(|Exception|Caused by|VerifyError' | Select-Object -First 20 | ForEach-Object { $_.Line.Substring(0, [math]::Min(200, $_.Line.Length)) }