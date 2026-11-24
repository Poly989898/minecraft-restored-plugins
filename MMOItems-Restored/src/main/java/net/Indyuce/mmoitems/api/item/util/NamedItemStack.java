package net.Indyuce.mmoitems.api.item.util;

import io.lumine.mythic.lib.MythicLib;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Deprecated
public class NamedItemStack extends ItemStack {
   @Deprecated
   public NamedItemStack(Material var1, String var2) {
      super(var1);
      ItemMeta var3 = this.getItemMeta();
      var3.setDisplayName(MythicLib.plugin.parseColors(var2));
      this.setItemMeta(var3);
   }
}
