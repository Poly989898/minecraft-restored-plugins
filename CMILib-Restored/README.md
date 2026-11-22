# CMILib 1.6.1.3 Restored Edition

Readable sources of CMILib 1.6.1.3, a helper library used by CMI. The code was recovered from the release jar by decompilation and repaired until it compiles.

## Status

- All sources compile against Paper 26.3. `excludes.txt` is empty, and the build needs no original jar.
- The jar starts together with CMI on Paper 26.3 with no errors in the log. Behaviour in game is not verified.

## Build

```sh
gradle jar   # produces build/libs/CMILib-1.6.1.3-restored.jar
```

Requires JDK 25 and network access to download the Paper API. Build this project before CMI-Restored.