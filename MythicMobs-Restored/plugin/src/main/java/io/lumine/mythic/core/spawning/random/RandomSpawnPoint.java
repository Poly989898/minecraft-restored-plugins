package io.lumine.mythic.core.spawning.random;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractBiome;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractWorld;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

@Getter
@Setter
@ToString
public class RandomSpawnPoint {
   protected final AbstractLocation location;
   protected final SpawnPointType pointType;
   protected AbstractEntity entity;
   protected SpawnReason spawnReason;
   protected float levelMod = 1.0F;
   protected int nearbyPlayers = 0;
   protected float localMobCount = 0.0F;
   protected AbstractPlayer triggeringPlayer;

   public RandomSpawnPoint(AbstractLocation location) {
      this(null, location, SpawnReason.CUSTOM, null);
   }

   public RandomSpawnPoint(AbstractLocation location, SpawnPointType pointType) {
      this(null, location, SpawnReason.NATURAL, pointType);
   }

   public RandomSpawnPoint(AbstractEntity entity, AbstractLocation location, SpawnReason spawnReason) {
      this(entity, location, spawnReason, null);
   }

   public RandomSpawnPoint(AbstractEntity entity, AbstractLocation location, SpawnReason spawnReason, SpawnPointType pointType) {
      this.entity = entity;
      this.location = location.center();
      this.spawnReason = spawnReason;
      if (pointType == null) {
         SpawnPointType pt;
         if (!location.getBlock().isOccluding()) {
            if (location.getBlock().isLiquid()) {
               if (location.getBlock().isLava()) {
                  pt = SpawnPointType.LAVA;
               } else {
                  pt = SpawnPointType.SEA;
               }
            } else if (location.subtract(0.0, 1.0, 0.0).getBlock().isOccluding()) {
               pt = SpawnPointType.LAND;
            } else {
               pt = SpawnPointType.AIR;
            }
         } else {
            pt = SpawnPointType.GROUND;
         }

         this.pointType = pt;
      } else {
         this.pointType = pointType;
      }
   }

   public SpawnReason getReason() {
      return this.spawnReason;
   }

   public AbstractBiome getBiome() {
      return MythicProvider.get().getBootstrap().getBiome(this.location);
   }

   public AbstractWorld getWorld() {
      return this.location.getWorld();
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof RandomSpawnPoint other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (Float.compare(this.getLevelMod(), other.getLevelMod()) != 0) {
         return false;
      } else if (this.getNearbyPlayers() != other.getNearbyPlayers()) {
         return false;
      } else if (Float.compare(this.getLocalMobCount(), other.getLocalMobCount()) != 0) {
         return false;
      } else {
         Object this$location = this.getLocation();
         Object other$location = other.getLocation();
         if (this$location == null ? other$location == null : this$location.equals(other$location)) {
            Object this$pointType = this.getPointType();
            Object other$pointType = other.getPointType();
            if (this$pointType == null ? other$pointType == null : this$pointType.equals(other$pointType)) {
               Object this$entity = this.getEntity();
               Object other$entity = other.getEntity();
               if (this$entity == null ? other$entity == null : this$entity.equals(other$entity)) {
                  Object this$spawnReason = this.getSpawnReason();
                  Object other$spawnReason = other.getSpawnReason();
                  if (this$spawnReason == null ? other$spawnReason == null : this$spawnReason.equals(other$spawnReason)) {
                     Object this$triggeringPlayer = this.getTriggeringPlayer();
                     Object other$triggeringPlayer = other.getTriggeringPlayer();
                     return this$triggeringPlayer == null ? other$triggeringPlayer == null : this$triggeringPlayer.equals(other$triggeringPlayer);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof RandomSpawnPoint;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + Float.floatToIntBits(this.getLevelMod());
      result = result * 59 + this.getNearbyPlayers();
      result = result * 59 + Float.floatToIntBits(this.getLocalMobCount());
      Object $location = this.getLocation();
      result = result * 59 + ($location == null ? 43 : $location.hashCode());
      Object $pointType = this.getPointType();
      result = result * 59 + ($pointType == null ? 43 : $pointType.hashCode());
      Object $entity = this.getEntity();
      result = result * 59 + ($entity == null ? 43 : $entity.hashCode());
      Object $spawnReason = this.getSpawnReason();
      result = result * 59 + ($spawnReason == null ? 43 : $spawnReason.hashCode());
      Object $triggeringPlayer = this.getTriggeringPlayer();
      return result * 59 + ($triggeringPlayer == null ? 43 : $triggeringPlayer.hashCode());
   }
}
