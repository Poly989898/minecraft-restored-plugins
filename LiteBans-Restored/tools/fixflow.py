#!/usr/bin/env python3
"""Driver for the flow-phase errors left after the decompiler. The original is Kotlin (no checked exceptions), so Java's checked-exception
rules fail on the decompiled code. Policy (no `throws` propagation, behaviour identical to the Kotlin bytecode):
  * `unreported exception X`            -> wrap the enclosing method body in try { } catch (X e) { throw SneakyThrow.rethrow(e); }
  * `exception X is never thrown in try`-> catch (Throwable t) { if (!(t instanceof X)) rethrow; X e = (X) t; ... }
  * `variable v might not have been initialized` -> default value at the declaration (tools/initfix.py)
Runs gradle until clean or no progress."""
import re,subprocess,sys,glob,os
G=r'D:\Tools\gradle-9.9.1\bin\gradle.bat'
IMPORTS={'SQLException':'java.sql.SQLException','SQLTransientException':'java.sql.SQLTransientException','SQLFeatureNotSupportedException':'java.sql.SQLFeatureNotSupportedException','SQLTransientConnectionException':'java.sql.SQLTransientConnectionException','IOException':'java.io.IOException','FileNotFoundException':'java.io.FileNotFoundException','UnsupportedEncodingException':'java.io.UnsupportedEncodingException','TimeoutException':'java.util.concurrent.TimeoutException','ExecutionException':'java.util.concurrent.ExecutionException','GeneralSecurityException':'java.security.GeneralSecurityException','NoSuchAlgorithmException':'java.security.NoSuchAlgorithmException','URISyntaxException':'java.net.URISyntaxException','MalformedURLException':'java.net.MalformedURLException','UnknownHostException':'java.net.UnknownHostException','SocketException':'java.net.SocketException','InvocationTargetException':'java.lang.reflect.InvocationTargetException','CertificateException':'java.security.cert.CertificateException','KeyStoreException':'java.security.KeyStoreException','InvalidKeyException':'java.security.InvalidKeyException','KeyManagementException':'java.security.KeyManagementException','UnrecoverableKeyException':'java.security.UnrecoverableKeyException','ProtocolException':'java.net.ProtocolException','SQLClientInfoException':'java.sql.SQLClientInfoException','SQLTimeoutException':'java.sql.SQLTimeoutException','SQLIntegrityConstraintViolationException':'java.sql.SQLIntegrityConstraintViolationException','SQLNonTransientException':'java.sql.SQLNonTransientException','SQLRecoverableException':'java.sql.SQLRecoverableException','BatchUpdateException':'java.sql.BatchUpdateException','SSLException':'javax.net.ssl.SSLException','SSLHandshakeException':'javax.net.ssl.SSLHandshakeException','ZipException':'java.util.zip.ZipException','DataFormatException':'java.util.zip.DataFormatException','UncheckedIOException':None,'NoSuchMethodException':None,'IllegalAccessException':None,'InstantiationException':None,'ClassNotFoundException':None,'InterruptedException':None}
PKGS=['java.io','java.net','java.nio.charset','java.nio','java.nio.file','java.sql','java.security','java.security.cert','java.util','java.util.concurrent','java.util.zip','java.lang.reflect','javax.net.ssl','java.text','java.time','javax.crypto','javax.naming','java.util.regex','java.lang']
_cache={}
def resolve(name):
    if name in _cache: return _cache[name]
    for pk in PKGS:
        r=subprocess.run(['javap',pk+'.'+name],capture_output=True,text=True)
        if r.returncode==0 and 'Exception' in r.stdout.split('{')[0]+name or (r.returncode==0 and 'class' in r.stdout):
            _cache[name]=pk+'.'+name; return _cache[name]
    _cache[name]=None; return None
def auto_imports_from_log(out):
    n=0
    for b in re.split(r'\n(?=\s*(?:/home/|[A-Za-z]:\\))',out):
        m=re.match(r'\s*(\S+?\.java):\d+: error: cannot find symbol\n[^\n]*\n[^\n]*\n\s*symbol:\s+class (\w+)',b)
        if m:
            fq=resolve(m.group(2))
            if fq and not fq.startswith('java.lang.') :
                f=m.group(1); s=open(f,encoding='utf-8').read()
                if 'import %s;'%fq not in s:
                    open(f,'w',encoding='utf-8').write(s.replace('package litebans;\n','package litebans;\n\nimport %s;'%fq,1)); n+=1
    return n
