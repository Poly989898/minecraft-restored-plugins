#!/bin/bash
# Builds build/libs/LPX-3.9.1-restored.jar from src/, stubs/ (compile-only), src/resources and Maven dependencies.
# usage: tools/build.sh
set -e
. "${TOOLS_ENV:-/d/Tools/env.sh}"
P="$(cd "$(dirname "$0")/.." && pwd)"; cd "$P"
"$GRADLE_BIN" build --console=plain
