#!/bin/bash
. "${TOOLS_ENV:-/d/Tools/env.sh}"
# Builds the jar and starts a throw-away Paper server (scratchpad copy, port 25601, only LiteBans) until startup finishes or fails.
# usage: tools/testserver.sh [extra console command to send after start]
cd "$(dirname "$0")/.."
G=$GRADLE_BIN
$G jar -q 2>&1 | grep -E 'error' | head -3
S=$TMPDIR/claude-1000/lbtest
T="$OLDPWD/../test-server"   # shared Paper test server (outside git): only its libraries/versions/cache and the Paper jar are reused
if [ ! -d $S ]; then
  mkdir -p $S/plugins && cd $S && ln -s $T/libraries libraries && ln -s $T/versions versions && ln -s $T/cache cache && cp $T/paper-26.3-253.jar $T/eula.txt . \
    && printf 'online-mode=false\nserver-port=25601\nview-distance=4\nlevel-type=flat\ngenerate-structures=false\n' > server.properties && cd - >/dev/null
fi
cd $S; rm -rf world plugins/LiteBans logs server.log cmd.fifo; cp -f "$OLDPWD/build/libs/LiteBans-Restored.jar" plugins/
mkdir -p plugins/LiteBans && unzip -p "$OLDPWD/build/libs/LiteBans-Restored.jar" config.yml | sed "s/^online_mode: true/online_mode: false/" | sed -e "${LB_SED:-s/^$//}" > plugins/LiteBans/config.yml   # offline UUIDs: no Mojang lookups (rate limits)
mkfifo cmd.fifo; (tail -f cmd.fifo | java -Xmx1G -jar paper-26.3-253.jar nogui > server.log 2>&1 &)
for i in $(seq 1 90); do sleep 2; grep -qE 'Done \(|Error occurred while enabling' server.log && break; done
sleep 3
if [ -n "$1" ]; then IFS=";" read -ra CMDS <<< "$1"; for c in "${CMDS[@]}"; do echo "$c" > cmd.fifo; sleep 3; done; fi
grep -nE 'LiteBans|ERROR|Exception|Caused by|Done \(' server.log | head -${LINES_MAX:-40} | cut -c1-230
echo stop > cmd.fifo
