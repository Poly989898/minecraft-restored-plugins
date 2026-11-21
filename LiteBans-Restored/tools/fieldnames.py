#!/usr/bin/env python3
"""Names obfuscated fields from the (already meaningful) identifier they are assigned from: `this.a_f = platform;` -> platform."""
import re,glob,os,collections
fab=set(l.strip()[:-5] for l in open('tools/fabric-files.txt') if l.strip())
FOBF=re.compile(r'^[a-zA-Z]{1,2}\d?_f$')
GEN={'true','false','null','text','value','flag','obj','message','list','map','set','bytes','chars','number','error','callback','supplier','boxed','builder','file','b','c','i','e','value2','value3','text2','flag2','array','string','object'}
KW=set('abstract assert boolean break byte case catch char class const continue default do double else enum extends final finally float for goto if implements import instanceof int interface long native new package private protected public return short static strictfp super switch synchronized this throw throws transient try void volatile while'.split())
out=[]
for f in sorted(glob.glob('src/main/java/litebans/*.java')):
    c=os.path.basename(f)[:-5]
    if c in fab: continue
    s=open(f,encoding='utf-8').read()
    decl=dict()
    for m in re.finditer(r'^   (?:@\w+ )*(?:private |public |protected )?(?:static )?(?:final )?(?:volatile )?([\w<>\[\], .?]+?) (\w+)(?: = .*)?;$',s,re.M):
        decl[m.group(2)]=m.group(1)
    allnames=set(re.findall(r'\b\w+\b',s))
    cand=collections.defaultdict(collections.Counter)
    for m in re.finditer(r'this\.(\w+) = (?:\([\w<>\[\]]+\))?(\w+);',s):
        fld,src=m.groups()
        if fld in decl and FOBF.match(fld) and src in allnames and not FOBF.match(src) and src not in GEN and src not in KW and re.match(r'^[a-z]\w*$',src) and not re.match(r'^[a-z]+\d+$',src): cand[fld][src]+=1
    used=set()
    for fld,cn in cand.items():
        if len(cn)!=1: continue
        n=next(iter(cn))
        # skip if the name already exists as any identifier (method names, other fields, locals ok only if not field)
        if n in decl or n in used: continue
        unq=[m for m in re.finditer(r'(?<![.\w])'+re.escape(fld)+r'\b',s)]
        if len(unq)>1: continue
        if re.search(r'\b(get|set|is)'+n[:1].upper()+n[1:]+r'\b',s): continue
        used.add(n); out.append('F %s.%s %s'%(c,fld,n))
open('mappings/names.map','a').write('\n'.join(out)+'\n'); print(len(out))
