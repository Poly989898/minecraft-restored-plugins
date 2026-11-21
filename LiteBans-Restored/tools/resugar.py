#!/usr/bin/env python3
"""Fix 'Unable to resugar constructor' spots left by Vineflower:  `T v = new T;` ... `v.<init>(args);`
-> `T v;` ... `v = new T(args);`  (the code between only computes the arguments)."""
import re, sys, glob
mark = re.compile(r'(\w+)\./\* \$VF: Unable to resugar constructor \*/<init>\(')
total = 0
for f in glob.glob(sys.argv[1] + '/*.java'):
    s = open(f, encoding='utf-8').read()
    if 'Unable to resugar' not in s: continue
    while True:
        m = mark.search(s)
        if not m: break
        v = m.group(1)
        head = s[:m.start()]
        # nearest preceding `new T;` assignment for this variable
        decl = None
        for d in re.finditer(r'^([ \t]*)(?:([\w<>\[\], .?]+) )?' + re.escape(v) + r' = new ([\w.$<>]+);\n', head, re.M):
            decl = d
        if not decl:
            # `X v = this; ... v.<init>(...)` inside a constructor: it is the super(...) call (Java 25 allows statements before it)
            alias = re.search(r'^[ \t]*[\w<>.]+ ' + re.escape(v) + r' = this;\n', head, re.M)
            if alias:
                s = s[:alias.start()] + s[alias.end():m.start() - 0]  + 'super(' + s[m.end():]
                s = s.replace('super(', 'super(', 1); total += 1; continue
            print('NO DECL', f, v); s = s[:m.start()] + 'XXRESUGARFAIL' + s[m.end():]; continue
        T = decl.group(3)
        newdecl = (decl.group(1) + decl.group(2) + ' ' + v + ';\n') if decl.group(2) else ''
        s = s[:decl.start()] + newdecl + s[decl.end():m.start()] + v + ' = new ' + T + '(' + s[m.end():]
        total += 1
    open(f, 'w', encoding='utf-8').write(s)
print('resugared', total)
