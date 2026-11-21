#!/bin/bash
# Compiles src/ + stubs/ against the access-flag-repaired original jar, Paper API 1.21.11 and the libraries of the shared test server.
# Result: build/classes (without the stubs), build/lpx-public.jar. usage: tools/build.sh
set -e
P="$(cd "$(dirname "$0")/.." && pwd)"; cd "$P"
ORIG="${ORIG:-$P/../originals/LPX/LPX-3.9.1.jar}"
ASM=$(find ~/.gradle/caches/modules-2/files-2.1/org.ow2.asm -name 'asm-9*.jar' ! -name '*sources*' | head -1)
API=$(find ~/.gradle -name 'paper-api-1.21.11*.jar' | head -1)
LIBS=$(find "$P/../test-server/libraries" -name '*.jar' | tr '\n' ':')
mkdir -p build/classes build/stubs-out
java -cp "$ASM" tools/MakePublic.java "$ORIG" build/lpx-public.jar
rm -rf build/classes build/stubs-out; mkdir -p build/classes build/stubs-out
SRC=$(find src -name '*.java' | grep -vFf excludes.txt || find src -name '*.java')
javac -proc:none -nowarn -encoding UTF-8 -cp "build/lpx-public.jar:$API:$LIBS" -d build/classes $SRC 2>&1 | grep -v '^Note:' || true
# stubs are compile-only: compile them separately so they never land in build/classes
javac -proc:none -nowarn -encoding UTF-8 -cp "$API:$LIBS" -d build/stubs-out $(find stubs -name '*.java')
CP2="build/lpx-public.jar:build/stubs-out:$API:$LIBS"
javac -proc:none -nowarn -encoding UTF-8 -cp "$CP2" -d build/classes $SRC 2>&1 | grep -v '^Note:' || true
echo "classes: $(find build/classes -name '*.class' | wc -l)"
