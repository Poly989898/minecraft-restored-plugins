package net.Indyuce.mmoitems.gui;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class PluginInventory implements InventoryHolder {
   protected final PlayerData playerData;
   protected final Player player;
   protected int page = 1;

   public PluginInventory(@NotNull Player player) {
      this(PlayerData.get(player));
   }

   public PluginInventory(PlayerData playerData) {
      this.playerData = playerData;
      this.player = playerData.getPlayer();
   }

   public int getPage() {
      return this.page;
   }

   public Player getPlayer() {
      return this.player;
   }

   public PlayerData getPlayerData() {
      return this.playerData;
   }

   public abstract Inventory getInventory();

   public abstract void whenClicked(InventoryClickEvent event);

   public void open() {
      if (Bukkit.isPrimaryThread()) {
         this.player.openInventory(this.getInventory());
      } else {
         Bukkit.getScheduler().runTask(MMOItems.plugin, () -> this.player.openInventory(this.getInventory()));
      }
   }
}
