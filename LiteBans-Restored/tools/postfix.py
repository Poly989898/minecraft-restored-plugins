#!/usr/bin/env python3
"""Merge enum-constant body classes (decompiled as stand-alone enums) into their parent enum constant.
Reads <deobf.jar>.bodies written by Deobf ("body parent CONST" per line)."""
import re, sys
src, bodies = sys.argv[1], sys.argv[2]
for line in open(bodies):
    body, parent, const = line.split()
    bp, pp = f"{src}/{body}.java", f"{src}/{parent}.java"   # names are internal names, e.g. litebans/eJ
    b = open(bp, encoding="utf-8").read()
    bn, cn = body.split("/")[-1], const
    m = re.search(r"enum %s \{\n\s+%s\([^)]*\) \{\n\s+\}\n+(.*)\n\}\s*$" % (bn, bn), b, re.S)
    if not m: print("cannot parse body", body); continue
    methods = m.group(1).rstrip()
    p = open(pp, encoding="utf-8").read()
    p, n = re.subn(r"^(\s+)%s(\([^)]*\))" % const, lambda x: x.group(1) + const + x.group(2) + " {\n" + methods + "\n" + x.group(1) + "}", p, count=1, flags=re.M)
    if n != 1: print("constant not found", parent, const); continue
    open(pp, "w", encoding="utf-8").write(p)
    import os; os.remove(bp)
print("enum bodies merged")

# ---- generic source fixes (applied to every file)
import glob
n = 0
for f in glob.glob(src + "/litebans/*.java"):
    s = open(f, encoding="utf-8").read(); o = s
    s = re.sub(r"\(boolean\)\s*0\b", "false", s)
    s = re.sub(r"\(boolean\)\s*1\b", "true", s)
    s = re.sub(r"^(\s*)(\w+)\[\] (\w+) = (\w+)\.toArray\(new \2\[0\]\);", r"\1\2[] \3 = (\2[])\4.toArray(new \2[0]);", s, flags=re.M)
    if s != o: open(f, "w", encoding="utf-8").write(s); n += 1
print("generic fixes in", n, "files")
