# Builds the jar and starts a throw-away Paper server (scratch copy, port 25601, only LiteBans) until startup finishes or fails.
# Windows counterpart of tools/testserver.sh. The console commands are sent to the server's stdin of the child process
# (instead of a fifo); the server output is appended to server.log in the scratch folder.
# usage: tools\testserver.ps1 [-Commands 'ban Bob griefing;unban Bob'] [-LinesMax 40]
# env:   TEST_SERVER  folder with paper-26.3-*.jar, libraries, versions, cache (default: ..\test-server next to the repository)
#        TMPDIR       scratch root (default: the system temp folder)
#        JAVA_HOME    JDK 25 (default: java on PATH)
#        GRADLE       gradle executable (default: gradle on PATH)
#        LB_SED       optional sed-style "s/pattern/replacement/" applied to config.yml (same as the shell version)
param(
    [string]$Commands = '',
    [int]$LinesMax = 40
)
$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
$gradle = if ($env:GRADLE) { $env:GRADLE } else { 'gradle' }
$java = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin\java.exe' } else { 'java' }
$tmp = if ($env:TMPDIR) { $env:TMPDIR } else { [IO.Path]::GetTempPath() }
$testServer = if ($env:TEST_SERVER) { $env:TEST_SERVER } else { Join-Path (Split-Path -Parent $root) 'test-server' }
if (-not (Test-Path $testServer)) { throw "test server folder not found: $testServer (set TEST_SERVER)" }
$paperName = (Get-ChildItem $testServer -Filter 'paper-26.3-*.jar' | Sort-Object Name | Select-Object -Last 1).Name
if (-not $paperName) { throw "no paper-26.3-*.jar in $testServer" }

# 1. build
Push-Location $root
try {
    & $gradle jar -q
    if ($LASTEXITCODE -ne 0) { throw 'gradle jar failed' }
} finally {
    Pop-Location
}
$jar = Join-Path $root 'build\libs\LiteBans-Restored.jar'
if (-not (Test-Path $jar)) { throw "missing $jar" }

# 2. scratch server: junctions to the shared libraries/versions/cache, Paper jar and eula copied
$s = Join-Path $tmp 'litebans-test'
New-Item -ItemType Directory -Force -Path (Join-Path $s 'plugins') | Out-Null
foreach ($d in 'libraries', 'versions', 'cache') {
    $link = Join-Path $s $d
    if (-not (Test-Path $link)) {
        New-Item -ItemType Junction -Path $link -Target (Join-Path $testServer $d) | Out-Null
    }
}
if (-not (Test-Path (Join-Path $s $paperName))) { Copy-Item (Join-Path $testServer $paperName) $s }
if (-not (Test-Path (Join-Path $s 'eula.txt'))) { Copy-Item (Join-Path $testServer 'eula.txt') $s }
$utf8 = New-Object Text.UTF8Encoding($false)
if (-not (Test-Path (Join-Path $s 'server.properties'))) {
    $props = "online-mode=false`nserver-port=25601`nview-distance=4`nlevel-type=flat`ngenerate-structures=false`n"
    [IO.File]::WriteAllText((Join-Path $s 'server.properties'), $props, $utf8)
}

# 3. clean run state and install the plugin
foreach ($p in 'world', 'plugins\LiteBans', 'logs', 'server.log') {
    $path = Join-Path $s $p
    if (Test-Path $path) { Remove-Item $path -Recurse -Force }
}
New-Item -ItemType Directory -Force -Path (Join-Path $s 'plugins') | Out-Null
Copy-Item $jar (Join-Path $s 'plugins') -Force

