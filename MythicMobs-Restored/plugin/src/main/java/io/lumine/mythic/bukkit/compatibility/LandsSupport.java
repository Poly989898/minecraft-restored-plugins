package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import me.angeschossen.lands.api.LandsIntegration;
import org.bukkit.Location;
import org.bukkit.World;

public class LandsSupport implements ClaimSupport {
   private final MythicBukkit core;
   private final LandsIntegration landsIntegration;

   public LandsSupport(MythicBukkit mm) {
      this.core = mm;
      this.landsIntegration = LandsIntegration.of(mm);
   }

   @Override
   public boolean isLocationClaimed(AbstractLocation aLocation) {
      Location location = BukkitAdapter.adapt(aLocation);
      World world = location.getWorld();
      return this.landsIntegration.getLandByUnloadedChunk(world, aLocation.getChunkX(), aLocation.getChunkZ()) != null;
   }

   @Override
   public boolean isNearClaim(AbstractLocation aLocation, int radius) {
      Location location = BukkitAdapter.adapt(aLocation);
      World world = location.getWorld();
      int smallX = Numbers.floor((location.getX() - radius) / 16.0);
      int bigX = Numbers.floor((location.getX() + radius) / 16.0);
      int smallZ = Numbers.floor((location.getZ() - radius) / 16.0);
      int bigZ = Numbers.floor((location.getZ() + radius) / 16.0);

      for (int x = smallX; x <= bigX; x++) {
         for (int z = smallZ; z <= bigZ; z++) {
            if (this.landsIntegration.getLandByUnloadedChunk(world, x, z) != null) {
               return true;
            }
         }
      }

      return false;
   }
}
