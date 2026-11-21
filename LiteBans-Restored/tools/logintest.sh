#!/bin/bash
# Player pre-login test: fires AsyncPlayerPreLoginEvent through a throw-away probe plugin (/tmp/claude-1000/lbprobe/probe.jar). usage: tools/logintest.sh
cd "$(dirname "$0")/.."
G=$(ls -d ~/.gradle/wrapper/dists/gradle-9.9.1-bin/*/gradle-9.9.1/bin/gradle)
$G jar -q 2>&1 | grep -E 'error' | head -3
S=/tmp/claude-1000/lbtest; cd $S; rm -rf world plugins/LiteBans logs server.log cmd.fifo; cp -f "$OLDPWD/build/libs/LiteBans-Restored.jar" /tmp/claude-1000/lbprobe/probe.jar plugins/
mkdir -p plugins/LiteBans && unzip -p "$OLDPWD/build/libs/LiteBans-Restored.jar" config.yml | sed "s/^online_mode: true/online_mode: false/" | sed -e "${LB_SED:-s/^$//}" > plugins/LiteBans/config.yml
mkfifo cmd.fifo; (tail -f cmd.fifo | java -Xmx1G -jar paper-26.3-253.jar nogui > server.log 2>&1 &)
for i in $(seq 1 90); do sleep 2; grep -qE 'Done \(|Error occurred while enabling' server.log && break; done; sleep 2
probe() { echo "lbprobe $1" > cmd.fifo; sleep 4; }
echo "--- 1 normal join"; probe Alice
echo "ban Bob griefing" > cmd.fifo; sleep 3
echo "--- 2 banned join (expect denied)"; probe Bob
echo "ipban Carol 127.0.0.1 x" > cmd.fifo; sleep 1
echo "unban Bob" > cmd.fifo; sleep 3
echo "--- 3 unbanned join (expect allowed)"; probe Bob
echo "tempban Dave 1h flood" > cmd.fifo; sleep 3
echo "--- 4 tempbanned join (expect denied)"; probe Dave
echo stop > cmd.fifo; sleep 6
grep -nE 'Exception|AssertionError|Caused by' server.log | head -5
grep -nE 'PROBE|banned|Alice|Bob' server.log | grep -vE 'UUID of player|Initializing' | cut -c1-200 | head -30
