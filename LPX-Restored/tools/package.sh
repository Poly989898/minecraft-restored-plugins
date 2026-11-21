#!/bin/bash
# build/libs/LPX-3.9.1-restored.jar = original jar + classes recompiled from src/. Run tools/build.sh first.
set -e
P="$(cd "$(dirname "$0")/.." && pwd)"; cd "$P"
ORIG="${ORIG:-$P/../originals/LPX/LPX-3.9.1.jar}"
W=build/pkg; OUT=$P/build/libs/LPX-3.9.1-restored.jar
rm -rf $W; mkdir -p $W build/libs; (cd $W && unzip -q "$ORIG")
# drop original classes that were renamed/removed in src/ (stale obfuscated names), except the synthetic switch-map holders listed in keep-original.txt
(cd $W && find it/ytnoos -name '*.class' | sed 's/\.class$//' | sort) > build/orig-classes.txt
(cd build/classes && find . -name '*.class' | sed 's#^\./##; s/\.class$//' | sort) > build/new-classes.txt
comm -23 build/orig-classes.txt build/new-classes.txt | grep -vxFf tools/keep-original.txt | while read -r c; do rm -f "$W/$c.class"; done
cp -r build/classes/. $W/
ASM=$(find ~/.gradle/caches/modules-2/files-2.1/org.ow2.asm -name 'asm-9*.jar' ! -name '*sources*' | head -1)
ASMC=$(find ~/.gradle/caches/modules-2/files-2.1/org.ow2.asm -name 'asm-commons-9*.jar' ! -name '*sources*' | head -1)
java -cp "$ASM:$ASMC" tools/RemapClasses.java tools/mappings.txt $W
java -cp "$ASM:$ASMC" tools/RelocateKyori.java $W
rm -f "$OUT"; (cd $W && zip -q -r -X "$OUT" META-INF/MANIFEST.MF && zip -q -r -X "$OUT" . -x META-INF/MANIFEST.MF)
echo "built $OUT"
