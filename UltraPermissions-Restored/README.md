# UltraPermissions (Restored Edition)

Readable source reconstructed from the UltraPermissions 5.6.8 plugin jar (not obfuscated). The plugin's own classes were decompiled
and then repaired by hand so that the project compiles again; third-party libraries are pulled from their upstream
artifacts and relocated at packaging time to the same `me.TechsCode.UltraPermissions.dependencies.*` packages the original jar uses.

## Build

Requires a JDK 25 toolchain and Gradle 9.x:

    gradle shadowJar

The result is `build/libs/UltraPermissions-5.6.8-restored.jar`. Optional integrations (Vault, LuckPerms, PlaceholderAPI,
authlib, BungeeCord API) are compile-only dependencies; API stubs for MVdWPlaceholderAPI, NametagEdit and PermissionsEx live in
`stubs/` and are never packaged.

## Runtime checks

Tested on a scratch Paper 26.3 (build 253) server, JDK 25, offline mode, with a tiny helper plugin that creates an
embedded-channel fake player (and fires `PlayerLoginEvent` for it, which is what UltraPermissions hooks to inject its
permissible). MariaDB 11.8 was used for the MySQL check. Nothing from these runs is stored in the repository.

- Startup and shutdown: no errors or warnings from the plugin; `Registry.json`, `Main.updb`, `Languages/*.lang`
  (37 files) and the file storage (`Groups.json`, `Users.json`, `Permissions.json`) are created. Vault and
  PlaceholderAPI are not installed, which produces no errors (they are soft dependencies).
- Console command `/upc` (create/copy/delete group, add/remove group and player permissions, add/remove/set groups,
  list permissions, super admin add, prefix, purge, unknown subcommand usage output): works, and data survives a restart.
- Permission checks: a group permission, a player permission, a negated (`-node`) group permission and removal of
  each were verified through `Player#hasPermission` / `isPermissionSet` on the fake player.
- GUI: `/uperms` for a super admin opens the overview; navigation into Groups, a group, its permission editor, its
  group list and its user list and back works, with no exceptions.
- MySQL: with credentials in `Registry.json` the plugin creates its five `UltraPermissions_*` tables
  (Paper's bundled MySQL connector is used), writes groups and permissions, and reads them back after a restart.

Behaviour that is the same as in the original jar (checked with `javap`, not changed):

- `/upc RegisterUser`, `SetGroupPriority`, `RestrictGroupToWorld`, `DeleteGroup` and similar sub-commands send their
  reply through the `Player` argument, so they throw a `NullPointerException` when run from the console (the action
  still happens before the reply). The original has the same 9 `Player.sendMessage` calls in `UPCCommand`.
- `/upc DeleteUser <unknown user>` throws a `NullPointerException`.
- Original `XMaterial` parses the server version with `MC: \d\.(\d+)`, which does not match `MC: 26.3`. On Paper 26.x
  the plugin therefore fails to enable (`XMaterial` fails to initialise). This restored source keeps that behaviour. The
  checks above were done with a scratch-only copy in which the regex accepts `MC: 26.3` and treats 26.x as 1.21
  (`VERSION = 21`); that patch is not in this repository.

Deliberate differences from the original:

- The buyer check (`SpigotLoader.loadConfig0`, which used the unstamped `%%__USER__%%` placeholders) was removed and is
  not restored.
- `GUI` uses `ItemFlag.valueOf("HIDE_LORE")` inside the existing try/catch instead of the direct
  `ItemFlag.HIDE_LORE` field access, so a server without that constant gives an `IllegalArgumentException` that is
  already caught instead of a `NoSuchFieldError`.

## Changes against the original

- `XMaterial.Data` accepts server versions without the `1.` prefix (`MC: 26.3`) and treats 26.x like 1.21; the original regex `MC: \d\.(\d+)` made the plugin fail to enable on Paper 26.x. Verified: the plugin now enables, runs and disables cleanly on Paper 26.3.

## Not verified

- Behaviour on Minecraft versions other than Paper 26.3 (the unmodified source does not enable there, see above).
- A real client or protocol bot (only an embedded-channel fake player was used); clicks were delivered as Bukkit
  `InventoryClickEvent`s, so item-moving and chat-input dialogs (rename, search, add permission) were not exercised.
- Vault, PlaceholderAPI and MVdWPlaceholderAPI hooks with the plugins actually present; LuckPerms migration.
- BungeeCord build, proxy MySQL credential transfer, Cloud addon, transfer and migration assistants, updater.
- `/promote` and `/demote`, rank-ups with expiry, world/server-restricted groups, group paths.
- Permission checks for real logins (the fake player skips the login pipeline except for the manually fired event).
