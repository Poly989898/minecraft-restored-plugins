# MMOItems 6.10

Readable sources of MMOItems 6.10 (`net.Indyuce.mmoitems`), recovered from the original jar and built against Paper 26.3.

## Status

- Compiles with 0 errors against Paper API `26.3.build.254-stable` (bytecode: Java 21, built on JDK 25) and builds `build/libs/MMOItems-6.10-restored.jar`.
- The build was checked from a clean state (`rm -rf build`, then `gradle --offline shadowJar`).
- The set of top-level classes in the jar is identical to the original jar. Resources (`plugin.yml`, `config.yml`, `default/`) are taken from the original jar. The original jar is not an input of the build.
- Not verified: behaviour in game. The plugin was not started on a server yet. The integrations (Denizen, BossShop, Heroes, AuraSkills, MMOCore, ...) were compiled against hand-written stubs only, never against the real plugins.

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
