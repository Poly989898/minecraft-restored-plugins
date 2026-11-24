package net.Indyuce.mmoitems.api.player.inventory;

import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUpdateHandler {
   private final PlayerData player;
   private final List<EquippedItem> items = new ArrayList<>();
   @Deprecated
   public ItemStack helmet = null;
   @Deprecated
   public ItemStack chestplate = null;
   @Deprecated
   public ItemStack leggings = null;
   @Deprecated
   public ItemStack boots = null;
   @Deprecated
   public ItemStack hand = null;
   @Deprecated
   public ItemStack offhand = null;

   public InventoryUpdateHandler(PlayerData playerData) {
      this.player = playerData;
   }

   public List<EquippedItem> getEquipped() {
      return this.items;
   }

   public void updateCheck() {
      PlayerInventory inventory = this.player.getPlayer().getInventory();
      if (this.isDifferent(this.helmet, inventory.getHelmet())
         || this.isDifferent(this.chestplate, inventory.getChestplate())
         || this.isDifferent(this.leggings, inventory.getLeggings())
         || this.isDifferent(this.boots, inventory.getBoots())
         || this.isDifferent(this.hand, inventory.getItemInMainHand())
         || this.isDifferent(this.offhand, inventory.getItemInOffHand())) {
         this.player.updateInventory();
      }
   }

   public void scheduleUpdate() {
      Bukkit.getScheduler().scheduleSyncDelayedTask(MMOItems.plugin, this.player::updateInventory);
   }

   private boolean isDifferent(ItemStack item, ItemStack item2) {
      if (item == null && item2 == null) {
         return false;
      } else {
         return (item != null || item2 == null) && (item == null || item2 != null) ? item.hashCode() != item2.hashCode() && !item.equals(item2) : true;
      }
   }
}
