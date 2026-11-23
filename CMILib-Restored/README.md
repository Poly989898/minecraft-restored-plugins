# CMILib 1.6.1.3 — Restored Edition (work in progress)

Readable sources of CMILib 1.6.1.3, the helper library required by CMI. Recovered from `originals/CMILib/CMILib1.6.1.3.jar` by decompilation with Vineflower 1.12.0. License: Apache-2.0 (as stated by the user).

## Status

- 252 source files, **all 252 compile** against Paper 26.3 (nothing excluded).
- All sources formatted with google-java-format 1.37.0.
- `Placeholders/Placeholder.java`: the `CMIPlaceHolders` enum has **no constants** in the original jar (`values()` is empty). The sources reflect this exactly (`enum CMIPlaceHolders { ; ... }`), so placeholders of that enum are empty in this build. This is a property of the original binary.
- Runs with CMI on Paper 26.3 with no ERROR or SEVERE lines in the log (startup only; not checked in game).

## Build

```sh
gradle jar   # from this folder; add --offline if the network is down
```

`originals/` is not part of the repository.