def ensure_imports():
    for f in glob.glob('src/main/java/litebans/*.java'):
        s=open(f,encoding='utf-8').read(); n=s
        for k,v in IMPORTS.items():
            if v and re.search(r'catch \([^)]*\b%s\b'%k,s) and ('import %s;'%v) not in s and not re.search(r'import [\w.]+\.%s;'%k,s):
                n=n.replace('package litebans;\n','package litebans;\n\nimport %s;'%v,1)
        if n!=s: open(f,'w',encoding='utf-8').write(n)
HDR=re.compile(r'^   (?:\)|(?!if |for |while |switch |try|else|catch|return|throw|synchronized \()\S.*\))\s*(throws [\w., ]+)?\s*\{\s*$')
JDKSUB={'FileNotFoundException':'IOException','MalformedURLException':'IOException','UnknownHostException':'IOException','SocketException':'IOException','UnsupportedEncodingException':'IOException','SQLTransientException':'SQLException','SQLFeatureNotSupportedException':'SQLException','SQLTransientConnectionException':'SQLTransientException','SQLClientInfoException':'SQLException','SQLTimeoutException':'SQLTransientException','SQLIntegrityConstraintViolationException':'SQLNonTransientException','SQLNonTransientException':'SQLException','SQLRecoverableException':'SQLException','BatchUpdateException':'SQLException','ProtocolException':'IOException','SSLException':'IOException','ZipException':'IOException'}
def parent_of(n):
    if n in JDKSUB: return JDKSUB[n]
    f='src/main/java/litebans/%s.java'%n
    if os.path.exists(f):
        m=re.search(r'class %s extends (\w+)'%re.escape(n),open(f,encoding='utf-8').read())
        return m.group(1) if m else None
    return None
def ancestors(n):
    r=[]; p=parent_of(n)
    while p and p not in r: r.append(p); p=parent_of(p)
    return r
def wrap_methods(todo):   # {(cls): {headerLineIdx: set(exc)}}
    n=0
    for cls,hs in todo.items():
        p='src/main/java/litebans/%s.java'%cls; src=open(p,encoding='utf-8').read().split('\n')
        for i in sorted(hs,reverse=True):
            excs=sorted(hs[i])
            SUB={'FileNotFoundException':'IOException','MalformedURLException':'IOException','UnknownHostException':'IOException','SocketException':'IOException','ProtocolException':'IOException','SSLException':'IOException','ZipException':'IOException','SSLHandshakeException':'SSLException','UnsupportedEncodingException':'IOException','SQLTransientException':'SQLException','SQLFeatureNotSupportedException':'SQLException','SQLTransientConnectionException':'SQLException'}
            excs=[e for e in excs if not any(a in excs for a in ancestors(e))]
            j=i; depth=0; started=False
            while True:
                depth+=src[j].count('{')-src[j].count('}')
                if '{' in src[j]: started=True
                if started and depth==0: break
                j+=1
            if src[i].lstrip().startswith(('static {',)) or re.search(r'\b(this|super)\(',src[i+1]): continue
            if 'SneakyThrow' in src[j-1] and 'try {' in src[i+1]:
                # already wrapped: extend catch types
                pass
            catch='      } catch (%s e) {\n         throw SneakyThrow.<RuntimeException>rethrow(e);\n      }'%' | '.join(excs)
            src[j]=catch+'\n   }'
            src[i]=src[i]+'\n      try {'
            n+=1
        open(p,'w',encoding='utf-8').write('\n'.join(src))
    return n
