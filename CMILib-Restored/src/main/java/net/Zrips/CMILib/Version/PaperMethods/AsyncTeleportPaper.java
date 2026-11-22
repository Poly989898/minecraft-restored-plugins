package net.Zrips.CMILib.Version.PaperMethods;

import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class AsyncTeleportPaper implements AsyncTeleport {
  @Override
  public CompletableFuture<Boolean> teleportAsync(
      Entity entity, Location location, TeleportCause cause) {
    return Version.isCurrentLower(Version.v1_19_R1)
        ? PaperLib.getChunkAtAsync(
                location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4, true)
            .thenCompose(
                chunk -> {
                  CompletableFuture<Boolean> result = new CompletableFuture<>();
                  CMIScheduler.runTask(() -> result.complete(entity.teleport(location, cause)));
                  return result;
                })
        : entity.teleportAsync(location, cause);
  }
}
