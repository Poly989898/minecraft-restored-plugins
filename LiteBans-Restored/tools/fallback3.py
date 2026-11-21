#!/usr/bin/env python3
"""Fallback names for remaining obfuscated members; unique per inheritance component (no accidental overrides/hiding), consistent per override key.
usage: fallback3.py  -> appends to mappings/names.map"""
import re,glob,os,collections,sys
fab=set(l.strip()[:-5] for l in open('tools/fabric-files.txt') if l.strip())
OBF=re.compile(r'^(?:[a-zA-Z]{1,2}|[a-zA-Z]{1,2}_\d+)$'); FO=re.compile(r'^[a-zA-Z]{1,2}\d?_f$')
KW=set('abstract assert boolean break byte case catch char class const continue default do double else enum extends final finally float for goto if implements import instanceof int interface long native new package private protected public return short static strictfp super switch synchronized this throw throws transient try void volatile while'.split())
PRIM={'boolean':'flag','int':'intValue','long':'longValue','byte':'byteValue','short':'shortValue','char':'charValue','double':'doubleValue','float':'floatValue'}
def cap(s): return s[:1].upper()+s[1:]
def decap(s):
    i=0
    while i<len(s) and s[i].isupper(): i+=1
    if i<=1: return s[:1].lower()+s[1:]
    if i==len(s): return s.lower()
    return s[:i-1].lower()+s[i-1:]
def tname(t):
    t=re.sub(r'<.*>','',t).strip().replace('final ','')
    if t.endswith('[]'):
        b=tname(t[:-2]); return {'byteValue':'bytes','charValue':'chars','intValue':'ints','longValue':'longs','flag':'flags','text':'texts'}.get(b,b+'Array')
    if t in PRIM: return PRIM[t]
    if t=='String': return 'text'
    if t=='CharSequence': return 'message'
    if t=='Object': return 'obj'
    t=t.split('.')[-1]; t=re.sub(r'\d+$','',t)
    return decap(t) if t else 'value'
def ctype(t): return cap(tname(t))
files={os.path.basename(f)[:-5]:f for f in glob.glob('src/main/java/litebans/*.java') if os.path.basename(f)[:-5] not in fab}
proj=set(files)
src={c:open(f,encoding='utf-8').read() for c,f in files.items()}
# hierarchy
parent=collections.defaultdict(lambda:None); par={}
def find(x):
    while par.setdefault(x,x)!=x: par[x]=par[par[x]]; x=par[x]
    return x
def union(a,b): par[find(a)]=find(b)
libsuper={}
for c,s in src.items():
    find(c)
    m=re.search(r'^(?:public |abstract |final |static )*(?:class|interface|enum) '+re.escape(c)+r'(?:<[^{]*?>)?(?: extends ([^{]*?))?(?: implements ([^{]*?))?\s*\{',s,re.M)
    sup=[]
    if m:
        for g in (m.group(1),m.group(2)):
            if g:
                for x in re.split(r',(?![^<]*>)',g):
                    x=re.sub(r'<.*','',x.strip()); sup.append(x)
    ls=False
    for x in sup:
        if x in proj: union(c,x)
        else: ls=True
    libsuper[c]=ls
comp=collections.defaultdict(list)
for c in src: comp[find(c)].append(c)
METH=re.compile(r'^   (?:@\w+(?:\([^)]*\))? )*(?:public |protected |private )?(?:static )?(?:final )?(?:abstract )?(?:default )?(?:synchronized )?((?:@\w+ )?[\w<>\[\], .?]+?) (\w+)\(((?:[^()])*)\)(?: throws [\w, .]+)? ?(\{|;)$')
def parse_methods(c):
    L=src[c].split('\n'); out=[]
    for i,l in enumerate(L):
        l2=re.sub(r'(?<=\s)@\w+ ','',l)
        m=METH.match(l2)
        if not m: continue
        ret=m.group(1).strip()
        if ret in ('new','return','else') or re.search(r'[()=]',ret): continue
        ps=[q for q in re.sub(r'<[^>]*>','',m.group(3)).split(',') if q.strip()]
        if any(' ' not in re.sub(r'@\w+ ','',q.strip()) for q in ps): continue
        pts=tuple(re.sub(r'<.*','',re.sub(r'@\w+ ','',q.strip()).rsplit(' ',1)[0]) for q in ps)
        body=' '.join(x.strip() for x in L[i+1:i+4]) if m.group(4)=='{' else ''
        out.append((m.group(2),pts,ret,body,l))
    return out
