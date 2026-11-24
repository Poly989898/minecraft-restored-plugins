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

   public CustomDurabilityDamage(@NotNull DurabilityItem durabilityItem, int count) {
      this.sourceItem = durabilityItem;
      this.durabilityDecrease = count;
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

   public void setCancelled(boolean flag) {
      this.cancelled = flag;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
