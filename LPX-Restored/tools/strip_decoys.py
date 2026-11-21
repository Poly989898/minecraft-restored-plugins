#!/usr/bin/env python3 -I
"""Removes the obfuscator's identity decoys `private static X a(X var0) { return var0; }` (X = package-private helper type) and their imports.
Usage: python3 -I tools/strip_decoys.py src"""
import re, sys, pathlib
pat = re.compile(r'\n[ \t]*private static (\w+) \w+\(\1 var0\) \{\s*return var0;\s*\}\n')
n = 0
for p in pathlib.Path(sys.argv[1]).rglob('*.java'):
    s = p.read_text(encoding='utf-8'); decoy_types = set(m.group(1) for m in pat.finditer(s))
    t = pat.sub('\n', s)
    for ty in decoy_types:
        if not re.search(r'\b%s\b' % ty, re.sub(r'^import .*$', '', t, flags=re.M)):
            t = re.sub(r'^import it\.ytnoos\.lpx\.%s;\n' % ty, '', t, flags=re.M)
    if t != s: p.write_text(t, encoding='utf-8'); n += 1
print('files changed', n)
