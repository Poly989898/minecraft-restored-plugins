#!/usr/bin/env python3
"""Hand-made repairs of decompiler defects that no generic tool handles. Idempotent; applied after pipeline.sh / the decompiler choice.
Each fix is keyed by (file, old text) -> new text, so it silently skips when the file differs (e.g. after renames)."""
import re, glob, os, sys
root = sys.argv[1] if len(sys.argv) > 1 else 'src/main/java/litebans'
def sub(f, old, new, regex=False, count=0):
    p = f'{root}/{f}.java'
    if not os.path.exists(p): print('no file', f); return
    s = open(p, encoding='utf-8').read()
    n = re.sub(old, new, s, count=count, flags=re.S) if regex else s.replace(old, new)
    if n == s: print('no change', f, old[:50].replace('\n', ' '))
    else: open(p, 'w', encoding='utf-8').write(n)

# d8: no concrete command implements suggest(); the obfuscated jar would throw AbstractMethodError, give it a safe default
sub('d8', '   @NotNull List suggest(@NotNull jV var1, @NotNull String[] var2);', '   default @NotNull List suggest(@NotNull jV var1, @NotNull String[] var2) {\n      return java.util.Collections.emptyList();\n   }')
# enum EnumEntries holder: `$VALUES` was hidden, iC.a(entries)
for p in glob.glob(f'{root}/*.java'):
    s = open(p, encoding='utf-8').read(); n = re.sub(r'iC\.a\(\w+_f\)', 'iC.a(values())', s)
    if n != s: open(p, 'w', encoding='utf-8').write(n)
# fields assigned through a `this` alias in constructors
sub('q', '   private final byte s_f;', '   private byte s_f;')
sub('eG', '   private final List aP_f;', '   private List aP_f;'); sub('eG', '   private final boolean aL_f;', '   private boolean aL_f;')
sub('u', '   private final byte[][] g_f;', '   private byte[][] g_f;')
# dk: constants with arguments were not inlined by the decompiler
p = f'{root}/dk.java'
if os.path.exists(p):
    s = open(p, encoding='utf-8').read()
    m = re.search(r'   static \{\n(.*?)\n   \}\n\}', s, re.S)
    if m and 'Failed to inline' in s:
        vals = re.findall(r'new String\[\]\{([^}]*)\};\n\s+(\w+) = new dk', m.group(1))
        for a, n in vals: s = re.sub(r'^   %s([,;])$' % n, '   %s(new String[]{%s})\\1' % (n, a), s, flags=re.M)
        s = s.replace(m.group(0), '}')
        s = re.sub(r'   // \$VF: Failed to inline enum fields\n   // Please report[^\n]*\n', '', s)
        open(p, 'w', encoding='utf-8').write(s)
