#!/usr/bin/env python3
"""Compact dossier of still-obfuscated classes (<=3 chars) with relations to already named classes. usage: dossier2.py <offset> <limit> [min_lines]"""
import re,glob,os,sys,collections
files={os.path.basename(f)[:-5]:open(f,encoding='utf-8').read() for f in glob.glob('src/main/java/litebans/*.java')}
names=set(files)
def toks(s): return set(re.findall(r'\b([A-Za-z_]\w*)\b',re.sub(r'"(?:[^"\\]|\\.)*"','""',s)))&names
uses={n:toks(s)-{n} for n,s in files.items()}
usedby=collections.defaultdict(set)
for n,u in uses.items():
    for x in u: usedby[x].add(n)
unn=[n for n in files if len(n)<=3 and n not in ('Z',)]
fan={n:len(usedby[n]) for n in files}
rows=sorted(unn,key=lambda n:-fan[n])
off=int(sys.argv[1]); lim=int(sys.argv[2]); minl=int(sys.argv[3]) if len(sys.argv)>3 else 0
rows=[n for n in rows if files[n].count('\n')>=minl][off:off+lim]
long_=lambda xs:[x for x in xs if len(x)>3]
for n in rows:
    s=files[n]
    head=re.search(r'^(?:@\w+(?:\([^)]*\))?\s*)*(?:(?:public|final|abstract|static) )*(class|interface|enum|@interface) \w+[^{]*',s,re.M)
    h=re.sub(r'\s+',' ',head.group(0)) if head else '?'
    h=re.sub(r'^(public |final |abstract )*','',h)[:95]
    strs=[x for x in re.findall(r'"((?:[^"\\]|\\.){4,50})"',s) if not x.startswith(('\\u','%'))][:3]
    consts=re.findall(r'^   ([A-Z][A-Z0-9_]{2,})[,;(]',s,re.M)[:6]
    meth=sorted(set(re.findall(r'\b(?:public|protected)\s+(?:static\s+)?(?:final\s+)?[\w<>\[\], .?]+?\s+([a-zA-Z]\w{4,})\(',s)))[:5]
    ub=sorted(long_(usedby[n]),key=lambda x:-fan[x])[:3]; us=sorted(long_(uses[n]),key=lambda x:-fan[x])[:3]
    print(f"{n} L{s.count(chr(10))} f{fan[n]} {h}")
    ex=[]
    if consts: ex.append('K:'+' '.join(consts))
    if meth: ex.append('m:'+' '.join(meth))
    if strs: ex.append('s:'+'|'.join(x[:32] for x in strs))
    if ub: ex.append('by:'+','.join(ub))
    if us: ex.append('uses:'+','.join(us))
    if ex: print('   '+'  '.join(ex)[:230])
