#!/usr/bin/env python3
"""Summarise javac errors from a log: distinct errors, per-category counts with one sample each."""
import re,collections,sys
t=open(sys.argv[1]).read(); nsamples=int(sys.argv[2]) if len(sys.argv)>2 else 12
blocks=re.split(r'\n(?=/home/)',t)
cat=collections.OrderedDict(); files=collections.Counter(); n=0
for b in blocks:
    m=re.match(r'.*?/litebans/(\w+)\.java:(\d+): error: ([^\n]*)',b,re.S)
    if not m: continue
    n+=1; files[m.group(1)]+=1
    msg=re.sub(r'[\w$.]+::\w+','X::y',m.group(3)); msg=re.sub(r'\b[a-zA-Z_]\w{0,3}\b','_',msg)[:50]
    cat.setdefault(msg,[]).append(b)
print('distinct',n,'files',len(files)); print(files.most_common(10))
for k,v in sorted(cat.items(),key=lambda x:-len(x[1]))[:nsamples]:
    print('=====',len(v),k); print(re.sub(r'/home/\S*/litebans/','',v[0])[:500])
