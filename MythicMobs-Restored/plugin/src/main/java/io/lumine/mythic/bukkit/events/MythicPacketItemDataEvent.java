package io.lumine.mythic.bukkit.events;

import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class MythicPacketItemDataEvent extends Event {
   private final Player player;
   private ItemStack itemStack;
   private boolean edited;
   private static final HandlerList handlers = new HandlerList();

   public MythicPacketItemDataEvent(Player player, ItemStack itemStack) {
      super(true);
      this.player = player;
      this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
   }

   public Player getPlayer() {
      return this.player;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public void setItemStack(ItemStack itemStack) {
      this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
      this.edited = true;
   }

   public boolean wasEdited() {
      return this.edited;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
