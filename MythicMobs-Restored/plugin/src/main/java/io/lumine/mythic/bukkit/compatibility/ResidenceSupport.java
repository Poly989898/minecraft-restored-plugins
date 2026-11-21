package io.lumine.mythic.bukkit.compatibility;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Location;

public class ResidenceSupport implements ClaimSupport {
   public ResidenceSupport(MythicBukkit mm) {
   }

   @Override
   public boolean isLocationClaimed(AbstractLocation aLocation) {
      Location location = BukkitAdapter.adapt(aLocation);
      return ResidenceApi.getResidenceManager().getByLoc(location) != null;
   }
}
