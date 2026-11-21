package io.lumine.mythic.api.adapters.items.components;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Collection;
import java.util.Map;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AbstractItemSpawnerComponent implements AbstractItemComponent {
   private Integer delay;
   private Integer maxSpawnDelay;
   private Integer minSpawnDelay;
   private Integer requiredPlayerRange;
   private Integer spawnCount;
   private Integer spawnRange;
   private Integer maxNearbyEntities;
   private Collection<AbstractItemSpawnerComponent.PotentialSpawns> potentialSpawns = Lists.newArrayList();

   public AbstractItemSpawnerComponent(MythicItem item, MythicConfig config) {
      if (config.isSet("Delay")) {
         this.delay = config.getInteger("Delay");
      }

      if (config.isSet("MaxSpawnDelay")) {
         this.maxSpawnDelay = config.getInteger("MaxSpawnDelay");
      }

      if (config.isSet("MinSpawnDelay")) {
         this.minSpawnDelay = config.getInteger("MinSpawnDelay");
      }

      if (config.isSet("RequiredPlayerRange")) {
         this.requiredPlayerRange = config.getInteger("RequiredPlayerRange");
      }

      if (config.isSet("SpawnCount")) {
         this.spawnCount = config.getInteger("SpawnCount");
      }

      if (config.isSet("SpawnRange")) {
         this.spawnRange = config.getInteger("SpawnRange");
      }

      if (config.isSet("MaxNearbyEntities")) {
         this.maxNearbyEntities = config.getInteger("MaxNearbyEntities");
      }

      if (config.isSet("Mobs")) {
         if (config.isList("Mobs") && !config.getStringList("Mobs").isEmpty()) {
            for (String line : config.getStringList("Mobs")) {
               String[] split = line.split(" ");
               String type = split[0];
               int weight = 1;
               if (split.length > 1) {
                  try {
                     weight = Integer.parseInt(split[1]);
                  } catch (NumberFormatException e) {
                     MythicLogger.errorItemConfig(item, config, "Invalid weight for spawner item");
                  }
               }

               this.potentialSpawns.add(new AbstractItemSpawnerComponent.PotentialSpawns(type, weight));
            }
         } else {
            for (Map<?, ?> mob : config.getMapList("Mobs")) {
               AbstractItemSpawnerComponent.PotentialSpawns spawn = new AbstractItemSpawnerComponent.PotentialSpawns(mob);
               this.potentialSpawns.add(spawn);
            }
         }
      } else if (config.isSet("Mob")) {
         String mob = config.getString("Mob");
         this.potentialSpawns.add(new AbstractItemSpawnerComponent.PotentialSpawns(mob));
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyMobSpawnerComponent(this);
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemSpawnerComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$delay = this.getDelay();
         Object other$delay = other.getDelay();
         if (this$delay == null ? other$delay == null : this$delay.equals(other$delay)) {
            Object this$maxSpawnDelay = this.getMaxSpawnDelay();
            Object other$maxSpawnDelay = other.getMaxSpawnDelay();
            if (this$maxSpawnDelay == null ? other$maxSpawnDelay == null : this$maxSpawnDelay.equals(other$maxSpawnDelay)) {
               Object this$minSpawnDelay = this.getMinSpawnDelay();
               Object other$minSpawnDelay = other.getMinSpawnDelay();
               if (this$minSpawnDelay == null ? other$minSpawnDelay == null : this$minSpawnDelay.equals(other$minSpawnDelay)) {
                  Object this$requiredPlayerRange = this.getRequiredPlayerRange();
                  Object other$requiredPlayerRange = other.getRequiredPlayerRange();
                  if (this$requiredPlayerRange == null ? other$requiredPlayerRange == null : this$requiredPlayerRange.equals(other$requiredPlayerRange)) {
                     Object this$spawnCount = this.getSpawnCount();
                     Object other$spawnCount = other.getSpawnCount();
                     if (this$spawnCount == null ? other$spawnCount == null : this$spawnCount.equals(other$spawnCount)) {
                        Object this$spawnRange = this.getSpawnRange();
                        Object other$spawnRange = other.getSpawnRange();
                        if (this$spawnRange == null ? other$spawnRange == null : this$spawnRange.equals(other$spawnRange)) {
                           Object this$maxNearbyEntities = this.getMaxNearbyEntities();
                           Object other$maxNearbyEntities = other.getMaxNearbyEntities();
                           if (this$maxNearbyEntities == null ? other$maxNearbyEntities == null : this$maxNearbyEntities.equals(other$maxNearbyEntities)) {
                              Object this$potentialSpawns = this.getPotentialSpawns();
                              Object other$potentialSpawns = other.getPotentialSpawns();
                              return this$potentialSpawns == null ? other$potentialSpawns == null : this$potentialSpawns.equals(other$potentialSpawns);
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
      return other instanceof AbstractItemSpawnerComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $delay = this.getDelay();
      result = result * 59 + ($delay == null ? 43 : $delay.hashCode());
      Object $maxSpawnDelay = this.getMaxSpawnDelay();
      result = result * 59 + ($maxSpawnDelay == null ? 43 : $maxSpawnDelay.hashCode());
      Object $minSpawnDelay = this.getMinSpawnDelay();
      result = result * 59 + ($minSpawnDelay == null ? 43 : $minSpawnDelay.hashCode());
      Object $requiredPlayerRange = this.getRequiredPlayerRange();
      result = result * 59 + ($requiredPlayerRange == null ? 43 : $requiredPlayerRange.hashCode());
      Object $spawnCount = this.getSpawnCount();
      result = result * 59 + ($spawnCount == null ? 43 : $spawnCount.hashCode());
      Object $spawnRange = this.getSpawnRange();
      result = result * 59 + ($spawnRange == null ? 43 : $spawnRange.hashCode());
      Object $maxNearbyEntities = this.getMaxNearbyEntities();
      result = result * 59 + ($maxNearbyEntities == null ? 43 : $maxNearbyEntities.hashCode());
      Object $potentialSpawns = this.getPotentialSpawns();
      return result * 59 + ($potentialSpawns == null ? 43 : $potentialSpawns.hashCode());
   }

   @Getter
   @Setter
   @ToString
   public class PotentialSpawns {
      private String type;
      private int weight;
      private int minBlockLight;
      private int maxBlockLight;
      private int minSkyLight;
      private int maxSkyLight;

      public PotentialSpawns(String type) {
         this.type = type;
         this.weight = 1;
         this.minBlockLight = 0;
         this.maxBlockLight = 7;
         this.minSkyLight = 0;
         this.maxSkyLight = 7;
      }

      public PotentialSpawns(String type, int weight) {
         this.type = type;
         this.weight = weight;
         this.minBlockLight = 0;
         this.maxBlockLight = 7;
         this.minSkyLight = 0;
         this.maxSkyLight = 7;
      }

      public PotentialSpawns(Map<?, ?> traits) {
         this.type = (String)traits.get("Type");
         if (this.type == null) {
            this.type = "PIG";
         }

         if (traits.containsKey("Weight")) {
            this.weight = (Integer)traits.get("Weight");
         }

         if (traits.containsKey("MinLight")) {
            this.minBlockLight = (Integer)traits.get("MinLight");
            this.minSkyLight = this.minBlockLight;
         }

         if (traits.containsKey("MaxLight")) {
            this.maxBlockLight = (Integer)traits.get("MaxLight");
            this.maxSkyLight = this.maxBlockLight;
         }

         if (traits.containsKey("MinBlockLight")) {
            this.minBlockLight = (Integer)traits.get("MinBlockLight");
         }

         if (traits.containsKey("MaxBlockLight")) {
            this.maxBlockLight = (Integer)traits.get("MaxBlockLight");
         }

         if (traits.containsKey("MinSkyLight")) {
            this.minSkyLight = (Integer)traits.get("MinSkyLight");
         }

         if (traits.containsKey("MaxSkyLight")) {
            this.maxSkyLight = (Integer)traits.get("MaxSkyLight");
         }
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof AbstractItemSpawnerComponent.PotentialSpawns other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (this.getWeight() != other.getWeight()) {
            return false;
         } else if (this.getMinBlockLight() != other.getMinBlockLight()) {
            return false;
         } else if (this.getMaxBlockLight() != other.getMaxBlockLight()) {
            return false;
         } else if (this.getMinSkyLight() != other.getMinSkyLight()) {
            return false;
         } else if (this.getMaxSkyLight() != other.getMaxSkyLight()) {
            return false;
         } else {
            Object this$type = this.getType();
            Object other$type = other.getType();
            return this$type == null ? other$type == null : this$type.equals(other$type);
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof AbstractItemSpawnerComponent.PotentialSpawns;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + this.getWeight();
         result = result * 59 + this.getMinBlockLight();
         result = result * 59 + this.getMaxBlockLight();
         result = result * 59 + this.getMinSkyLight();
         result = result * 59 + this.getMaxSkyLight();
         Object $type = this.getType();
         return result * 59 + ($type == null ? 43 : $type.hashCode());
      }
   }
}
