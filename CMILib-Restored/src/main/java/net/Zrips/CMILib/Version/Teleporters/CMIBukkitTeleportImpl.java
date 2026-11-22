package net.Zrips.CMILib.Version.Teleporters;

import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.CMILib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CMIBukkitTeleportImpl implements CMIBaseTeleportImpl {
  private final JavaPlugin plugin;

  public CMIBukkitTeleportImpl(CMILib plugin) {
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
    CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
    Bukkit.getScheduler().runTask(this.plugin, () -> completableFuture.complete(ent.teleport(loc)));
    return completableFuture;
  }

  @NotNull
  @Override
  public CompletableFuture<Boolean> teleportAsync(Entity ent, Location loc, TeleportCause cause) {
    CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
    Bukkit.getScheduler()
        .runTask(this.plugin, () -> completableFuture.complete(ent.teleport(loc, cause)));
    return completableFuture;
  }
}
