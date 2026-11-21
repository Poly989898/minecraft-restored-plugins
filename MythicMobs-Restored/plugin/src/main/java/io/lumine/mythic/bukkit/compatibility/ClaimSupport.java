package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.api.adapters.AbstractLocation;

public interface ClaimSupport {
   boolean isLocationClaimed(AbstractLocation abstractLocation);

   default boolean isNearClaim(AbstractLocation location, int radius) {
      return false;
   }
}
