#!/bin/bash
. "${TOOLS_ENV:-/d/Tools/env.sh}"
# Applies mappings/names.map: checks name clashes, renames, compiles, archives the map (usage: tools/rename.sh <archive-name>)
set -e
cd "$(dirname "$0")/.."
python3 tools/checknames.py mappings/names.map | grep -q 'clashes: 0' || { python3 tools/checknames.py mappings/names.map; exit 1; }
java --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED tools/Renamer.java .work/cp.txt src/main/java stubs mappings/names.map
G=$GRADLE_BIN
if timeout 900 $G compileJava -q 2>&1 | grep -E 'error' | head -8 | grep .; then echo "COMPILE FAILED"; exit 2; fi
cp mappings/names.map "mappings/applied/$1.map"
printf '# pending renames (Renamer applies this file; applied ones are archived in mappings/applied/)\n' > mappings/names.map
git add -A >/dev/null; git commit -q -m "LiteBans: naming $1"; echo "ok $1"
