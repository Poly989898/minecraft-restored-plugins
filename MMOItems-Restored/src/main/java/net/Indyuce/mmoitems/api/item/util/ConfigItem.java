package net.Indyuce.mmoitems.api.item.util;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.util.AdventureUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public class ConfigItem {
   private final String id;
   @Deprecated
   private final ItemStack icon;
   private String name;
   private List<String> lore;
   @Deprecated
   private ItemStack item;
   @Deprecated
   @Nullable
   protected Material material = null;
   @Deprecated
   @Nullable
   protected Integer customModelData = null;

   public ConfigItem(String var1, Material var2) {
      this(var1, var2, null);
   }

   public ConfigItem(String var1, Material var2, String var3, String... var4) {
      Validate.notNull(var1, "ID cannot be null");
      Validate.notNull(var2, "Material cannot be null");
      this.id = var1;
      this.icon = new ItemStack(var2);
      this.name = var3;
      this.lore = Arrays.asList(var4);
   }

   public ConfigItem(ConfigurationSection var1) {
      Validate.notNull(var1, "Config cannot be null");
      this.id = var1.getName();
      Validate.isTrue(var1.contains("material"), "Could not find material");
      this.icon = MMOUtils.readIcon(var1.getString("material"));
      this.name = var1.getString("name", "");
      this.lore = var1.getStringList("lore");
      this.updateItem();
   }

   public String getId() {
      return this.id;
   }

   @Deprecated
   public void setup(ConfigurationSection var1) {
      var1.set("name", this.getName());
      var1.set("lore", this.getLore());
   }

   @Deprecated
   public void update(ConfigurationSection var1) {
      Validate.notNull(var1, "Config cannot be null");
      this.setName(var1.getString("name", ""));
      this.setLore(var1.contains("lore") ? var1.getStringList("lore") : new ArrayList<>());
      String var2 = var1.getString("material");
      if (var2 != null && !var2.isEmpty()) {
         try {
            Material var3 = Material.valueOf(var1.getString("material"));
            if (var3.isItem()) {
               this.setMaterial(var3);
            }
         } catch (IllegalArgumentException var4) {
         }
      }

      this.setModel(SilentNumbers.IntegerParse(var1.getString("model")));
      this.updateItem();
   }

   public void updateItem() {
      this.setItem(this.icon);
      if (this.icon.getType() != Material.AIR) {
         ItemMeta var1 = this.item.getItemMeta();
         AdventureUtils.setDisplayName(var1, this.name);
         var1.addItemFlags(ItemFlag.values());
         if (this.hasLore()) {
            AdventureUtils.setLore(var1, this.getLore().stream().map(var0 -> ChatColor.GRAY + var0).collect(Collectors.toList()));
         }

         this.item.setItemMeta(var1);
         this.item = MythicLib.plugin.getVersion().getWrapper().getNBTItem(this.item).addTag(new ItemTag[]{new ItemTag("ItemId", this.id)}).toItem();
      }
   }

   public String getName() {
      return this.name;
   }

   public List<String> getLore() {
      return this.lore;
   }

   public boolean hasLore() {
      return this.lore != null;
   }

   @Deprecated
   public ItemStack getItem() {
      return this.item;
   }

   @Deprecated
   public ItemStack getNewItem() {
      return this.item.clone();
   }

   protected void setName(String var1) {
      this.name = var1;
   }

   protected void setLore(List<String> var1) {
      this.lore = var1;
   }

   @Deprecated
   protected void setItem(ItemStack var1) {
      this.item = var1;
   }

   @Deprecated
   protected void setMaterial(@Nullable Material var1) {
      this.material = var1;
   }

   @Deprecated
   protected void setModel(@Nullable Integer var1) {
      this.customModelData = var1;
   }
}
