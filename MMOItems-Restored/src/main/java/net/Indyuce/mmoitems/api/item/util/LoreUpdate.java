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

   public LoreUpdate(ItemStack item, String text, String replacement) {
      this.item = item;
      this.old = text;
      this.replace = replacement;
      this.lore = item.getItemMeta().getLore();
   }

   public ItemStack updateLore() {
      if (this.lore != null && !this.lore.isEmpty()) {
         for (int i = 0; i < this.lore.size(); i++) {
            if (this.lore.get(i).equalsIgnoreCase(this.old)) {
               this.lore.set(i, this.replace);
               ItemMeta itemMeta = this.item.getItemMeta();
               AdventureUtils.setLore(itemMeta, this.lore);
               this.item.setItemMeta(itemMeta);
               return this.item;
            }
         }

         return this.item;
      } else {
         return this.item;
      }
   }
}
