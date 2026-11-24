package net.Indyuce.mmoitems.api.item.util;

import io.lumine.mythic.lib.util.AdventureUtils;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoreUpdate {
   private final ItemStack item;
   private final String old;
   private final String replace;
   private final List<String> lore;

   public LoreUpdate(ItemStack var1, String var2, String var3) {
      this.item = var1;
      this.old = var2;
      this.replace = var3;
      this.lore = var1.getItemMeta().getLore();
   }

   public ItemStack updateLore() {
      if (this.lore != null && !this.lore.isEmpty()) {
         for (int var1 = 0; var1 < this.lore.size(); var1++) {
            if (this.lore.get(var1).equalsIgnoreCase(this.old)) {
               this.lore.set(var1, this.replace);
               ItemMeta var2 = this.item.getItemMeta();
               AdventureUtils.setLore(var2, this.lore);
               this.item.setItemMeta(var2);
               return this.item;
            }
         }

         return this.item;
      } else {
         return this.item;
      }
   }
}
