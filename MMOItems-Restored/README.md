# MMOItems 6.10

Readable sources of MMOItems 6.10 (`net.Indyuce.mmoitems`), recovered from the original jar and built against Paper 26.3.

## Status

- Compiles with 0 errors against Paper API `26.3.build.254-stable` (bytecode: Java 21, built on JDK 25) and builds `build/libs/MMOItems-6.10-restored.jar`.
- The build was checked from a clean state (`rm -rf build`, then `gradle --offline shadowJar`).
- The set of top-level classes in the jar is identical to the original jar; the anonymous class `TemplateMap$1` (removed with the lambda fix) and the `META-INF/maven` pom files are the only other differences. Resources (`plugin.yml`, `config.yml`, `default/`) are taken from the original jar. The original jar is not an input of the build.
- Started on a Paper 26.3 server (build 253, offline mode) together with the restored MythicLib 1.7; see "Verified in-server" below.
- Not verified: the integrations (Denizen, BossShop, Heroes, AuraSkills, MMOCore, ...) were compiled against hand-written stubs only and never run against the real plugins.

## Changes against the decompiled code

Decompiler defects fixed so that the code compiles (no behaviour change intended):

- Raw collection types restored to their real element types (`ArrayList<String>`, `HashMap<Integer, ItemStack>`, `Entry<ItemSet, Integer>`, `ExperienceSourceManager<RepairItemExperienceSource>`, ...) in about 40 methods.
- `CanBreak.whenApplied`: lost local restored (`Material::valueOf` mapping, then `setCanMine`), taken from the original bytecode.
- `ModifierNode` and `CraftingStation`: lambdas in field initializers read final fields before they are assigned; the `ModifierNode` lambda is now the method `loadChildrenAndStats`, `CraftingStation` reads the id through `getId()`.
- `ConfigManager.YamlConverter.convert` and `RPGHandler.PluginEnum.load`: the original rethrows checked exceptions without declaring them (sneaky throws); this is reproduced with a small `sneakyThrow` helper, the public signatures are unchanged.
- `MMOItemsOnUseAura.Tracker`: super constructor call of the inner class `Aura.AuraTracker` written with the implicit enclosing instance.
- `Tool` bouncing-crack runnable: `Tool.super.<field>` replaced by the anonymous class's own fields.
- `MMOItemsPlaceholders`: `Type` is spelled out as `net.Indyuce.mmoitems.api.Type` (the inherited `PlaceholderExpansion.Type` shadowed the import).
- `MMOCoreMMOLoader`, `UpdateItemCompletion`, `TemplateMap`, `StatManager`, `MMOUtils`, `HeroesHook`, Denizen `PropertyParser.registerTag`: explicit generic types / type witnesses.
- Missing imports restored in `MMOItems` (`MMOItemUIFilter`, `MMOItemReforger`, `RecipeTypeListGUI`) and `MMOCoreMMOLoader` (`CustomBlock`).
- `net/Indyuce/mmoitems/MMOItems20.txt` of the original jar (an unreferenced binary blob) is not carried over.

## Verified in-server

Setup: clean Paper 26.3 server, `MythicLib-1.7-restored.jar` + `MMOItems-6.10-restored.jar`, default MMOItems config generated on first start.

Checked and clean (no exception in the log):

- Enable: all 40 item types, 10 tiers, 9 sets, 2 upgrade templates, 3 crafting stations, 37 recipes and 171 skills load; the plugin stays enabled.
- Console commands: `/mmoitems reload`, `reload all|recipes|stations|skills`, `list type|ability|lute`, `itemlist`, `allitems`, `stations list`, `create`, `copy`, `delete`, `revid increase|decrease`, `update list|info|apply`, `drop`, `giveall`, `debug info|test|checktags`.
- Commands that need a player (`give`, `browse`, `edit`, `stations open`, `item repair|identify`, `generate` usage) were run as a stand-in `Player` object (a dynamic proxy in a throw-away helper plugin, not part of this repository, because no Minecraft client for protocol 777 was available). With it: `give` was run for all 216 default items without an error (items are built and added to the inventory, names and lore are present); `browse` and `edit` open their GUIs and survive clicks; `stations open` works for all 3 default stations; join and interact events with an MMOItems sword, staff, bow, consumable, tool and a hit with a sword and armor piece produced no errors.
- The only errors in the log are a default-config problem (`LUCKY_CHARM` uses the tier `NORMAL`, which does not exist in the default `item-tiers.yml`) and messages about MMOCore stats (`required-dexterity` and similar) that exist only with MMOCore installed.

Not verified: real client play (GUI item moving, actual combat damage and stats, skills firing, crafting in a station, upgrading, gems, drops by mobs), anything that needs MMOCore / MMOProfiles / other integrations installed, `/mmoitems generate <player>` (needs a real online player).

Defect found and worked around in this plugin: `FriendlyFeedbackProvider.sendAllTo(ConsoleCommandSender)` of the restored MythicLib calls itself (infinite recursion, StackOverflowError), which aborted the enable of MMOItems while loading templates. MMOItems now calls the `CommandSender` overload (`TemplateManager`, `RecipeEditorGUI`). The cause is still in MythicLib (`sendAllTo(console)` must call `sendAllTo((CommandSender) console)`).

MMOInventory 1.9.1 (restored) does not enable together with this plugin: its `MMOItemsCompatibility` calls `MMOItems.getInventory()` expecting the stub class `comp.inventory.InventoryManager`, while the real MMOItems 6.10 returns `comp.inventory.PlayerInventoryHandler` (the original MMOInventory jar references `PlayerInventoryHandler`), so a `NoSuchMethodError` is thrown. The wrong stub is in `../MMOInventory-Restored/stubs`.

## Stubs

Compile-only API stubs of optional integrations live in `stubs/` and are never packaged. Their signatures cover only what MMOItems calls:

- RPG and level plugins: Heroes, AuraSkills, AureliumSkills, RPG Player Leveling, BattleLevels, Skills, SkillsPro, McRPG, Fabled, RacesAndClasses.
- Other: Denizen (`denizencore`, `denizen`), BossShop, PhatLoots, CrazyEnchantments, AdvancedEnchantments, MythicEnchants, RPGInventory, and the MMOProfiles API (`fr.phoenixdevt.profiles`).
- MMOCore (`net.Indyuce.mmocore.*`): MMOCore depends on MMOItems, so the cycle is broken by signature-only stubs written by hand after the sources in `../MMOCore-Restored`. They are not generated automatically; if the MMOCore signatures change they must be updated by hand.

Vault, mcMMO, Citizens, Mythic, PlaceholderAPI, WorldEdit and the rest come from public Maven artifacts.

## Build inputs

`build.gradle.kts` references no jar files and nothing under `originals/`:

- Paper API, libraries and the integrations above that have public artifacts: Maven repositories (offline builds use the Gradle cache).
- MythicLib: built from `../MythicLib-Restored` by the `buildMythicLib` task (a nested Gradle build running `shadowJar`), which `compileJava` depends on. A clean clone therefore needs the sibling folder, but no prebuilt jar.
- MMOCore and the other integrations: only the stubs from `stubs/`.

## Building

```sh
gradle shadowJar
```

JDK 25 is required (Gradle toolchain). The jar needs MythicLib at runtime (`depend` in `plugin.yml`).
