#!/usr/bin/env python3
"""Compact error list for one file: line, message, source line."""
import re,sys
t=open(sys.argv[1]).read(); want=sys.argv[2]
for b in re.split(r'\n(?=/home/)',t):
    m=re.match(r'.*?/litebans/(\w+)\.java:(\d+): error: ([^\n]*)\n([^\n]*)',b,re.S)
    if m and m.group(1)==want:
        extra=' | '.join(x.strip() for x in b.split('\n')[3:6] if x.strip() and not x.strip().startswith('^'))[:150]
        print(f"{m.group(2)}: {m.group(3)[:110]}\n     {m.group(4).strip()[:140]}\n     {extra}")
