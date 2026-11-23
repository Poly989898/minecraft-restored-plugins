# Nexo 1.26

Readable, rebuildable Java sources of Nexo 1.26 (custom items, blocks, furniture, glyphs and resource pack generation for Paper). The code was recovered from Kotlin bytecode, repaired to compile and cleaned up.

## Layout

| Path | Content |
|---|---|
| `src/` | Sources (`com/nexomc/nexo`) |
| `stubs/` | Compile-only stubs of third-party APIs (never shipped) |
| `libs/` | Build dependencies (not in git) |
| `tools/` | `package.sh` / `package.ps1` (final jar), `testserver.sh` / `testserver.ps1` (build and run Paper 26.3 in a scratch copy), `Check.java`, `Verify.java`, `fixnames.py` |
| `excludes.txt` | Files that are not part of the build (bundled third-party libraries, old NMS) |

## Building

Gradle (`build.gradle.kts`, paperweight, Paper 26.3). All of Nexo's own code compiles, including the integrations (WorldEdit/FAWE, MythicMobs, MMOItems, BlockLocker, Axiom) and the resource pack servers (S3, Hermes, Polymath). Excluded from compilation:

- bundled third-party libraries (`com/google`, `team/unnamed`, `com/nexomc/libs`, `customblockdata`, `protectionlib`);
- the shared classes `nms/commons/EntityUtils` and `PlayerUtils`: they come from the `com.nexomc:commons` library built against NMS 1.21 and do not compile against Paper 26.3 (`EntityType` and `GameProfile` have different members). The versioned `v26_R2` takes them from the original jar;
- the old NMS adapters `v1_21_R*` and `v26_R1` (other Minecraft versions).

Integration dependencies come from Maven (FAWE core, lin-bus, BlockLocker, AWS SDK S3). Axiom has no API in the public repositories, so `stubs/` contains compile-only stubs. `tools/package.sh` keeps the original bytecode for `compatibilities/` classes (recompiled ones fail bytecode checks when the partner plugin is absent) and uses recompiled classes for `pack/server/*`.

```sh
gradle --offline compileJava
tools/package.sh      # build/libs/nexo-1.26-custom.jar = original jar + recompiled classes
```

Windows (PowerShell), the same steps:

```powershell
gradle --offline compileJava
tools\package.ps1
```

The original jar is required as `libs/nexo-1.26-patched.jar` and is kept outside the repository (`../originals/Nexo/`).

## Status

- Compiles; ran on Paper 26.3. Logs match the original jar, the resource pack builds, and `nexo reload all`, `nexo list` and `nexo recipes reload` run without exceptions; the plugin unloads cleanly.
- The recompiled `HermesServer` uploads the pack to Hermes and receives a URL (hash and URL stay the same on `nexo reload`; hashes were not compared with the original because they differ between runs).
- Not verified: S3 and Polymath servers, in-game behaviour (commands, mechanics, blocks, furniture), and the WorldEdit, Axiom and BlockLocker integrations (compile only).

## Architecture

### Entry points

`paper-plugin.yml`: `main: com.nexomc.nexo.NexoPlugin`, `loader: PaperPluginLoader` (library loading), `bootstrapper: NexoBootstrap` (early initialization). `NexoPlugin.onEnable` starts the managers (items, blocks, furniture, resource pack, recipes) and selects the NMS handler through `nms/NMSHandlers`.

### Packages (`com.nexomc.nexo`)

| Package | Files | Role |
|---|---|---|
| `nms` | 252 | Adapters per Minecraft version (`v1_21_R4 … v1_21_R11`, `v26_R1`, `v26_R2`); shared interfaces `NMSHandler`, `IPacketHandler`, `IBlockUtils`, `ICustomEntityHandler`; `commons` for shared code |
| `mechanics` | 172 | Item mechanics. `MechanicFactory` reads config and creates a `Mechanic`; `MechanicsManager` registers it. Subpackages: `custom_block`, `furniture`, `breakable`, `farming`, `light`, `limitedplacing`, `repair`, `storage`, `trident`, `misc` |
| `utils` | 89 | Utilities |
| `pack` | 47 | Resource pack generation and distribution: `PackGenerator`, `ModelGenerator`, `AtlasGenerator`, `Overlays`, `PackValidator`, `NexoPackSquash`, `PackObfuscator`; servers in `pack/server`; `PackDownloader` |
| `api` | 37 | Public API: `NexoItems`, `NexoBlocks`, `NexoFurniture`, `NexoPack`, events in `api/events` |
| `recipes`, `items`, `glyphs`, `fonts`, `dialog`, `commands`, `configs`, `converter` | | Recipes, item builder, glyphs and fonts, dialogs, commands, config loading, ItemsAdder and Oraxen converters |
| `compatibilities` | 26 | Optional integrations (WorldEdit, MythicMobs and others) |

### Data flow

Configs (`items/`, `glyphs/`, `languages.yml`) → mechanic factories and item builders → registration in the managers → `PackGenerator` builds the resource pack → served to clients (`PackListener`, `pack/server`).

### Source notes

- Files named like `Class$method$lambda$0$$inlined$sortedBy$1.java` are Kotlin inline function classes. Do not delete them: bytecode references them.
- Kotlin traces are visible: `Companion`, `$default`, `Intrinsics` checks.
- Local names and comments of the original are not preserved. Readability work changed names only: 12,873 `varN` locals and 6,518 Kotlin inline names (`x$iv`, `$this$map$iv`) were renamed, `labelNN` labels were replaced, about 480 redundant casts were removed, and the bytecode of 871 classes was identical before and after (`javap -v` without constant pool). Javadoc was added to the public API (`NexoItems`, `NexoBlocks`, `NexoFurniture`, `NexoPack`).

## Build inputs

- `libs/nexo-1.26-patched.jar` is the base jar that `tools/package.sh` copies from. It is the original 1.26 release jar (the same file as `nexo-original.jar`), which is not in the repository.
- `libs/MMOItems-API-6.10.1-SNAPSHOT.jar` and `libs/MythicLib-dist-1.7.1-SNAPSHOT.jar` are the integration APIs; they are published on the phoenixdevt Maven repository and are not in git.
- The MythicMobs integration compiles against `../MythicMobs-Restored/dist/MythicMobs-5.14.0-rebuilt.jar` (copy the built jar there).
- `tools/package.sh` uses the JDK `jar` tool instead of `zip` and `unzip`.
- Tested on Paper 26.3 with CMI, CMILib, MythicMobs, ModelEngine, LiteBans and LPX: Nexo enables, detects MythicMobs and ModelEngine, and `nexo reload` succeeds. Pack generation and in-game behaviour are not verified.
