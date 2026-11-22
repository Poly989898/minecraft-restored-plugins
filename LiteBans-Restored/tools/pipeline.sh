#!/bin/bash
# Regenerates src/main/java from the original jar: clean flags -> decrypt string tables (static) -> split local-variable webs -> deobfuscate (auto + mappings/manual.map) -> decompile -> mechanical source fixes.
# Usage: tools/pipeline.sh   (needs Vineflower: VF env or the Gradle-cache copy)
set -e
cd "$(dirname "$0")/.."
# Tools live in D:\Tools (Git Bash paths); override with env vars if needed
export PATH="/d/Tools/jdk-25/bin:$PATH"
ASM=${ASM:-/d/Tools/asm}
PY=${PY:-/d/Tools/python/python.exe}
CP="$ASM/asm-9.11.1.jar:$ASM/asm-commons-9.11.1.jar:$ASM/asm-tree-9.11.1.jar:$ASM/asm-analysis-9.11.1.jar"
VF=${VF:-/d/Tools/vineflower/vineflower-1.12.0-slim.jar}
ORIG=${ORIG_JAR:-../originals/LiteBans/LiteBans-2.19.0-bm.jar}
mkdir -p .work
java -cp "$CP" tools/Cleaner.java "$ORIG" .work/cleaned.jar
java -cp "$CP" tools/Strings.java .work/cleaned.jar .work/strings.jar .work/strings.txt
java -cp "$CP" tools/Lambdas.java .work/strings.jar .work/lambdas.jar
java -cp "$CP" tools/Locals.java .work/lambdas.jar .work/locals.jar
java -cp "$CP" tools/Deobf.java .work/locals.jar .work/deobf.jar mappings/manual.map
rm -rf .work/dec; mkdir -p .work/dec
java -Xmx4g -jar "$VF" --silent -lac=1 .work/deobf.jar .work/dec/ >/dev/null 2>&1 || true
"$PY" -I tools/resugar.py .work/dec/litebans
"$PY" -I tools/postfix.py .work/dec .work/deobf.jar.bodies
rm -rf src/main/java/litebans; cp -r .work/dec/litebans src/main/java/
"$PY" -I tools/manual-fixes.py
[ -f .work/cp.txt ] && java tools/Fixer.java .work/cp.txt src/main/java stubs || echo "(Fixer failed or .work/cp.txt missing: run gradle printCp)"
echo "sources regenerated: $(find src/main/java -name '*.java' | wc -l) files"
