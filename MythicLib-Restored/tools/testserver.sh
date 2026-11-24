#!/bin/bash
# Starts a throw-away Paper 26.3 server (scratch copy, port 25604) with the built MythicLib jar and runs console commands.
# usage: [EXTRA="a.jar b.jar"] tools/testserver.sh [console commands separated by ;]
# needs a Paper server directory in $SERVER_DIR (default ../test-server) with paper-26.3-*.jar, eula.txt, libraries/, versions/
cd "$(dirname "$0")/.."
P=$(pwd)
T=${SERVER_DIR:-$P/../test-server}
JAR=${JAR:-$P/build/libs/MythicLib-1.7-restored.jar}
S=${TMPDIR:-/tmp}/mlibtest; rm -rf $S; mkdir -p $S/plugins && cd $S
ln -s $T/libraries libraries; ln -s $T/versions versions; ln -s $T/cache cache; cp $T/paper-26.3-253.jar $T/eula.txt .
printf 'online-mode=false\nserver-port=%s\nview-distance=4\nlevel-type=flat\ngenerate-structures=false\n' "${PORT:-25604}" > server.properties
cp "$JAR" plugins/; for j in $EXTRA; do cp "$j" plugins/; done
mkfifo cmd.fifo; (tail -f cmd.fifo | java -Xmx1G -jar paper-26.3-253.jar nogui > server.log 2>&1 &)
for i in $(seq 1 90); do sleep 2; grep -qE 'Done \(|Error occurred while enabling' server.log && break; done
sleep 4
IFS=";" read -ra CMDS <<< "${1:-ml}"; for c in "${CMDS[@]}"; do echo "$c" > cmd.fifo; sleep 5; done
echo stop > cmd.fifo; sleep 6
echo "exceptions/errors: $(grep -ciE 'exception|caused by|NoClassDef|VerifyError|LinkageError|AbstractMethodError' server.log)"
sed 's/\x1b\[[0-9;]*m//g' server.log | grep -E 'MythicLib|Done \(|Exception|Caused by|VerifyError|Error' | cut -c1-220 | head -40
