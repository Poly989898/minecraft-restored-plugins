package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import net.crashcraft.crashclaim.CrashClaim;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class CrashClaimSupport implements ClaimSupport {
   private final MythicBukkit core;
   private CrashClaim crashClaim;

   public CrashClaimSupport(MythicBukkit mm) {
      this.core = mm;
      this.crashClaim = JavaPlugin.getPlugin(CrashClaim.class);
      if (this.crashClaim != null) {
         ;
      }
   }

   @Override
   public boolean isLocationClaimed(AbstractLocation aLocation) {
      Location location = BukkitAdapter.adapt(aLocation);
      return this.crashClaim.getDataManager().getClaim(location) != null;
   }

   @Override
   public boolean isNearClaim(AbstractLocation aLocation, int radius) {
      Location location = BukkitAdapter.adapt(aLocation);
      int smallX = Numbers.floor((location.getX() - radius) / 16.0);
      int bigX = Numbers.floor((location.getX() + radius) / 16.0);
      int smallZ = Numbers.floor((location.getZ() - radius) / 16.0);
      int bigZ = Numbers.floor((location.getZ() + radius) / 16.0);

      for (int x = smallX; x <= bigX; x++) {
         for (int z = smallZ; z <= bigZ; z++) {
            if (this.crashClaim.getDataManager().getClaims(x, z, location.getWorld().getUID()).size() > 0) {
               return true;
            }
         }
      }

      return false;
   }
}
