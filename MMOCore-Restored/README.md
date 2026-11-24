# MMOCore 1.13.1

Readable sources of MMOCore 1.13.1 (`net.Indyuce.mmocore`, RPG core: classes, skills, skill trees, professions, quests, parties/guilds, GUIs), recovered from the original jar and built against Paper 26.3.

## Status

- Compiles with 0 errors against Paper API `26.3.build.254-stable` (bytecode: Java 21, built on JDK 25) and builds `build/libs/MMOCore-1.13.1-restored.jar`. A clean build (`rm -rf build; gradle shadowJar`) works.
- The jar has the same 639 classes and the same non-class resources (`plugin.yml`, `config.yml`, `default/`, ...) as the original.
- No original jar is an input of the build (nothing under `../originals` is referenced).
- Not verified: the plugin has not been started on a server, so nothing is checked at runtime (class loading, commands, GUIs, data storage, integrations).

## Important: MythicLib version

MMOCore 1.13.1 is written against a MythicLib 1.7.x development build (for example `Skill()`, `StatMap.bufferUpdates`, `PluginInventory.onClick`, `gui.editable.*`, `Navigator`) that is newer than the restored `MythicLib-1.7` and older than the public `MythicLib-dist:1.7.1-SNAPSHOT`. Neither of them matches exactly. The compile-only classpath is therefore layered (see `build.gradle.kts`):

1. a few classes extracted from the public `io.lumine:MythicLib-dist:1.7.1-SNAPSHOT` Maven artifact where MMOCore needs the newer shape (`gui.**`, `version.Attributes`, `version.Sounds`, `util.lang3.Validate`, `skill.Skill`, `api.stat.StatMap`, `version.VersionUtils`, `player.modifier.PlayerModifier`);
2. the restored MythicLib, built from `../MythicLib-Restored`;
3. the rest of `MythicLib-dist` as a gap filler.

Nothing from MythicLib is packaged. At runtime the plugin needs a MythicLib that provides those APIs; the restored MythicLib 1.7 alone does not, so loading MMOCore with it is expected to fail.

## Changes against the decompiled code

- `PlayerClass`: removed the obfuscator annotation `@InsnComment` (and its import); fixed lambda/catch variable names (`exception`, `exceptionx`).
- `KillMobExperienceSource`, `ProjectileExperienceSource`: `new FlushableRegistry<>(...)` instead of the raw type (decompiler lost the generics).
- `InventoryManager`: unchecked cast for the `Map<String, ? extends EditableInventory>` put.
- `McMMOPartyModule`: uses `mcMMO.p.getPartyManager().getParty(...)`; the original called the static `PartyManager.getParty`, which does not exist in mcMMO 2.2.x.
- Resources are taken from the original jar.
- Compile-only API stubs of optional integrations live in `stubs/` and are not packaged: MMOProfiles API (`fr.phoenixdevt.profiles`), FactionsBridge, Guilds, KingdomsX, UltimateClans, DungeonsXL, Heroes, MythicDungeons, OBTeam, PartyAndFriends (both APIs), Parties, BeautyQuests, Quests, QuestCreator. Their signatures cover only what the plugin calls and were written from that usage.

## Build inputs

- Public Maven artifacts only (Paper API, MythicLib-dist, Mythic-Dist, PlaceholderAPI, WorldGuard/WorldEdit, mcMMO, Vault, Citizens, ...); the offline Gradle cache is sufficient.
- MythicLib is built from `../MythicLib-Restored` by the `buildMythicLib` task (nested Gradle build running `shadowJar`), which `compileJava` depends on.
- No other sibling plugin (MMOItems, MMOProfiles) is needed: they are not referenced by this source tree beyond the stubs.

## Building

```sh
gradle shadowJar
```

JDK 25 is required (Gradle toolchain).
