#!/bin/bash
# Builds build/libs/nexo-1.26-custom.jar = original jar + classes recompiled from src/ (names fixed back to Kotlin's hyphenated form).
# Usage: ./tools/package.sh      (run `gradle compileJava` first)
set -e
P="$(cd "$(dirname "$0")/.." && pwd)"
ORIG="$P/libs/nexo-1.26-patched.jar"
OUT="$P/build/libs/nexo-1.26-custom.jar"
W="$P/build/pkg"; rm -rf "$W"; mkdir -p "$W/orig" "$W/new" "$P/build/libs"
(cd "$W/orig" && unzip -q "$ORIG")
cp -r "$P/build/classes/java/main/." "$W/new/"
rm -rf "$W/new/kotlin"            # compile-time Duration stub, must not be packaged
# compile-only stubs of third-party APIs (see stubs/) must not be packaged either
# optional-dependency integrations: keep original bytecode (javac-built versions fail class verification when the plugin is absent)
rm -rf "$W/new/com/nexomc/nexo/compatibilities" "$W/new/com/nexomc/nexo/mechanics/furniture/compatibility"
rm -rf "$W/new/nl" "$W/new/gs" "$W/new/ai" "$W/new/me" "$W/new/com/moulberry"
python3 "$P/tools/fixnames.py" "$W/orig" "$W/new"
cp -r "$W/new/." "$W/orig/"
rm -f "$OUT"
(cd "$W/orig" && zip -q -r -X "$OUT" META-INF/MANIFEST.MF && zip -q -r -X "$OUT" . -x META-INF/MANIFEST.MF)
echo "built $OUT"
