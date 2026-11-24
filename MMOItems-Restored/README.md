# MMOItems 6.10

Readable sources of MMOItems 6.10 (`net.Indyuce.mmoitems`), recovered from the original jar and built against Paper 26.3.

## Status

- Compiles with 0 errors against Paper API `26.3.build.254-stable` (bytecode: Java 21, built on JDK 25) and builds `build/libs/MMOItems-6.10-restored.jar`.
- The build was checked from a clean state (`rm -rf build`, then `gradle --offline shadowJar`).
- The set of top-level classes in the jar is identical to the original jar; the anonymous class `TemplateMap$1` (removed with the lambda fix) and the `META-INF/maven` pom files are the only other differences. Resources (`plugin.yml`, `config.yml`, `default/`) are taken from the original jar. The original jar is not an input of the build.
- Started on a Paper 26.3 server (build 253, offline mode) together with the restored MythicLib 1.7: 40 item types, 10 tiers, 9 sets, 3 crafting stations, 37 recipes and 171 skills load; console commands (`reload`, `list`, `itemlist`, `allitems`, `stations list`, `create`, `copy`, `delete`, `revid`, `update`, `drop`, `giveall`, `debug`) run without errors.
- Commands that need a player were run with a stand-in `Player` object (a dynamic proxy in a throw-away helper plugin that is not part of this repository, since no client for protocol 777 was available): `give` for all 216 default items, `browse`, `edit`, `stations open` for all 3 stations, join and interact events with a sword, staff, bow, consumable and tool, a hit with a sword and an armor piece. No errors.
- Local variables are renamed from the decompiler names (`var1`, ...); only the synthetic capture-field names of 8 anonymous classes differ from the build before the rename.

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
- Workaround for a defect in MythicLib 1.7 as restored earlier (`FriendlyFeedbackProvider.sendAllTo(ConsoleCommandSender)` called itself, StackOverflowError while loading templates): `TemplateManager` and `RecipeEditorGUI` call the `CommandSender` overload. The cause is fixed in MythicLib-Restored as well.

### Stubs

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

## Not verified

- Real client play: GUI item moving, combat damage and stats, skills firing, crafting in a station, upgrading, gems, drops by mobs.
- Anything that needs MMOCore, MMOProfiles or other integrations installed; the integrations were compiled against hand-written stubs only. `/mmoitems generate <player>` needs a real online player.
- MMOInventory 1.9.1 (restored) together with this plugin: MMOInventory failed with a `NoSuchMethodError` on `MMOItems.getInventory()` because its compile stub had the wrong return type (`InventoryManager` instead of `PlayerInventoryHandler`). The stub is fixed in `../MMOInventory-Restored`; the combination was not started again.
- Default-config messages seen in the log: `LUCKY_CHARM` uses the tier `NORMAL`, which `item-tiers.yml` does not define, and stat messages such as `required-dexterity` exist only with MMOCore installed.
