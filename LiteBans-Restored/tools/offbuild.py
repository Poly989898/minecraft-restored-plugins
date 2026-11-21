#!/usr/bin/env python3
"""Re-applies all class renames (mappings/applied/*.map, in order) to the files that are not part of the build (Fabric, mixin), working on identifier
tokens only (never inside string literals or comments). Base text is taken from git revision <rev> (the state before any renaming)."""
import re,glob,os,subprocess,sys
rev=sys.argv[1]
fab=[l.strip() for l in open('tools/fabric-files.txt') if l.strip()]
maps=sorted(glob.glob('mappings/applied/*.map'))
ren=[]
for m in maps:
    for l in open(m):
        p=l.split()
        if len(p)==3 and p[0]=='C': ren.append((p[1],p[2]))
TOK=re.compile(r"""\"(?:[^\"\\\n]|\\.)*\"|'(?:[^'\\\n]|\\.)*'|//[^\n]*|/\*.*?\*/|\b[A-Za-z_$][\w$]*\b""",re.S)
def apply(text,mp):
    def f(m):
        t=m.group(0)
        if t[0] in '"/': return t
        return mp.get(t,t)
    return TOK.sub(f,text)
# sequential rename chain -> final name for each original token
final={}
for a,b in ren:
    for k,v in list(final.items()):
        if v==a: final[k]=b
    if a not in final: final[a]=b
files=[]
for f in glob.glob('src/main/java/litebans/mixin/*.java'): files.append((f,f))
cur={}
for f in fab: cur[f[:-5]]=f
for f in fab:
    files.append(('src/main/java/litebans/'+f,'src/main/java/litebans/'+f))
n=0
for path,_ in files:
    base=os.path.basename(path)
    r=subprocess.run(['git','show','%s:LiteBans-Restored/%s'%(rev,path)],capture_output=True,text=True)
    if r.returncode: continue
    txt=apply(r.stdout,final)
    open(path,'w',encoding='utf-8').write(txt); n+=1
# rename files whose class was renamed
newlist=[]
for f in fab:
    old=f[:-5]; new=final.get(old,old)
    src='src/main/java/litebans/%s.java'%old; dst='src/main/java/litebans/%s.java'%new
    if new!=old and os.path.exists(src):
        os.rename(src,dst)
    newlist.append(new+'.java')
open('tools/fabric-files.txt','w').write('\n'.join(newlist)+'\n')
print('rewrote',n,'files')
