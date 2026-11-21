package io.lumine.mythic.api.volatilecode.handlers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.spawning.MythicSpawnState;

public interface VolatileSpawningHandler {
   MythicSpawnState getSpawnState(AbstractWorld abstractWorld);

   byte getSpawnRange(AbstractWorld abstractWorld);

   default int getMobCountNear(AbstractEntity entity) {
      return 0;
   }

   default void trackForSpawnCap(AbstractEntity entity) {
   }
}
