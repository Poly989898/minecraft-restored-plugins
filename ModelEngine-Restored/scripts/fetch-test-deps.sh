#!/usr/bin/env bash
# Downloads the JUnit 5 console launcher (Maven Central) into .work/test and verifies its sha1.
source "$(dirname "$0")/env.sh"; need curl; need sha1sum
V=1.11.4
JAR="junit-platform-console-standalone-$V.jar"
D="$WORK/test"; mkdir -p "$D"
[ -f "$D/$JAR" ] && { echo "JUnit $V already present"; exit 0; }
U="https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$V/$JAR"
curl -fsS -o "$D/$JAR" "$U"
echo "$(curl -fsS "$U.sha1")  $D/$JAR" | sha1sum -c -
