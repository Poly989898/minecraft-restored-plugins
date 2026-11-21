#!/usr/bin/env python3
"""Whitespace-only re-indentation by brace depth (3 spaces, existing style); continuation lines keep their relative offset."""
import re,sys,glob,os
fab=set(l.strip() for l in open('tools/fabric-files.txt') if l.strip())
def scan(line, inblock):
    """returns (opens, closes, first_is_close, ends_char, inblock) ignoring strings/chars/comments"""
    i=0;n=len(line);opens=closes=0;last='';first=None
    while i<n:
        c=line[i]
        if inblock:
            if line.startswith('*/',i): inblock=False;i+=2;continue
            i+=1;continue
        if line.startswith('//',i): break
        if line.startswith('/*',i): inblock=True;i+=2;continue
        if c=='"':
            if line.startswith('"""',i): return None
            i+=1
            while i<n and line[i]!='"':
                i+=2 if line[i]=='\\' else 1
            i+=1; last='"'
            if first is None: first='s'
            continue
        if c=="'":
            i+=1
            while i<n and line[i]!="'":
                i+=2 if line[i]=='\\' else 1
            i+=1; last="'"
            if first is None: first='s'
            continue
        if c=='{': opens+=1
        elif c=='}': closes+=1
        if not c.isspace():
            if first is None: first=c
            last=c
        i+=1
    return opens,closes,first,last,inblock
def process(src):
    lines=src.split('\n'); out=[]; depth=0; inblock=False
    casestack=[]  # per open brace: whether inside a case body
    base_old=base_new=0; cont=False; pending_case=False
    for ln in lines:
        s=ln.strip()
        if not s: out.append(''); continue
        old=len(ln)-len(ln.lstrip(' '))
        r=scan(ln,inblock)
        if r is None: return None
        opens,closes,first,last,inblock_next=r
        if inblock:   # inside block comment: keep relative
            out.append(' '*max(0,base_new+(old-base_old))+s if False else ln); inblock=inblock_next; continue
        d=depth
        if first=='}': d-=1
        extra=0
        iscase=bool(re.match(r'^(case\b.*|default\s*):?(\s*->.*)?$',s)) and (s.endswith(':') or s.startswith('default') or s.startswith('case'))
        cs=casestack[:-1] if (iscase or first=='}') else casestack
        extra=sum(1 for x in cs if x)
        if first=='}' and casestack: pass
        if cont:
            new=max(base_new+(old-base_old),0)
        else:
            new=3*(d+extra)
            base_old,base_new=old,new
        out.append(' '*new+s)
        # update state
        for _ in range(closes):
            if casestack: casestack.pop()
            depth-=1
        for _ in range(opens): casestack.append(False); depth+=1
        if iscase and s.endswith(':') and casestack: casestack[-1]=True
        # continuation if line does not end a statement
        e=last
        cont = e not in (';','{','}','',':') and not s.startswith('@') and not (e==')' and False)
        if s.startswith('//') or s.startswith('*') or s.startswith('/*'): cont=False
        if s.endswith(':') and iscase: cont=False
        inblock=inblock_next
    return '\n'.join(out)
changed=0
for f in sorted(glob.glob('src/main/java/litebans/*.java')):
    if os.path.basename(f) in fab: continue
    s=open(f,encoding='utf-8').read()
    r=process(s)
    if r is None or r==s: continue
    if [x.strip() for x in r.split('\n')]!=[x.strip() for x in s.split('\n')]: print('skip',f); continue
    open(f,'w',encoding='utf-8').write(r); changed+=1
print('reindented',changed)
