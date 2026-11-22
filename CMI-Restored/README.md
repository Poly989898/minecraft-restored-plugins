# CMI 9.8.7.5 Restored Edition

Readable sources of CMI 9.8.7.5, recovered from the release jar by decompilation and repaired until it compiles. It requires CMILib, which is in the `CMILib-Restored` folder.

## Status

- The core compiles from `src/main/java` against Paper 26.3 and the CMILib build. No original jar is needed.
- Supported server version: Paper 26.3. Its version-specific NMS adapter is in `nms/v26_3`.
- Adapters for other versions (NBT code for Minecraft 1.7 to 1.21) are not included yet. They depend on the NMS code of each version and must be ported to the current `NMS` interface. Their files are listed in `excludes.txt`.
- The jar starts on Paper 26.3 with no errors in the log. Commands and features are not verified in game.
- `stubs/` holds compile-only signatures of optional plugin APIs (DiscordSRV, LuckPerms, Jobs and others). They are generated from bytecode, are never packaged, and are not verified against the real plugins.
- The Spigot license check of the original (`loadConfig0`) is removed.

## Build

Build order: `CMILib-Restored`, then `nms/v26_3`, then `CMI-Restored`.

```sh
(cd ../CMILib-Restored && gradle jar)
(cd nms/v26_3 && gradle compileJava)   # paperweight downloads the Paper dev bundle, needs network
gradle jar                             # produces build/libs/CMI-9.8.7.5-restored.jar
```

Requires JDK 25.