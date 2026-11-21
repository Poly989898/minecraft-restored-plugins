#!/usr/bin/env python3
"""Replaces the bodies of methods whose parameters were mis-split by the first Locals pass with the corrected decompilation.
Input: .work/dec2 (decompile of the fixed jar, pre-rename names), .work/locals-diag.jar.affected (class.method+desc list), maps.
Usage: patchparams.py [--dry]"""
import re,glob,os,sys
dry='--dry' in sys.argv
# --- renames: class chain, deobf method/field map, applied M/F maps
deob_m={}; 
for l in open('.work/deobf2.jar.map'):
    p=l.split()
    if p[0]=='M': deob_m[(p[1],p[2],p[3])]=p[4]
cls_ren=[]; m_ren=[]
for m in sorted(glob.glob('mappings/applied/*.map')):
    for l in open(m):
        p=l.split()
        if len(p)==3 and p[0]=='C': cls_ren.append((p[1],p[2]))
final={}
for a,b in cls_ren:
    for k,v in list(final.items()):
        if v==a: final[k]=b
    if a not in final: final[a]=b
def fin(c): return final.get(c,c)
def methren(cur_cls,name,ptypes):
    for m in sorted(glob.glob('mappings/applied/*.map')):
        for l in open(m):
            p=l.split()
            if len(p)==3 and p[0]=='M':
                mm=re.match(r'(\w+)\.(\w+)\((.*)\)',p[1])
                if mm and mm.group(2)==name and mm.group(3).replace(' ','')==','.join(ptypes): return p[2]
    return name
TOK=re.compile(r"""\"(?:[^\"\\\n]|\\.)*\"|'(?:[^'\\\n]|\\.)*'|//[^\n]*|/\*.*?\*/|\b[A-Za-z_$][\w$]*\b""",re.S)
def rename_tokens(text):
    return TOK.sub(lambda m: m.group(0) if m.group(0)[0] in '"/' else final.get(m.group(0),m.group(0)),text)
def desc_types(desc):
    from_ = re.findall(r'\[*(?:[BCDFIJSZ]|L[^;]+;)',desc.split(')')[0][1:])
    out=[]
    for t in from_:
        dims=t.count('['); b=t.lstrip('[')
        n={'B':'byte','C':'char','D':'double','F':'float','I':'int','J':'long','S':'short','Z':'boolean'}.get(b)
        if n is None:
            n=b[1:-1].split('/')[-1]; n=fin(n)
        out.append(n+'[]'*dims)
    return out
HDR=re.compile(r'^   (?!if |for |while |switch |try|else|catch|return|throw|synchronized \()(?:@\w+(?:\([^)]*\))? )*(?:(?:public|protected|private|static|final|synchronized|abstract|default) )*(?:[\w<>\[\], .?@]+? )?(\w+)\(')
def blocks(text):
    """yield (start_line, end_line, name, param_simple_types) for member methods (3-space indent)."""
    L=text.split('\n'); res=[]; i=0
    while i<len(L):
        m=HDR.match(L[i])
        if m and not L[i].rstrip().endswith(';'):
            j=i; hdr=L[i]
            while not hdr.rstrip().endswith('{') and j+1<len(L) and ';' not in hdr.split('(')[0]: 
                j+=1; hdr+=' '+L[j].strip()
                if hdr.rstrip().endswith('{') or hdr.rstrip().endswith(';'): break
            if hdr.rstrip().endswith('{'):
                # closing brace at same indent
                k=j+1
                while k<len(L) and not L[k].startswith('   }'): k+=1
                params=hdr[hdr.index('(')+1:hdr.rindex(')')]
                depth=0; cur=''; parts=[]
                for ch in params:
                    if ch in '<([': depth+=1
                    if ch in '>)]': depth-=1
                    if ch==',' and depth==0: parts.append(cur); cur=''
                    else: cur+=ch
                if cur.strip(): parts.append(cur)
                types=[]
                for pt in parts:
                    pt=re.sub(r'@[\w.]+(?:\([^)]*\))?\s*','',pt.strip()); pt=re.sub(r'\bfinal\s+','',pt)
                    t=pt.rsplit(' ',1)[0] if ' ' in pt else pt
                    t=re.sub(r'<.*?>','',t).replace('...','[]').strip(); t=t.split('.')[-1]
                    types.append(t)
                res.append((i,k,m.group(1),types,hdr.strip().startswith('public') or True))
                i=k
        i+=1
    return res
affected=[l.strip() for l in open('.work/locals-diag.jar.affected') if l.strip()]
ok=0; miss=[]
for a in affected:
    mm=re.match(r'litebans/(\w+)\.(<?\w+>?)(\(.*)',a)
    cls,name,desc=mm.group(1),mm.group(2),mm.group(3)
    src2=f'.work/dec2/litebans/{cls}.java'
    cur_cls=fin(cls); curp=f'src/main/java/litebans/{cur_cls}.java'
    if not os.path.exists(src2) or not os.path.exists(curp): miss.append((a,'file')); continue
    ptypes_old=desc_types(desc)  # already final-named
    new_name=deob_m.get((f'litebans/{cls}',name,desc),name)
    # new block from dec2 (pre-rename names): compare param types by simple old names
    old_types=[]
    for t in re.findall(r'\[*(?:[BCDFIJSZ]|L[^;]+;)',desc.split(')')[0][1:]):
        dims=t.count('['); b=t.lstrip('[')
        n={'B':'byte','C':'char','D':'double','F':'float','I':'int','J':'long','S':'short','Z':'boolean'}.get(b) or b[1:-1].split('/')[-1]
        old_types.append(n+'[]'*dims)
    t2=open(src2,encoding='utf-8').read(); bl2=blocks(t2)
    ctor = name=='<init>'
    cand=[b for b in bl2 if (b[2]==cls if ctor else b[2]==new_name) and b[3]==old_types]
    if len(cand)!=1: miss.append((a,'new block %d'%len(cand))); continue
    s2,e2=cand[0][0],cand[0][1]; newtext='\n'.join(t2.split('\n')[s2:e2+1])
    newtext=rename_tokens(newtext)
    cur=open(curp,encoding='utf-8').read(); blc=blocks(cur)
    cname=cur_cls if ctor else methren(cur_cls,new_name,ptypes_old)
    cand=[b for b in blc if b[2]==cname and b[3]==ptypes_old]
    if len(cand)!=1: miss.append((a,'cur block %d (%s %s)'%(len(cand),cname,ptypes_old))); continue
    s,e=cand[0][0],cand[0][1]; Lc=cur.split('\n')
    if not dry: open(curp,'w',encoding='utf-8').write('\n'.join(Lc[:s]+newtext.split('\n')+Lc[e+1:]))
    ok+=1
print('patched',ok,'missing',len(miss))
for m in miss[:40]: print(m)
