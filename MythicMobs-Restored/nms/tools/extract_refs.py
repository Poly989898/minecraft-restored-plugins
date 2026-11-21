#!/usr/bin/env python3
"""Extract external (server/Paper) references from the ORIGINAL jar's compiled bytecode of one NMS module.
usage: extract_refs.py ORIGINAL.jar MODULE(e.g. v1_20_R1) OUT.txt
Output lines: KIND<TAB>owner<TAB>name<TAB>desc   KIND: M virtual/special, S static, I interface, F getfield, P putfield,
              G getstatic, T putstatic, C class (new/checkcast/instanceof/anewarray)"""
import re, subprocess, sys, tempfile, zipfile, os, shutil
jar, mod, out = sys.argv[1:4]
PFX = ('net/minecraft/', 'org/bukkit/craftbukkit/', 'ca/spottedleaf/', 'io/papermc/paper/', 'com/destroystokyo/paper/', 'org/spigotmc/')
tmp = tempfile.mkdtemp()
names = []
with zipfile.ZipFile(jar) as z:
    for n in z.namelist():
        if n.endswith('.class') and (f'io/lumine/mythic/core/volatilecode/{mod}/' in n or n.endswith(f'/VolatileCodeEnabled_{mod}.class')):
            z.extract(n, tmp); names.append(os.path.join(tmp, n))
refs = set()
rx = re.compile(r'^\s+\d+: (invoke\w+|getfield|putfield|getstatic|putstatic|new|checkcast|instanceof|anewarray)\s+#\d+(?:,\s*\d+)?\s*//\s*(?:(?:Interface)?(?:Method|Field)|class)?\s*(.*)$')
for i in range(0, len(names), 100):
    txt = subprocess.run(['javap', '-p', '-c'] + names[i:i+100], capture_output=True, text=True).stdout
    for line in txt.split('\n'):
        m = rx.match(line)
        if not m: continue
        op, tgt = m.groups()
        tgt = tgt.strip()
        if op in ('new', 'checkcast', 'instanceof', 'anewarray'):
            c = tgt.strip('"')
            if c.startswith('['): c = re.sub(r'^\[+L?', '', c).rstrip(';')
            if c.startswith(PFX): refs.add(('C', c, '', ''))
            continue
        m2 = re.match(r'^("?[\w/$\[;]+"?)\.(.+):(.*)$', tgt)
        if not m2: continue
        owner, name, desc = m2.group(1).strip('"'), m2.group(2).strip('"'), m2.group(3)
        if owner.startswith('['): continue
        if not owner.startswith(PFX): continue
        kind = {'invokestatic': 'S', 'invokeinterface': 'I', 'getfield': 'F', 'putfield': 'P', 'getstatic': 'G', 'putstatic': 'T'}.get(op, 'M')
        refs.add((kind, owner, name, desc))
with open(out, 'w', encoding='utf-8') as f:
    for r in sorted(refs): f.write('\t'.join(r) + '\n')
shutil.rmtree(tmp)
print(mod, len(names), 'classes,', len(refs), 'refs')
