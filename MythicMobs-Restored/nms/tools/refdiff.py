#!/usr/bin/env python3
"""Compare external references (invoke/field/checkcast/new) of ORIGINAL vs REBUILT module classes. Run from nms/.
usage: refdiff.py [module ...]   (default: all 11)"""
import os, re, subprocess, collections, zipfile, tempfile, shutil, sys
ORIG = '../original/MythicMobs-5.14.0.jar'
ALL = ['v1_20_R1','v1_20_R3','v1_21_R1','v1_21_R3','v1_21_R4','v1_21_R5','v1_21_R6','v1_21_R7','v26_1','v26_2','v26_3']
ref = re.compile(r'^\s+\d+: (invoke\w+|getfield|putfield|getstatic|putstatic|new|checkcast|instanceof)\s+#\d+(?:,\s*\d+)?\s*//\s*(?:(?:Interface)?(?:Method|Field)|class)?\s*(.*)$')
noise = re.compile(r'(lambda\$|access\$|makeConcat|\$\$|\$SwitchMap\$|\$VALUES|\$values|InvokeDynamic|Intrinsics|valueOf|requireNonNull|Iterator)')
def collect(files):
    res = {}
    for i in range(0, len(files), 100):
        out = subprocess.run(['javap', '-p', '-c'] + files[i:i+100], capture_output=True, text=True).stdout
        cur = cnt = None
        for line in out.split('\n'):
            m = re.match(r'^(?:[\w ]+ )?(?:class|interface|enum) ([\w.$]+)', line)
            if m and not line.startswith(' '):
                if cur: res[cur] = cnt
                cur, cnt = m.group(1), collections.Counter(); continue
            m = ref.match(line)
            if m and cnt is not None:
                op, t = m.groups(); t = t.strip()
                if noise.search(t): continue
                if op.startswith('invoke') and op not in ('invokeinterface', 'invokestatic'): op = 'invoke'
                cnt[(op, t)] += 1
        if cur: res[cur] = cnt
    return res
for mod in (sys.argv[1:] or ALL):
    tmp = tempfile.mkdtemp()
    with zipfile.ZipFile(ORIG) as z:
        names = [n for n in z.namelist() if n.endswith('.class') and (f'io/lumine/mythic/core/volatilecode/{mod}/' in n or n.endswith(f'/VolatileCodeEnabled_{mod}.class'))]
        z.extractall(tmp, names)
    O = collect([os.path.join(tmp, n) for n in names])
    new = [os.path.join(dp, f) for dp, _, fs in os.walk(f'{mod}/build/classes/java/main') for f in fs if f.endswith('.class')]
    N = collect(new)
    diffs = []
    for c in sorted(set(O) | set(N)):
        a, b = O.get(c, collections.Counter()), N.get(c, collections.Counter())
        r, ad = a - b, b - a
        if r or ad: diffs.append((c, r, ad))
    print(f'== {mod}: classes orig {len(O)} new {len(N)}; classes with ref differences: {len(diffs)}')
    for c, r, ad in diffs:
        for (op, t), v in r.items(): print(f'   {c.split(".")[-1]:34} - {op:15} {t[:115]} x{v}')
        for (op, t), v in ad.items(): print(f'   {c.split(".")[-1]:34} + {op:15} {t[:115]} x{v}')
    shutil.rmtree(tmp)
