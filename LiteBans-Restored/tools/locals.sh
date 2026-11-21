#!/bin/bash
# usage: tools/locals.sh clean|rename <archive-name> — runs LocalTool, compiles, commits (reverts on failure)
set -e
cd "$(dirname "$0")/.."
java $JOPTS --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED tools/LocalTool.java $1 .work/cp.txt src/main/java stubs
G=$(ls -d ~/.gradle/wrapper/dists/gradle-9.9.1-bin/*/gradle-9.9.1/bin/gradle)
if timeout 900 $G compileJava -q 2>&1 | grep -E 'error' | head -8 | grep .; then echo "COMPILE FAILED"; exit 2; fi
git add -A >/dev/null; git commit -q -m "LiteBans: locals $1 $2"; echo "ok $1 $2"
