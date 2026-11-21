package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MythicMobDespawnEvent extends Event {
   private final ActiveMob mob;
   private static final HandlerList handlers = new HandlerList();

   public MythicMobDespawnEvent(ActiveMob am) {
      this.mob = am;
   }

   public ActiveMob getMob() {
      return this.mob;
   }

   public Entity getEntity() {
      return BukkitAdapter.adapt(this.mob.getEntity());
   }

   public MythicMob getMobType() {
      return this.mob.getType();
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
