package net.Indyuce.mmoitems.api.event;

import java.util.List;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.inventory.EquippedItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RefreshInventoryEvent extends Event {
   @NotNull
   final List<EquippedItem> itemsToEquip;
   @NotNull
   final Player player;
   @NotNull
   final PlayerData playerData;
   @NotNull
   static final HandlerList handlers = new HandlerList();

   @NotNull
   public List<EquippedItem> getItemsToEquip() {
      return this.itemsToEquip;
   }

   @NotNull
   public Player getPlayer() {
      return this.player;
   }

   @NotNull
   public PlayerData getPlayerData() {
      return this.playerData;
   }

   public RefreshInventoryEvent(@NotNull List<EquippedItem> var1, @NotNull Player var2, @NotNull PlayerData var3) {
      this.itemsToEquip = var1;
      this.player = var2;
      this.playerData = var3;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   @NotNull
   public static HandlerList getHandlerList() {
      return handlers;
   }
}
