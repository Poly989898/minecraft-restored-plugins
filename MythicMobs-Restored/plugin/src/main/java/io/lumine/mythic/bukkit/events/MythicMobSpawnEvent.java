package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MythicMobSpawnEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private ActiveMob mob;
   private double level;
   private MythicSpawner spawner;
   private SpawnReason spawnReason;
   private boolean cancelled = false;

   public MythicMobSpawnEvent(ActiveMob am, double level) {
      this(am, level, SpawnReason.OTHER);
   }

   public MythicMobSpawnEvent(ActiveMob am, double level, SpawnReason spawnReason) {
      this.mob = am;
      this.level = level;
      this.spawnReason = spawnReason;
      if (am.getSpawner() != null) {
         this.spawner = am.getSpawner();
      }
   }

   public ActiveMob getMob() {
      return this.mob;
   }

   public MythicMob getMobType() {
      return this.mob.getType();
   }

   public double getMobLevel() {
      return this.level;
   }

   public void setMobLevel(double level) {
      this.level = level;
   }

   public Location getLocation() {
      return this.mob.getEntity().getBukkitEntity().getLocation();
   }

   public Entity getEntity() {
      return this.mob.getEntity().getBukkitEntity();
   }

   public Boolean isFromMythicSpawner() {
      return this.spawner != null;
   }

   public MythicSpawner getMythicSpawner() {
      return this.spawner;
   }

   @Deprecated
   public LivingEntity getLivingEntity() {
      return this.mob.getEntity().getBukkitEntity() instanceof LivingEntity ? (LivingEntity)this.mob.getEntity().getBukkitEntity() : null;
   }

   @Deprecated
   public void setCancelled() {
      this.cancelled = true;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancel) {
      this.cancelled = cancel;
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
