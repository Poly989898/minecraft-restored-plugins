#!/usr/bin/env python3 -I
"""Renames obfuscated check implementations `CheckResult xx(Object var1, T var2)` back to `check(ProtocolPlayer var1, T var2)`
in classes extending PlayerCheck, and updates same-class calls. Usage: python3 -I tools/restore_check.py src"""
import re, sys, pathlib
n = 0
for p in pathlib.Path(sys.argv[1], 'it/ytnoos/lpx/check/checks').rglob('*.java'):
    s = p.read_text(encoding='utf-8')
    m = re.search(r'^(\s+)(public|protected) CheckResult (\w+)\(Object var1, ([\w.<>]+) var2\) \{', s, re.M)
    if not m or 'extends PlayerCheck<' not in s: continue
    name = m.group(3)
    t = s.replace(m.group(0), f'{m.group(1)}public CheckResult check(ProtocolPlayer var1, {m.group(4)} var2) {{')
    t = re.sub(r'\bthis\.%s\(' % re.escape(name), 'this.check(', t)
    if 'import it.ytnoos.lpx.api.player.ProtocolPlayer;' not in t:
        t = re.sub(r'(package [\w.]+;\n)', r'\1\nimport it.ytnoos.lpx.api.player.ProtocolPlayer;', t, count=1)
    p.write_text(t, encoding='utf-8'); n += 1
print('renamed', n)
