#!/usr/bin/env python3
import re,sys
for c in sys.argv[1:]:
    s=open('src/main/java/litebans/%s.java'%c,encoding='utf-8').read(); L=s.split('\n')
    print('=====',c)
    for i,l in enumerate(L):
        m=re.match(r'^   (?:@\w+ )*((?:private |public |protected )?(?:static )?(?:final )?(?:volatile )?[\w<>\[\], .?]+?) ([a-zA-Z]{1,2}\d?_f)(?: = (.*))?;$',l)
        if not m: continue
        f=m.group(2); hint=''
        if m.group(3): hint='= '+m.group(3)[:60]
        else:
            for j,x in enumerate(L):
                if j!=i and re.search(r'\b'+f+r'\b',x) and 'return this.'+f not in x:
                    hint='@ '+x.strip()[:90]; break
        print('%-40s %s %s'%(m.group(1)[:38],f,hint))
