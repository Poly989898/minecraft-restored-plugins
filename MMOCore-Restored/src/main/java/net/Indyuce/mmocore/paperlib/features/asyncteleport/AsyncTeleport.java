package net.Indyuce.mmocore.paperlib.features.asyncteleport;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public interface AsyncTeleport {
   CompletableFuture<Boolean> teleportAsync(Entity entity, Location location, TeleportCause teleportCause);
}
