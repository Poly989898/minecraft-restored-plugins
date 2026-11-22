# LPX 3.9.1

Readable sources of LPX 3.9.1, an anti-exploit plugin for Paper/Spigot 1.8 – 1.21. The code was recovered from an obfuscated jar and repaired until it compiles.

## Status

- All 189 files of the `it.ytnoos` package compile with 0 errors against the original jar, Paper API 1.21.11 and the NMS 1.8 stubs in `stubs/` (`excludes.txt` is empty). The bundled PacketEvents, Adventure and Gson are not part of `src/`. There is no Gradle project.
- Packaging: `tools/build.sh`, then `tools/package.sh` (original jar + rebuilt classes, `build/libs/LPX-3.9.1-restored.jar`). `tools/testserver.sh` starts a scratch Paper 26.3 server (port 25603); `JAR=<original>` runs the same test against the original jar. On Windows the same steps are `tools\build.ps1`, `tools\package.ps1` and `tools\testserver.ps1` (`-Jar <original>` for the baseline run).
- On Paper 26.3 the rebuilt jar starts: `LPX enabled!`, PacketEvents loads, `lpx` and `lpx reload` work, 0 exceptions.
- The original jar does not start on that server (`ClassNotFoundException: it.ytnoos.lpx.lib.kyori...DataComponentValueConversionImpl`). It bundles Adventure as `net/kyori/...` while PacketEvents looks for the relocated name, and Paper 26.3 ships its own Adventure 5.x with the same names. `tools/RelocateKyori.java` (a `package.sh` step) moves the bundled Adventure, examination and option packages from `net/kyori` to `it/ytnoos/lpx/lib/kyori` across the whole jar (classes, references, strings, `META-INF/services`) and leaves references to classes that are not bundled (`ComponentLogger`, provided by Paper) as `net.kyori.*`.
- Not verified: the behaviour of the individual checks in game. The original has no baseline run to compare against.

## What was done

- Decompiled with Vineflower 1.12.0 from a jar whose access flags were repaired first (`tools/MakePublic.java` removes the stray `ACC_PUBLIC` flag from classes).
- `tools/strip_decoys.py` removes decoy methods of the form `private static X a(X var0) { return var0; }`. `tools/fixraw.py` restores types from javac errors. `tools/restore_check.py` turns the check implementations that the obfuscator renamed (`AB`, `hi`, `Fl`, …) and weakened to `check(Object, T)` back into `check(ProtocolPlayer, T)`.
- Renamed bridges and implementations restored: `check2`, `getRecipeId2`, `examine*`; `PlayerCheck.check(ProtocolPlayer, T)`, `ItemUtils_w7.getRecipeId(T)`, `ItemK.ComponentExaminer.examine(...)`. The `$SwitchMap` classes (`q_`, `iy`) are replaced by direct `switch` statements over the enum.
- Encrypted DES constants were decrypted by the classes' own code in a throwaway JVM (`tools/DecryptW7.java`, `tools/ReadStatics.java`; needs `-XX:+UnlockDiagnosticVMOptions -XX:-BytecodeVerificationRemote` because the original `PlayerCheck.handle` fails JVM verification). Substituted: `ItemUtils_w7` (`recipeId`, `displaysSize`, the reflection chain `MinecraftServer.getServer().getRecipeManager().allDisplays`), `RollingAverage` (`"size must be > 0"`), `PlayerCheck.PATH_SEPARATOR = "options."`.
- Removed: the re-uploader print in `LPXBukkitPlugin.onEnable`, the license server client (`z`, `R`, `SecurityUtils`, `AESCipher`, `StreamUtils`, `Constants`; nothing referenced them and `LPXBukkit` starts `LPXInit` directly), marker fields (`"ed4e41953ef842b13d921984d0cf4800"`, `"forged"`) and static get/set decoy pairs (`PlayerCheck._f/zN`, `ItemBuilder._n/Qb`, `IterationType.wb`).
- Dead code and decoys that were not restored: `ItemUtils_w7.BS/tq` (empty and `return null`), unused methods with encrypted strings in `NettyUtils` (`VQ`, `r7`, `f2`) and `CharSequenceUtils` (`WL`, `W2`, `YA`, `MT`, `lastIndexOf` with `long`), the message of the private `ItemUtils` constructor, `i2.<clinit>` with invalid bytecode (`aload_0` in a static method, field `r` never read), and the string decryption tables in `SecurityUtils`, `wq`, `wz` and `ItemBuilder`.
- `SlotIterator`, `VERTICAL` branch: opaque keyed predicates (`1L < var1`, `-50606855931998043L <= var10`) are removed and the body runs unconditionally. This assumes they are always true in the original, since the iterator would not advance otherwise; it was not checked against bytecode. The other key parameters (`a ^ var`, `z ^ var`) are unchanged.
- Manual: `public` access for the `InventoryManager` fields and the `qq`/`qn`/`we`/`wq` constructors (package-private in the original because of stripped flags), and the `this$0` field of `wq`.

## Names

Class and member names are inferred from the code, since the jar has none. `tools/mappings.txt` lists them (lines `class`, `method [Owner#]name/paramCount`, `field Owner#name`) and `tools/rename.sh` applies them with a symbol-aware renamer built on the javac Tree API (`tools/Rename.java`; references are resolved by symbol, not by text). The same tool names `varN` locals and parameters by type (`player`, `list`, `itemStack`, …; duplicates get a digit). Names come from three sources: the code and its call sites, getter names (`getLastAlert` → field `lastAlert`), and knowledge of the open-source libraries inside (SmartInvs, the DiscordWebhook template). Some names may be approximate.

Left unrenamed: `ClassKeyDerivation`, `KeyDerivationCore` and `KeySource` (obfuscator internals that compute constant keys; fields and methods `a`, `b`, …) and the static key fields `a` in several classes.

The obfuscated bytecode of the bundled libraries refers to the old names `_c`, `h4`, `t9`. In `package.sh`, `tools/RemapClasses.java` rewrites those references from the same `class` lines and drops the stale old-named classes from the final jar. `tools/keep-original.txt` lists the ones that stay (`iy`, `q_`, the `$SwitchMap` holders).

## Hooks

The bundled PacketEvents and Adventure (obfuscated bytecode) call state hooks by name: `ConfigManager.TG()/Tc()/MK(int)` and `FlagInfo.z9()/WX(int[])`. They are missing from the original jar, so the first call used to fail with `NoSuchMethodError` (for example in Adventure's Gson serializer). They are restored in `src/`: `TG()` returns 0, `Tc()` returns a non-zero value (84) and `z9()` returns `null`, which selects the live branch in the bytecode (`ifeq`/`ifne`/`ifnull`). Verified by running the serializer on components with style, `hover_event` (UUID), `translatable` and `keybind`: the JSON is valid and reads back without loss. Do not rename the hooks.

## Checking compilation

```sh
javac -proc:none -encoding UTF-8 -d /tmp/lpx-out \
  -cp ../originals/LPX/LPX-3.9.1.jar:<paper-api-1.21.11.jar>:<netty and others from a server's libraries> \
  $(find src -name '*.java' | grep -vFf excludes.txt)
```

The original jar is kept outside the repository: `../originals/LPX/LPX-3.9.1.jar`.
