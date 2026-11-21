#!/bin/bash
# Builds + packages LPX and starts a throw-away Paper 26.3 server (scratch copy, port 25603, only LPX) until startup finishes.
# usage: [JAR=/path/to/other.jar] tools/testserver.sh [console commands separated by ;]   (JAR=original jar gives a baseline run)
cd "$(dirname "$0")/.."
P=$(pwd)
if [ -z "$JAR" ]; then tools/build.sh >/dev/null 2>&1; tools/package.sh >/dev/null; JAR="$P/build/libs/LPX-3.9.1-restored.jar"; fi
S=${TMPDIR:-/tmp}/lpxtest; T="$P/../test-server"
rm -rf $S; mkdir -p $S/plugins && cd $S
ln -s $T/libraries libraries; ln -s $T/versions versions; ln -s $T/cache cache; cp $T/paper-26.3-253.jar $T/eula.txt .
printf 'online-mode=false\nserver-port=25603\nview-distance=4\nlevel-type=flat\ngenerate-structures=false\n' > server.properties
cp "$JAR" plugins/
mkfifo cmd.fifo; (tail -f cmd.fifo | java -Xmx1G -jar paper-26.3-253.jar nogui > server.log 2>&1 &)
for i in $(seq 1 90); do sleep 2; grep -qE 'Done \(|Error occurred while enabling' server.log && break; done
sleep 4
IFS=";" read -ra CMDS <<< "${1:-lpx}"; for c in "${CMDS[@]}"; do echo "$c" > cmd.fifo; sleep 6; done
echo stop > cmd.fifo; sleep 6
echo "exceptions/errors: $(grep -ciE 'exception|caused by|NoClassDef|VerifyError|LinkageError|AbstractMethodError' server.log)"
sed 's/\x1b\[[0-9;]*m//g' server.log | grep -E 'LPX|Done \(|Exception|Caused by|VerifyError|Error' | cut -c1-200 | head -30
