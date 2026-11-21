#!/usr/bin/env python3
"""Reject mapping names that clash with imported/wildcard-visible library classes (a same-named import silently wins over a same-package class)."""
import re,glob,sys
new=[l.split()[2] for l in open(sys.argv[1]) if l.startswith('C ')]
imports={}
for f in glob.glob('src/main/java/litebans/*.java')+glob.glob('stubs/**/*.java',recursive=True)+glob.glob('src/main/java/litebans/mixin/*.java'):
    for m in re.finditer(r'^import (?:static )?([\w.]+)\.(\w+);',open(f,encoding='utf-8').read(),re.M): imports.setdefault(m.group(2),set()).add(m.group(1)+'.'+m.group(2))
java_lang={'Object','String','Class','Module','Record','Process','Thread','Runtime','System','Math','Number','Enum','Error','Exception','Override','Void','Iterable','Comparable','Record','Package'}
bad=[n for n in new if n in imports or n in java_lang]
for n in bad: print('CLASH',n,sorted(imports.get(n,[]))[:3])
print('clashes:',len(bad))
