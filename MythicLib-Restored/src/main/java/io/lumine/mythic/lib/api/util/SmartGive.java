package io.lumine.mythic.lib.api.util;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SmartGive {
   private final Inventory inv;
   private final Location loc;

   public SmartGive(Player player) {
      this.inv = player.getInventory();
      this.loc = player.getLocation();
   }

   public void give(ItemStack... item) {
      for (ItemStack drop : this.inv.addItem(item).values()) {
         this.loc.getWorld().dropItem(this.loc, drop);
      }
   }

   public void give(List<ItemStack> item) {
      for (ItemStack drop : this.inv.addItem(item.toArray(new ItemStack[0])).values()) {
         this.loc.getWorld().dropItem(this.loc, drop);
      }
   }
}
