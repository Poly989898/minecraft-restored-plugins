# MMOInventory 1.9.1

Readable sources of MMOInventory 1.9.1 (custom RPG inventory, `net.Indyuce.inventory`, plus the `ru.endlesscode.rpginventory` compatibility classes), recovered from the original jar and built against Paper 26.3.

## Status

- Compiles with 0 errors against Paper API `26.3.build.254-stable` (bytecode: Java 21, built on JDK 25) and builds `build/libs/MMOInventory-1.9.1-restored.jar`.
- No original jar is an input of the build (nothing under `../originals` is referenced).
- Verified on a scratch Paper 26.3 (build 253) server together with `MythicLib-1.7-restored.jar`: the plugin loads and enables, registers its 5 inventory slots, and `/mmoinventory reload` works from the console. Without MMOItems installed the 8 slots that use the `mmoitemstype` restriction are skipped with a warning; this is the plugin's normal behaviour without MMOItems.
- No runtime defects in this plugin's own code were found, so none were patched.

## Changes against the decompiled code

- Decompiler output needed no repairs to compile; local variable names were renamed by a tool (bytecode unchanged).
- Resources (`plugin.yml`, `config.yml`, `default/`) are taken from the original jar.
- Compile-only API stubs for optional integrations live in `stubs/` and are not packaged: AureliumSkills, SkillAPI, Heroes, RacesAndClasses, RPGPlayerLeveling, McRPG, BattleLevels, Skills, SkillsPro, Fabled, Oraxen, the MMOProfiles API (`fr.phoenixdevt.profiles`), and the small part of MMOItems (`MMOItems`, `Type`, `PlayerData`, `EquippedItem`, `PlayerInventory`, `RPGInventoryHook`, `MMOUtils`) and MMOCore (`PlayerData`) that this plugin calls. Their signatures cover only what the plugin calls.

## Build inputs

- MythicLib is built from `../MythicLib-Restored` by the `buildMythicLib` task (a nested Gradle build running `shadowJar`), which `compileJava` depends on, so a clean clone builds without prebuilt jars.
- MMOItems and MMOCore are compile-only stubs from `stubs/`. The sibling `../MMOItems-Restored` and `../MMOCore-Restored` are not build inputs.

## Building

```sh
gradle shadowJar
```

JDK 25 is required (Gradle toolchain); the output is `build/libs/MMOInventory-1.9.1-restored.jar`. The jar needs MythicLib at runtime (`depend` in `plugin.yml`).

## Not verified

- Anything that needs a player: opening the inventory GUI, equipping, death drops, YAML/SQL player data.
- The MMOItems and MMOCore integrations (those plugins were not running).
- `/mmoinventory export-data` fails inside MythicLib's `DataExport` (`listFiles()` of a missing `userdata` folder returns null), not in this plugin.
