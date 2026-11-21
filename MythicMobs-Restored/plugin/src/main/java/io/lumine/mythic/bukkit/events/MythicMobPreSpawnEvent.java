package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MythicMobPreSpawnEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final MythicMob mobType;
   private final AbstractLocation location;
   private double level;
   private SpawnReason spawnReason;
   private boolean cancelled = false;

   public MythicMobPreSpawnEvent(MythicMob type, AbstractLocation location, double level) {
      this(type, location, level, SpawnReason.OTHER);
   }

   public MythicMobPreSpawnEvent(MythicMob type, AbstractLocation location, double level, SpawnReason spawnReason) {
      this.mobType = type;
      this.location = location;
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

   public Location getLocation() {
      return BukkitAdapter.adapt(this.location);
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
