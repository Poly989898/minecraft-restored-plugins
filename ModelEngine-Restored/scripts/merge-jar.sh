#!/usr/bin/env bash
# Builds dist/UltimateModelEngine-fixed.jar = original jar + every recompiled class.
source "$(dirname "$0")/env.sh"; need unzip; need jar
cd "$ROOT"; M="$BUILD/merge"; rm -rf "$M"; mkdir -p "$M" "$DIST"
(cd "$M" && unzip -q "$ORIG_JAR")
# project resources win over the original jar (as in merge-jar.ps1)
[ -d "$ROOT/resources" ] && cp -r "$ROOT"/resources/. "$M"/
for d in "$BUILD"/ov/17 "$BUILD"/ov/21 "$BUILD"/ov/25 "$BUILD"/ov-v*; do [ -d "$d" ] && cp -r "$d"/. "$M"/; done
OUT="$DIST/UltimateModelEngine-fixed.jar"; rm -f "$OUT"
(cd "$M" && jar --create --file "$OUT" --manifest META-INF/MANIFEST.MF .)
sha256sum "$OUT"
