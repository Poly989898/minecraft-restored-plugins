# MMOInventory 1.9.1

Readable sources of MMOInventory 1.9.1 (custom RPG inventory, `net.Indyuce.inventory`, plus the `ru.endlesscode.rpginventory` compatibility classes), recovered from the original jar and built against Paper 26.3.

## Status

- Compiles with 0 errors against Paper API `26.3.build.254-stable` (bytecode: Java 21, built on JDK 25) and builds `build/libs/MMOInventory-1.9.1-restored.jar`.
- No original jar is an input of the build (nothing under `../originals` is referenced).
- Verified on a scratch Paper 26.3 (build 253, offline mode) server with a headless protocol client as the test player (the client sends real container-click packets) and a throw-away helper plugin that is not part of this repository (it only gives items, dumps inventories and logs events):
  - With `MythicLib-1.7-restored.jar` alone: the plugin enables, `/mmoinventory` opens the GUI, `/mmoinventory reload` works. Without MMOItems the 8 slots that use the `mmoitemstype` restriction are skipped with a warning (normal behaviour).
  - Clicks: equipping and unequipping helmet, boots and off-hand slots (the vanilla armor slots follow), a wrong item type in the helmet slot is rejected, a stack of 2 is rejected (`disable-equiping-stacked-items`), clicking the empty-slot placeholders does nothing, shift-click equip and shift-click unequip. Equipped items are still there after a relog and after a server restart.
  - `/mmoinventory <player>` opens another player's inventory with the `inventory-name.other` title; an item equipped through it appears in the target player's own open GUI at once.
  - Together with MythicLib and the real `MMOItems-6.10-restored.jar` built from `../MMOItems-Restored` ("Hooked onto MMOItems", 13 slots): the `mmoitemstype` restriction accepts and rejects MMOItems by type, MMOItems talismans, ornaments and gauntlets can be equipped by drag and by shift-click, the equipped MMOItems stay in the yaml data across a restart, and MMOItems' own `getInventory()` lists them as `ACCESSORY` items.
  - Together with MMOProfiles: the contents of the custom slots belong to the selected profile (a new profile has empty slots, the first profile gets its items back).
  - Death with `keepInventory` off drops the items of the custom slots after the fix below.
- The two defects found at runtime are fixed (see "Changes against the decompiled code").

## Changes against the decompiled code

- Decompiler output needed no repairs to compile; local variable names were renamed by a tool (bytecode unchanged).
- The compile-only stubs in `stubs/` had signatures that differed from the real plugins, so the compiled calls did not match the original jar. The most visible case, `MMOItems.getLanguage()` returning `MMOItems$Language` instead of `net.Indyuce.mmoitems.manager.ConfigManager`, threw `NoSuchMethodError` in `DeathDrops` on every player death when MMOItems was installed. The stubs were corrected (MMOItems `getLanguage()` / `PlayerData.get(OfflinePlayer)`, MMOCore `profess.PlayerClass`, and the AureliumSkills, Heroes, SkillAPI, Fabled, RacesAndClasses, Skills, SkillsPro and McRPG classes) so that the external method, constructor and field references of the compiled jar are now identical to the ones in the original jar (compared with `javap -v` over all classes outside the plugin's own packages). No source file of the plugin itself changed.
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

- The test client has no world data (chunk packets are not parsed) and does not move, so everything that needs real player movement, real mobs or the vanilla client UI is untested: the optional `inventory-button` and `no-custom-inventory` modes, the resource-pack option, drag, number-key and drop clicks.
- SQL player data (`mysql.enabled`) and `/mmoinventory export-data`, which fails inside MythicLib's `DataExport` (`listFiles()` of a missing `userdata` folder returns null), not in this plugin.
- Hooks for third-party plugins (AureliumSkills, Heroes, SkillAPI, Fabled, mcMMO, Skills, SkillsPro, McRPG, BattleLevels, RacesAndClasses, RPGPlayerLeveling, Oraxen, PlaceholderAPI) and the MMOCore hook: none of these plugins was running. Their calls into the stubs now match the original bytecode, but no real plugin was loaded to prove it.
- With MMOItems running, the `PlayerData.getInventory().getEquipped()` list stayed empty even for a sword in the main hand (with and without MMOInventory), so MMOItems' permanent potion effects of equipped talismans could not be observed. This happens in MMOItems/MythicLib, not in this plugin; MMOItems itself does see the items in the custom slots.
- Gameplay with the equipped items (stats, abilities) is not tested.