# config.yml from the jar, offline UUIDs (no Mojang lookups)
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [IO.Compression.ZipFile]::OpenRead($jar)
try {
    $reader = New-Object IO.StreamReader($zip.GetEntry('config.yml').Open(), [Text.Encoding]::UTF8)
    $config = $reader.ReadToEnd()
    $reader.Dispose()
} finally {
    $zip.Dispose()
}
$config = [regex]::Replace($config, '(?m)^online_mode: true', 'online_mode: false')
$sed = if ($env:LB_SED) { $env:LB_SED } else { 's/^$//' }
if ($sed -match '^s/(.*?)/(.*)/$') { $config = [regex]::Replace($config, '(?m)' + $matches[1], $matches[2]) }
New-Item -ItemType Directory -Force -Path (Join-Path $s 'plugins\LiteBans') | Out-Null
[IO.File]::WriteAllText((Join-Path $s 'plugins\LiteBans\config.yml'), $config, $utf8)

# 4. start the server; its output goes to server.log
$log = Join-Path $s 'server.log'
$writer = New-Object IO.StreamWriter($log, $false, $utf8)
$writer.AutoFlush = $true

$psi = New-Object Diagnostics.ProcessStartInfo
$psi.FileName = $java
$psi.Arguments = "-Xmx1G -jar $paperName nogui"
$psi.WorkingDirectory = $s
$psi.UseShellExecute = $false
$psi.CreateNoWindow = $true
$psi.RedirectStandardInput = $true
$psi.RedirectStandardOutput = $true
$psi.RedirectStandardError = $true
$proc = New-Object Diagnostics.Process
$proc.StartInfo = $psi
$proc.EnableRaisingEvents = $true

$id = [guid]::NewGuid().ToString('N')
$outId = "lb-out-$id"
$errId = "lb-err-$id"
Register-ObjectEvent -InputObject $proc -EventName OutputDataReceived -SourceIdentifier $outId -MessageData $writer -Action {
    if ($null -ne $EventArgs.Data) { $Event.MessageData.WriteLine($EventArgs.Data) }
} | Out-Null
Register-ObjectEvent -InputObject $proc -EventName ErrorDataReceived -SourceIdentifier $errId -MessageData $writer -Action {
    if ($null -ne $EventArgs.Data) { $Event.MessageData.WriteLine($EventArgs.Data) }
} | Out-Null

try {
    [void]$proc.Start()
    $proc.BeginOutputReadLine()
    $proc.BeginErrorReadLine()

    # wait until startup finishes or fails (up to 180 s)
    for ($i = 0; $i -lt 90; $i++) {
        Start-Sleep -Seconds 2
        if ($proc.HasExited) { break }
        if (Select-String -Path $log -Pattern 'Done \(|Error occurred while enabling' -Quiet) { break }
    }
    Start-Sleep -Seconds 3
    # an empty first line absorbs a stray character the console reads before the first real command
    $proc.StandardInput.WriteLine('')
    $proc.StandardInput.Flush()
    Start-Sleep -Seconds 2

    foreach ($c in ($Commands -split ';')) {
        $c = $c.Trim()
        if (-not $c) { continue }
        $proc.StandardInput.WriteLine($c)
        $proc.StandardInput.Flush()
        Start-Sleep -Seconds 3
    }

    Start-Sleep -Seconds 1
    $hits = @(Select-String -Path $log -Pattern 'LiteBans|ERROR|Exception|Caused by|Done \(')
    $errors = @(Select-String -Path $log -Pattern '\sERROR\]|SEVERE').Count
    Write-Output "server exited early: $($proc.HasExited)"
    Write-Output "ERROR/SEVERE lines: $errors"
    $hits | Select-Object -First $LinesMax | ForEach-Object {
        $line = $_.Line
        if ($line.Length -gt 230) { $line = $line.Substring(0, 230) }
        Write-Output ('{0}:{1}' -f $_.LineNumber, $line)
    }

    $proc.StandardInput.WriteLine('stop')
    $proc.StandardInput.Flush()
    if (-not $proc.WaitForExit(60000)) { Stop-Process -Id $proc.Id -Force }
    Start-Sleep -Seconds 2
} finally {
    if (-not $proc.HasExited) { Stop-Process -Id $proc.Id -Force }
    Unregister-Event -SourceIdentifier $outId -ErrorAction SilentlyContinue
    Unregister-Event -SourceIdentifier $errId -ErrorAction SilentlyContinue
    $writer.Dispose()
    $proc.Dispose()
}
