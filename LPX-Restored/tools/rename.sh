#!/bin/bash
# Applies tools/mappings.txt (+ auto names for varN locals) to src/ with the symbol-aware tools/Rename.java.
# usage: tools/rename.sh [--dry]   (run tools/build.sh once first: it creates build/lpx-public.jar and build/stubs-out)
set -e
P="$(cd "$(dirname "$0")/.." && pwd)"; cd "$P"
API=$(find ~/.gradle -name 'paper-api-1.21.11*.jar' | head -1)
LIBS=$(find "$P/../test-server/libraries" -name '*.jar' | tr '\n' ':')
CP="build/lpx-public.jar:build/stubs-out:$API:$LIBS"
java tools/Rename.java tools/mappings.txt "$CP" src --locals "$@"
