# MMOCore 1.13.1

Readable sources of MMOCore 1.13.1 (`net.Indyuce.mmocore`, RPG core: classes, skills, skill trees, professions, quests, parties/guilds, GUIs), recovered from the original jar and built against Paper 26.3.

## Status

- Compiles with 0 errors against Paper API `26.3.build.254-stable` (bytecode: Java 21, built on JDK 25) and builds `build/libs/MMOCore-1.13.1-restored.jar`. A clean build (`rm -rf build; gradle shadowJar`) works.
- The jar has the same 639 classes and the same non-class resources (`plugin.yml`, `config.yml`, `default/`, ...) as the original.
- No original jar is an input of the build (nothing under `../originals` is referenced).
- Compiles only against the restored MythicLib built from `../MythicLib-Restored` (which implements the MythicLib API MMOCore needs, see its README); no `MythicLib-dist` artifact is used for compiling or packaged.
- Runtime check on a scratch Paper 26.3 server (build 253, offline mode, MythicLib + MMOCore only, none of the soft dependencies) with a headless protocol client as the test player:
  - MythicLib and MMOCore enable without exceptions, `/mmocore reload` and `/mmocore` / `/mmocore admin` help work.
  - Verified admin commands: `admin info`, `admin class`, `admin exp give`, `admin skill-points give`, `admin attribute give`, `admin resource-mana set`, `admin savedata`, `admin reset all`, `booster create/list`; the data of the player is saved and loaded again after a relog.
  - Opened and clicked through (clicks simulated with `InventoryClickEvent`) the class selection, attributes, profile (player stats), skills, skill trees, quests, friends, party and guild creation/view, waypoints GUIs and the MythicLib attribute explorer. They render the configured items and clicks do not throw.
  - Runtime defect fixed in MMOCore: `SmallParticleEffect` spawned `INSTANT_EFFECT` without data (Paper 26.3 requires `Particle.Spell`), which threw every tick after a level-up.
- Not verified: anything that needs a real client (real clicks, chat input prompts such as friend requests or party invites, drag events), skill casting and combat, professions (mining, fishing, ...), loot chests, quests objectives, the SQL data backend, and all integrations (Vault, MythicMobs, MMOItems, MMOProfiles, WorldGuard, PlaceholderAPI, Residence, Citizens, parties/guilds plugins). The default `skill-tree.yml` GUI config does not define a `maxed-out` status, so the console shows "Missing path type ... for status: maxed-out" warnings on load; this comes from the shipped configuration, not from the port. Other warnings at startup (`region` condition, `mmoitem` drops, `talkto`/`getitem` objectives) appear because the plugins that provide them are not installed.

## MythicLib API

MMOCore 1.13.1 needs a MythicLib newer than the original 1.7 jar (`gui.editable.*`, `gui.Navigator`, `Skill()`, `StatMap.bufferUpdates`, `PluginInventory` with an accessible player, `version.Attributes/Sounds`, `PlayerModifier.registerPlayerModifierType`, ...). These classes were written by us in `../MythicLib-Restored`, so MMOCore compiles and runs with the restored MythicLib alone. Nothing from MythicLib is packaged in the MMOCore jar. The GUI framework is a compatible reimplementation; its behaviour was reconstructed from how MMOCore uses it and checked at runtime as described above, not compared against another implementation.

## Changes against the decompiled code

- `PlayerClass`: removed the obfuscator annotation `@InsnComment` (and its import); fixed lambda/catch variable names (`exception`, `exceptionx`).
- `KillMobExperienceSource`, `ProjectileExperienceSource`: `new FlushableRegistry<>(...)` instead of the raw type (decompiler lost the generics).
- `SmallParticleEffect`: passes `Particle.Spell` data for particles that require it (Paper 26.3).
- `InventoryManager`: unchecked cast for the `Map<String, ? extends EditableInventory>` put.
- `McMMOPartyModule`: uses `mcMMO.p.getPartyManager().getParty(...)`; the original called the static `PartyManager.getParty`, which does not exist in mcMMO 2.2.x.
- Resources are taken from the original jar.
- Compile-only API stubs of optional integrations live in `stubs/` and are not packaged: MMOProfiles API (`fr.phoenixdevt.profiles`), FactionsBridge, Guilds, KingdomsX, UltimateClans, DungeonsXL, Heroes, MythicDungeons, OBTeam, PartyAndFriends (both APIs), Parties, BeautyQuests, Quests, QuestCreator. Their signatures cover only what the plugin calls and were written from that usage.

## Build inputs

- Public Maven artifacts only (Paper API, Mythic-Dist, PlaceholderAPI, WorldGuard/WorldEdit, mcMMO, Vault, Citizens, ...); the offline Gradle cache is sufficient.
- MythicLib is built from `../MythicLib-Restored` by the `buildMythicLib` task (nested Gradle build running `shadowJar`), which `compileJava` depends on.
- No other sibling plugin (MMOItems, MMOProfiles) is needed: they are not referenced by this source tree beyond the stubs.

## Building

```sh
gradle shadowJar
```

JDK 25 is required (Gradle toolchain).
