package net.Indyuce.mmocore.paperlib.features.bedspawnlocation;

import java.util.concurrent.CompletableFuture;
import net.Indyuce.mmocore.paperlib.PaperLib;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BedSpawnLocationPaper implements BedSpawnLocation {
   @Override
   public CompletableFuture<Location> getBedSpawnLocationAsync(Player player, boolean isUrgent) {
      Location bedLocation = player.getPotentialBedLocation();
      return bedLocation == null
         ? CompletableFuture.completedFuture(null)
         : PaperLib.getChunkAtAsync(bedLocation.getWorld(), bedLocation.getBlockX() >> 4, bedLocation.getBlockZ() >> 4, false, isUrgent)
            .thenCompose(chunk -> CompletableFuture.completedFuture(player.getBedSpawnLocation()));
   }
}