def fix_never_thrown(items):
    n=0
    for cls,line,exc in items:
        p_=p='src/main/java/litebans/%s.java'%cls; src=open(p,encoding='utf-8').read().split('\n'); l=src[line-1]
        mm=re.match(r'^(\s*)\} catch \(([\w ]+(?: \| [\w ]+)+) (\w+)\) \{\s*$',l)
        if mm and exc in [t.strip() for t in mm.group(2).split('|')]:
            ind,var=mm.group(1),mm.group(3); types=[t.strip() for t in mm.group(2).split('|')]
            cond=' || '.join('%s_t instanceof %s'%(var,t) for t in types)
            src[line-1]='%s} catch (Throwable %s_t) {\n%s   if (!(%s)) {\n%s      throw SneakyThrow.<RuntimeException>rethrow(%s_t);\n%s   }\n%s   Exception %s = (Exception) %s_t;'%(ind,var,ind,cond,ind,var,ind,ind,var,var)
            open(p_,'w',encoding='utf-8').write('\n'.join(src)); n+=1; continue
        m=re.match(r'^(\s*)\} catch \((%s) (\w+)\) \{\s*$'%re.escape(exc),l)
        if not m: print('cannot rewrite catch',cls,line,l.strip()); continue
        ind,var=m.group(1),m.group(3)
        src[line-1]='%s} catch (Throwable %s_t) {\n%s   if (!(%s_t instanceof %s)) {\n%s      throw SneakyThrow.<RuntimeException>rethrow(%s_t);\n%s   }\n%s   %s %s = (%s) %s_t;'%(ind,var,ind,var,exc,ind,var,ind,ind,exc,var,exc,var)
        open(p,'w',encoding='utf-8').write('\n'.join(src)); n+=1
    return n
for it in range(int(sys.argv[1]) if len(sys.argv)>1 else 20):
    ensure_imports()
    out=subprocess.run([G,'compileJava','-q'],capture_output=True,text=True).stderr
    open(os.path.join(os.environ.get('TMPDIR','D:/Tools/tmp'),'fixflow.log'),'w').write(out)
    n=len(re.findall(r'error:',out))//2
    print('iter',it,'errors',n,flush=True)
    if n==0: break
    todo={}; never=[]; seen=set(); lam=[]
    for b in re.split(r'\n(?=\s*(?:/home/|[A-Za-z]:\\))',out):
        b=b.strip()
        m=re.match(r'.*?/litebans/(\w+)\.java:(\d+): error: unreported exception ([\w.]+);[^\n]*\n([^\n]*)',b,re.S)
        if m:
            cls,line,exc,code=m.group(1),int(m.group(2)),m.group(3).split('.')[-1],m.group(4)
            if (cls,line,exc) in seen: continue
            seen.add((cls,line,exc))
            if '->' in code: lam.append((cls,line,exc)); continue
            src=open('src/main/java/litebans/%s.java'%cls,encoding='utf-8').read().split('\n')
            i=line-1
            while i>=0 and not HDR.match(src[i]): i-=1
            if i>=0: todo.setdefault(cls,{}).setdefault(i,set()).add(exc)
        m=re.match(r'.*?/litebans/(\w+)\.java:(\d+): error: exception ([\w.]+) is never thrown in body of corresponding try statement',b,re.S)
        if m and (m.group(1),int(m.group(2)),m.group(3).split('.')[-1]) not in never: never.append((m.group(1),int(m.group(2)),m.group(3).split('.')[-1]))
    nf=auto_imports_from_log(out)
    for b in re.split(r'\n(?=\s*(?:/home/|[A-Za-z]:\\))',out):
        m=re.match(r'\s*(\S+?\.java):(\d+): error: variable (\w+) not initialized in the default constructor',b)
        if m:
            src=open(m.group(1),encoding='utf-8').read().split('\n'); i=int(m.group(2))-1
            if ' final ' in src[i]: src[i]=src[i].replace(' final ',' ',1); open(m.group(1),'w',encoding='utf-8').write('\n'.join(src)); nf+=1
    changed=nf+(fix_never_thrown(sorted(never,key=lambda x:(x[0],-x[1]))) if never else wrap_methods(todo))
    if lam: print('lambda sites need manual care:',lam)
    r=subprocess.run(['python3','tools/initfix.py',os.path.join(os.environ.get('TMPDIR','D:/Tools/tmp'),'fixflow.log')],capture_output=True,text=True)
    if 'initialised 0' not in r.stdout: changed+=1
    if not changed: print('no progress'); break
print(re.findall(r'error: ([^\n]*)',out)[:6])