out=[]
for root,cl in comp.items():
    used_f=set(); used_m=set(); meths={}
    for c in cl:
        s=src[c]
        for x in re.findall(r'^   [^=\n(]*? (\w+)(?: = .*)?;$',s,re.M): used_f.add(x)
        ms=parse_methods(c); meths[c]=ms
        for (n,p,r,b,l) in ms:
            if not OBF.match(n): used_m.add((n,p))
    allnames=set()
    for c in cl: allnames|=set(re.findall(r'\b\w+\b',src[c]))
    fmaps={}
    # fields
    for c in cl:
        s=src[c]; fmap={}
        for l in s.split('\n'):
            m=re.match(r'^   (?:@\w+ )*((?:private |public |protected )?(?:static )?(?:final )?(?:volatile )?(?:transient )?)([\w<>\[\], .?]+?) ([a-zA-Z]{1,2}\d?_f)(?: = (.*))?;$',l)
            if not m: continue
            mods,ty,fld,init=m.group(1),m.group(2).strip(),m.group(3),m.group(4)
            static='static' in mods and 'final' in mods
            if static:
                if init and re.match(r'^-?\d+L?$',init): base='VALUE_'+init.rstrip('L').replace('-','MINUS_')
                elif init and init.startswith('"'): base='TEXT'
                else: base=re.sub(r'(?<!^)(?=[A-Z])','_',tname(ty)).upper()
            else: base=tname(ty)
            n=base; i=2
            while n in used_f or n in KW or n in allnames: n=base+('_' if static else '')+str(i); i+=1
            used_f.add(n); fmap[fld]=n; out.append('F %s.%s %s'%(c,fld,n))
        fmaps[c]=fmap
    assigned={}
    for c in cl:
        fmap=fmaps[c]
        for (o,pts,ret,body,l) in meths[c]:
            if not OBF.match(o): continue
            key=(o,pts)
            if key in assigned: out.append('M %s.%s(%s) %s'%(c,o,','.join(pts),assigned[key])); continue
            libs=libsuper[c]
            g=re.match(r'^return this\.(\w+); \}',body); st=re.match(r'^this\.(\w+) = \w+; \}',body)
            nm=None
            if not libs:
                if g and not pts and (g.group(1) in fmap or not FO.match(g.group(1))):
                    nm=('is' if ret=='boolean' else 'get')+cap(fmap.get(g.group(1),g.group(1)))
                elif st and len(pts)==1 and (st.group(1) in fmap or not FO.match(st.group(1))):
                    nm='set'+cap(fmap.get(st.group(1),st.group(1)))
            if nm is None:
                if ret=='void': nm=('handle'+ctype(pts[0])) if pts else 'perform'
                elif not pts: nm='obtain'+ctype(ret)
                elif ret=='boolean': nm='check'+ctype(pts[0])
                else: nm='compute'+ctype(ret)
            nm=re.sub(r'[^A-Za-z0-9_]','',nm) or 'process'
            base=nm; i=2
            while (nm,pts) in used_m or nm in KW:
                nm=base+str(i); i+=1
            used_m.add((nm,pts)); assigned[key]=nm
            out.append('M %s.%s(%s) %s'%(c,o,','.join(pts),nm))
open('mappings/names.map','w').write('# fallback3\n'+'\n'.join(out)+'\n'); print(len(out),sum(1 for x in out if x.startswith('F')),'fields')
