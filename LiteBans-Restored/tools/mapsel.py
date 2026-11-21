#!/usr/bin/env python3
"""usage: mapsel.py spec.txt  — lines `Class :: regex-on-declaration :: newName`; emits M lines (appends to names.map). Each regex must match exactly one obfuscated declaration."""
import re,sys,collections
OBF=re.compile(r'^(?:[a-zA-Z]{1,2}|[a-zA-Z]{1,2}_\d+)$')
out=[];bad=0
for spec in open(sys.argv[1],encoding='utf-8'):
    spec=spec.rstrip('\n')
    if not spec.strip() or spec.startswith('#'): continue
    c,rx,new=[x.strip() for x in spec.split('::')]
    L=open('src/main/java/litebans/%s.java'%c,encoding='utf-8').read().split('\n')
    hits=[]
    J=[];i=0
    while i<len(L):
        l=L[i]
        if re.match(r'^   [\w@].*\($',l):
            k=i; buf=l
            while not buf.rstrip().endswith('{') and k<i+12: k+=1; buf+=' '+L[k].strip()
            l='   '+re.sub(r' \)',')',re.sub(r'\( +','(',re.sub(r'\s+',' ',buf.strip()))); i=k
        l=re.sub(r'@(NotNull|Nullable) ','',l); J.append(l); i+=1
    for i,l in enumerate(J):
        m=re.match(r'^   (?:@\w+ )*(?:public |protected |private )?(?:static )?(?:final )?(?:abstract )?(?:synchronized )?([\w<>\[\], .?]+?) (\w+)\(((?:[^()])*)\)(?: throws [\w, .]+)? ?[{;]$',l)
        if not m or not OBF.match(m.group(2)): continue
        decl=l.strip()
        if re.search(rx,decl): hits.append((m,decl))
    if len(hits)!=1: print('BAD',len(hits),spec,[h[1][:80] for h in hits][:4]); bad+=1; continue
    m=hits[0][0]
    pts=[re.sub(r'<.*','',re.sub(r'@\w+ ','',p.strip()).rsplit(' ',1)[0]) for p in m.group(3).split(',') if p.strip()]
    out.append('M %s.%s(%s) %s'%(c,m.group(2),','.join(pts),new))
open('mappings/names.map','a').write('\n'.join(out)+'\n'); print(len(out),'ok',bad,'bad')
