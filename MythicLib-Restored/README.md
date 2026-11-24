# MythicLib 1.7

Readable sources of MythicLib 1.7 (`io.lumine.mythic.lib`), recovered from the original jar and built against Paper 26.3.

## Status

- Compiles with 0 errors against Paper API `26.3.build.254-stable` (bytecode: Java 21, built on JDK 25) and builds `build/libs/MythicLib-1.7-restored.jar` from a clean tree.
- The original jar is not an input of the build: `build.gradle.kts` only uses public Maven artifacts, the `stubs/` sources and the paperweight module `nms-v26`.
- Verified: on a scratch Paper 26.3 server (build 253) the plugin loads and enables without exceptions ("Detected Bukkit Version: v26_3_R0", TextDisplays hologram hook), `/ml reload` prints "MythicLib reloaded!", and it disables cleanly.
- Verified together with MMOCore (see below and `../MMOCore-Restored/README.md`) on a scratch Paper 26.3 server with a headless test client: the GUI framework, `Navigator`, `PluginInventory` and the debug `AttributeExplorer` GUI open, render and react to clicks without exceptions.

## Changes against the decompiled code

- Decompiler output was repaired where it did not compile; public signatures were kept.
- `nms-v26` is a paperweight-userdev module with the Mojang-mapped `VersionWrapper` for Paper 26.x; its classes are merged into the plugin jar.
- `nms-legacy/` holds the decompiled wrappers for 1.14 - 1.21 (R1) for reference. They are not built.
- Resources (`plugin.yml`, `config.yml`, `default/`) are taken from the original jar.
- Compile-only API stubs of optional integrations and of the MMOProfiles API live in `stubs/` and are not packaged.
- Bundled libraries are relocated into `io.lumine.mythic.lib.*`: commons-math3, gson, exp4j, HikariCP.

### API added for MMOCore 1.13 (and the other MMO plugins)

MMOCore 1.13.1 was written against a MythicLib development build that is newer than the original 1.7 jar. The missing API was implemented here from scratch (no code from the public `MythicLib-dist` artifact is used or packaged), in the same package names, from the compile-time shapes MMOCore uses; behaviour was reconstructed from how the callers use it, so it is a compatible implementation, not a byte-for-byte copy. Existing classes keep their public signatures; the changes to them are additive except where noted.

- `gui.editable.*`: `EditableInventory`, `GeneratedInventory`, `item.InventoryItem`, `item.PhysicalItem`, `item.SimpleItem`, `item.ItemOptions`, `item.builtin.{GoBackItem, CloseInventoryItem, NextPageItem, PreviousPageItem}`, `placeholder.{Placeholders, EmptyPlaceholders, ErrorPlaceholders}`. GUI layouts are read from config files (`name`, `slots`, `items.<key>.{function, slots, item, name, lore, custom_model_data, custom_model_data_string, texture, hide-flags, on-click, ...}`).
- `gui.util.IconOptions` (material, custom model data int/string/float, item model, skull texture, item flags, ...), `gui.Navigator` (per-player GUI stack that listens to the player's inventory events only while one of its GUIs is shown).
- `gui.PluginInventory` was reworked: it now belongs to a `Navigator`, exposes `player`, `playerData`, `navigator`, `page` and has `onClick`, `onDrag`, `onOpen`, `onClose`, `registerRepeatingTask`. `whenClicked(InventoryClickEvent)` is no longer abstract and is called by the default `onClick`, so older subclasses (`AttributeExplorer`, `AttributeCreator`) keep working. The global click dispatch that used to live in `PlayerListener` was removed because the navigator routes the events now.
- `Navigator` shuts itself down when a click arrives while another inventory is displayed. Paper 26.3 does not fire `InventoryCloseEvent` when one GUI is replaced by another, so without this a stale navigator kept handling clicks made in the new GUI (found at runtime: NPE in the MMOCore skill tree after opening the party GUI).
- `version.Attributes`, `version.Sounds` (registry-based lookups with the old and new names), `util.lang3.Validate`, `util.config.YamlUtils`, `VersionUtils.name(Biome|Sound)` and `getModifier(AttributeInstance, NamespacedKey)`, `UtilityMethods.{prettyValueOf, dummyConsume, getHolder}`, `Tasks.runSync`.
- `Skill` got a public no-argument constructor, `Skill(SkillHandler<?>)` and `cast(MMOPlayerData, TriggerType)`; `getHandler()` is no longer abstract (it throws if the skill has no handler and does not override it). `SkillMetadata.of(MMOPlayerData)`.
- `StatMap.bufferUpdates(Runnable)` (stats are recomputed once when the outermost batch ends; the per-modifier updates are not suppressed inside the batch), `isBufferingUpdates()`, `getActionHand()`, `invalidateReferences()`.
- `PlayerModifier.registerPlayerModifierType(...)`, `PlayerModifier.from(ConfigObject)` and `getMap(MMOPlayerData)`. Only the types registered by plugins (MMOCore registers `attribute`) are known; the types MythicLib itself would register (`stat`, `skill`, `potion_effect`, ...) are not registered here.

## Build inputs

Only public Maven repositories (Paper, PlaceholderAPI, EngineHub, JitPack, CodeMC, Lumine) plus `stubs/`. No `files(...)` jars and nothing from `originals/`.

Other restored plugins can use this project as a composite build (`includeBuild("../MythicLib-Restored")`) or take the `build/libs/MythicLib-1.7-restored.jar` artifact.

## Building

```sh
gradle shadowJar
```

JDK 25 is required (Gradle toolchain); the output is `build/libs/MythicLib-1.7-restored.jar`. `tools/testserver.sh` starts a throw-away Paper server in a scratch directory with the built jar (`PORT` selects the port, `SERVER_DIR` a Paper directory with `paper-26.3-*.jar`).

## Not verified

- Gameplay behaviour: stats, skills, damage, crafting, SQL, and the integrations with WorldGuard, PlaceholderAPI, MythicMobs and others.
- GUIs other than the ones opened together with MMOCore.
- Any server other than Paper 26.3.
