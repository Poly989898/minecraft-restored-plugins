package io.lumine.mythic.lib.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class IndicatorDisplayEvent extends EntityEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private String message;
   private boolean cancelled;
   private final IndicatorDisplayEvent.IndicatorType type;

   public IndicatorDisplayEvent(@NotNull Entity entity, String message, IndicatorDisplayEvent.IndicatorType type) {
      super(entity);
      this.message = message;
      this.type = type;
   }

   public IndicatorDisplayEvent.IndicatorType getType() {
      return this.type;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancel) {
      this.cancelled = cancel;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public enum IndicatorType {
      DAMAGE,
      REGENERATION;
   }
}
