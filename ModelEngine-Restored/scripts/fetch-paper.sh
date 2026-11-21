#!/usr/bin/env bash
# usage: fetch-paper.sh <minecraft-version>
# Downloads the latest Paper build from the official PaperMC API, verifies its sha256,
# and unpacks the server libraries with -Dpaperclip.patchonly=true (the server is not started).
source "$(dirname "$0")/env.sh"; need curl; need java; need python3
V="${1:?usage: fetch-paper.sh <minecraft-version>}"
D="$(paper_dir "$V")"
if compgen -G "$D/versions/*/paper-*.jar" >/dev/null; then echo "Paper $V already prepared: $D"; exit 0; fi
mkdir -p "$D"; cd "$D"
read -r URL SHA NAME < <(curl -fsS "https://fill.papermc.io/v3/projects/paper/versions/$V/builds/latest" | python3 -I -c '
import sys,json; d=json.load(sys.stdin)["downloads"]["server:default"]
print(d["url"], d["checksums"]["sha256"], d["name"])')
curl -fsS -o "$NAME" "$URL"
echo "$SHA  $NAME" | sha256sum -c -
java -Dpaperclip.patchonly=true -jar "$NAME" >/dev/null 2>&1 || true
compgen -G "$D/versions/*/paper-*.jar" >/dev/null || { echo "Paper $V: library extraction failed" >&2; exit 1; }
echo "Paper $V ready: $D"
