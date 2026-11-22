# Minecraft Plugins: Restored Edition

Readable, rebuildable Java sources for Paper plugins. The sources were recovered by decompiling the release jars and then repaired by hand and with scripts until they compile again: obfuscated names replaced with meaningful ones, encrypted strings decrypted, decoy and dead obfuscator code removed, Kotlin and decompiler artifacts cleaned up.

## Plugins

| Folder | Plugin | Author | Platform | State |
|---|---|---|---|---|
| [`ModelEngine-Restored`](ModelEngine-Restored) | Ultimate Model Engine R4.1.1 (continuation of ModelEngine) | Ticxo | Paper 1.19.4 – 26.3 | All 19 modules compile; ran on Paper 26.3, logs and resource pack match the original |
| [`Nexo-Restored`](Nexo-Restored) | Nexo 1.26 | boy0000 | Paper 26.x | All own code compiles, including integrations and pack servers; ran on Paper 26.3 |
| [`MythicMobs-Restored`](MythicMobs-Restored) | MythicMobs 5.14.0 | Lumine | Paper 1.20 – 26.3 | Plugin and all NMS modules build from source; smoke-tested on Paper 1.21.4, 1.21.5, 1.21.8, 1.21.10, 1.21.11 and 26.3 |
| [`LiteBans-Restored`](LiteBans-Restored) | LiteBans 2.19.0 | Ruan | Paper, Bungee, Velocity | Compiles (Fabric/Mixin excluded), deobfuscation partial; console commands verified on Paper 26.3 with H2 |
| [`LPX-Restored`](LPX-Restored) | LPX 3.9.1 | Ytnoos | Paper / Spigot 1.8 – 1.21 | All 189 classes compile; starts on Paper 26.3; in-game check behaviour not verified |

What "verified" means for each plugin, what was **not** tested, and the exact build commands are in the README inside each folder.

## How it was done

Every plugin went through the same kind of pipeline, adapted to what the jar looked like:

1. **Decompile** the release jar (Vineflower, CFR for some LiteBans files). Where the jar has deliberately broken access flags, they are repaired first so the decompiler can read it.
2. **Decrypt** string constants statically (LiteBans: 11,568 strings) or by running the plugin's own decryptor in a throwaway JVM (LPX).
3. **Repair** until `javac` accepts the code: wrong overloads, lost default arguments, merged local variable types, Kotlin `Intrinsics` and inline artifacts, missing generics and casts.
4. **Rename** classes, fields, methods and local variables with symbol-aware tools. Class and member names in obfuscated plugins are *inferred* from structure, strings and library prototypes, so some are approximate.
5. **Verify** by recompiling and comparing bytecode against the previous stage (`javap -v` without the constant pool) for the readability-only changes, then by running the plugin on a real Paper server.

## Repository layout

```
<Plugin>-Restored/   one folder per plugin: sources, build and recovery tools, README
LICENSE                 GPL-3.0
NOTICE.md               origin and behavioural changes of every plugin
```

## Building

LPX and Nexo also ship PowerShell versions of their build scripts (`tools/*.ps1`, same steps as the `.sh` ones) for Windows. The other plugins are Linux and Git Bash only for now.

General requirements: **JDK 25** (the bytecode target is Java 17, 21 or 25 depending on the module, as in the original), Gradle where a `build.gradle` exists, `bash`, `curl`, `unzip` and `python3` for the helper scripts, and network access to download Paper and dependencies.

Several builds repack the recompiled classes into the original jar, and a few steps compare against it. **Original jars are not included.** Put them in `originals/` next to the plugin folders (the exact file names are given in each plugin's README). `build/`, `dist/`, `.work/` and downloaded dependencies are generated and ignored by git.

Short version per plugin (details in each README):

| Plugin | Build |
|---|---|
| ModelEngine | `scripts/fetch-deps.sh && scripts/fetch-paper.sh 26.3 && scripts/build.sh && scripts/build-adapters.sh && scripts/merge-jar.sh` |
| Nexo | `gradle --offline compileJava && tools/package.sh` |
| MythicMobs | `cd nms && gradle :v26_3:compileJava …`, then `cd plugin && gradle compileJava jar` |
| LiteBans | `gradle compileJava jar` |
| LPX | `tools/build.sh && tools/package.sh` |

## Changes from the originals

Most work is renaming and cleanup that does not change behaviour. The changes that **do** change behaviour are listed in [NOTICE.md](NOTICE.md). In short:

- Obfuscator and distributor leftovers are removed: decoy methods, buyer-ID watermarks, re-uploader prints, build-flag checks, phone-home and update checks.
- Kotlin runtime null checks (`Intrinsics.checkNotNull*`) are removed in Nexo; checked exceptions in LiteBans are rethrown with a sneaky-throw helper to match Kotlin semantics.
- MythicMobs: code that upstream gated behind its separate premium module is enabled, bStats metrics are not started.
- LPX: the bundled Adventure library is relocated so the plugin starts on Paper 26.3.

## Not included

- Original jars, built plugins, Paper/server files, worlds and plugin data.
- Third-party libraries bundled inside the original jars (PacketEvents, Adventure, HikariCP, Kotlin stdlib, jsoup, MineSkin client and others).
- Fabric/Mixin parts of LiteBans (they need Minecraft/Fabric jars to compile).
