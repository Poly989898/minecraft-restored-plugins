# NOTICE

Every plugin in this repository is an unofficial, modified version of the original. None of them is endorsed by or affiliated with the original authors, who remain the authors of the code.

## Ultimate Model Engine R4.1.1

- Original: ModelEngine by Ticxo.
- Source: decompiled from the R4.1.1 build (Vineflower), repaired to compile. Code builds from src/ and patches/; the original jar is not needed for code.
- Resources are not in this repository: config.yml, internal/, pack/, plugin-data.json were copied from the original jar. Copy them into resources/ yourself (see the plugin README).
- Changes:
  - The buyer watermark resource (me/lucko/shadow/play.rotmc.net) is removed. No license check or activation code was found in the recovered source.
  - MineSkin skin generation is reimplemented with plain HTTP (MineSkinGeneratorService), because the MineSkin Java client is not published to a public repository. The protocol follows the public client. It is not tested with a real API key.
## Nexo 1.26

- Original: Nexo by boy0000 (release jar 1.26).
- Source: decompiled from the 1.26 release jar (Vineflower, CFR), repaired to compile. The build uses Maven dependencies and the sources only; the original jar is not used.
- Data files in `src/` (`*.yml`, `messages/`, `items/`, `glyphs/`, `shaders/`, `custom_armor/`, `META-INF/`) are byte-identical to the files of the original jar.
- Changes:
  - Libraries: Gson, Adventure (`adventure-platform-bukkit`), EvalEx and error_prone annotations are Maven dependencies. The bundled `com/google` sources are not compiled. Other third-party APIs are compile-only.
  - Compile-only stubs of APIs that are not on the build classpath (Axiom, BentoBox, CrashClaim, Factions, HuskClaims, HuskTowns, mclo.gs, NoBuildPlus, Vacan, Vulcan, TAB, Spartan, and `kotlin/time/Duration`) are never packaged. The jar contains no class from them.
  - `ProtectionLib` resolves protection plugins by class name inside a `try`, so missing plugins are skipped. WorldGuard flags are registered only when the WorldGuard classes exist. WorldEdit-only code is in its own class, so the plugin loads without WorldEdit.
  - Other optional integrations in `compatibilities/` resolve their partner plugins by name. Not re-tested one by one.
  - NMS adapters for Minecraft 1.21.x and `v26_R1` are not compiled. Only `v26_R2` (Paper 26.3) is included.
  - Kotlin inline-class member names (`getZERO-UwyO8pc` and similar) are written as hyphenated JVM names in the compiled classes.
  - Behaviour change: `BannerBuilder.baseColor` changes the item material to `<COLOR>_BANNER` instead of setting the color on the banner metadata.
  - The bug in `CustomBlockData.getBlockFromKey` is kept: the leading empty token makes `Integer.parseInt` throw, so `getBlocksWithCustomData` returns nothing, as in the original.
  - Behaviour change: the "Unknown metadata part name" warning of the vendored Unnamed Creative code is printed to `System.out`. The original used `System.err`.
  - `token.secret`, a 232-byte credential resource of the original jar, is not included. Without it the log shows `Missing token-file`.
- Tested: clean build with `gradle jar`; startup on Paper 26.3 next to the other restored plugins (CMI, CMILib, LiteBans, LPX, MythicMobs, ModelEngine); `nexo reload` and `nexo` without exceptions.
- Not tested: gameplay, the resource pack in a client, the S3 and Polymath pack servers, and the real partner plugins (WorldEdit, WorldGuard, GriefPrevention, Towny and the others listed in the README).
## MythicMobs 5.14.0

