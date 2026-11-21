#!/usr/bin/env python3
"""Per-file choice between decompiler outputs: for every file, keep the variant with fewer javac errors.
usage: choose.py <log of current mix> <log of baseline (Vineflower)>   (swaps files back to the Vineflower copy kept in .work/vf-src when it is better)"""
import re,collections,shutil,os,sys
def per(path):
    c=collections.Counter()
    for m in re.finditer(r'/litebans/(\w+)\.java:\d+: error:',open(path).read()): c[m.group(1)]+=1
    return c
cur=per(sys.argv[1]); base=per(sys.argv[2]); n=0
for f in set(cur)|set(base):
    if base.get(f,0) < cur.get(f,0):
        s=f'.work/vf-src/{f}.java'; d=f'src/main/java/litebans/{f}.java'
        if os.path.exists(s) and open(s).read()!=open(d).read(): shutil.copy(s,d); n+=1
print('swapped back to Vineflower:',n)
