#!/usr/bin/env python3 -I
"""Fixes decompiler raw-type errors reported by javac (usage: python3 -I tools/fixraw.py <javac-output.txt>):
  for (T v : expr) {                 ->  for (Object v_o : expr) { T v = (T) v_o;
  X[] v = list.toArray(new X[0]);    ->  X[] v = (X[]) list.toArray(new X[0]);"""
import re, sys, collections
errs = collections.defaultdict(list)
lines = open(sys.argv[1], encoding='utf-8', errors='replace').read().split('\n')
for l in lines:
    m = re.match(r'(src/\S+\.java):(\d+): error: incompatible types: (Object|Object\[\]) cannot be converted to ([\w.<>\[\]?, ]+)$', l)
    if m: errs[m.group(1)].append((int(m.group(2)), m.group(3), m.group(4)))
n = 0
for f, items in errs.items():
    src = open(f, encoding='utf-8').read().split('\n')
    for ln, kind, ty in sorted(items, reverse=True):
        s = src[ln-1]
        if kind == 'Object':
            m = re.match(r'(\s*)for \((?:final )?([\w.<>?, \[\]]+?) (\w+) : (.*)\) \{\s*$', s)
            if m:
                ind, t, v, e = m.groups()
                src[ln-1] = f'{ind}for (Object {v}_o : {e}) {{ {t} {v} = ({t}) {v}_o;'
                n += 1
        else:
            m = re.search(r'= ((?:\w|\.)+\.toArray\(new [\w.]+\[0\]\));', s)
            if m:
                src[ln-1] = s.replace(m.group(0), f'= ({ty}) {m.group(1)};'); n += 1
    open(f, 'w', encoding='utf-8').write('\n'.join(src))
print('fixed', n)
