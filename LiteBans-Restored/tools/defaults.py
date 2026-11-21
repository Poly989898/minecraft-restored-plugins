#!/usr/bin/env python3
"""Names Kotlin `$default` forwarders (static, last params `int mask, Object marker`) after the method they call: <target>WithDefaults."""
import re,glob,os
fab=set(l.strip()[:-5] for l in open('tools/fabric-files.txt') if l.strip())
OBF=re.compile(r'^(?:[a-zA-Z]\w?|[a-zA-Z]{1,2}_\d+)$')
out=[]; seen=set()
for f in sorted(glob.glob('src/main/java/litebans/*.java')):
    c=os.path.basename(f)[:-5]
    if c in fab: continue
    L=open(f,encoding='utf-8').read().split('\n')
    for i,l in enumerate(L):
        m=re.match(r'^   (?:@\w+ )*(?:public |protected |private )?static (?:final )?(?:[\w<>\[\], .?]+?) (\w+)\(((?:[^()])*?)int (\w+), Object (\w+)\) \{',l)
        if not m or not OBF.match(m.group(1)): continue
        j=i+1; body=[]
        while not L[j].startswith('   }'): body.append(L[j].strip()); j+=1
        last=[b for b in body if b and b!='}'][-1] if body else ''
        t=re.search(r'(?:return )?(?:[\w.]+\.)?(\w+)\([^;]*\);$',last)
        if not t: continue
        tn=t.group(1)
        if OBF.match(tn) or tn in ('toString',): continue
        pt=[re.sub(r'@\w+ ','',p.strip()).rsplit(' ',1)[0] for p in m.group(2).split(',') if p.strip()]+['int','Object']
        key=(c,tn,tuple(pt))
        out.append('M %s.%s(%s) %sWithDefaults'%(c,m.group(1),','.join(re.sub(r'<.*','',x) for x in pt),tn))
open('mappings/names.map','w').write('# pending renames\n'+'\n'.join(out)+'\n'); print(len(out))
