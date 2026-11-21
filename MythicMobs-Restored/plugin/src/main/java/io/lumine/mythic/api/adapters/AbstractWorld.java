package io.lumine.mythic.api.adapters;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public interface AbstractWorld {
   Collection<AbstractEntity> getLivingEntities();

   @Override
   boolean equals(Object obj);

   String getName();

   boolean isLoaded();

   void createExplosion(AbstractLocation abstractLocation, float value);

   void createExplosion(AbstractLocation abstractLocation, float value, boolean flag, boolean flag2);

   default void createExplosion(AbstractLocation location, float yield, boolean fire, boolean blockdamage, AbstractEntity source) {
      this.createExplosion(location, yield, fire, blockdamage);
   }

   List<AbstractPlayer> getPlayers();

   int getPlayerCount();

   List<AbstractPlayer> getPlayersNearLocation(AbstractLocation abstractLocation, int value);

   Collection<AbstractEntity> getEntities();

   void setStorm(boolean flag);

   void setThundering(boolean flag);

   void setWeatherDuration(int value);

   boolean playEffect(AbstractLocation abstractLocation, int value);

   boolean playEffect(AbstractLocation abstractLocation, int value, int value2);

   AbstractLocation getSpawnLocation();

   AbstractLocation getHighestBlock(AbstractLocation abstractLocation);

   boolean isLocationLoaded(AbstractLocation abstractLocation);

   AbstractBiome getLocationBiome(AbstractLocation abstractLocation);

   long getFullTime();

   boolean isChunkLoaded(int value, int value2);

   UUID getUniqueId();

   int getEntitiesInChunk(int value, int value2);

   byte getLightLevel(AbstractLocation abstractLocation);

   byte getLightLevelFromBlocks(AbstractLocation abstractLocation);

   byte getLightLevelFromSky(AbstractLocation abstractLocation);

   Collection<AbstractPlayer> getPlayersInRadius(AbstractLocation abstractLocation, double value);

   Collection<AbstractEntity> getEntitiesNearLocation(AbstractLocation abstractLocation, double value);

   Collection<AbstractEntity> getEntitiesNearLocation(AbstractLocation abstractLocation, double value, Predicate<AbstractEntity> predicate);

   Optional<AbstractLocation> getSurfaceLocation(AbstractLocation abstractLocation, int value);
}
