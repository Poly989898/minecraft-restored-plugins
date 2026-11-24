package net.Indyuce.mmoitems.api.item.util;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.util.AdventureUtils;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class CustomSkull extends ConfigItem {
   private final String textureValue;

   public CustomSkull(String var1, String var2) {
      this(var1, var2, null);
   }

   public CustomSkull(String var1, String var2, String var3, String... var4) {
      super(var1, Material.PLAYER_HEAD, var3, var4);
      this.textureValue = var2;
   }

   @Override
   public void updateItem() {
      this.setItem(new ItemStack(Material.PLAYER_HEAD));
      SkullMeta var1 = (SkullMeta)this.getItem().getItemMeta();
      AdventureUtils.setDisplayName(var1, this.getName());
      var1.addItemFlags(ItemFlag.values());
      UtilityMethods.setTextureValue(var1, this.textureValue);
      if (this.hasLore()) {
         AdventureUtils.setLore(var1, this.getLore().stream().map(var0 -> ChatColor.GRAY + var0).collect(Collectors.toList()));
      }

      this.getItem().setItemMeta(var1);
      this.setItem(MythicLib.plugin.getVersion().getWrapper().getNBTItem(this.getItem()).addTag(new ItemTag[]{new ItemTag("ItemId", this.getId())}).toItem());
   }
}
