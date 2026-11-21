#!/usr/bin/env python3
"""addthrows.py <File> <line> <ExceptionType>... : adds `throws X` to the method that encloses the given source line (hand-fix helper)."""
import re,sys
f='src/main/java/litebans/%s.java'%sys.argv[1]; line=int(sys.argv[2]); types=sys.argv[3:]
src=open(f,encoding='utf-8').read().split('\n')
i=line-1
hdr_re=re.compile(r'^   (?!if |for |while |switch |try|else|catch|return|throw|synchronized \()\S.*\)\s*(throws [\w., ]+)?\s*\{\s*$')
while i>=0 and not hdr_re.match(src[i]): i-=1
j=i
while '{' not in src[j]: j+=1
hdr=src[j]
m=re.search(r'\)\s*(throws [\w., ]+)?\s*\{\s*$', hdr)
if not m: sys.exit('cannot locate header at %d: %s'%(j+1,hdr))
cur=[t.strip() for t in (m.group(1) or '')[7:].split(',') if t.strip()]
for t in types:
    if t not in cur: cur.append(t)
src[j]=hdr[:m.start()]+') throws '+', '.join(cur)+' {'
open(f,'w',encoding='utf-8').write('\n'.join(src))
print('ok',sys.argv[1],j+1,src[j].strip())
