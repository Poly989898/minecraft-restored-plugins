# LiteBans 2.19.0

Deobfuscated, rebuildable sources of LiteBans 2.19.0, a ban, mute and warning plugin for Paper, Bungee and Velocity.

## Status

- `gradle compileJava jar` succeeds on JDK 25: 716 classes of the Paper, Bungee and Velocity parts. Fabric and Mixin (`tools/fabric-files.txt`, `litebans/mixin/`) are not part of the build because they need the Minecraft and Fabric jars.
- Run on Paper 26.3 (`tools/testserver.sh`, a separate server copy, H2): the plugin enables, templates and the database load, and the console commands `ban`, `tempmute`, `warn`, `checkban`, `history`, `banlist`, `staffhistory`, `unban`, `lockdown`, `dupeip` and `litebans reload|info|servers|license` work without exceptions. The same jar was also run next to Nexo, MythicMobs and ModelEngine without errors.
- Databases: H2, PostgreSQL and MariaDB (the MySQL driver against MariaDB) were tested with table creation and ban/warn/mute/history/unban. Player login (ban, temporary ban, unban through the login event) is covered by `tools/logintest.sh`.
- Not verified: mute in chat, Bungee, Velocity, Fabric, GeoIP, Discord.
- The sources are frozen. After the pipeline below ran, manual fixes and renames were applied on top, so running `tools/pipeline.sh` again overwrites them. Work directly in `src/`.

## Building

```sh
gradle compileJava jar        # build/libs/LiteBans-Restored.jar (without Fabric)
gradle printCp                # classpath for tools/Fixer.java and tools/Renamer.java -> .work/cp.txt
```

To re-run the pipeline the original jar is needed (`ORIG_JAR`, default `../originals/LiteBans/LiteBans-2.19.0-bm.jar`; not in git).

### Windows PowerShell

The `.sh` scripts have PowerShell counterparts in `tools/` with the same steps and environment variables:

```powershell
gradle compileJava jar                       # same build as above
$env:TOOLS = 'D:\Tools'                      # folder with asm\, vineflower\ and python\python.exe
tools\pipeline.ps1                           # regenerates src/main/java (needs the original jar)
$env:TEST_SERVER = 'D:\Paper\test-server'    # optional: folder with paper-26.3-*.jar, libraries, versions, cache
tools\testserver.ps1 -Commands 'litebans info;banlist'   # builds, starts a scratch Paper server, sends commands
```

Other variables: `ASM`, `VF`, `PY` and `ORIG_JAR` override single tool paths, `JAVA_HOME` selects the JDK, `GRADLE` the Gradle executable, `TMPDIR` the scratch folder (default: the system temp folder), `LB_SED` a `s/pattern/replacement/` applied to the test `config.yml`. `testserver.ps1` links `libraries`, `versions` and `cache` from `TEST_SERVER` with junctions and does not write into the repository.

## Pipeline

The original jar is obfuscated: classes such as `a0`, `kU`, `gZ` share one package, strings are encrypted, bytecode flags are deliberately corrupted, and Kotlin stdlib, HikariCP and Commons Compress are bundled and obfuscated too. `tools/pipeline.sh` runs these steps in order:

| Step | What it does |
|---|---|
| `Cleaner.java` | Repairs invalid flags (static on a class, final on `<clinit>`, missing `ACC_ENUM`) and opens synthetic fields and methods |
| `Strings.java` | Statically decrypts 11,568 strings (`hl.a(string, key)` is an XOR with the key) and inlines them as constants; no code from the jar is executed |
| `Lambdas.java` | Wraps method references with captured values so the decompiler shows real lambdas |
| `Locals.java` | Splits local variables: the obfuscator reused one slot for values of different types |
| `Deobf.java` | Unique method and field names (overloads by return type), enum constant names from `<clinit>` strings, `mappings/manual.map` |
| Vineflower 1.12.0 | Decompilation, followed by `resugar.py`, `postfix.py` and `Fixer.java` (javac diagnostics: types, casts, declarations, method stubs) |

After that the code was brought to 0 compile errors with `fixflow.py`, `initfix.py`, `addthrows.py`, `lambda-casts.py` and manual edits. Some classes come from CFR where it produced fewer errors (chosen per file, `choose.py`).

### Decompilation defects found in testing

Some code compiles but behaves differently: an overload chosen by javac from the static type (for example `ConfigFile.b(String,String)` instead of `b(String,Object)`), parameters lost with Kotlin default arguments (a bug in the `Locals` step, fixed, 156 methods rewritten), and merged local variable types. `tools/invcompare.py` with `tools/InvokeDump.java` compares invoked descriptors with the original bytecode, and `tools/TrapFix.java` handles a call used as a statement that picked a non-void overload.

### Differences from the jar

- Kotlin has no checked exceptions. To make Java accept the code, exceptions are rethrown through `SneakyThrow.rethrow` (same type, no `throws`), and `catch (X)` without a throwing call is rewritten as `catch (Throwable)` with a type check. Behaviour matches the Kotlin bytecode.
- There are no `throw new AbstractMethodError()` stubs left. Methods missing from the jar are implemented by meaning (`isSet`/`contains` of the config readers, the `ConfigFile` helper with a parent section, `isInitialized` of `InitializedLazyImpl`; `KotlinDatabaseSessionImpl`: a session without a database answers empty/`false`).
- Unreachable archive formats (ar, arj, cpio, dump, zip, jar) and their helper classes are removed: the plugin uses only `tar` (GeoIP download), and the other readers had no `getNextEntry()` in the original. `UByteArray` remnants (`KotlinCollectionImpl`) no longer implement `Collection`.
- Uninitialized locals get default values; `final` is removed from fields in a few constructors.