- Original: MythicMobs.
- Source: decompiled from the 5.14.0 release jar (CFR, Vineflower), repaired to compile. The plugin and all NMS modules build from source, without the original or shaded jars.
- Changes:
  1. MythicBukkit.isVolatile() always returns true. Code that upstream gates behind its separate premium module is present in this code base and is enabled.
  2. bStats metrics are not started. The bStats classes are only bundled.
  3. The startup message and /mm version state that this is a modified build.
  4. AdventureBossBar.getTitle() returns the plain-text title (upstream called a method that no longer exists in current Adventure).
  5. BukkitEntityType.spawn calls World#spawn(Location, Class, SpawnReason, boolean, Consumer) reflectively with a fallback, because that overload is missing from the newest Paper API.
  6. Third-party libraries are Maven dependencies, not the shaded copies of the original. Packages are the original names (org.apache.commons.lang3, org.jooq, com.github.benmanes.caffeine, edis.clients.jedis, org.bstats), so the jar is not relocated. jedis is 3.10.0, because the 5.x line has no JedisExhaustedPoolException.
  7. Platform bridges for Paper and Spigot (PaperPlatformBridge, SpigotPlatformBridge) are written from the interface, not recovered. They only parse item component config.
  8. The unused NBT reflection layer (utils/nbt, utils/shadows) is removed. Nothing referenced it, so the Lumine Shadow library is not needed.
  9. Third-party plugin APIs are real artifacts where public, otherwise compile-only stubs that are compiled with the plugin and never packaged.
## LiteBans 2.19.0

- Original: LiteBans by Ruan.
- Source: decompiled from the 2.19.0 jar (Vineflower, CFR), deobfuscated and renamed. Class and member names are inferred, not original.
- Changes:
  - Update checks, phone-home calls, a trust-all TLS verifier, an author verification command and buyer-ID watermarks are removed.
  - Kotlin has no checked exceptions, so checked exceptions are rethrown through `SneakyThrow.rethrow` (same exception type, no `throws`). Behaviour matches the Kotlin bytecode.
  - Methods that were missing from the decompiled output are implemented by meaning instead of throwing `AbstractMethodError`.
  - Fabric and Mixin code is not part of the build.

## LPX 3.9.1

- Original: LPX by Ytnoos and contributors.
- Source: decompiled from the 3.9.1 jar (Vineflower 1.12.0), repaired by hand and with scripts.
- Changes:
  - Removed decoy methods, dead obfuscator code and the re-uploader print in `LPXBukkitPlugin.onEnable`.
  - Restored the names of bridges and check implementations that the obfuscator renamed, and substituted decrypted constants.
  - The bundled Adventure library is relocated from `net/kyori` to `it/ytnoos/lpx/lib/kyori` across the whole jar, so the plugin starts on Paper 26.3. The original jar does not start there.
  - Bundled libraries (PacketEvents, Adventure, Gson, bStats) are not part of `src/`.

## CMILib 1.6.1.3

- Original: CMILib by Zrips.
- Source: decompiled from the 1.6.1.3 jar (Vineflower), repaired to compile.
- Changes: decompiler artifacts are removed. The version parser reads Paper version strings such as `26.3.build.257` (only the leading digits of each part are a number).
## CMI 9.8.7.5

- Original: CMI by Zrips.
- Source: decompiled from the 9.8.7.5 jar (Vineflower, CFR and bytecode listings where the decompiler failed), repaired to compile.
- Changes:
  - The Spigot license check (`loadConfig0`, a phone-home request) is removed.
  - Version-specific NMS code is moved behind the `NMS` interface. The Paper 26.3 adapter is in `nms/v26_3`. Adapters for other versions are not included yet.
  - Decompiler artifacts (numbered locals, labels, GOTO, synthetic accessors) are rewritten by meaning.

## MythicLib 1.7

- Original: MythicLib by Indyuce.
- Source: decompiled from the 1.7 jar (Vineflower). Class, field and method names are original; local variable names are decompiler names. The build uses only the sources, `stubs/` and public Maven artifacts.
- Changes:
  - Decompiler defects are repaired so the sources compile; resources come from the original jar.
  - Paper 26.x wrapper `nms-v26` (paperweight module) is merged into the plugin jar. The decompiled wrappers for 1.14 - 1.21 are kept in `nms-legacy/` and are not built.
  - API that MMOCore, MMOItems, MMOInventory and MMOProfiles need and the 1.7 jar lacks is implemented from scratch: `gui.editable.*`, `gui.Navigator`, `gui.util.IconOptions`, `version.Attributes`, `version.Sounds`, `Skill` constructors, `StatMap.bufferUpdates`, `PlayerModifier.registerPlayerModifierType` and related helpers. `gui.PluginInventory` is reworked to belong to a `Navigator`; the global click dispatch in `PlayerListener` is removed.
  - Compile-only stubs of optional integrations are in `stubs/` and are not packaged.
