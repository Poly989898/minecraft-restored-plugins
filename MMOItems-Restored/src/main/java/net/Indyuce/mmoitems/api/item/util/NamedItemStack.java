package net.Indyuce.mmoitems.api.item.util;

import io.lumine.mythic.lib.MythicLib;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Deprecated
public class NamedItemStack extends ItemStack {
   @Deprecated
   public NamedItemStack(Material material, String text) {
      super(material);
      ItemMeta itemMeta = this.getItemMeta();
      itemMeta.setDisplayName(MythicLib.plugin.parseColors(text));
      this.setItemMeta(itemMeta);
   }
}
