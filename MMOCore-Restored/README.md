# MMOCore 1.13.1

Readable sources of MMOCore 1.13.1 (`net.Indyuce.mmocore`, RPG core: classes, skills, skill trees, professions, quests, parties/guilds, GUIs), recovered from the original jar and built against Paper 26.3.

## Status

- Compiles with 0 errors against Paper API `26.3.build.254-stable` (bytecode: Java 21, built on JDK 25) and builds `build/libs/MMOCore-1.13.1-restored.jar`. A clean build (`rm -rf build; gradle shadowJar`) works.
- The jar has the same 639 classes and the same resources (`plugin.yml`, `config.yml`, `default/`, ...) as the original jar, except the `META-INF/maven` pom files.
- No original jar is an input of the build (nothing under `../originals` is referenced).
- Compiles only against the restored MythicLib built from `../MythicLib-Restored` (which implements the MythicLib API MMOCore needs, see its README); no `MythicLib-dist` artifact is used for compiling or packaged.
- Runtime check on a scratch Paper 26.3 server (build 253, offline mode, MythicLib + MMOCore only, none of the soft dependencies) with a headless protocol client as the test player:
  - MythicLib and MMOCore enable without exceptions, `/mmocore reload` and `/mmocore` / `/mmocore admin` help work.
  - Verified admin commands: `admin info`, `admin class`, `admin exp give`, `admin skill-points give`, `admin attribute give`, `admin resource-mana set`, `admin savedata`, `admin reset all`, `booster create/list`; the data of the player is saved and loaded again after a relog.
  - Opened and clicked through (clicks simulated with `InventoryClickEvent`) the class selection, attributes, profile (player stats), skills, skill trees, quests, friends, party and guild creation/view, waypoints GUIs and the MythicLib attribute explorer. They render the configured items and clicks do not throw.
  - Runtime defect fixed in MMOCore: `SmallParticleEffect` spawned `INSTANT_EFFECT` without data (Paper 26.3 requires `Particle.Spell`), which threw every tick after a level-up.

## Changes against the decompiled code

- `PlayerClass`: removed the obfuscator annotation `@InsnComment` (and its import); fixed lambda/catch variable names (`exception`, `exceptionx`).
- `KillMobExperienceSource`, `ProjectileExperienceSource`: `new FlushableRegistry<>(...)` instead of the raw type (decompiler lost the generics).
- `SmallParticleEffect`: passes `Particle.Spell` data for particles that require it (Paper 26.3).
- `InventoryManager`: unchecked cast for the `Map<String, ? extends EditableInventory>` put.
- `McMMOPartyModule`: uses `mcMMO.p.getPartyManager().getParty(...)`; the original called the static `PartyManager.getParty`, which does not exist in mcMMO 2.2.x.
- Resources are taken from the original jar.
- Compile-only API stubs of optional integrations live in `stubs/` and are not packaged: MMOProfiles API (`fr.phoenixdevt.profiles`), FactionsBridge, Guilds, KingdomsX, UltimateClans, DungeonsXL, Heroes, MythicDungeons, OBTeam, PartyAndFriends (both APIs), Parties, BeautyQuests, Quests, QuestCreator. Their signatures cover only what the plugin calls and were written from that usage.

### MythicLib API

MMOCore 1.13.1 needs a MythicLib newer than the original 1.7 jar (`gui.editable.*`, `gui.Navigator`, `Skill()`, `StatMap.bufferUpdates`, `PluginInventory` with an accessible player, `version.Attributes/Sounds`, `PlayerModifier.registerPlayerModifierType`, ...). These classes are written in `../MythicLib-Restored`, so MMOCore compiles and runs with the restored MythicLib alone. Nothing from MythicLib is packaged in the MMOCore jar. The GUI framework is a compatible reimplementation; its behaviour was reconstructed from how MMOCore uses it and checked at runtime as described above, not compared against another implementation.

## Build inputs

- Public Maven artifacts only (Paper API, Mythic-Dist, PlaceholderAPI, WorldGuard/WorldEdit, mcMMO, Vault, Citizens, ...); the offline Gradle cache is sufficient.
- MythicLib is built from `../MythicLib-Restored` by the `buildMythicLib` task (nested Gradle build running `shadowJar`), which `compileJava` depends on.
- No other sibling plugin (MMOItems, MMOProfiles) is needed: they are not referenced by this source tree beyond the stubs.

## Building

```sh
gradle shadowJar
```

JDK 25 is required (Gradle toolchain).

## Runtime checks

Run on a scratch Paper 26.3 server with the restored MythicLib, using an in-process fake player (no real client) driven by a throwaway test plugin that calls the plugin API and fires Bukkit events:

- Skills: class change, binding, casting `FIRE_STORM` (target skill, mana spent), `FIREBALL` and a location skill without a target (correctly fails).
- Professions: woodcutting experience from `mineblock` sources, level-up, custom mining of gold ore (drops, experience, temporary block regeneration), professions JSON round trip.
- Loot chests: spawning with `OFFSET`/`HELIX`/`GALAXY` effects and `FLAME`/`INSTANT_EFFECT`/`DUST` particles, opening and closing, break protection, expiry. Every `Particle` constant was spawned with the default data; only `VIBRATION`, `TRAIL` and the `GEYSER*` ones still need data the plugin cannot invent.
- Quests: `goto`, `mineblock` and `killmob` objectives, triggers (item, experience, message), finished-quest persistence.
- SQL backend (MariaDB): table creation, save on quit, load on join (class, level, experience, points, professions, skill tree nodes, unlocked items).
- Skill trees: maxing out a node and opening both skill tree GUIs.

Fixes found by these checks:

- `LootChest.expire` never removed the chest from `LootChestManager`, as in the original. Expired chests stayed in the active map and the shutdown loop then threw "Chest has already expired". Chests now unregister themselves and the shutdown loop works on a copy of active chests only.
- The shipped `skill-tree.yml` and `specific-skill-tree-default.yml` had no `maxed-out` status, which logged one warning per path/node type and made the skill tree GUI throw (`has no icon for the type ... and the status MAXED_OUT`) as soon as a node reached its maximum level. The default files now define `maxed-out` icons (green concrete for nodes) and a `Maxed Out` status name. Existing servers keep their own copies of these files and need the same section added.
- `CastingParticle.defaultData` also supplies data for `DUST`, `DUST_COLOR_TRANSITION`, block and item particles.

## Not verified

- Anything that needs a real client: real clicks, chat input prompts such as friend requests or party invites, drag events, the visual look of particles, GUIs and holograms. The fake player has no client, so messages sent to it were not read.
- Fishing, farming, smelting, alchemy, enchanting and smithing professions, the `goto`/`clickon` objectives against real movement, skill casting through key combos or the skill bar, combat damage numbers.
- All integrations: Vault, MythicMobs, MMOItems, MMOProfiles, WorldGuard, PlaceholderAPI, Residence, Citizens, parties and guilds plugins. Startup warnings (`region` condition, `mmoitem` drops, `talkto`/`getitem`/`killmythicmob` objectives) appear because the plugins that provide them are not installed.
- SQL mode was checked only against MariaDB on one server; multi-server synchronisation and the YAML-to-SQL export command were not run.
