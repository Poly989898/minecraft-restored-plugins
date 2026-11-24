package net.Indyuce.mmocore.api.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.resource.PlayerResource;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerResourceUpdateEvent extends PlayerDataEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final PlayerResource resource;
   private final PlayerResourceUpdateEvent.UpdateReason reason;
   private double amount;
   private boolean cancelled = false;

   public PlayerResourceUpdateEvent(PlayerData playerData, PlayerResource resource, double amount, PlayerResourceUpdateEvent.UpdateReason reason) {
      super(playerData);
      this.resource = resource;
      this.amount = amount;
      this.reason = reason;
   }

   public PlayerResource getResource() {
      return this.resource;
   }

   public double getAmount() {
      return this.amount;
   }

   public PlayerResourceUpdateEvent.UpdateReason getReason() {
      return this.reason;
   }

   public void setAmount(double amount) {
      this.amount = amount;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public enum UpdateReason {
      REGENERATION,
      SKILL_REGENERATION,
      SKILL_COST,
      USE_WAYPOINT,
      TRIGGER,
      COMMAND,
      OTHER;

      public boolean isSkill() {
         return this == SKILL_COST || this == SKILL_REGENERATION;
      }
   }
}
