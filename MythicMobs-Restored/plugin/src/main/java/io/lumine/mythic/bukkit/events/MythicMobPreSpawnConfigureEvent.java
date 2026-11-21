package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MythicMobPreSpawnConfigureEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private final MythicMob mobType;
   private final AbstractEntity entity;
   private double level;
   private final SpawnReason spawnReason;

   public MythicMobPreSpawnConfigureEvent(MythicMob type, AbstractEntity entity, double level) {
      this(type, entity, level, SpawnReason.OTHER);
   }

   public MythicMobPreSpawnConfigureEvent(MythicMob type, AbstractEntity entity, double level, SpawnReason spawnReason) {
      this.mobType = type;
      this.entity = entity;
      this.level = level;
      this.spawnReason = spawnReason;
   }

   public MythicMob getMobType() {
      return this.mobType;
   }

   public double getMobLevel() {
      return this.level;
   }

   public void setMobLevel(double level) {
      this.level = level;
   }

   public Entity getEntity() {
      return BukkitAdapter.adapt(this.entity);
   }

   public SpawnReason getSpawnReason() {
      return this.spawnReason;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
