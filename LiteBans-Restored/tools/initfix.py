#!/usr/bin/env python3
"""Fixes `variable X might not have been initialized` by giving the declaration a default value (decompiler lost some initialisations)."""
import re,sys
log=open(sys.argv[1]).read(); done=set(); n=0
def default(t):
    t=t.strip()
    return {'int':'0','short':'0','byte':'0','char':"'\\0'",'long':'0L','boolean':'false','float':'0.0F','double':'0.0'}.get(t,'null')
for b in re.split(r'\n(?=/home/)',log):
    m=re.match(r'(\S+?\.java):(\d+): error: variable (\w+) might not have been initialized',b.strip())
    if not m: continue
    path,line,var=m.group(1),int(m.group(2)),m.group(3)
    if (path,var,line) in done: continue
    done.add((path,var,line))
    src=open(path,encoding='utf-8').read().split('\n')
    for i in range(line-1,-1,-1):
        d=re.match(r'^(\s*)((?:final )?[\w<>\[\], .?]+?) %s;\s*$'%re.escape(var),src[i])
        if d:
            src[i]='%s%s %s = %s;'%(d.group(1),d.group(2),var,default(d.group(2))); n+=1; break
    open(path,'w',encoding='utf-8').write('\n'.join(src))
print('initialised',n)
