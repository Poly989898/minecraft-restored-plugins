package net.Zrips.CMILib.Version.Teleporters;

import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.CMILib;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CMIFoliaImpl implements CMIBaseTeleportImpl {
  private final JavaPlugin plugin;

  public CMIFoliaImpl(CMILib plugin) {
    this.plugin = plugin;
  }

  @NotNull
  @Override
  public boolean teleport(Entity ent, Location loc) {
    return ent.teleport(loc);
  }

  @NotNull
  @Override
  public boolean teleport(Entity ent, Location loc, TeleportCause cause) {
    return ent.teleport(loc, cause);
  }

  @NotNull
  @Override
  public CompletableFuture<Boolean> teleportAsync(Entity ent, Location loc) {
    return ent.teleportAsync(loc);
  }

  @NotNull
  @Override
  public CompletableFuture<Boolean> teleportAsync(Entity ent, Location loc, TeleportCause cause) {
    return ent.teleportAsync(loc, cause);
  }
}
