#!/bin/bash
# Builds the jar (compileJava + package.sh) and starts a throw-away Paper 26.3 server (scratch copy, port 25602, only Nexo) until startup finishes.
# usage: tools/testserver.sh [console commands separated by ;]   (the shared ../test-server is only used for its Paper jar and libraries)
cd "$(dirname "$0")/.."
P=$(pwd)
G=$(ls -d ~/.gradle/wrapper/dists/gradle-*-bin/*/gradle-*/bin/gradle | tail -1)
$G --offline compileJava -q 2>&1 | grep -E 'error:' | head -5
tools/package.sh >/dev/null
S=${TMPDIR:-/tmp}/nexotest; T="$P/../test-server"
rm -rf $S; mkdir -p $S/plugins && cd $S
ln -s $T/libraries libraries; ln -s $T/versions versions; ln -s $T/cache cache; cp $T/paper-26.3-253.jar $T/eula.txt .
printf 'online-mode=false\nserver-port=25602\nview-distance=4\nlevel-type=flat\ngenerate-structures=false\n' > server.properties
cp $P/build/libs/nexo-1.26-custom.jar plugins/
mkfifo cmd.fifo; (tail -f cmd.fifo | java -Xmx1G -jar paper-26.3-253.jar nogui > server.log 2>&1 &)
for i in $(seq 1 90); do sleep 2; grep -qE 'Done \(|Error occurred while enabling' server.log && break; done
sleep 4
IFS=";" read -ra CMDS <<< "${1:-nexo reload all}"; for c in "${CMDS[@]}"; do echo "$c" > cmd.fifo; sleep 8; done
echo stop > cmd.fifo; sleep 6
echo "exceptions/errors: $(grep -ciE 'exception|caused by|NoClassDef|VerifyError|LinkageError' server.log)"
sed 's/\x1b\[[0-9;]*m//g' server.log | grep -E 'Nexo \| (ResourcePack|Successfully)|Done \(|Exception|Caused by|VerifyError' | cut -c1-200 | head -20
