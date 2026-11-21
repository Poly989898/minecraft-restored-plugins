#!/usr/bin/env bash
# Shared settings. Source this file; do not run it.
# Override any variable from the environment.
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WORK="${WORK:-$ROOT/.work}"            # downloaded Paper builds and dependency jars
BUILD="${BUILD:-$ROOT/build}"          # compiled overlay classes
DIST="${DIST:-$ROOT/dist}"             # final jar
DEPS="$WORK/deps"
LOMBOK="$DEPS/lombok-1.18.42.jar"   # annotation processor: the recovered sources use Lombok annotations again
# The original (binary) plugin jar. It supplies the shaded libraries (jsoup, mineskin),
# resources, and every class not rebuilt from source. You must provide it yourself.
ORIG_JAR="${ORIG_JAR:-$ROOT/../originals/ModelEngine/UltimateModelEngine.jar}"
MAIN_PAPER="${MAIN_PAPER:-26.3}"       # Paper version for api/core/core21/core25/v26_3

# module -> "paper-version java-target"
declare -A ADAPTERS=(
  [v1_19_R3]="1.19.4 17"  [v1_20_R1]="1.20.1 17"  [v1_20_R2]="1.20.2 17"  [v1_20_R3]="1.20.4 17"
  [v1_20_R4]="1.20.6 21"  [v1_21_R1]="1.21.1 21"  [v1_21_R2]="1.21.3 21"  [v1_21_R3]="1.21.4 21"
  [v1_21_R4]="1.21.5 21"  [v1_21_R5]="1.21.8 21"  [v1_21_R6]="1.21.10 21" [v1_21_R7]="1.21.11 21"
  [v26_1]="26.1.2 25"     [v26_2]="26.2 25"
)

need() { command -v "$1" >/dev/null || { echo "missing tool: $1" >&2; exit 1; }; }
paper_dir() { echo "$WORK/paper/$1"; }
classpath_for() {  # $1 = paper version
  { find "$(paper_dir "$1")/libraries" "$(paper_dir "$1")/versions" "$DEPS" -name '*.jar' 2>/dev/null \
      | grep -v '/orig-UME.jar$' | sort; echo "$DEPS/orig-UME.jar"; } | paste -sd: -
}
