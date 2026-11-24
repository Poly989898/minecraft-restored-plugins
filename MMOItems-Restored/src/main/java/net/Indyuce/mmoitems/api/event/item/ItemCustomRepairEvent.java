package net.Indyuce.mmoitems.api.event.item;

import net.Indyuce.mmoitems.api.interaction.util.DurabilityItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ItemCustomRepairEvent extends Event implements Cancellable {
   @NotNull
   private final DurabilityItem sourceItem;
   private final int durabilityIncrease;
   private boolean cancelled;
   private static final HandlerList handlers = new HandlerList();

   public ItemCustomRepairEvent(@NotNull DurabilityItem var1, int var2) {
      this.sourceItem = var1;
      this.durabilityIncrease = var2;
   }

   public boolean hasPlayer() {
      return this.sourceItem.getPlayer() != null;
   }

   public Player getPlayer() {
      return this.sourceItem.getPlayer();
   }

   public int getDurabilityIncrease() {
      return this.durabilityIncrease;
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
