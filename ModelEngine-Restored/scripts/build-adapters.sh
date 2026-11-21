#!/usr/bin/env bash
# usage: build-adapters.sh [module ...]   (default: all NMS adapters except v26_3)
# Each adapter is compiled against its own Paper version, downloaded on demand.
source "$(dirname "$0")/env.sh"; need javac
cd "$ROOT"
mods=("$@"); [ ${#mods[@]} -gt 0 ] || mods=("${!ADAPTERS[@]}")
fail=0
for m in $(printf '%s\n' "${mods[@]}" | sort); do
  read -r ver tgt <<< "${ADAPTERS[$m]:?unknown module $m}"
  "$ROOT/scripts/fetch-paper.sh" "$ver" >/dev/null
  out="$BUILD/ov-$m"; rm -rf "$out"; mkdir -p "$out"
  find "src/com/ticxo/modelengine/$m" -name '*.java' > "$BUILD/$m.txt"
  if javac -processorpath "$LOMBOK" -nowarn -encoding UTF-8 -source "$tgt" -target "$tgt" -cp "$(classpath_for "$ver")" -d "$out" @"$BUILD/$m.txt" > "$BUILD/$m.log" 2>&1
  then echo "$m (Paper $ver, java $tgt): $(wc -l < "$BUILD/$m.txt") files OK"
  else echo "$m: FAILED, see $BUILD/$m.log" >&2; fail=1; fi
done
exit $fail
