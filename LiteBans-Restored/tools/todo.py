#!/usr/bin/env python3
"""Per-class list of still-obfuscated methods (name <=2 chars or X_N). usage: todo.py [class-regex] [show]"""
import re,glob,os,sys,collections
OBF=re.compile(r'^(?:[a-zA-Z]\w?|[a-zA-Z]{1,2}_\d+)$')
KEEP={'to','is','of','on','or','if','in'}
rows={}
hdr=re.compile(r'^   (?:@\w+(?:\([^)]*\))? )*(?:(?:public|protected|private|static|final|abstract|synchronized|default) )*(?:<[^>]+> )?([\w<>\[\], .?]+?) (\w+)\(((?:[^()]|\n)*?)\)(?: throws [\w., ]+)? ?[{;]\s*$',re.M)
rx=re.compile(sys.argv[1]) if len(sys.argv)>1 else None
fab=set(l.strip()[:-5] for l in open('tools/fabric-files.txt') if l.strip())
for f in sorted(glob.glob('src/main/java/litebans/*.java')):
    c=os.path.basename(f)[:-5]
    if c in fab or (rx and not rx.search(c)): continue
    s=open(f,encoding='utf-8').read()
    ms=[]
    for m in hdr.finditer(s):
        if OBF.match(m.group(2)) and m.group(2) not in KEEP and not re.match(r'^[A-Z]',m.group(2)):
            ms.append((m.group(1).strip(),m.group(2),re.sub(r'\s+',' ',re.sub(r'@[\w.]+(?:\([^)]*\))? ?','',m.group(3)))[:90],s[:m.start()].count('\n')))
    if ms: rows[c]=ms
if len(sys.argv)>2:
    for c,ms in rows.items():
        s=open(f'src/main/java/litebans/{c}.java',encoding='utf-8').read().split('\n')
        print('=====',c)
        for r,n,p,line in ms:
            body=[]; j=line+1
            if s[line].rstrip().endswith('{'):
                while j<len(s) and not s[j].startswith('   }'): body.append(s[j].strip()); j+=1
            print(f"{r} {n}({p}) :: {' '.join(body)[:130]}")
else:
    tot=0
    for c,ms in sorted(rows.items(),key=lambda x:-len(x[1])): print(len(ms),c); tot+=len(ms)
    print('total',tot,'classes',len(rows))
