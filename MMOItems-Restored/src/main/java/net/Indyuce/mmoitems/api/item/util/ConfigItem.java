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

   public ConfigItem(String text, Material material) {
      this(text, material, null);
   }

   public ConfigItem(String text, Material material, String text2, String... texts) {
      Validate.notNull(text, "ID cannot be null");
      Validate.notNull(material, "Material cannot be null");
      this.id = text;
      this.icon = new ItemStack(material);
      this.name = text2;
      this.lore = Arrays.asList(texts);
   }

   public ConfigItem(ConfigurationSection section) {
      Validate.notNull(section, "Config cannot be null");
      this.id = section.getName();
      Validate.isTrue(section.contains("material"), "Could not find material");
      this.icon = MMOUtils.readIcon(section.getString("material"));
      this.name = section.getString("name", "");
      this.lore = section.getStringList("lore");
      this.updateItem();
   }

   public String getId() {
      return this.id;
   }

   @Deprecated
   public void setup(ConfigurationSection section) {
      section.set("name", this.getName());
      section.set("lore", this.getLore());
   }

   @Deprecated
   public void update(ConfigurationSection section) {
      Validate.notNull(section, "Config cannot be null");
      this.setName(section.getString("name", ""));
      this.setLore(section.contains("lore") ? section.getStringList("lore") : new ArrayList<>());
      String material = section.getString("material");
      if (material != null && !material.isEmpty()) {
         try {
            Material material2 = Material.valueOf(section.getString("material"));
            if (material2.isItem()) {
               this.setMaterial(material2);
            }
         } catch (IllegalArgumentException exception) {
         }
      }

      this.setModel(SilentNumbers.IntegerParse(section.getString("model")));
      this.updateItem();
   }

   public void updateItem() {
      this.setItem(this.icon);
      if (this.icon.getType() != Material.AIR) {
         ItemMeta itemMeta = this.item.getItemMeta();
         AdventureUtils.setDisplayName(itemMeta, this.name);
         itemMeta.addItemFlags(ItemFlag.values());
         if (this.hasLore()) {
            AdventureUtils.setLore(itemMeta, this.getLore().stream().map(text -> ChatColor.GRAY + text).collect(Collectors.toList()));
         }

         this.item.setItemMeta(itemMeta);
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

   protected void setName(String text) {
      this.name = text;
   }

   protected void setLore(List<String> texts) {
      this.lore = texts;
   }

   @Deprecated
   protected void setItem(ItemStack item) {
      this.item = item;
   }

   @Deprecated
   protected void setMaterial(@Nullable Material material) {
      this.material = material;
   }

   @Deprecated
   protected void setModel(@Nullable Integer value) {
      this.customModelData = value;
   }
}
