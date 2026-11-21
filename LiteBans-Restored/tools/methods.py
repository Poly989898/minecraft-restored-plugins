#!/usr/bin/env python3
"""Lists still-obfuscated methods ranked by call-site count with a one-line body summary. usage: methods.py <offset> <limit> [class-regex]"""
import re,glob,os,sys,collections
files={os.path.basename(f)[:-5]:open(f,encoding='utf-8').read() for f in glob.glob('src/main/java/litebans/*.java')}
OBF=re.compile(r'^(?:[a-zA-Z]{1,2}_\d+|[a-zA-Z]\d)$')
allcode='\n'.join(files.values())
calls=collections.Counter(re.findall(r'\.(\w+)\(',allcode))
rows=[]
hdr=re.compile(r'^   (?:@\w+(?:\([^)]*\))? )*(?:(?:public|protected|private|static|final|abstract|synchronized|default) )*([\w<>\[\], .?]+?) (\w+)\(([^)]*)\)(?: throws [\w., ]+)? ?[{;]$',re.M)
rx=re.compile(sys.argv[3]) if len(sys.argv)>3 else None
for c,s in files.items():
    if rx and not rx.search(c): continue
    L=s.split('\n')
    for m in hdr.finditer(s):
        name=m.group(2)
        if not OBF.match(name) or name in ('a','b','c','d','e','f') and False: continue
        if len(name)<=1 and name not in 'abcdef' : pass
        line=s[:m.start()].count('\n')
        body=[]
        if m.group(0).endswith('{'):
            j=line+1
            while j<len(L) and not L[j].startswith('   }'): body.append(L[j].strip()); j+=1
        rows.append((calls[name],c,m.group(1).strip(),name,re.sub(r'@\w+ ','',m.group(3))[:60],' '.join(body)[:110]))
rows.sort(key=lambda r:-r[0])
off=int(sys.argv[1]); lim=int(sys.argv[2])
for n,c,r,name,p,b in rows[off:off+lim]: print(f"{c}.{name}({p}) -> {r} [{n}] {b}")
