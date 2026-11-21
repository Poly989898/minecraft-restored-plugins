# NOTICE

Every plugin in this repository is an unofficial, modified version of the original. None of them is endorsed by or affiliated with the original authors, who remain the authors of the code.

## Ultimate Model Engine R4.1.1

- Original: ModelEngine by Ticxo.
- Source: decompiled from the R4.1.1 build (Vineflower), repaired to compile.
- Changes: the buyer watermark resource (`me/lucko/shadow/play.rotmc.net`) is removed. No license check or activation code was found in the recovered source.

## Nexo 1.26

- Original: Nexo by boy0000.
- Source: decompiled from the public 1.26 release jar (SHA-256 identical to the GitHub release), repaired to compile.
- Changes:
  - Removed build-flag checks (`VersionUtil.isCompiled/isCI/isDevBuild/isLeaked`) and their warnings in `NexoPlugin.onEnable`, buyer watermarks (`Print`, `L`), the marketplace tag in `NexoMetrics`, and a hidden reflective switch that blocked sending the resource pack.
  - Removed Kotlin `Intrinsics.checkNotNull*` calls (5,778 lines in 504 files). Behaviour change: a `null` passed from Java now fails later or not at all. `@NotNull`/`@Nullable` annotations are kept.
  - The resource pack token is still read from `token.secret`; no token is included.

## MythicMobs 5.14.0

- Original: MythicMobs.
- Source: decompiled from the 5.14.0 release jar (CFR, Vineflower), repaired to compile. All NMS modules are built from source; no original bytecode is shipped except third-party libraries.
- Changes:
  1. `MythicBukkit.isVolatile()` always returns `true`. Code that upstream gates behind its separate premium module (inline target conditions, `origin=` overrides, skill parameters, stat scaling equations, ChainMissile bounces, custom menus, dialogs, item and mob editors, command skills) is present in this code base and is enabled.
  2. bStats metrics are not started.
  3. The startup message and `/mm version` state that this is a modified build.
  4. `AdventureBossBar.getTitle()` returns the plain-text title (upstream called a method that no longer exists in current Adventure).
  5. `BukkitEntityType.spawn` calls `World#spawn(Location, Class, SpawnReason, boolean, Consumer)` reflectively with a fallback, because that overload is missing from the newest Paper API.
  6. Third-party plugin APIs are replaced by the real artifacts where public, otherwise by compile-only stubs that are never shipped.

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
