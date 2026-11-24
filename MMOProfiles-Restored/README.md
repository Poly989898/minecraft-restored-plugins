# MMOProfiles 1.1.1

Rebuildable sources of MMOProfiles 1.1.1 (RPG profile selection for Paper and Velocity). The code was recovered from the jar `MMOProfiles-1.1.1-36-4` with Vineflower and repaired until it compiles.

## Status

- All source files of `fr.phoenixdevt` compile with 0 errors against Paper API 26.3, the Velocity API and MythicLib. `gradle shadowJar` produces `build/libs/MMOProfiles-1.1.1-restored.jar`.
- Started on a scratch Paper 26.3 server together with `MythicLib-1.7-restored.jar`: the plugin enables without errors, loads both GUI configs and `/mmoprofiles reload` works.
- Not verified: the profile GUI with a real player (no client was connected), the proxy mode and the database modes.

## What was done

- Only the plugin's own packages are in `src/`: `fr.phoenixdevt.mmoprofiles` (bukkit, shared, velocity) and the API package `fr.phoenixdevt.profiles` used by MythicLib, MMOCore, MMOItems and MMOInventory.
- The original jar bundles about 3000 third-party classes (Guava, Gson, Apache Commons, Checker Framework, JetBrains annotations, `javax`). They are not part of the sources; they are compile-only Maven dependencies because Paper and Velocity provide them at runtime. The shadow jar contains only the plugin's own classes.
- `ProxyBasedProfiles`: raw `List` cast replaced by `List<PlayerInfoData>`.
- `ConfigManager`: the sound name written to `messages.yml` is the key of the Bukkit sound.
- `ProfileListViewer`: `UtilityMethods.getPageNumber` replaced by an inline ceiling division.
- Runtime fix: the original code uses the GUI framework of a newer MythicLib (`io.lumine.mythic.lib.gui.editable.*`, `gui.Navigator`, `version.Attributes`, `version.Sounds`), which MythicLib 1.7 does not contain, so the plugin failed on enable with `ClassNotFoundException: EditableInventory`. These classes are now implemented inside the plugin: `fr.phoenixdevt.mmoprofiles.bukkit.gui.lib` (editable inventories from the `language/gui/*.yml` files, navigator with blocked closing and delayed reopen, placeholders, next/previous page and go-back items, click listener) and `bukkit.utils.Attributes` / `bukkit.utils.Sounds` (registry based lookup that accepts old `GENERIC_`/`PLAYER_` names). The imports of the plugin classes point to them; the former API stubs were removed.
- `PlayerListener`: the deprecated `PlayerSpawnLocationEvent` replaced by Paper's `AsyncPlayerSpawnLocationEvent`.

## Build inputs

- MythicLib is built from the sibling folder: the `buildMythicLib` task runs `shadowJar` of `../MythicLib-Restored` before anything is compiled, so a plain `gradle shadowJar` works from a clean clone that contains both folders.
- External libraries: only public Maven artifacts (Paper API, Velocity API, LuckPerms API, Vault API, PlaceholderAPI, commons-lang, commons-lang3, annotations).
- Integrations: compile-only stubs in `stubs/protocollib` (the few ProtocolLib classes used by the proxy mode). They are never packaged.
- No original jar is used; the build files do not mention `originals/`.

Limits of the GUI reimplementation: item names and lore support `&` and `&#rrggbb` colours and `{placeholders}` of the plugin, but not PlaceholderAPI placeholders inside GUI items.

## Risks on Paper 26.3

- `plugin.yml` keeps `api-version: 1.13` from the original, so Paper treats the plugin as a legacy plugin.
- `Attributes.fromName(...)` and `adaptName` (attribute names with and without the `GENERIC_`/`PLAYER_` prefix in saved profile data) depend on the MythicLib implementation of `Attributes`.
- The proxy mode needs ProtocolLib at runtime; its packet classes (`PLAYER_INFO`, `PLAYER_INFO_REMOVE`, `CHAT_SESSION_UPDATE`) were not checked on 26.3.

## Building

```sh
gradle shadowJar
```

Gradle runs on JDK 25; the bytecode is Java 21.
