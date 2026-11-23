# Nexo 1.26 Restored Edition

Source code of Nexo 1.26 (custom items, blocks, furniture, glyphs and resource pack generation for Paper), recovered from the release jar and repaired to compile. The jar is built from these sources, Maven dependencies and the data files in `src/`. The original jar is not used.

Target server: Paper 26.3 (NMS adapter `v26_R2`). Other Minecraft versions are not compiled.

## Layout

| Path | Content |
|---|---|
| `src/` | Java sources (`com/nexomc/nexo`, vendored `team/unnamed/creative`, `com/nexomc/libs`, `com/nexomc/customblockdata`, `com/nexomc/protectionlib`) and the data files (`paper-plugin.yml`, `*.yml`, `messages/`, `items/`, `glyphs/`, `shaders/`, `custom_armor/`, `META-INF/`) |
| `stubs/` | Compile-only signatures of optional plugin APIs. Never packaged (see below) |
| `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties` | Build |

## Build

Requirements: JDK 25 and Gradle 9.x. The Maven repositories listed in `build.gradle.kts` must be reachable.

```sh
gradle jar
```

Output: `build/libs/nexo-1.26-restored.jar`. The sources compile without any exclusion file.

Clean build of this version (Windows, Gradle 9.9.1, JDK 25): `BUILD SUCCESSFUL`, 0 compile errors, 40 compiler warnings. The jar has 2,350 class files and 2,833 entries (404 of them are directory entries).

## Replacements compared with the original jar

- **Original jar removed from the build.** Earlier builds copied the original jar and recompiled classes into it. Now `gradle jar` packages the compiled classes, the runtime dependencies and the resources of `src/`. The `tools/` scripts that repacked the original jar (`package.sh`, `package.ps1`, `testserver.*`, `fixnames.py`, `Check.java`, `Verify.java`) are removed.
- **Data files.** All non-Java files in `src/` are byte-identical to the files of the original jar (75 files). They are included as data.
- **Maven instead of bundled copies.**
  - Bundled into the jar: `gson` 2.13.2 (replaces the bundled `com/google` sources), `adventure-platform-bukkit` 4.4.1, `EvalEx` 2.7, `error_prone_annotations` 2.36.0.
  - Compile-only (provided by the server or by the partner plugin at runtime): MMOItems-API 6.10.1-SNAPSHOT, MythicLib-dist 1.7.1-SNAPSHOT, Mythic-Dist 5.14.0 (MythicMobs API), ModelEngine R4.0.9, GriefPrevention 16.18.4, Residence 6.0.2.3, LandsAPI 7.25.4, PlotSquared 7.6.0, Towny 0.100.2.11, Guice 7.0.0, PlaceholderAPI 2.11.6, BlockLocker 1.13, FastAsyncWorldEdit 2.16.2, WorldEdit 7.3.9, WorldGuard 7.0.12, lin-bus 0.2.0, Spring 6.1.14, httpclient5 5.4.1, AWS SDK S3 2.29.0, authlib 7.0.63, Paper dev bundle 26.3.build.254-stable.
  - Kotlin stdlib 2.2.20 is compile-only. The server provides it at runtime, as the original jar relies on.
- **Stubs are not packaged.** `stubs/` holds signatures for APIs that are not on the build classpath: Axiom (`com/moulberry/axiom`), BentoBox (`world/bentobox`), CrashClaim (`net/crashcraft`), Factions (`com/massivecraft/factions`), HuskClaims and HuskTowns (`net/william278`), mclo.gs (`gs/mclo/java`), NoBuildPlus (`p1xel/nobuildplus`), Vacan (`ai/idealistic/vacan`), Vulcan (`me/frep/vulcan`), TAB (`me/neznamy/tab`), Spartan (`me/vagdedes/spartan`), and the `kotlin/time/Duration` stub. The build drops every class compiled from `stubs/` from the jar. In the clean build the jar contains 0 stub class files and no `kotlin/time/Duration*` class. Some empty directory entries of these packages remain.
- **Kotlin `Duration` call sites.** The stub cannot spell Kotlin's hyphenated inline-class names (for example `getZERO-UwyO8pc`). `build.gradle.kts` rewrites the underscore names in the compiled classes that reference `kotlin/time/Duration` to the hyphenated names. The rewrite keeps the byte length.
- **ProtectionLib rewritten.** `com.nexomc.protectionlib` resolves each protection plugin class by name inside a `try`. A missing plugin is skipped, so the library loads without any of the optional plugins.
- **Optional integrations guarded.**
  - `NexoPlugin.onLoad` looks up the WorldGuard flag classes with `Class.forName` before it registers flags.
  - `WrappedWorldEdit` moved its WorldEdit-only code into `WorldEditParserRegistrar`, so `WrappedWorldEdit` loads without WorldEdit.
  - The other classes of `compatibilities/` were rewritten to resolve partner plugins by name. They were not re-tested one by one.
