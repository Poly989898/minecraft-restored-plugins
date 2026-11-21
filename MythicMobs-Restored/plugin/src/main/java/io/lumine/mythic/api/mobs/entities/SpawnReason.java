package io.lumine.mythic.api.mobs.entities;

import lombok.Generated;
import lombok.Getter;

@Getter
public class SpawnReason {
   public static final SpawnReason NATURAL = new SpawnReason(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
   public static final SpawnReason COMMAND = new SpawnReason(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
   public static final SpawnReason SPAWNER = new SpawnReason(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
   public static final SpawnReason SUMMON = new SpawnReason(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
   public static final SpawnReason TOTEM = new SpawnReason(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
   public static final SpawnReason OTHER = new SpawnReason(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
   public static final SpawnReason DISPENSER = new SpawnReason(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
   private final org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason bukkitReason;
   private boolean randomizeSpawnProperties = true;

   public SpawnReason(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason bukkitReason) {
      this.bukkitReason = bukkitReason;
   }

   public SpawnReason(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason bukkitReason, boolean randomizeSpawnProperties) {
      this.bukkitReason = bukkitReason;
      this.randomizeSpawnProperties = randomizeSpawnProperties;
   }

   @Generated
   public SpawnReason withRandomizeSpawnProperties(boolean randomizeSpawnProperties) {
      return this.randomizeSpawnProperties == randomizeSpawnProperties ? this : new SpawnReason(this.bukkitReason, randomizeSpawnProperties);
   }
}
