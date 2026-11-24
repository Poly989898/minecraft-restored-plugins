package io.lumine.mythic.lib.api.explorer;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder extends ItemStack {
   public ItemBuilder(Material material, String name) {
      super(material);
      ItemMeta meta = this.getItemMeta();
      meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
      meta.addItemFlags(ItemFlag.values());
      this.setItemMeta(meta);
   }

   public ItemBuilder setLore(String... lines) {
      ItemMeta meta = this.getItemMeta();
      List<String> lore = new ArrayList<>();

      for (String line : lines) {
         lore.add(lore.size(), ChatColor.translateAlternateColorCodes('&', line));
      }

      meta.setLore(lore);
      this.setItemMeta(meta);
      return this;
   }
}
