#!/usr/bin/env bash
# Compiles tests/java against the rebuilt classes (run build.sh first) and runs them with JUnit 5.
# Tests only cover logic that needs no running server (math, colours, easing, codecs, data helpers).
source "$(dirname "$0")/env.sh"; need javac; need java
cd "$ROOT"; "$ROOT/scripts/fetch-test-deps.sh" >/dev/null
JUNIT="$(ls "$WORK"/test/junit-platform-console-standalone-*.jar)"
OUT="$BUILD/test"; rm -rf "$OUT"; mkdir -p "$OUT"
CP="$BUILD/ov/17:$BUILD/ov/21:$BUILD/ov/25:$(classpath_for "$MAIN_PAPER"):$JUNIT"
find tests/java -name '*.java' > "$BUILD/tests.txt"
javac -proc:none -nowarn -encoding UTF-8 -source 17 -target 17 -cp "$CP" -d "$OUT" @"$BUILD/tests.txt"
java -jar "$JUNIT" execute -cp "$OUT:$CP" --scan-classpath "$OUT" --disable-banner --details=summary "$@"