- **NMS adapters.** `v1_21_R4`, `v1_21_R5`, `v1_21_R8`, `v1_21_R10`, `v1_21_R11` and `v26_R1` are excluded from compilation (`build.gradle.kts`). Only `v26_R2` and the shared `commons` code are compiled. `commons/EntityUtils.nextEntityId` goes through `CraftWorld.getHandle()`.
- **Vendored sources.** Unnamed Creative (`team/`) is compiled from its sources in `src/team`. Gson is taken from Maven, so `com/google` is not compiled from source.
- **Kotlin lambda singletons** were rewritten as Java classes in the earlier repair pass. This was not re-checked in the clean build.
- **Resource pack token.** `token.secret` is not included (see Behaviour changes).

## Behaviour changes

- `BannerBuilder.baseColor(DyeColor)` changes the item material to `<COLOR>_BANNER` (for example `RED_BANNER`). If that material does not exist, the item is returned unchanged. The original set the color on the banner metadata.
- `CustomBlockData.getBlockFromKey` keeps a bug of the original. The key is split on `[xyz]`, which gives an empty first token; `Integer.parseInt("")` throws and the method returns no block. As a result `getBlocksWithCustomData` returns an empty collection. The bug is kept as it is in the original.
- The vendored Unnamed Creative `MetadataSerializer` prints its "Unknown metadata part name" warning to `System.out`. The original printed it to `System.err`. On the server the line appears as `[STDOUT]`, and Paper logs a "System.out/err usage" notice for the author.
- `token.secret` is not included. `PackDownloader` reads that token from a file or from the jar resource. The original jar contained a 232-byte file under that name. It is a credential and is not published. Without it the log shows `Missing token-file, please contact the developer!`.
- Resource pack generation on reload uploaded the pack to `hermes.nexomc.com`, and the test reload received a URL from it. The behaviour without a token was not compared with the original.

## Tested

- Clean copy build: `gradle jar` `BUILD SUCCESSFUL`, 0 compile errors. The jar was started on a Paper 26.3 test server with the other restored plugins in `plugins/` (CMI, CMILib, LiteBans, LPX, MythicMobs, ModelEngine). Startup: `Done (19.4s)` and no exception in the log. Nexo detects MythicMobs and ModelEngine and enables their hooks.
- `nexo reload`: reloads dialogs, configs, items and recipes, and regenerates the resource pack. `nexo`: prints the command list. No exception after either command.

## Not tested

- Gameplay: placing and breaking blocks, furniture, mechanics, recipes and items in a client.
- The resource pack in a client, the S3 and Polymath pack servers, self-hosting.
- Real integrations: WorldEdit/FAWE, WorldGuard, GriefPrevention, Residence, Towny, PlotSquared, Lands, MMOItems, MythicCrucible, BlockLocker, Axiom, HuskClaims, HuskTowns, BentoBox, Factions, Vacan, Vulcan, Spartan, TAB, NoBuildPlus. They compile against Maven artifacts or stubs; their runtime behaviour is unknown.
- Servers other than Paper 26.3.

## Source notes

- Names such as `Class$method$lambda$0$$inlined$sortedBy$1.java` are Kotlin inline-function classes. Do not delete them: the bytecode refers to them.
- Local names of the original are not preserved. Names were recovered from the decompiled code.
