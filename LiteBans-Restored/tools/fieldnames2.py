#!/usr/bin/env python3
"""Field names from accessor names (getFoo/isFoo/setFoo touching exactly one obfuscated field) and from constant initialisers (new X(..., "name", ..) -> NAME)."""
import re,glob,os,collections
fab=set(l.strip()[:-5] for l in open('tools/fabric-files.txt') if l.strip())
FOBF=re.compile(r'^[a-zA-Z]{1,2}\d?_f$')
out=[]
for f in sorted(glob.glob('src/main/java/litebans/*.java')):
    c=os.path.basename(f)[:-5]
    if c in fab: continue
    s=open(f,encoding='utf-8').read(); L=s.split('\n')
    decl={}
    for m in re.finditer(r'^   (?:@\w+ )*(?:private |public |protected )?(static )?(?:final )?(?:volatile )?([\w<>\[\], .?]+?) (\w+)(?: = (.*))?;$',s,re.M):
        decl[m.group(3)]=(m.group(1) is not None,m.group(2),m.group(4))
    allnames=set(re.findall(r'\b\w+\b',s))
    cand=collections.defaultdict(collections.Counter)
    for i,l in enumerate(L):
        m=re.match(r'^   (?:@\w+ )*(?:public |protected |private )?(?:static )?(?:final )?(?:synchronized )?[\w<>\[\], .?]+? (get|is|set)([A-Z]\w*)\(([^()]*)\) \{$',l)
        if not m: continue
        j=i+1; body=[]
        while j<len(L) and not L[j].startswith('   }'): body.append(L[j]); j+=1
        refs=set(re.findall(r'(?:this\.)?\b([a-zA-Z]{1,2}\d?_f)\b','\n'.join(body)))
        refs={r for r in refs if r in decl}
        if len(refs)!=1: continue
        fld=next(iter(refs)); n=m.group(2); n=n[:1].lower()+n[1:]
        kind=m.group(1)
        if kind=='set' and not re.search(r'(this\.)?'+fld+r' = ',  '\n'.join(body)): continue
        cand[fld][n]+=1
    used=set(); res={}
    for fld,cn in cand.items():
        if not FOBF.match(fld) or len(cn)!=1: continue
        n=next(iter(cn))
        if n in decl or n in used or n in allnames and n not in ('',) and False: continue
        unq=[m for m in re.finditer(r'(?<![.\w])'+re.escape(fld)+r'\b',s)]
        if decl[fld][0] and len(unq)>1: continue
        if not decl[fld][0] and len([x for x in unq])>1: continue
        used.add(n); res[fld]=n
    # constants from string literal in initialiser
    for fld,(st,ty,init) in decl.items():
        if fld in res or not FOBF.match(fld) or not st or not init: continue
        m=re.match(r'^new \w+\(.*?"([a-z][a-z_0-9]{2,})".*\)$',init)
        if m:
            n=m.group(1).upper()
            if n not in decl and n not in used and n not in allnames: used.add(n); res[fld]=n
    for fld,n in res.items(): out.append('F %s.%s %s'%(c,fld,n))
open('mappings/names.map','a').write('\n'.join(out)+'\n'); print(len(out))
