#!/usr/bin/env python3
"""Compare class signatures (javap -p) of rebuilt classes against the original jar.

usage: verify-signatures.py <original.jar> <classpath> <overlay-dir> [<overlay-dir> ...]
Synthetic members (lambda$, access$, $values, $VALUES) are ignored because their numbering
changes on every compile. Exit code 1 if any real difference is found.
"""
import os, re, subprocess, sys

SKIP = re.compile(r"lambda\$|access\$\d+|\$values\(|\$VALUES|^Compiled from")

def classes(dirs):
    for d in dirs:
        for base, _, files in os.walk(d):
            for f in files:
                if f.endswith(".class"):
                    yield os.path.relpath(os.path.join(base, f), d)[:-6].replace(os.sep, ".")

def sig(classpath, name):
    out = subprocess.run(["javap", "-p", "-classpath", classpath, name],
                         capture_output=True, text=True).stdout
    return sorted(re.sub(r"\s+", " ", l.strip()) for l in out.splitlines()
                  if l.strip() and not SKIP.search(l))

def main():
    orig, cp, dirs = sys.argv[1], sys.argv[2], sys.argv[3:]
    names = sorted(set(classes(dirs)))
    bad = []
    for n in names:
        a = sig(orig + os.pathsep + cp, n)
        b = sig(os.pathsep.join(dirs) + os.pathsep + cp, n)
        if a != b:
            bad.append((n, [x for x in a if x not in b][:3], [x for x in b if x not in a][:3]))
    print(f"signature differences: {len(bad)} of {len(names)} classes")
    for n, a, b in bad:
        print("==", n, "\n  original only:", a, "\n  rebuilt only: ", b)
    sys.exit(1 if bad else 0)

main()
