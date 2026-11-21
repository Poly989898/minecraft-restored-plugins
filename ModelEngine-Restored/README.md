# Ultimate Model Engine R4.1.1

Rebuildable sources of Ultimate Model Engine R4.1.1-UME.2, a continuation of ModelEngine for Paper 26.3. The plugin renders custom 3D models (Blockbench `.bbmodel`) with display entities, animates them and attaches them to mobs, players and hitboxes.

## Layout

| Path | Content |
|---|---|
| `src/` | Sources: `api`, `core`, `core21`, `core25` and the NMS adapters `v1_19_R3` … `v26_3` |
| `patches/java/` | Two patched classes (`TFile`, `BlockbenchDeserializer`) that override the files of the same name in `src/` |
| `resources/` | Resource overrides (`plugin.yml`, the `entity.vsh` shader) |
| `scripts/` | Dependency download, build, jar merge and verification scripts |
| `tests/` | 47 JUnit tests for pure logic (no server needed) |

Not included: third-party libraries (`org.jsoup`, `org.mineskin`), jars and Paper. They are downloaded to `.work/` or taken from the original jar.

## Status

- All 19 modules compile: the main part against Paper 26.3, every adapter against its own Paper version (1.19.4 … 26.2).
- Bytecode levels match the original: `api`, `core` and the 1.19–1.20.4 adapters target Java 17, `core21` and the 1.20.6–1.21.11 adapters Java 21, `core25` and the 26.x adapters Java 25.
- Ran on Paper 26.3 (build 253) with a 26.3 client in offline mode. Startup and shutdown logs match the original apart from timings, the generated `resource pack.zip` is identical (39 files, same SHA-256 each), and `/meg summon|menu|health|reload|…` work.
- Not verified: adapters on servers of their own version (1.19.4 … 26.2), animations, hitboxes, mounts, and the MythicMobs and Citizens integrations in game.

## Building

Requires JDK 25, `bash`, `curl`, `unzip`, `python3`, network access and the original `UltimateModelEngine.jar` (looked up in `../originals/ModelEngine/`, or set `ORIG_JAR`).

```sh
scripts/fetch-deps.sh        # dependencies -> .work/deps
scripts/fetch-paper.sh 26.3  # Paper 26.3 -> .work/paper/26.3
scripts/build.sh             # api, core, core21, core25, v26_3
scripts/build-adapters.sh    # remaining adapters (downloads the matching Paper versions)
scripts/merge-jar.sh         # dist/UltimateModelEngine-fixed.jar
scripts/test.sh              # tests (after build.sh)
```

The result is the original jar with every rebuilt class replaced. Environment variables: `ORIG_JAR`, `WORK`, `BUILD`, `DIST`, `MAIN_PAPER`.

Build notes:
- The sources use Lombok; compile with `-processorpath .work/deps/lombok-1.18.42.jar` (1.18.34 does not support JDK 25). Settings are in `src/lombok.config`.
- A file in `patches/java/` replaces the `src/` file with the same package path.
- Classes without sources (`org.jsoup`, `org.mineskin`, legacy dependencies) are used from the original jar as a binary dependency.
- Each module is compiled with the same `-source/-target` as the original.
- `verify-signatures.py` compares the signatures of the rebuilt classes with the original, `LoadAll.java` loads and verifies every class of the jar.
- Tests cover pure logic (`TMath`, `Easing`, `ColorHelper`, `OrientedBoundingBox`, collections). Classes that need a running `ModelEngineAPI` get a `TestApi` stub; anything that depends on a Bukkit server is not covered.

## Architecture

### Entry point

`plugin.yml` → `com.ticxo.modelengine.core.ModelEngine` (extends `api.ModelEngineAPI`, which extends `JavaPlugin`). `onEnable` creates the managers (models, resource pack, menus, compatibility) and calls `configureNMSHandler()`, which picks the adapter for the server version:

- 1.19.4 – 1.20.4: `NMSHandler_v1_19_R3`, `v1_20_R1…R3` directly;
- 1.20.6 – 1.21.x: through `Java21Access`;
- 26.x: through `Java25Access`.

### Modules (`com.ticxo.modelengine`)

| Module | Role |
|---|---|
| `api` | Public API and abstractions: `ModelEngineAPI`, `ModeledEntity`, `ActiveModel`, `ModelBone`, animations, generator, events, `nms.NMSHandler`. Signatures here must not change: third-party plugins depend on them |
| `core` | API implementation: `ModeledEntityImpl`, `ActiveModelImpl`, `BoneSequencer`, the resource pack generator (`ModelGeneratorImpl`), commands, menus, listeners, data storage (`data/V3Data`), Citizens and LibsDisguises integrations |
| `core21` | Code that needs Java 21; mainly the MythicMobs integration (`core21/mythic`: mechanics, conditions, targeters) |
| `core25` | Java 25 bridge (`Java25Helper`) |
| `v1_19_R3 … v26_3` | One adapter per Minecraft version: `NMSHandler_vX`, `entity/` (hitboxes and entities), `network/` (packets), `NMSFields`/`NMSMethods` (NMS reflection). The modules are nearly identical and differ in NMS signatures |

### Concepts

- **ModeledEntity** is a world entity with attached models, **ActiveModel** a model instance, **ModelBone** a bone. Bone behaviours (`api/model/bone/behavior`) cover held items, names, seats, limbs and more.
- **Blueprint** is a parsed model (`api/generator/blueprint`), built from `.bbmodel` by the parser in `api/generator/parser/blockbench` (implementations in `core/generator/parser`).
- **Animations**: `api/animation` (states, timelines), handlers in `core/animation`.
- **Generator** builds the resource pack (atlases, JSON models, skins) from the `blueprints/` folder; the cache lives in `core/generator`.
- **Hitboxes and input**: `api/interaction`, `api/mount` (riding controllers).

