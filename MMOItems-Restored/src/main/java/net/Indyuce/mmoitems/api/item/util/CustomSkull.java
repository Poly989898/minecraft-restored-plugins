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

   public CustomSkull(String text, String text2) {
      this(text, text2, null);
   }

   public CustomSkull(String text, String text2, String text3, String... texts) {
      super(text, Material.PLAYER_HEAD, text3, texts);
      this.textureValue = text2;
   }

   @Override
   public void updateItem() {
      this.setItem(new ItemStack(Material.PLAYER_HEAD));
      SkullMeta itemMeta = (SkullMeta)this.getItem().getItemMeta();
      AdventureUtils.setDisplayName(itemMeta, this.getName());
      itemMeta.addItemFlags(ItemFlag.values());
      UtilityMethods.setTextureValue(itemMeta, this.textureValue);
      if (this.hasLore()) {
         AdventureUtils.setLore(itemMeta, this.getLore().stream().map(text -> ChatColor.GRAY + text).collect(Collectors.toList()));
      }

      this.getItem().setItemMeta(itemMeta);
      this.setItem(MythicLib.plugin.getVersion().getWrapper().getNBTItem(this.getItem()).addTag(new ItemTag[]{new ItemTag("ItemId", this.getId())}).toItem());
   }
}
