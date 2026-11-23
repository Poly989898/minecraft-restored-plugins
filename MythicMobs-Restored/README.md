# MythicMobs 5.14.0

Rebuildable sources of MythicMobs 5.14.0 for Paper: the plugin and all version-specific NMS modules build from source.

## Layout

- `plugin/` is the main Gradle project. Sources are in `src/main/java`, resources in `src/main/resources`.
  - `src/volatile/java`: sources of the NMS modules (the working copies live in `nms/`).
  - `src/stubs/java`: compile-only stubs for plugins without a buildable API: CrashClaim, AriKeys, SkillAPI, PhatLoots (signatures checked against their GitHub sources), MiniaturePets, Crucible, OTG (unchecked, no API available). They are compiled with the plugin but not packaged (the jar task excludes them).
  - Third-party libraries (Apache Commons Lang3 and Math3, jOOQ, exp4j, jedis, Caffeine, bStats, aho-corasick) are normal Maven dependencies, bundled into the jar. No original jar is needed.
- `nms/` is a Gradle project with the NMS modules:
  - `v26_1 … v26_3`: paperweight, built and included in the jar;
  - `w1_18_R2 … w1_20_R3`: version wrappers, built and included;
  - `v1_20_R1 … v1_21_R7`: legacy NMS (Spigot mappings, MC 1.20.1 … 1.21.11), built from source against generated shim jars (`nms/shim/`, see below); included.
- The original jar is not needed for the build. Use it only for comparison, if you have it.

## Building (JDK 25 for Gradle, Java 21 bytecode)

1. `cd nms && gradle :v26_1:compileJava :v26_2:compileJava :v26_3:compileJava :w1_18_R2:compileJava :w1_19_R2:compileJava :w1_19_R3:compileJava :w1_20_R1:compileJava :w1_20_R2:compileJava :w1_20_R3:compileJava :v1_20_R1:compileJava :v1_20_R3:compileJava :v1_21_R1:compileJava :v1_21_R3:compileJava :v1_21_R4:compileJava :v1_21_R5:compileJava :v1_21_R6:compileJava :v1_21_R7:compileJava`
2. `cd plugin && gradle compileJava jar`
3. Output: `plugin/build/libs/MythicMobs-5.14.0-rebuilt.jar`

### Windows

`tools/build.ps1` runs the same steps (plugin compile, NMS modules, plugin jar). Set the environment variables first:

- `JAVA_HOME`: JDK 25 directory;
- `GRADLE`: full path to `gradle.bat` (optional, `gradle` from `PATH` is used if unset);
- `GRADLE_USER_HOME`: Gradle home directory (optional).

```powershell
powershell -ExecutionPolicy Bypass -File tools\build.ps1          # online
powershell -ExecutionPolicy Bypass -File tools\build.ps1 -Offline # --offline for Gradle
```

The script compiles `plugin` before the NMS modules, because they compile against `plugin/build/classes/java/main`. No original or shaded jar is needed.

### Legacy NMS modules (`v1_20_R1 … v1_21_R7`)

The shim jars (`nms/shim/`) are not stored in the repository and must be generated before building:

```sh
cd nms && python3 tools/fix_legacy.py && python3 tools/make_shims.py
```

This needs the Spigot jars of the required versions in `~/.m2` (produced by BuildTools on JDK 21). `python3 tools/refdiff.py` compares the NMS references in the bytecode with the original.

## Status

Smoke-tested on real Paper servers (startup, mob spawning, `mm reload`, skills) on 1.21.4, 1.21.5, 1.21.8, 1.21.10, 1.21.11 and 26.3 without errors. On 1.20.1, 1.20.4 and 1.21.1 it works, with errors that also occur with the original jar (missing attributes and events in the old API, `EntityType.SULFUR_CUBE`, an `IncompatibleClassChangeError` on `Attribute` on 1.20.1). The 1.18 – 1.20.x `w1_*` wrappers and 1.20.2, 1.21.2, 1.21.3, 1.21.6, 1.21.7 and 1.21.9 have not been run.

Readability changes (names and redundant casts only, behaviour unchanged):
- `plugin/src/main/java`: 5,034 `varN` locals and parameters renamed in 735 files, 156 redundant casts removed. All 3,222 classes are bytecode-identical to the previous build (`javap -v` without constant pool).
- `nms/v26_1`, `v26_2`, `v26_3`: 72–73 variables in 32 files and 36–39 casts each; 138 classes per module are identical.
- Lombok annotations restored in `plugin/src/main/java` (212 of 342 files fully, 96 without `@EqualsAndHashCode`, 10 with a reduced set; 24 stay expanded because the Lombok bytecode differed). All 3,222 classes were compared against the previous build. `build.gradle` uses `annotationProcessor lombok:1.18.42` (1.18.34 does not support JDK 25), settings in `plugin/lombok.config`.
- Not touched: the legacy NMS modules (their sources need the `unrename` step and shim jars) and `src/volatile`.

## Architecture

### Entry point

`plugin.yml`: `main: io.lumine.mythic.bukkit.MythicBukkit`, loaded by `BukkitBootstrap`. `MythicBukkit` holds the managers and the `VolatileCodeHandler` (the NMS wrapper).

### Layers (`io.lumine.mythic`)

| Layer | Role |
|---|---|
| `api` | Platform-independent interfaces and contracts: adapters, skills, mobs, drops, configs, holograms, `volatilecode` |
| `core` | Logic: **skills** (`core/skills`), mobs (`core/mobs`: `ActiveMob`, `ai`), drops, spawning, menus, items, worlds, players, configs, glow |
| `bukkit` | Bukkit/Paper binding: type adapters, entities, commands, events, listeners, integrations with other plugins (`compatibility`), metrics, utilities (`bukkit/utils`, the largest package) |

### Skills (`core/skills`, 1,206 files)

- `mechanics` (327) are the actions: damage, teleport, effects, summon and more. `conditions` (221), `targeters` (109) select targets, and `placeholders` (339) substitute text.
- Core classes: `SkillExecutor` (execution), `MetaSkill` (a set of actions), `SkillMechanic`, `SkillCondition`, `SkillTargeter`, `SkillTriggers`, `SkillMetadataImpl` (skill context).
- A new mechanic is a class in `mechanics` registered through `CustomComponentRegistry`.

### Volatile (NMS) code

Version-specific code lives in the separate Gradle project `nms/`. Contracts are in `api/volatilecode`, implementations in `core/volatilecode` and the `nms/*` modules; `MythicBukkit` picks the implementation by server version. `plugin/src/volatile` holds old working copies that the build does not use directly.

### Configuration

Resources in `plugin/src/main/resources`: `config.yml`, `config-mobs.yml`, `config-skills.yml`, `config-spawning.yml`, `config-items.yml` and examples.