## Removed from the sources

- The update check (`litebans.d-n-s.name/update.php`) with buyer identifiers, the host name check that accepted any certificate, the `/litebans verify` subcommand (hard-coded author UUID), remotely controlled fields, a "random" block in `BanCommand`, the "Plugin access has been disabled" branches, a distributor banner and a static flag `LiteBansCommand.i_f` (always `""`) with checks that never did anything, and an empty `loadConfig0()`.
- Kept: `KotlinData5`/`Bitflipper` and the `litebans_cache8k` table (a compressed cache of offline player names for tab completion), the alt warning consent, and `/litebans license`, which only prints the licenses of third-party libraries.
- Network access in the code: Mojang sessionserver, MaxMind (GeoIP) and Maven Central (database drivers). No full audit was done.

## Names

No obfuscated names (`a_f`, `b_5`) remain in the buildable sources. Names are inferred from the code and call sites, so they are approximate:

- Kotlin stdlib, Commons Compress, HikariCP, JSON.simple/JFlex: named after the library prototypes (`Intrinsics`, `HikariPool`, `TarArchiveEntry`, `JsonLexer`, …).
- Core: `PluginModule`, `Platform`, `DatabaseModule`, `DatabaseSessionImpl`, `Settings`, `Punishment`, `PunishmentType`, `Message`, `AbstractCommand`, every command (`HistoryCommand`, `LockdownCommand`, …), platform bridges and listeners (`BukkitBridge`, `BungeeLockdownListener`, …). `Message` constants and `Settings` fields are named after the config and message keys.
- `BroadcastModule` holds the SQL dialect fragments (`sqlTrue`, `sqlFalse`, `sqlLikeOperator`, `historyColumns`, `collation`) selected by `setDatabaseType`.
- `DupeIpScanTask` is the dupeip scanner (`target`, `quiet`, `failed`, `bannedFound`, `mutedFound`, `multipleAddresses`, `results`, …).
- `ImportSource` is the base of the punishment importers (`tablePrefix`, `batchSize`, `batchDelayMillis`); the importers are named after their source (`AdvancedBanImport`, `MaxBansImport`, `BanManagerV4Import`/`V5`, `BanHammerImport`, `LibertyBansImport`, `UltrabansImport`, `BungeeAdminToolsImport`, `VanillaImport`).
- Discord webhook model: `DiscordEmbed`, `EmbedFooter`, `EmbedImage`, `EmbedAuthor`, `EmbedField`. `DriverDefinition` holds a database driver (name, class, URL, version, checksum). `TemplateInfo` holds `message`, `reason`, `broadcast`, `permission`, `actions`, `flags`. Duration limits: `DurationLimitGroup`, `DurationLimits`, `CommandCooldowns`.
- Tasks per `/litebans` subcommand (`AllowTask`, `ResetDatabaseTask`, `RevealTask`, `BroadcastCommandTask`, `BenchGenTask`/`BenchJoinTask`, …), platform listeners (`BukkitPreLoginListener`, `BukkitLoginListener`, `BukkitJoinListener`, `AuthMeLoginListener`, `BungeeLoginListener`, `VelocityConnectListener`, `VelocityChatListener`, …), `MojangProfileLookup`, `DiscordWebhookNotifier`, `WebhookConfig`.
- Fields of `Settings` (`serverUuid`, `serverName`, `limitGroups`, …), `ConfigModule`, `DatabaseModule` (`disclaimerAccepted`, `lastExpiredUpdate`, `databaseTimeOffset`) and `HistoryQuery` (`target`, `activeOnly`, `limit`, `offset`).

About 120 minor library internals have generic names (`KotlinHelperN`, `ArchiveUtilN`, `PoolStubN`) because they never had real ones. Around 340 fields and more than 500 methods still carry generic names (`flag9`, `intValue3`, `KotlinUtilN`, …). Some of them are in libraries (Kotlin, Hikari, Commons Compress); parts of the core (`SyncModule`, `LockdownHandler.flag39`, `Template.flag2`, `DatabaseModule.flag9`) need a separate reading. Methods and fields are otherwise named by type and role (`handleText`, `computeBytes`); such names are unique within an inheritance family so they cannot cause accidental overrides.

To continue renaming, add entries to `mappings/names.map` and run `tools/rename.sh <patch-name>`. It checks for import conflicts, runs the symbol-aware renamer `tools/Renamer.java`, compiles and commits; applied maps are kept in `mappings/applied/`. Helpers: `tools/dossier2.py`, `tools/cluster.py`, `tools/fallback3.py`, `tools/applyloop.py`, `tools/LocalTool.java` (locals), `tools/InlineTool.java` (lambdas), `tools/OverrideCheck.java` (methods that override without `@Override`). Fabric and Mixin files are outside the build and are renamed with `tools/offbuild.py`.
