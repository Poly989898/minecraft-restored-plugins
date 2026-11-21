"""Rewrite underscore-mangled Kotlin names back to hyphenated JVM names inside .class files.
usage: fixnames.py <orig_jar_extracted_dir> <classes_dir>
Names come from the original jar: every method/field name containing '-' there.
"""
import os,sys,struct,re,subprocess

def read_cp(data):
    magic,minor,major,count=struct.unpack('>IHHH',data[:10])
    i=10;entries=[None]*count;idx=1
    while idx<count:
        tag=data[i]
        if tag==1:
            ln=struct.unpack('>H',data[i+1:i+3])[0]
            entries[idx]=(1,data[i+3:i+3+ln]);i+=3+ln
        elif tag in(3,4):entries[idx]=(tag,data[i+1:i+5]);i+=5
        elif tag in(5,6):entries[idx]=(tag,data[i+1:i+9]);i+=9;idx+=1
        elif tag in(7,8,16,19,20):entries[idx]=(tag,data[i+1:i+3]);i+=3
        elif tag in(9,10,11,12,17,18):entries[idx]=(tag,data[i+1:i+5]);i+=5
        elif tag==15:entries[idx]=(tag,data[i+1:i+4]);i+=4
        else:raise ValueError('bad tag %d'%tag)
        idx+=1
    return (magic,minor,major,count),entries,i

def rewrite(data,mapping):
    hdr,entries,end=read_cp(data)
    out=bytearray(struct.pack('>IHHH',*hdr));changed=0
    for e in entries[1:]:
        if e is None: continue   # second slot of long/double
        tag,b=e
        if tag==1:
            s=b
            if s in mapping: s=mapping[s];changed+=1
            out+=bytes([1])+struct.pack('>H',len(s))+s
        else: out+=bytes([tag])+b
    out+=data[end:]
    return bytes(out),changed

def hyphen_names(orig_dir):
    names=set()
    for dp,_,fs in os.walk(orig_dir):
        for f in fs:
            if not f.endswith('.class'):continue
            data=open(os.path.join(dp,f),'rb').read()
            try:_,entries,_=read_cp(data)
            except Exception:continue
            for e in entries[1:]:
                if e and e[0]==1:
                    try:s=e[1].decode('utf8')
                    except Exception:continue
                    if '-' in s and re.fullmatch(r'[a-z_$][\w$]*-(?:impl\d*|(?=[A-Za-z0-9]*\d|[A-Za-z0-9]*[a-z][A-Z]|[A-Za-z0-9]*[A-Z][a-z]+[A-Z])[A-Za-z0-9]{7})',s) and 'Metrics' not in s: names.add(s)
    return names

if __name__=='__main__':
    orig,classes=sys.argv[1],sys.argv[2]
    hn=hyphen_names(orig)
    mapping={n.replace('-','_').encode():n.encode() for n in hn}
    print('hyphen names in original:',len(hn))
    tot=0;files=0
    for dp,_,fs in os.walk(classes):
        for f in fs:
            if not f.endswith('.class'):continue
            p=os.path.join(dp,f);d=open(p,'rb').read()
            nd,c=rewrite(d,mapping)
            if c: open(p,'wb').write(nd);tot+=c;files+=1
    print('renamed',tot,'entries in',files,'classes')
