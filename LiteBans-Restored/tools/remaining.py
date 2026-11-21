#!/usr/bin/env python3
import re,glob,os,collections,sys
fab=set(l.strip()[:-5] for l in open('tools/fabric-files.txt') if l.strip())
OBF=re.compile(r'^(?:[a-zA-Z]{1,2}|[a-zA-Z]{1,2}_\d+)$'); FO=re.compile(r'^[a-zA-Z]{1,2}\d?_f$')
cnt=collections.Counter()
for p in glob.glob('src/main/java/litebans/*.java'):
    c=os.path.basename(p)[:-5]
    if c in fab: continue
    s=open(p,encoding='utf-8').read()
    n=sum(1 for x in re.finditer(r'^   (?:@\w+ )*(?:public |protected |private )?(?:static )?(?:final )?(?:abstract )?(?:default )?(?:synchronized )?[\w<>\[\], .?]+? (\w+)\(',s,re.M) if OBF.match(x.group(1)))
    n2=sum(1 for x in re.finditer(r'^   (?:@\w+ )*(?:private |public |protected )?(?:static )?(?:final )?(?:volatile )?[\w<>\[\], .?]+? (\w+)(?: = .*)?;$',s,re.M) if FO.match(x.group(1)))
    if n+n2: cnt[c]=(n,n2)
tot=sum(a+b for a,b in cnt.values())
print('total',tot,'classes',len(cnt))
for c,(a,b) in sorted(cnt.items(),key=lambda x:-(x[1][0]+x[1][1]))[:int(sys.argv[1]) if len(sys.argv)>1 else 40]: print(c,a,b)
