package net.Zrips.CMILib.Version.PaperMethods;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public interface AsyncTeleport {
  CompletableFuture<Boolean> teleportAsync(Entity entity, Location location, TeleportCause cause);
}
