#!/usr/bin/env bash
# Downloads compile-time dependencies into .work/deps and copies the original jar there.
source "$(dirname "$0")/env.sh"; need curl; need python3
[ -f "$ORIG_JAR" ] || { echo "Original jar not found: $ORIG_JAR (set ORIG_JAR=...)" >&2; exit 1; }
mkdir -p "$DEPS"; cd "$DEPS"; cp "$ORIG_JAR" orig-UME.jar
get() { [ -f "$(basename "$1")" ] || curl -fsS -O "$1"; }
MC=https://repo1.maven.org/maven2
get $MC/org/projectlombok/lombok/1.18.42/lombok-1.18.42.jar
get $MC/org/jetbrains/annotations/24.1.0/annotations-24.1.0.jar
get $MC/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar
get $MC/net/kyori/adventure-text-serializer-bungeecord/4.3.0/adventure-text-serializer-bungeecord-4.3.0.jar
get https://repo.viaversion.com/com/viaversion/viaversion-api/5.5.1/viaversion-api-5.5.1.jar   # optional integration (compile only)
get https://mvn.lumine.io/repository/maven-public/io/lumine/Mythic-Dist/5.14.0/Mythic-Dist-5.14.0.jar   # MythicMobs API
get https://repo.md-5.net/content/repositories/releases/LibsDisguises/LibsDisguises/10.0.44/LibsDisguises-10.0.44.jar
CIT=https://maven.citizensnpcs.co/repo/net/citizensnpcs/citizens-main/2.0.44-SNAPSHOT
CJ=$(curl -fsS "$CIT/" | grep -oP 'citizens-main-2\.0\.44-[0-9.]+-[0-9]+\.jar' | sort -u | tail -1)
get "$CIT/$CJ"
ls -1 "$DEPS"