### Data flow

1. Startup: config is loaded, `ModelGenerator` reads `blueprints/*.bbmodel`, builds the resource pack and fills `ModelRegistry`.
2. `/meg summon` or the API creates a `ModeledEntity` and an `ActiveModel`.
3. Every tick `BoneSequencer` updates the bones from the animations and the adapter (`NMSHandler`) sends display entity packets to clients.

### Notes

- The three code groups compile for different Java versions (17 / 21 / 25); do not mix their APIs.
- Names of private fields and locals were partly lost in decompilation; rely on types and usage.

## Changes against the decompiled output

Public signatures are unchanged. Compile errors from the decompiler were fixed as follows:

| Problem | Where | Fix |
|---|---|---|
| lost generic casts `(BoneBehaviorType<T>)…` | `ActiveModel`, `ModeledEntityImpl`, `Look/MoveControlWrapper` | raw cast `(BoneBehaviorType)` |
| raw `BehaviorManager` does not satisfy intersection generics | `Look/MoveControlWrapper` (all adapters) | `var` |
| `instanceof Packet<? super X>` | `ModelEngineChannelHandler` | `instanceof Packet<?>` plus an explicit cast |
| `new Inner<>()` for non-static inner classes | `PacketInterceptor` | `new Modifier<T>()`, `new Listener<T>()` |
| `renderer instanceof … ? parser : null` | `NMSHandler_*` | cast `(VFXRendererParser<T>)` |
| lambda captures the wrong variable | `InteractionTracker` | `hitPos` → `hitPosx` |
| `Object` instead of `T`/`String` | `ItemUtils`, `RotationSolver`, `ActiveModelImpl`, `LeashManagerImpl` | explicit casts |
| redeclared local variable | `CitizensCommand` | renamed |
| `Class<? extends Placeholder>` | `ModelEngineComponentRegistry` | cast to `Class<Placeholder>` |
| raw `Optional` loses its type | `DismountModelMechanic` | cast to `BoneBehavior` |
| wrong `Vec3D.a` overload | `v1_19_R3`…`v1_20_R3` navigation | cast `(IPosition)` |
| ambiguous `FriendlyByteBuf.a` overload | `MountParser`, `SegmentParser` (legacy adapters) | explicit type arguments |
| method named `do` (a Java keyword) | `v1_20_R2/MoveControlWrapper` | call through `MethodHandle` |
| lambda for the `Context` interface | `v26_1`, `v26_2` `NetworkUtils` | anonymous class (the interface has two methods in current 26.2 builds) |

Paper 26.3 API changes: `Particle.REDSTONE` → `Particle.DUST` (`OrientedBoundingBox`, `MiscUtils`, `BugTrait`).

Readability changes do not alter behaviour or bytecode:
- About 1000 `varN` locals and parameters renamed by type, by initializer (`getFoo()` → `foo`) and by method name (`setFoo(x)` → `foo`), using javac symbols rather than regular expressions. Loop labels `labelNN` → `outer`. The original names are not recoverable, so the names are heuristic.
- 248 redundant casts removed (`javac -Xlint:cast`); the hitbox search in `InteractionTracker` rewritten as a for-each loop.
- Parameter names of abstract methods recovered from their implementations (`setPosition(Entity, double x, double y, double z)` instead of `value, value2, value3`).
- Javadoc for `ModelEngineAPI`, `ModeledEntity`, `AnimationHandler` and `EntityHandler`, checked against the implementations.
- Lombok annotations restored: 284 of 328 files with expanded code are back to annotations, 15 with a reduced set, 12 stay expanded because the generated bytecode differed.
- After these steps all 17 module groups (api/core/core21/core25+v26_3 and 14 adapters) compile to classes identical to the previous build (`javap -v` without constant pool, `MethodParameters` and member order). The only difference is the `final` flag of parameters in `MethodParameters`, which is metadata.

## Verification

Static:
- The original jar (SHA-256 `b380fe64…edd38f`) was audited at bytecode level for all 1,361 classes: no process spawning, dynamic code loading, operator grants or console command execution; network access only to `api.mojang.com`, `sessionserver.mojang.com` and `api.mineskin.org`.
- Signatures of 735 rebuilt classes of the main part match the original (0 differences, synthetic members ignored).
- All 1,268 classes of the jar load and pass JVM verification (`-Xverify:all`) as in the original. Only the `v1_*` adapters fail on Paper 26.3 (foreign NMS), exactly like the original.
- From a clean checkout, 1,826 of 1,826 classes reproduce byte for byte against the jar that was run on the server.

Notes from the live test: a bare `/meg` prints nothing (`MECommand.onCommand` returns `false`), and `Skin URL is null` at login in offline mode is expected because such a name has no Mojang profile.

## Quirks of the original code

Found while reading the code and writing tests. They are original behaviour and were left untouched; changing them changes the plugin.

- `ColorHelper.getArgbFloat` computes a channel as `floor(clamp(value * 255, 0, 1))`. The clamp to 1 happens after the multiplication, so any channel above ~0.004 yields 1 instead of 0..255. The method is not called anywhere.
- `InteractionTracker`: in the nearest-hitbox search `dist` stays `Double.MAX_VALUE` and is never updated, so `!(dist < hitDistSqr)` is always true and the last intersected hitbox is chosen instead of the nearest. If the ray starts inside a box (`obb.contains(start)`), the search stops at once.
- `ConstSet` defines equality by `hashCode()` only: different objects with the same hash count as one element.
