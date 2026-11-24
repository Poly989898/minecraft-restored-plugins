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

   public InventoryUpdateHandler(PlayerData var1) {
      this.player = var1;
   }

   public List<EquippedItem> getEquipped() {
      return this.items;
   }

   public void updateCheck() {
      PlayerInventory var1 = this.player.getPlayer().getInventory();
      if (this.isDifferent(this.helmet, var1.getHelmet())
         || this.isDifferent(this.chestplate, var1.getChestplate())
         || this.isDifferent(this.leggings, var1.getLeggings())
         || this.isDifferent(this.boots, var1.getBoots())
         || this.isDifferent(this.hand, var1.getItemInMainHand())
         || this.isDifferent(this.offhand, var1.getItemInOffHand())) {
         this.player.updateInventory();
      }
   }

   public void scheduleUpdate() {
      Bukkit.getScheduler().scheduleSyncDelayedTask(MMOItems.plugin, this.player::updateInventory);
   }

   private boolean isDifferent(ItemStack var1, ItemStack var2) {
      if (var1 == null && var2 == null) {
         return false;
      } else {
         return (var1 != null || var2 == null) && (var1 == null || var2 != null) ? var1.hashCode() != var2.hashCode() && !var1.equals(var2) : true;
      }
   }
}
