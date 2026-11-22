#!/usr/bin/env python3
"""Applies mappings/names.map with Renamer; on compile errors drops offending lines (whole override families by original name+params) and retries."""
import subprocess,re,sys,os
def sh(c): return subprocess.run(c,shell=True,capture_output=True,text=True)
G=r'D:\Tools\gradle-9.9.1\bin\gradle.bat'
def key(l):
    m=re.match(r'^M \w+\.(\w+)\((.*?)\) (\w+)$',l)
    return (m.group(1),m.group(2)) if m else None
for attempt in range(40):
    sh('git ls-files --others --exclude-standard src/main/java | xargs -r rm -f; git checkout HEAD -- src/main/java tools/fabric-files.txt')
    r=sh('java --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED tools/Renamer.java .work/cp.txt src/main/java stubs mappings/names.map')
    c=sh(G+' compileJava -q 2>&1')
    errs=sorted(set(l for l in c.stdout.split('\n')+c.stderr.split('\n') if ' error:' in l))
    if not errs: print('applied after',attempt,'retries'); break
    lines=open('mappings/names.map').read().split('\n'); dropkeys=set(); dropidx=set()
    def drop_by_class_newname(cl,nm):
        for i,l in enumerate(lines):
            if re.match(r'^[MF] %s\.'%cl,l) and l.split()[-1]==nm:
                dropidx.add(i); k=key(l)
                if k: dropkeys.add(k)
    for e in errs:
        m=re.search(r'(\w+)\.java:(\d+): error: (.*)',e)
        if not m: continue
        cl,ln,msg=m.group(1),int(m.group(2)),m.group(3)
        src=open('src/main/java/litebans/%s.java'%cl,encoding='utf-8').read().split('\n')
        line=src[ln-1] if ln-1<len(src) else ''
        mm=re.search(r'method (\w+)\(.*\) is already defined in class (\w+)',msg) or re.search(r'variable (\w+) is already defined in class (\w+)',msg)
        if mm: drop_by_class_newname(mm.group(2),mm.group(1)); continue
        mm=re.search(r'(\w+)\(.*\) in (\w+) cannot (?:override|implement) (\w+)\(.*\) in (\w+)',msg)
        if mm:
            drop_by_class_newname(mm.group(2),mm.group(1)); drop_by_class_newname(mm.group(4),mm.group(1)); continue
        mm=re.search(r'name clash: (\w+)\(.*\) in (\w+) and (\w+)\(.*\) in (\w+)',msg)
        if mm:
            for n in (mm.group(1),mm.group(3)):
                drop_by_class_newname(mm.group(2),n); drop_by_class_newname(mm.group(4),n)
            continue
        if 'does not override or implement' in msg:
            # method name on the following line(s)
            for k in range(ln-1,min(ln+3,len(src))):
                t=re.search(r'(\w+)\(',src[k])
                if t and not src[k].strip().startswith('@'): drop_by_class_newname(cl,t.group(1)); break
            continue
        mm=re.search(r'(\w+) is not abstract and does not override abstract method (\w+)\(.*\) in (\w+)',msg)
        if mm:
            drop_by_class_newname(mm.group(3),mm.group(2)); continue
    for i,l in enumerate(lines):
        if key(l) in dropkeys and key(l): dropidx.add(i)
    print('attempt',attempt,'errors',len(errs),'drop',len(dropidx))
    if not dropidx:
        print('\n'.join(errs[:8])); sys.exit(2)
    open('mappings/names.map','w').write('\n'.join(l for i,l in enumerate(lines) if i not in dropidx))
