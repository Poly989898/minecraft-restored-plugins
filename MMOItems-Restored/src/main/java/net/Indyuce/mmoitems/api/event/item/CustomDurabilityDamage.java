package net.Indyuce.mmoitems.api.event.item;

import net.Indyuce.mmoitems.api.interaction.util.DurabilityItem;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CustomDurabilityDamage extends Event implements Cancellable {
   @NotNull
   DurabilityItem sourceItem;
   int durabilityDecrease;
   boolean cancelled = false;
   private static final HandlerList handlers = new HandlerList();

   public CustomDurabilityDamage(@NotNull DurabilityItem var1, int var2) {
      this.sourceItem = var1;
      this.durabilityDecrease = var2;
   }

   public int getDurabilityDecrease() {
      return this.durabilityDecrease;
   }

   @NotNull
   public DurabilityItem getSourceItem() {
      return this.sourceItem;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
