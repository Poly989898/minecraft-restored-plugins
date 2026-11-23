# MythicMobs 5.14.0

Rebuildable sources of MythicMobs 5.14.0 for Paper: the plugin and the NMS modules that build from public Maven artifacts. The build uses no original or shaded jar.

## Layout

- `plugin/` is the main Gradle project. Sources are in `src/main/java`, resources in `src/main/resources`.
  - `src/volatile/java`: old working copies of the NMS sources (the working copies live in `nms/`); not used by the build.
  - `src/stubs/java`: compile-only stubs for plugins without a buildable API: CrashClaim, AriKeys, SkillAPI, PhatLoots (signatures checked against their GitHub sources), MiniaturePets, Crucible, OTG (unchecked, no API available). They are compiled with the plugin but not packaged (the jar task excludes them).
  - Third-party libraries (Apache Commons Lang3 and Math3, jOOQ, exp4j, jedis, Caffeine, bStats, aho-corasick) are normal Maven dependencies, bundled into the jar.
- `nms/` is a Gradle project with the NMS modules (see the table below).

### NMS modules

| Module | Built | Reason |
|---|---|---|
| `v26_1`, `v26_2`, `v26_3` | yes, included in the jar | Paper devBundle (`paperweight.paperDevBundle`) from the public Paper repository |
| `w1_18_R2`, `w1_19_R2`, `w1_19_R3`, `w1_20_R1`, `w1_20_R2`, `w1_20_R3` | no | need `org.spigotmc:spigot:<version>-R0.1-SNAPSHOT`, which is not on any public Maven repository (only BuildTools produces it) |
| `v1_20_R1`, `v1_20_R3`, `v1_21_R1`, `v1_21_R3`, `v1_21_R4`, `v1_21_R5`, `v1_21_R6`, `v1_21_R7` | no | same Spigot snapshots; also need shim jars generated from them (`nms/shim/`, not in the repository) |

The sources of the modules that are not built stay in `nms/` for reference. This build does not support them: the jar contains no implementation for Minecraft versions other than 26.1 to 26.3. Their Spigot-based versions are not built from public sources and are not supported by this build. Nothing here substitutes for those Spigot jars.

The `nms/tools/` scripts (`make_shims.py`, `fix_legacy.py`, `extract_refs.py`, `refdiff.py`) belong to the unsupported legacy modules. They are not run by the build. `refdiff.py` and `make_shims.py` read a reference jar from `../original/`, which is not part of this repository.

## Building (JDK 25 for Gradle, Java 21 bytecode)

1. `cd nms && gradle :v26_1:compileJava :v26_2:compileJava :v26_3:compileJava`
2. `cd plugin && gradle compileJava jar`
3. Output: `plugin/build/libs/MythicMobs-5.14.0-rebuilt.jar`

Run Gradle from inside `plugin/` or `nms/`, not from the repository root.

### Windows

`tools/build.ps1` runs the same steps (plugin compile, NMS modules, plugin jar). Set the environment variables first:

- `JAVA_HOME`: JDK 25 directory;
- `GRADLE`: full path to `gradle.bat` (optional, `gradle` from `PATH` is used if unset);
- `GRADLE_USER_HOME`: Gradle home directory (optional).

```powershell
powershell -ExecutionPolicy Bypass -File tools\build.ps1          # online
powershell -ExecutionPolicy Bypass -File tools\build.ps1 -Offline # --offline for Gradle
```

The script compiles `plugin` before the NMS modules, because they compile against `plugin/build/classes/java/main`.

## Status

Smoke-tested on real Paper servers (startup, mob spawning, `mm reload`, skills) on 26.3 without errors, using the v26_3 module. The 1.18 to 1.21.x Spigot-based modules listed above were tested with earlier builds that used Spigot jars; they are not part of the current build.

Readability changes (names and redundant casts only, behaviour unchanged):
- `plugin/src/main/java`: 5,034 `varN` locals and parameters renamed in 735 files, 156 redundant casts removed. All 3,222 classes are bytecode-identical to the previous build (`javap -v` without constant pool).
- `nms/v26_1`, `v26_2`, `v26_3`: 72–73 variables in 32 files and 36–39 casts each; 138 classes per module are identical.
- Lombok annotations restored in `plugin/src/main/java` (212 of 342 files fully, 96 without `@EqualsAndHashCode`, 10 with a reduced set; 24 stay expanded because the Lombok bytecode differed). All 3,222 classes were compared against the previous build. `build.gradle` uses `annotationProcessor lombok:1.18.42` (1.18.34 does not support JDK 25), settings in `plugin/lombok.config`.

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

Version-specific code lives in the separate Gradle project `nms/`. Contracts are in `api/volatilecode`, implementations in `core/volatilecode` and the `nms/*` modules; `MythicBukkit` picks the implementation by server version.

### Configuration

Resources in `plugin/src/main/resources`: `config.yml`, `config-mobs.yml`, `config-skills.yml`, `config-spawning.yml`, `config-items.yml` and examples.
