#!/usr/bin/env python3
"""Mechanical fallback names (type/shape based) for every remaining obfuscated method and field. usage: fallback2.py [skip-file]"""
import re,glob,os,collections,sys
fab=set(l.strip()[:-5] for l in open('tools/fabric-files.txt') if l.strip())
OBF=re.compile(r'^(?:[a-zA-Z]{1,2}|[a-zA-Z]{1,2}_\d+)$'); FO=re.compile(r'^[a-zA-Z]{1,2}\d?_f$')
skip=set(open(sys.argv[1]).read().split('\n')) if len(sys.argv)>1 else set()
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
    t=t.split('.')[-1]
    t=re.sub(r'\d+$','',t)
    return decap(t) if t else 'value'
def ctype(t):  # for method suffixes
    n=tname(t); return cap(n)
out=[]
for f in sorted(glob.glob('src/main/java/litebans/*.java')):
    c=os.path.basename(f)[:-5]
    if c in fab or c in skip: continue
    s=open(f,encoding='utf-8').read(); L=s.split('\n')
    names=set(re.findall(r'\b\w+\b',s))
    used=set()
    fields={}
    for i,l in enumerate(L):
        m=re.match(r'^   (?:@\w+ )*((?:private |public |protected )?(?:static )?(?:final )?(?:volatile )?(?:transient )?)([\w<>\[\], .?]+?) ([a-zA-Z]{1,2}\d?_f)(?: = (.*))?;$',l)
        if m and FO.match(m.group(3)): fields[m.group(3)]=(m.group(1),m.group(2).strip(),m.group(4))
    decl_names=set(re.findall(r'^   .*? (\w+)(?: = .*)?;$',s,re.M))|set(re.findall(r'^   [^=]*? (\w+)\(',s,re.M))
    cnt=collections.Counter(); fmap={}
    for fld,(mods,ty,init) in fields.items():
        static='static' in mods and 'final' in mods
        if static:
            base=None
            if init and re.match(r'^-?\d+L?$',init): base='VALUE_'+init.rstrip('L').replace('-','MINUS_')
            elif init and init.startswith('"'): base='TEXT'
            else: base=re.sub(r'(?<!^)(?=[A-Z])','_',tname(ty)).upper()
            n=base
        else: n=tname(ty)
        k=n; i=2
        while n in names or n in used or n in KW or n in decl_names:
            n=k+(('_' if static else '')+str(i)); i+=1
        used.add(n); fmap[fld]=n; out.append('F %s.%s %s'%(c,fld,n))
    cnt=collections.Counter()
    meths=[]
    for i,l in enumerate(L):
        l=re.sub(r'(?<=\s)@\w+ ','',l)
        m=re.match(r'^   (?:@\w+ )*(?:public |protected |private )?(?:static )?(?:final )?(?:abstract )?(?:default )?(?:synchronized )?([\w<>\[\], .?]+?) (\w+)\(((?:[^()])*)\)(?: throws [\w, .]+)? ?(\{|;)$',l)
        if not m or not OBF.match(m.group(2)): continue
        ret=re.sub(r'@\w+ ','',m.group(1)).strip()
        if ret in ('new','return','else') or re.search(r'[()=]',ret): continue
        if any(' ' not in re.sub(r'@\w+ ','',q.strip()) for q in re.sub(r'<[^>]*>','',m.group(3)).split(',') if q.strip()): continue
        if len(re.sub(r'<[^>]*>','',m.group(3)).split(','))>=2 and re.sub(r'@\w+ ','',re.sub(r'<[^>]*>','',m.group(3)).split(',')[-2].strip()).startswith('int ') and re.sub(r'@\w+ ','',m.group(3)).strip().endswith(' obj') and 'Object' in m.group(3).split(',')[-1]: continue
        pts=[re.sub(r'<.*','',re.sub(r'@\w+ ','',p.strip()).rsplit(' ',1)[0]) for p in re.sub(r'<[^>]*>','',m.group(3)).split(',') if p.strip()]
        body=' '.join(x.strip() for x in L[i+1:i+4]) if m.group(4)=='{' else ''
        nm=None
        g=re.match(r'^return this\.(\w+); \}',body)
        st=re.match(r'^this\.(\w+) = \w+; \}',body)
        gs=re.match(r'^return (\w+); \}',body)
        fld=None
        if g and not pts: fld=g.group(1)
        elif gs and not pts and gs.group(1) in fmap: fld=gs.group(1)
        if fld and (fld in fmap or not FO.match(fld)):
            fnm=fmap.get(fld,fld); nm=('is' if ret=='boolean' else 'get')+cap(fnm)
        elif st and len(pts)==1 and (st.group(1) in fmap or not FO.match(st.group(1))):
            nm='set'+cap(fmap.get(st.group(1),st.group(1)))
        elif ret=='void':
            nm=('handle'+ctype(pts[0])) if pts else 'run'
        elif not pts: nm='get'+ctype(ret)
        elif ret=='boolean': nm='check'+ctype(pts[0])
        else: nm='compute'+ctype(ret)
        nm=re.sub(r'[^A-Za-z0-9_]','',nm)
        meths.append((m.group(2),pts,nm))
    for o,pts,nm in meths:
        k=nm; i=2
        key=lambda x:(x,tuple(pts))
        while (key(nm) in cnt) or nm in KW:
            nm=k+str(i); i+=1
        cnt[key(nm)]+=1
        out.append('M %s.%s(%s) %s'%(c,o,','.join(pts),nm))
open('mappings/names.map','w').write('# fallback\n'+'\n'.join(out)+'\n'); print(len(out), sum(1 for x in out if x.startswith('F')),'fields')
