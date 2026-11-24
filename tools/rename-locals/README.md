# rename-locals

Renames decompiler-style local variables and parameters (`var1`, `var2`, ...) in Java sources
to meaningful names. It is symbol accurate: the javac Tree API (`com.sun.source`) resolves every
identifier, and a rename is applied to the declaration and to exactly those identifiers that
resolve to the same variable. Files are patched by character offsets, so formatting is untouched.

## Usage

    GRADLE=/path/to/gradle tools/rename-locals/run.sh <plugin-dir> [--dry-run] [--verify] [--map out.txt] [--pattern regex]

* The compile classpath is read from the plugin's Gradle build (`compileClasspath` of `main`,
  run with `--offline`); sources are `<plugin-dir>/src/main/java` (override with `--src`).
* All files are analysed in a single javac run; the tool refuses to run if the sources do not compile.
* `--dry-run` changes nothing, `--map` writes `file:line old -> new` for every declaration.
* `--verify` compiles with `javac -g:none` before and after and compares every `.class` byte for
  byte; a renamed local must not change the bytecode.
* `--pattern` selects the names to rename (default `var\d+`).

## Naming

* declared type: `Player` -> `player`, `ItemStack` -> `item`, `String` -> `text`, `List<X>` -> `xs`,
  `Map` -> `map`, `boolean` -> `flag`, `int` -> `count` (`index` when used as an index),
  `*Event` -> `event`, `*Exception`/`Throwable` -> `exception`
* initialiser: `getFoo()` -> `foo`, `isFoo()` -> `isFoo`, `getString("a.b")` -> `aB`, `this.foo` -> `foo`
* for-each: element name taken from the iterable (`players` -> `player`), otherwise from the type
* for counters: `i`, `j`, `k`, ...; catch parameters: `exception`
* parameters that override a method with real parameter names reuse those names
* Java keywords and contextual keywords are avoided

A name is never chosen if it is already used in the variable's scope: other locals and parameters
with an overlapping scope, any identifier referenced inside the scope (fields, captured variables,
types), or declarations inside nested lambdas and classes. On a clash a numeric suffix is added
(`item`, `item2`). Variables with disjoint scopes may share a name. Record constructor parameters
are never renamed.
