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

   public ItemCustomRepairEvent(@NotNull DurabilityItem durabilityItem, int count) {
      this.sourceItem = durabilityItem;
      this.durabilityIncrease = count;
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
