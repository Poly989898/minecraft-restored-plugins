#!/usr/bin/env python3
"""Compares invoked method descriptors per class between the original bytecode and the rebuilt classes (class names mapped through mappings/applied).
A differing descriptor means javac picked another overload than the original code called (overload trap)."""
import re,glob,collections,sys
final={}
chain=[]
for m in sorted(glob.glob('mappings/applied/*.map')):
    for l in open(m):
        p=l.split()
        if len(p)==3 and p[0]=='C': chain.append((p[1],p[2]))
for a,b in chain:
    for k,v in list(final.items()):
        if v==a: final[k]=b
    if a not in final: final[a]=b
def mapname(n): return final.get(n,n)
def mapdesc(d): return re.sub(r'Llitebans/(\w+);',lambda m:'Llitebans/%s;'%mapname(m.group(1)),d)
orig=collections.defaultdict(collections.Counter); new=collections.defaultdict(collections.Counter)
for l in open('.work/inv-orig.txt'):
    c,o,n,d=l.rstrip('\n').split('\t')
    if o.endswith('SneakyThrow'): continue
    orig[mapname(c.split('/')[-1])][(mapname(o.split('/')[-1]),mapdesc(d))]+=1
for l in open('.work/inv-new.txt'):
    c,o,n,d=l.rstrip('\n').split('\t')
    if o.endswith('SneakyThrow'): continue
    new[c.split('/')[-1]][(o.split('/')[-1],d)]+=1
out=[]
for c in sorted(set(orig)&set(new)):
    # compare by descriptor only (names and owners differ through renaming / call qualifier)
    a=collections.Counter(); b=collections.Counter()
    for (o,d),n in orig[c].items(): a[d]+=n
    for (o,d),n in new[c].items(): b[d]+=n
    miss=a-b; extra=b-a
    if miss or extra: out.append((c,miss,extra))
print('classes with differences:',len(out))
for c,miss,extra in out:
    print('##',c)
    for d,n in miss.items(): print('   - orig only',n,d)
    for d,n in extra.items(): print('   + new only ',n,d)
