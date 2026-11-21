#!/usr/bin/env python3
"""Assigns each still-obfuscated class to a library/core cluster (kotlin, compress, hikari, core, fabric) from strings, supertypes and neighbours."""
import re,glob,os,collections,sys,json
files={os.path.basename(f)[:-5]:open(f,encoding='utf-8').read() for f in glob.glob('src/main/java/litebans/*.java')}
fab=set(l.strip()[:-5] for l in open('tools/fabric-files.txt') if l.strip())
names=set(files)
def toks(s): return set(re.findall(r'\b([A-Za-z_]\w*)\b',re.sub(r'"(?:[^"\\]|\\.)*"','""',s)))&names
uses={n:toks(s)-{n} for n,s in files.items()}
usedby=collections.defaultdict(set)
for n,u in uses.items():
    for x in u: usedby[x].add(n)
short=lambda n:len(n)<=3
seed={}
def seeds(label,xs):
    for x in xs:
        if x in files: seed[x]=label
seeds('kotlin','Intrinsics StringsKt CollectionsKt Unit ArraysKt ArraysKt___ArraysKt StandardKt TuplesKt Pair Triple UInt UByte ULong KotlinFunction Function1 Function0 Lazy SynchronizedLazyImpl InitializedLazyImpl UnsafeLazyImpl RangesKt IntProgression SetsKt___SetsKt SetsJVM CollectionsKt___CollectionsKt Regex KotlinRandom KMappedMarker EnumEntries EnumEntriesList LazyThreadSafetyMode DefaultConstructorMarker KotlinCharsets CloseableKt NotImplementedError FastList'.split())
seeds('compress','ArchiveInputStream ArArchiveInputStream ArjArchiveInputStream CpioArchiveInputStream DumpArchiveInputStream TarArchiveInputStream ZipArchiveInputStream JarArchiveInputStream ArchiveEntry ZipShort ZipLong ZipEightByteInteger ZipArchiveEntry ZipExtraField ExtraFieldUtils ZipMethod NameSource HashAlgorithm ZipFeature X0017StrongEncryptionExtraField TarArchiveStructSparse SegmentType BitInputStream ZipLocalFileHeader UnicodeExtraField AbstractUnparseableExtraField ArchiveException ZipEncodingHelper UnrecognizedExtraField UnparseableExtraField'.split())
seeds('hikari','HikariConfig HikariDataSource HikariPool PoolBase PoolEntry ConcurrentBag IConcurrentBagEntry ProxyConnection ClockSource SuspendResumeLock PoolLogger ProxyResultSet ProxyPreparedStatement ProxyStatement PoolThreadFactory PoolInitializationException TransactionIsolation ProxyFactory LeakTask EvictionResult PropertyElf'.split())
for n,s in files.items():
    if n in seed or not short(n): continue
    if 'kotlin.' in s or 'Intrinsics.' in s and 'extends' not in s and False: seed.setdefault(n,'kotlin')
hubs={'Intrinsics','SneakyThrow','Platform','ConfigModule','DatabaseModule','DefaultConstructorMarker','StringsKt','CollectionsKt','Unit','Settings','PluginModule','AltAccountPunisher'}
label=dict(seed)
for n in files:
    if short(n) and n in fab: label[n]='fabric'
for it in range(6):
    new={}
    for n in files:
        if not short(n) or n in label: continue
        vote=collections.Counter()
        for x in (uses[n]|usedby[n])-hubs:
            if x in label: vote[label[x]]+=1
        s=files[n]
        if re.search(r'\bjava\.sql|PreparedStatement|ResultSet|Connection\b',s) and not 'DatabaseSession' in s: vote['hikari']+=1
        if vote:
            best=vote.most_common(2)
            new[n]=best[0][0] if len(best)==1 or best[0][1]>best[1][1] else 'core'
    if not new: break
    label.update(new)
for n in files:
    if short(n) and n not in label: label[n]='core'
json.dump({n:label[n] for n in files if short(n)},open('.work/clusters.json','w'))
print(collections.Counter(label[n] for n in files if short(n)))
