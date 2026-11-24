package net.Indyuce.mmoitems.api.event;

import net.Indyuce.mmoitems.api.ReforgeOptions;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMOItemReforgeFinishEvent extends Event implements Cancellable {
   @NotNull
   final MMOItemReforger reforger;
   @NotNull
   final ReforgeOptions options;
   @NotNull
   ItemStack finishedItem;
   private static final HandlerList handlers = new HandlerList();
   boolean cancelled;

   @NotNull
   public MMOItemReforger getReforger() {
      return this.reforger;
   }

   @NotNull
   public ReforgeOptions getOptions() {
      return this.options;
   }

   @NotNull
   public ItemStack getFinishedItem() {
      return this.finishedItem;
   }

   public void setFinishedItem(@NotNull ItemStack var1) {
      this.finishedItem = var1;
   }

   public MMOItemReforgeFinishEvent(@NotNull ItemStack var1, @NotNull MMOItemReforger var2, @NotNull ReforgeOptions var3) {
      this.finishedItem = var1;
      this.reforger = var2;
      this.options = var3;
   }

   @Nullable
   public Player getPlayer() {
      return this.getReforger().getPlayer() == null ? null : this.getReforger().getPlayer().getPlayer();
   }

   @NotNull
   public Type getType() {
      return this.getReforger().getTemplate().getType();
   }

   @NotNull
   public String getTypeName() {
      return this.getReforger().getTemplate().getType().getId();
   }

   @NotNull
   public String getID() {
      return this.getReforger().getTemplate().getId();
   }

   @NotNull
   public LiveMMOItem getOldMMOItem() {
      return this.getReforger().getOldMMOItem();
   }

   @NotNull
   public MMOItem getNewMMOItem() {
      return this.getReforger().getFreshMMOItem();
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }
}
