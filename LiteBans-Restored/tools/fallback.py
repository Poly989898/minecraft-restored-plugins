#!/usr/bin/env python3
"""Fallback names for classes that stay obfuscated after the manual waves: <Cluster><Role><Detail>; keeps them readable and unique.
Writes mappings/names.map (C lines). Fabric-only files (not built) are skipped."""
import re,glob,os,collections,json
files={os.path.basename(f)[:-5]:open(f,encoding='utf-8').read() for f in glob.glob('src/main/java/litebans/*.java')}
fab=set(l.strip()[:-5] for l in open('tools/fabric-files.txt') if l.strip())
lab=json.load(open('.work/clusters.json'))
taken=set(files); cnt=collections.Counter(); out=[]
CL={'kotlin':'Kotlin','compress':'Archive','hikari':'Pool','core':'Lite','fabric':'Fabric'}
def cap(x): return x[:1].upper()+x[1:]
def camel(c): return ''.join(p.capitalize() for p in c.lower().split('_') if p)
def uniq(n):
    base=n; i=2
    while n in taken: n='%s%d'%(base,i); i+=1
    taken.add(n); return n
# switch-map holders: named after the (single) referencing class when that class is already named
auto={}
for l in open('mappings/auto-classes.map'):
    p=l.split()
    if len(p)==3 and p[0]=='C': auto[p[1]]=p[2]
for n in sorted(files):
    if len(n)>3 or n in fab: continue
    s=files[n]; cl=CL.get(lab.get(n,'core'),'Lite')
    head=re.search(r'^(?:@\w+(?:\([^)]*\))?\s*)*(?:(?:public|final|abstract|static) )*(class|interface|enum|@interface) \w+(?: extends ([\w<>, ]+?))?(?: implements ([\w<>, ]+?))?\s*\{',s,re.M)
    kind=head.group(1) if head else 'class'; ext=head.group(2) if head else None; impl=head.group(3) if head else None
    if n in auto and re.match(r'^[A-Za-z]{4,}',auto[n]) and not re.search(r'\b[a-z]\w?SwitchMap|^[a-z]\w?[A-Z]',auto[n]): new=auto[n]
    elif '// $VF: synthetic class' in s:
        e=re.search(r'new int\[(\w+)\.values\(\)\.length\]',s); new=(e.group(1) if e else 'Enum')+'SwitchMap'
    elif kind=='enum':
        c=re.findall(r'^   ([A-Z][A-Z0-9_]{1,})[,;(]',s,re.M)[:3]
        new=cl+(''.join(camel(x) for x in c) if c else 'Enum')+'Enum'
    elif kind=='@interface': new=cl+'Annotation'
    elif kind=='interface':
        m=re.findall(r'^   [\w<>\[\], .?]+? ([a-zA-Z]\w*)\(',s,re.M)
        base=re.sub(r'_\d+$','',m[0]) if m else 'Marker'
        parent=(ext or '').split(',')[0].strip().split('<')[0]
        new=cl+(cap(base) if base else 'Marker')+'Interface' if not (parent and len(parent)>3) else cl+parent+'Sub'
    elif ext and re.search(r'(Exception|Error)\b',ext): new=cl+ext.split('<')[0]+'Sub'
    elif ext and len(ext.split('<')[0])>3: new=cl+ext.split('<')[0]+'Sub'
    elif impl and len(impl.split(',')[0].strip().split('<')[0])>3: new=cl+impl.split(',')[0].strip().split('<')[0]+'Impl'
    elif re.search(r'static final \w+ \w+\(',s) and not re.search(r'^   (private |public )?(final )?[A-Z]\w+ \w+_f;',s,re.M): new=cl+'Util'
    elif 'toString' in s and re.search(r'private final',s): new=cl+'Data'
    elif s.count('\n')<=6: new=cl+'Stub'
    else: new=cl+'Helper'
    new=re.sub(r'[^A-Za-z0-9_]','',new)
    out.append('C %s %s'%(n,uniq(new)))
open('mappings/names.map','w').write('# fallback names (tools/fallback.py)\n'+'\n'.join(out)+'\n')
print(len(out)); print('\n'.join(out[:40]))
