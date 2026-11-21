package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MythicMobInteractEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private ActiveMob mob;
   private Player p;
   private boolean cancelled = false;

   public MythicMobInteractEvent(Player p, ActiveMob am) {
      this.mob = am;
      this.p = p;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public ActiveMob getActiveMob() {
      return this.mob;
   }

   public MythicMob getActiveMobType() {
      return this.mob.getType();
   }

   public Player getPlayer() {
      return this.p;
   }

   public void setCancelled() {
      this.cancelled = true;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }
}
