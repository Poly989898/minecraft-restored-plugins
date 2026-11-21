#!/usr/bin/env python3
"""After patchparams: patched bodies still use pre-rename member names (fields/methods). For each javac `cannot find symbol` on a variable/method
whose location class (or a superclass) has a rename in mappings/applied (F / M lines), rewrite the token at the error position."""
import re,subprocess,glob,os,sys
G=subprocess.run("ls -d ~/.gradle/wrapper/dists/gradle-9.9.1-bin/*/gradle-9.9.1/bin/gradle",shell=True,capture_output=True,text=True).stdout.split()[0]
F={}; M={}
for m in sorted(glob.glob('mappings/applied/*.map')):
    for l in open(m):
        p=l.split()
        if len(p)==3 and p[0]=='F':
            c,n=p[1].split('.'); F[(c,n)]=p[2]
        elif len(p)==3 and p[0]=='M':
            mm=re.match(r'(\w+)\.(\w+)\(',p[1])
            if mm: M[(mm.group(1),mm.group(2))]=p[2]
def chain(c):
    out=[c]
    for _ in range(12):
        f='src/main/java/litebans/%s.java'%c
        if not os.path.exists(f): break
        s=open(f,encoding='utf-8').read()
        m=re.search(r'(?:class|interface|enum) %s(?:<[^>]*>)? extends ([\w, ]+?)(?: implements ([\w, ]+))?\s*\{'%c,s) or re.search(r'class %s[^{]* implements ([\w, ]+)\{'%c,s)
        parents=[]
        if m:
            for g in m.groups():
                if g: parents+= [x.strip() for x in g.split(',')]
        out+=parents
        if not parents: break
        c=parents[0]
    return out
def run():
    out=subprocess.run([G,'compileJava','-q'],capture_output=True,text=True).stderr
    return out
total=0
for rnd in range(int(sys.argv[1]) if len(sys.argv)>1 else 6):
    out=run(); fixes=0; edits={}
    for b in re.split(r'\n(?=\s*/home/)',out):
        b=b.strip()
        m=re.match(r'(\S+?\.java):(\d+): error: cannot find symbol\n([^\n]*)\n(\s*)\^\n\s*symbol:\s+(variable|method) (\w+)(?:\([^)]*\))?\n\s*location:\s+(?:variable \w+ of type |class |interface )(\w+)',b)
        if not m: continue
        path,line,code,caretind,kind,name,loc=m.group(1),int(m.group(2)),m.group(3),m.group(4),m.group(5),m.group(6),m.group(7)
        col=len(caretind)
        new=None
        for c in chain(loc):
            new=(F.get((c,name)) if kind=='variable' else M.get((c,name)))
            if new: break
        if not new: continue
        edits.setdefault(path,[]).append((line,col,name,new))
    for path,es in edits.items():
        L=open(path,encoding='utf-8').read().split('\n')
        for line,col,name,new in sorted(set(es),key=lambda x:(-x[0],-x[1])):
            l=L[line-1]
            if l[col:col+len(name)]==name: L[line-1]=l[:col]+new+l[col+len(name):]; fixes+=1
            else:
                i=l.find(name)
                if i>=0: L[line-1]=l[:i]+new+l[i+len(name):]; fixes+=1
        open(path,'w',encoding='utf-8').write('\n'.join(L))
    total+=fixes
    print('round',rnd,'errors',len(re.findall(r'error:',out))//2,'fixed',fixes,flush=True)
    if not fixes: break
open('/tmp/claude-1000/memberfix.log','w').write(out)
