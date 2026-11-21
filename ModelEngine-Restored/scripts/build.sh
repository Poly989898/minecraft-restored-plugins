#!/usr/bin/env bash
# Compiles api, core, core21, core25 and v26_3 (plus the two patches) into build/ov/{17,21,25}.
# Targets match the original jar: api/core = Java 17, core21 = Java 21, core25/v26_3 = Java 25.
source "$(dirname "$0")/env.sh"; need javac; need python3
cd "$ROOT"; CP="$(classpath_for "$MAIN_PAPER")"
rm -rf "$BUILD/ov"; mkdir -p "$BUILD/ov/17" "$BUILD/ov/21" "$BUILD/ov/25"
group() {  # <java-target> <module regex>
  find src patches/java -name '*.java' | grep -E "$2" | python3 -I -c '
import sys
l=[x.strip() for x in sys.stdin]
p={x.split("patches/java/")[1] for x in l if x.startswith("patches/java/")}
# a patch replaces the recovered file with the same package path
print("\n".join(x for x in l if x.startswith("patches/java/") or not any(x.endswith("src/"+k) for k in p)))' > "$BUILD/group$1.txt"
  javac -processorpath "$LOMBOK" -nowarn -encoding UTF-8 -source "$1" -target "$1" -cp "$CP" -d "$BUILD/ov/$1" @"$BUILD/group$1.txt" > "$BUILD/group$1.log" 2>&1 \
    || { echo "FAILED (java $1), see $BUILD/group$1.log" >&2; head -30 "$BUILD/group$1.log" >&2; exit 1; }
  echo "java $1: $(wc -l < "$BUILD/group$1.txt") files OK"
}
group 17 'modelengine/(api|core)/'
group 21 'modelengine/core21/'
group 25 'modelengine/(core25|v26_3)/'
