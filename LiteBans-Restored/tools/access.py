#!/usr/bin/env python3
"""Names synthetic static accessors (single statement forwarding on var0) as access$<name>."""
import re,glob,os,collections
fab=set(l.strip()[:-5] for l in open('tools/fabric-files.txt') if l.strip())
OBF=re.compile(r'^(?:[a-zA-Z]{1,2}|[a-zA-Z]{1,2}_\d+)$')
out=[];
for f in sorted(glob.glob('src/main/java/litebans/*.java')):
    c=os.path.basename(f)[:-5]
    if c in fab: continue
    L=open(f,encoding='utf-8').read().split('\n')
    names=collections.Counter(); cand=[]
    for i,l in enumerate(L):
        m=re.match(r'^   (?:@\w+ )*(?:public |protected |private )?static (?:final )?[\w<>\[\], .?]+? (\w+)\(((?:[^()])*)\) \{$',l)
        if not m: continue
        names[m.group(1)]+=0
        if not OBF.match(m.group(1)) or not m.group(2).strip(): continue
        body=[]; j=i+1
        while not L[j].startswith('   }'): body.append(L[j].strip()); j+=1
        if len(body)!=1: continue
        b=body[0]
        t=re.match(r'^(?:return )?(?:\([\w<>\[\]]+\))?var0\.(\w+)\(',b) or re.match(r'^(?:return )?(?:\([\w<>\[\]]+\))?var0\.(\w+);$',b)
        g=re.match(r'^return \((\w+)\)var0\.\w+\.getModule\(\1\.class\);$',b) or re.match(r'^return \((\w+)\)var0\.\w+\.getModule\(\1\.class\);',b)
        if g: n='access$get'+g.group(1)
        elif t and not OBF.match(t.group(1)): n='access$'+t.group(1)
        else: continue
        pts=[re.sub(r'<.*','',re.sub(r'@\w+ ','',p.strip()).rsplit(' ',1)[0]) for p in m.group(2).split(',')]
        cand.append((c,m.group(1),pts,n))
    cnt=collections.Counter((a,n) for a,_,_,n in cand)
    seen=collections.Counter()
    for c,o,pts,n in cand:
        seen[(c,n)]+=1
        nn=n if seen[(c,n)]==1 else n+str(seen[(c,n)])
        out.append('M %s.%s(%s) %s'%(c,o,','.join(pts),nn))
open('mappings/names.map','w').write('# pending renames\n'+'\n'.join(out)+'\n'); print(len(out))
