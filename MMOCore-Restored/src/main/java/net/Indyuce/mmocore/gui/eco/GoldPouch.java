package net.Indyuce.mmocore.gui.eco;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.gui.PluginInventory;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GoldPouch extends PluginInventory {
   private final boolean mob;
   private final NBTItem nbt;
   private Inventory lastBukkitInventory;

   public GoldPouch(Player player, NBTItem nbt) {
      super(player);
      this.nbt = nbt;
      this.mob = nbt.getBoolean("RpgPouchMob");
   }

   public Inventory getInventory() {
      Inventory inv = Bukkit.createInventory(this, 18, ChatColor.UNDERLINE + "Gold Pouch");
      inv.setContents(MMOCoreUtils.itemStackArrayFromBase64(this.nbt.getString("RpgPouchInventory")));
      return this.lastBukkitInventory = inv;
   }

   public void onClick(InventoryClickEvent event) {
      ItemStack item = event.getCurrentItem();
      if (item != null && item.getType() != Material.AIR) {
         NBTItem nbt = NBTItem.get(item);
         if (!nbt.hasTag("RpgWorth")) {
            event.setCancelled(true);
         } else if (this.mob) {
            event.setCancelled(true);
            if (event.getSlot() < 18) {
               int empty = this.player.getInventory().firstEmpty();
               if (empty < 0) {
                  return;
               }

               this.player.playSound(this.player.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 1.0F, 2.0F);
               this.player.getInventory().addItem(new ItemStack[]{event.getCurrentItem()});
               event.getInventory().setItem(event.getSlot(), null);
            }
         } else {
            if (nbt.hasTag("RpgPouchInventory")) {
               event.setCancelled(true);
            }
         }
      } else {
         event.setCancelled(true);
      }
   }

   public void onClose() {
      if (this.mob && this.isEmpty(this.lastBukkitInventory)) {
         this.player.getEquipment().setItemInMainHand(null);
      } else {
         ItemStack updated = NBTItem.get(this.player.getEquipment().getItemInMainHand())
            .addTag(new ItemTag[]{new ItemTag("RpgPouchInventory", MMOCoreUtils.toBase64(this.lastBukkitInventory.getContents()))})
            .toItem();
         this.player.getEquipment().setItemInMainHand(updated);
      }
   }

   private boolean isEmpty(Inventory inv) {
      for (ItemStack item : inv.getContents()) {
         if (item != null && item.getType() != Material.AIR) {
            return false;
         }
      }

      return true;
   }
}
