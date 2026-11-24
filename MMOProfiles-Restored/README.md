# MMOProfiles 1.1.1

Rebuildable sources of MMOProfiles 1.1.1 (RPG profile selection for Paper and Velocity). The code was recovered from the jar `MMOProfiles-1.1.1-36-4` with Vineflower and repaired until it compiles.

## Status

- All source files of `fr.phoenixdevt` compile with 0 errors against Paper API 26.3, the Velocity API and MythicLib. `gradle shadowJar` produces `build/libs/MMOProfiles-1.1.1-restored.jar`.
- Started on a scratch Paper 26.3 server (build 253, offline mode) together with `MythicLib-1.7-restored.jar`: the plugin enables without errors, loads both GUI configs and `/mmoprofiles reload` works.
- GUI verified with a headless protocol client that sends real container-click packets (plus a throw-away helper plugin outside this repository that gives items and dumps inventories): the profile selection opens by itself on login; clicking an empty slot creates a profile and selects it; a left click on an existing profile selects it; right click opens the "Character Deletion" GUI, "No" returns to the list and "Yes" removes the profile and refreshes the list; the "Profile Removal" toggle changes the title and makes left clicks open the deletion GUI; the next/previous page items switch pages. Switching profiles swaps the vanilla inventory, and together with MMOInventory also the contents of its custom slots. Profile data is saved to `userdata/<uuid>.json` and read back after a relog and after a restart.
- `/mmoprofiles`, `reload`, `export-data` (with the warning about logged-in players) and `open <player>` run from the console; `open` for a player who has already chosen a profile throws `Player has already chosen a profile`, as the code of the original does.

## Changes against the decompiled code

- Only the plugin's own packages are in `src/`: `fr.phoenixdevt.mmoprofiles` (bukkit, shared, velocity) and the API package `fr.phoenixdevt.profiles` used by MythicLib, MMOCore, MMOItems and MMOInventory.
- The original jar bundles about 3000 third-party classes (Guava, Gson, Apache Commons, Checker Framework, JetBrains annotations, `javax`). They are not part of the sources; they are compile-only Maven dependencies because Paper and Velocity provide them at runtime. The shadow jar contains only the plugin's own classes.
- `ProxyBasedProfiles`: raw `List` cast replaced by `List<PlayerInfoData>`.
- `ConfigManager`: the sound name written to `messages.yml` is the key of the Bukkit sound.
- `ProfileListViewer`: `UtilityMethods.getPageNumber` replaced by an inline ceiling division.
- Runtime fix: the original code uses the GUI framework of a newer MythicLib (`io.lumine.mythic.lib.gui.editable.*`, `gui.Navigator`, `version.Attributes`, `version.Sounds`), which MythicLib 1.7 does not contain, so the plugin failed on enable with `ClassNotFoundException: EditableInventory`. These classes are now implemented inside the plugin: `fr.phoenixdevt.mmoprofiles.bukkit.gui.lib` (editable inventories from the `language/gui/*.yml` files, navigator with blocked closing and delayed reopen, placeholders, next/previous page and go-back items, click listener) and `bukkit.utils.Attributes` / `bukkit.utils.Sounds` (registry based lookup that accepts old `GENERIC_`/`PLAYER_` names). The imports of the plugin classes point to them; the former API stubs were removed.
- Local variable names were renamed by a tool (bytecode unchanged).
- `gui.lib.Navigator` (our reimplementation): the delayed reopen task and `popOpen` call `MMOPlayerData.getPlayer()` for a player who has already left, which threw `NullPointerException: Player is offline` every time a player disconnected with a selection GUI open; they now check `isOnline()` first.
- The ProtocolLib compile stubs now have the same signatures as the real ProtocolLib API used by the original jar (`PacketListener`, `EquivalentConverter`, `NativeGameMode`, `WrappedChatComponent`, `WrappedRemoteChatSessionData`), so the compiled ProtocolLib references are identical to the original jar's (compared with `javap -v`); with the old stubs the proxy mode would have thrown `NoSuchMethodError`.
- `PlayerListener`: the deprecated `PlayerSpawnLocationEvent` replaced by Paper's `AsyncPlayerSpawnLocationEvent`.

## Build inputs

- MythicLib is built from the sibling folder: the `buildMythicLib` task runs `shadowJar` of `../MythicLib-Restored` before anything is compiled, so a plain `gradle shadowJar` works from a clean clone that contains both folders.
- External libraries: only public Maven artifacts (Paper API, Velocity API, LuckPerms API, Vault API, PlaceholderAPI, commons-lang, commons-lang3, annotations).
- Integrations: compile-only stubs in `stubs/protocollib` (the few ProtocolLib classes used by the proxy mode). They are never packaged.
- No original jar is used; the build files do not mention `originals/`.

## Building

```sh
gradle shadowJar
```

Gradle runs on JDK 25 (toolchain); the bytecode is Java 21. The output is `build/libs/MMOProfiles-1.1.1-restored.jar`.

## Not verified

- Anything that needs a real Minecraft client: the test client does not render, move or parse chunks, so the profile spawn teleport, the profile-selection spawn location and skin/head textures were not seen.
- The default `profile-select` script (`sound{sound=ENTITY_PLAYER_LEVELUP}`) logs `Could not execute mechanic ... Non [a-z0-9/._-] character in path of location: minecraft:ENTITY_PLAYER_LEVELUP` on Paper 26.3; the sound mechanic belongs to MythicLib (`Sound.valueOf`), so it is not changed here; the remaining mechanic of that script was not checked.
- The proxy mode (needs ProtocolLib and a Velocity proxy; the ProtocolLib packet classes `PLAYER_INFO`, `PLAYER_INFO_REMOVE`, `CHAT_SESSION_UPDATE` were not checked on 26.3) and the MySQL data mode were not run.
- Combination with MMOCore and with LuckPerms/Vault/PlaceholderAPI was not run (only MythicLib, MMOItems and MMOInventory were installed).
- `plugin.yml` keeps `api-version: 1.13` from the original, so Paper treats the plugin as a legacy plugin.
- `Attributes.fromName(...)` and `adaptName` (attribute names with and without the `GENERIC_`/`PLAYER_` prefix in saved profile data) depend on the implementation in `gui/lib`/`utils`; saved profile attributes from a real server were not loaded.
- GUI reimplementation: item names and lore support `&` and `&#rrggbb` colours and `{placeholders}` of the plugin, but not PlaceholderAPI placeholders inside GUI items. Its behaviour was reconstructed from how the plugin uses it and checked as described above, not compared against the newer MythicLib implementation.
