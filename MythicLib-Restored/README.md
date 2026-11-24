# MythicLib 1.7

Readable sources of MythicLib 1.7 (`io.lumine.mythic.lib`), recovered from the original jar and built against Paper 26.3.

## Status

- Compiles with 0 errors against Paper API `26.3.build.254-stable` (bytecode: Java 21, built on JDK 25) and builds `build/libs/MythicLib-1.7-restored.jar` from a clean tree.
- The original jar is not an input of the build: `build.gradle.kts` only uses public Maven artifacts, the `stubs/` sources and the paperweight module `nms-v26`.
- Verified: on a scratch Paper 26.3 server (build 253) the plugin loads and enables without exceptions ("Detected Bukkit Version: v26_3_R0", TextDisplays hologram hook), `/ml reload` prints "MythicLib reloaded!", and it disables cleanly.
- Not verified: gameplay behaviour (stats, skills, damage, crafting, GUIs, SQL, integrations with WorldGuard, PlaceholderAPI, MythicMobs and others), and any server other than Paper 26.3.

## Changes against the decompiled code

- Decompiler output was repaired where it did not compile; public signatures were kept.
- `nms-v26` is a paperweight-userdev module with the Mojang-mapped `VersionWrapper` for Paper 26.x; its classes are merged into the plugin jar.
- `nms-legacy/` holds the decompiled wrappers for 1.14 - 1.21 (R1) for reference. They are not built.
- Resources (`plugin.yml`, `config.yml`, `default/`) are taken from the original jar.
- Compile-only API stubs of optional integrations and of the MMOProfiles API live in `stubs/` and are not packaged.
- Bundled libraries are relocated into `io.lumine.mythic.lib.*`: commons-math3, gson, exp4j, HikariCP.

## Build inputs

Only public Maven repositories (Paper, PlaceholderAPI, EngineHub, JitPack, CodeMC, Lumine) plus `stubs/`. No `files(...)` jars and nothing from `originals/`.

Other restored plugins can use this project as a composite build (`includeBuild("../MythicLib-Restored")`) or take the `build/libs/MythicLib-1.7-restored.jar` artifact.

## Building

```sh
gradle shadowJar
```

JDK 25 is required (Gradle toolchain). `tools/testserver.sh` starts a throw-away Paper server in a scratch directory with the built jar (`PORT` selects the port, `SERVER_DIR` a Paper directory with `paper-26.3-*.jar`).
