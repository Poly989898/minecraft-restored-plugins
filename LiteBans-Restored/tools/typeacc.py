#!/usr/bin/env python3
"""Type-based names for trivial accessors/fields: field of unique non-generic named class type -> get<Type>/set<Type>, field <type>."""
import re,glob,os,collections
fab=set(l.strip()[:-5] for l in open('tools/fabric-files.txt') if l.strip())
OBF=re.compile(r'^(?:[a-zA-Z]{1,2}|[a-zA-Z]{1,2}_\d+)$')
FOBF=re.compile(r'^[a-zA-Z]{1,2}\d?_f$')
BAD={'String','Object','Integer','Long','Boolean','Byte','Short','Double','Float','List','Map','Set','HashMap','ArrayList','Collection','Lazy','Function0','Function1','Pair','Triple','Class','Throwable','Exception','Iterable'}
out=[]
for f in sorted(glob.glob('src/main/java/litebans/*.java')):
    c=os.path.basename(f)[:-5]
    if c in fab: continue
    s=open(f,encoding='utf-8').read(); L=s.split('\n')
    fields={}
    for l in L:
        m=re.match(r'^   (?:@\w+ )*(?:private |public |protected )?(?:static )?(?:final )?(?:volatile )?([A-Z]\w*) (\w+)(?: = .*)?;$',l)
        if m and FOBF.match(m.group(2)) and m.group(1) not in BAD: fields[m.group(2)]=m.group(1)
    bytype=collections.defaultdict(list)
    for fn,t in fields.items(): bytype[t].append(fn)
    allnames=set(re.findall(r'\b(\w+)\b',s))
    for t,fl in bytype.items():
        if len(fl)!=1: continue
        fn=fl[0]; base=t[:1].lower()+t[1:]
        if base in allnames or 'get'+t in allnames or 'set'+t in allnames or not base.isidentifier(): continue
        nm=[]
        for i,l in enumerate(L):
            m=re.match(r'^   (?:@\w+ )*(?:public |protected |private )?(?:static )?(?:final )?([\w<>\[\], .?]+?) (\w+)\(((?:[^()])*)\) \{$',l)
            if not m or not OBF.match(m.group(2)): continue
            b=' '.join(x.strip() for x in L[i+1:i+4])
            pts=[re.sub(r'<.*','',re.sub(r'@\w+ ','',p.strip()).rsplit(' ',1)[0]) for p in m.group(3).split(',') if p.strip()]
            if re.match(r'^return this\.%s; }'%fn,b) and not pts: nm.append('M %s.%s() get%s'%(c,m.group(2),t))
            elif re.match(r'^this\.%s = var1; }'%fn,b) and pts==[t]: nm.append('M %s.%s(%s) set%s'%(c,m.group(2),t,t))
        if nm: out.append('F %s.%s %s'%(c,fn,base)); out+=nm
open('mappings/names.map','a').write('\n'.join(out)+'\n'); print(len(out))
