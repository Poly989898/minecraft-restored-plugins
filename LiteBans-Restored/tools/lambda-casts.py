#!/usr/bin/env python3
"""For javac errors `no suitable method found for NAME(.., Object ..)` on a line `p -> NAME(.., p)` (lambda parameter lost its type because
the functional interface is raw), cast the lambda parameter to the parameter type of the only candidate with the right arity."""
import re,sys
log=open(sys.argv[1]).read(); n=0
for b in re.split(r'\n(?=/home/|[A-Za-z]:\\)',log):
    m=re.match(r'((?:/home/|[A-Za-z]:\\)\S+?\.java):(\d+): error: no suitable method found for (\w+)\((.*?)\)\n',b)
    if not m: continue
    path,line,name,args=m.group(1),int(m.group(2)),m.group(3),m.group(4)
    arity=len(args.split(',')) if args else 0
    cands=[c for c in re.findall(r'method [\w.$]+\.%s\(([^)]*)\) is not applicable'%name,b)]
    cands=[c.replace('@org.jetbrains.annotations.NotNull ','').replace('@org.jetbrains.annotations.Nullable ','') for c in cands]
    cands=[c.split(',') for c in cands if len([x for x in c.split(',') if x])==arity]
    atypes=[re.sub(r'^.*\.','',x.strip()) for x in args.split(',')] if args else []
    if len(cands)>1:   # prefer the candidate whose parameters equal the (known) argument types exactly
        exact=[c for c in cands if all(a=='Object' or re.sub(r'^.*\.','',c[i].strip())==a for i,a in enumerate(atypes))]
        if len(exact)==1: cands=exact
    if len(cands)!=1: continue
    ptypes=[re.sub(r'^.*\.','',t.strip()) for t in cands[0]]
    src=open(path,encoding='utf-8').read().split('\n'); l=src[line-1]
    lm=re.search(r'\b(\w+) -> '+name+r'\(',l) or re.search(r'\(?(\w+)\)? -> '+name+r'\(',l)
    if not lm: continue
    p=lm.group(1)
    i=l.index(name+'(',lm.start())+len(name)+1
    # split args at top level
    depth=0; cur=''; parts=[]
    for ch in l[i:]:
        if ch=='(' : depth+=1
        if ch==')':
            if depth==0: break
            depth-=1
        if ch==',' and depth==0: parts.append(cur); cur=''; continue
        cur+=ch
    parts.append(cur)
    if len(parts)!=arity: continue
    newparts=[]
    for k,a in enumerate(parts):
        if a.strip()==p and ptypes[k] not in ('Object',): newparts.append(a.replace(p,'(%s) %s'%(ptypes[k],p)).strip() if k==0 else ' (%s) %s'%(ptypes[k],p))
        else: newparts.append(a)
    new=l[:i]+','.join(newparts)+l[i+len(','.join(parts)):]
    if new!=l: src[line-1]=new; open(path,'w',encoding='utf-8').write('\n'.join(src)); n+=1
print('lambda params cast:',n)