- Tested: startup and `/ml reload` on Paper 26.3; GUI framework opened and clicked through together with MMOCore using a headless test client.
- Not tested: gameplay behaviour (stats, skills, damage, crafting, SQL) and integrations.

## MMOCore 1.13.1

- Original: MMOCore by Indyuce.
- Source: decompiled from the 1.13.1 jar (Vineflower). Class, field and method names are original; local variable names are decompiler names.
- Changes:
  - Decompiler defects are repaired so the sources compile; resources come from the original jar. The jar has the same classes as the original; the `META-INF/maven` pom files are not included.
  - `SmallParticleEffect` passes `Particle.Spell` data for particles that require it on Paper 26.3.
  - The mcMMO party lookup uses `mcMMO.p.getPartyManager()` (mcMMO 2.2.x).
  - `PlayerClass`: the obfuscator annotation `@InsnComment` is removed.
  - Compile-only stubs of optional integrations are in `stubs/` and are not packaged.
  - The plugin compiles against the restored MythicLib, which implements the MythicLib API that MMOCore needs.
- Tested: startup, reload, admin commands, player data save and load, and the main GUIs on Paper 26.3 with a headless test client.
- Not tested: real client input, skill casting and combat, professions, loot, quest objectives, the SQL backend and integrations.

## MMOItems 6.10

- Original: MMOItems by Indyuce.
- Source: decompiled from the 6.10 jar (Vineflower). Class, field and method names are original; local variable names are decompiler names.
- Changes:
  - Decompiler defects are repaired so the sources compile (restored generic types, lost locals, field-initializer lambdas, sneaky-throw helper for rethrown checked exceptions). Resources come from the original jar.
  - The set of top-level classes equals the original jar. The anonymous class `TemplateMap$1`, the unreferenced blob `MMOItems20.txt` and the `META-INF/maven` pom files are not included.
  - Compile-only stubs of optional integrations and of the MMOCore API (MMOCore depends on MMOItems) are in `stubs/` and are not packaged.
- Tested: startup, reload and console commands on Paper 26.3 with MythicLib (see the plugin README).
- Not tested: play with a real client and the integrations with other plugins.

## MMOInventory 1.9.1

- Original: MMOInventory by Indyuce.
- Source: decompiled from the 1.9.1 jar (Vineflower). Class, field and method names are original; local variable names were restored by a rename tool (bytecode unchanged).
- Changes:
  - The decompiled sources compile without repairs; resources come from the original jar.
  - Compile-only stubs of optional integrations, of the used part of MMOItems and of MMOCore are in `stubs/` and are not packaged.
- Tested: startup and `/mmoinventory reload` on Paper 26.3 with MythicLib.
- Not tested: opening the inventory, equipping, death drops, player data, and the MMOItems and MMOCore integrations.

## MMOProfiles 1.1.1

- Original: MMOProfiles by Phoenix Development.
- Source: decompiled from the 1.1.1 jar (Vineflower). Local variable names were restored by a rename tool (bytecode unchanged). Bundled third-party libraries are not part of the sources.
- Changes:
  - The GUI framework that MythicLib 1.7 lacks (`gui.editable`, `Navigator`, `Attributes`, `Sounds`) is implemented inside the plugin.
  - `PlayerSpawnLocationEvent` is replaced by `AsyncPlayerSpawnLocationEvent`.
  - Small decompiler repairs in `ProxyBasedProfiles`, `ConfigManager` and `ProfileListViewer`.
- Tested: startup, GUI config loading and `/mmoprofiles reload` on Paper 26.3 with MythicLib.
- Not tested: the profile GUI with a player, proxy mode and database modes.
