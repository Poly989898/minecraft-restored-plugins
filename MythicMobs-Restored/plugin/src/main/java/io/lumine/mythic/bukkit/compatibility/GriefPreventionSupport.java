package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class GriefPreventionSupport implements ClaimSupport {
   private final MythicBukkit core;
   private GriefPrevention griefPrevention;

   public GriefPreventionSupport(MythicBukkit mm) {
      this.core = mm;
      this.griefPrevention = JavaPlugin.getPlugin(GriefPrevention.class);
      if (this.griefPrevention != null) {
         ;
      }
   }

   @Override
   public boolean isLocationClaimed(AbstractLocation aLocation) {
      Location location = BukkitAdapter.adapt(aLocation);
      if (!GriefPrevention.instance.claimsEnabledForWorld(location.getWorld())) {
         return false;
      } else {
         Claim claim = this.griefPrevention.dataStore.getClaimAt(location, false, null);
         return claim != null;
      }
   }

   @Override
   public boolean isNearClaim(AbstractLocation aLocation, int radius) {
      Location location = BukkitAdapter.adapt(aLocation);
      if (!GriefPrevention.instance.claimsEnabledForWorld(location.getWorld())) {
         return false;
      } else {
         int smallX = Numbers.floor((location.getX() - radius) / 16.0);
         int bigX = Numbers.floor((location.getX() + radius) / 16.0);
         int smallZ = Numbers.floor((location.getZ() - radius) / 16.0);
         int bigZ = Numbers.floor((location.getZ() + radius) / 16.0);

         for (int x = smallX; x <= bigX; x++) {
            for (int z = smallZ; z <= bigZ; z++) {
               if (this.griefPrevention.dataStore.getClaims(x, z).size() > 0) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
