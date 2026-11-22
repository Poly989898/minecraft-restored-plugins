#!/bin/bash
. "${TOOLS_ENV:-/d/Tools/env.sh}"
set -e
cd "$(dirname "$0")/.."
java --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED tools/InlineTool.java .work/cp.txt src/main/java stubs
python3 tools/reindent.py >/dev/null
G=$GRADLE_BIN
if timeout 900 $G compileJava -q 2>&1 | grep -E ' error' | head -8 | grep .; then echo "COMPILE FAILED"; exit 2; fi
git add -A >/dev/null; git commit -q -m "LiteBans: inline lambda helpers $1"; echo "ok $1"
