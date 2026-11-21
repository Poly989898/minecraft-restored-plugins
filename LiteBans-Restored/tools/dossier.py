#!/usr/bin/env python3
"""Per-class summary used to pick meaningful names: kind, supertypes, size, fan-in, constants, strings, non-obfuscated method names.
usage: dossier.py [min_fanin] [limit] [offset]"""
import re,glob,os,sys,collections
files={os.path.basename(f)[:-5]:open(f,encoding='utf-8').read() for f in glob.glob('src/main/java/litebans/*.java')}
fan=collections.Counter()
names=set(files)
for n,s in files.items():
    body=re.sub(r'"(?:[^"\\]|\\.)*"','""',s)
    for w in set(re.findall(r'\b([A-Za-z_]\w*)\b',body))&names:
        if w!=n: fan[w]+=1
mn=int(sys.argv[1]) if len(sys.argv)>1 else 3; lim=int(sys.argv[2]) if len(sys.argv)>2 else 60; off=int(sys.argv[3]) if len(sys.argv)>3 else 0
rows=sorted(((fan[n],n) for n in files),reverse=True)
rows=[r for r in rows if r[0]>=mn][off:off+lim]
for f,n in rows:
    s=files[n]; head=re.search(r'^(?:@\w+(?:\([^)]*\))?\s*)*(public |final |abstract )*(class|interface|enum|@interface) \w+[^{]*',s,re.M)
    h=re.sub(r'\s+',' ',head.group(0)) if head else '?'
    strs=[x for x in re.findall(r'"((?:[^"\\]|\\.){4,60})"',s) if not x.startswith(('\\u','%'))][:6]
    meth=sorted(set(re.findall(r'\b(?:public|protected)\s+(?:static\s+)?(?:final\s+)?[\w<>\[\], .?]+?\s+([a-zA-Z]\w{3,})\(',s)))[:8]
    consts=re.findall(r'^   ([A-Z][A-Z0-9_]{2,})[,;(]',s,re.M)[:8]
    print(f"## {n}  fanin={f} lines={s.count(chr(10))}  {h[:110]}")
    if consts: print("   const:",' '.join(consts))
    if meth: print("   meth:",' '.join(meth))
    if strs: print("   str:",' | '.join(x[:45] for x in strs))
