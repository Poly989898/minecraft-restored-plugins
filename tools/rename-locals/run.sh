#!/usr/bin/env bash
# Usage: run.sh <plugin-dir> [--dry-run] [--verify] [--map file] [--pattern regex] [--src dir]
# Renames decompiler-style locals (var1, var2, ...) in <plugin-dir>/src/main/java.
# The compile classpath is taken from the plugin's Gradle build (GRADLE env var = gradle binary).
set -euo pipefail
here="$(cd "$(dirname "$0")" && pwd)"
plugin="$(cd "$1" && pwd)"; shift
GRADLE="${GRADLE:-gradle}"
work="${TMPDIR:-/tmp}/rename-locals.$$"; mkdir -p "$work"; trap 'rm -rf "$work"' EXIT
src="$plugin/src/main/java"; verify=0; extra=()
while [ $# -gt 0 ]; do case "$1" in
  --verify) verify=1;;
  --src) src="$2"; shift;;
  *) extra+=("$1"); case "$1" in --map|--pattern) extra+=("$2"); shift;; esac;;
esac; shift; done

cat > "$work/init.gradle" <<'G'
allprojects { afterEvaluate { p ->
  if (p.plugins.hasPlugin('java')) p.tasks.register('printRenameCp') { doLast {
    println('RENAME_CP=' + p.sourceSets.main.compileClasspath.asPath) } } } }
G
cp_line="$("$GRADLE" --offline -q -p "$plugin" -I "$work/init.gradle" printRenameCp | grep '^RENAME_CP=' | tail -1)"
cp="${cp_line#RENAME_CP=}"

javac_all() { # <outdir>
  mkdir -p "$1"; find "$src" -name '*.java' > "$work/files.txt"
  javac -g:none -proc:none -nowarn -Xlint:none -encoding UTF-8 -cp "$cp" -d "$1" @"$work/files.txt" 2>&1 | grep -v '^Note:' || true
}
run_tool() {
  java --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED "$here/RenameLocals.java" \
    --src "$src" --classpath "$cp" "$@"
}

if [ $verify = 1 ]; then
  javac_all "$work/before"
  run_tool "${extra[@]}"
  javac_all "$work/after"
  nb=$(find "$work/before" -name '*.class' | wc -l); bad=0
  while IFS= read -r f; do
    r="${f#$work/before/}"
    cmp -s "$f" "$work/after/$r" || { echo "DIFF: $r"; bad=$((bad+1)); }
  done < <(find "$work/before" -name '*.class')
  na=$(find "$work/after" -name '*.class' | wc -l)
  echo "verify: classes before=$nb after=$na differing=$bad"
  [ "$bad" = 0 ] && [ "$nb" = "$na" ] || exit 1
else
  run_tool "${extra[@]}"
fi
