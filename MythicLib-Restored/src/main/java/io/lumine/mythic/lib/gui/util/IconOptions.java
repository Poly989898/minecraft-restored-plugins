package io.lumine.mythic.lib.gui.util;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.util.config.YamlUtils;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

/**
 * The look of an icon in a GUI: material and the optional custom model data, item model, skull texture,
 * item flags... Every field is optional, unset fields are inherited when two options are {@link #combine combined}.
 */
public class IconOptions {
   private final Material material;
   private final Integer customModelDataInt;
   private final String customModelDataString;
   private final Float customModelDataFloat;
   private final ItemFlag[] itemFlags;
   private final Boolean hideTooltip;
   private final Boolean fakeAttribute;
   private final Boolean unbreakable;
   private final NamespacedKey itemModel;
   private final String skullTexture;
   private final String tooltipStyle;

   public static final IconOptions EMPTY = new IconOptions();

   public IconOptions() {
      this((Material)null, (Integer)null);
   }

   public IconOptions(Material material) {
      this(material, (Integer)null);
   }

   public IconOptions(Material material, Integer customModelData) {
      this(material, customModelData, null, null, null, null, null, null, null, null, null);
   }

   public IconOptions(Material material, String customModelData) {
      this(material, null, customModelData, null, null, null, null, null, null, null, null);
   }

   public IconOptions(
      Material material,
      Integer customModelDataInt,
      String customModelDataString,
      Float customModelDataFloat,
      NamespacedKey itemModel,
      String skullTexture,
      String tooltipStyle,
      ItemFlag[] itemFlags,
      Boolean hideTooltip,
      Boolean fakeAttribute,
      Boolean unbreakable
   ) {
      this.material = material;
      this.customModelDataInt = customModelDataInt;
      this.customModelDataString = customModelDataString;
      this.customModelDataFloat = customModelDataFloat;
      this.itemModel = itemModel;
      this.skullTexture = skullTexture;
      this.tooltipStyle = tooltipStyle;
      this.itemFlags = itemFlags;
      this.hideTooltip = hideTooltip;
      this.fakeAttribute = fakeAttribute;
      this.unbreakable = unbreakable;
   }

   public Material getMaterial() {
      return this.material;
   }

   public Material getMaterialElse(Material fallback) {
      return this.material == null ? fallback : this.material;
   }

   public Integer getCustomModelDataInt() {
      return this.customModelDataInt;
   }

   public String getCustomModelDataString() {
      return this.customModelDataString;
   }

   public NamespacedKey getItemModel() {
      return this.itemModel;
   }

   public String getSkullTexture() {
      return this.skullTexture;
   }

   public void applyToItemMeta(ItemMeta meta) {
      if (this.customModelDataInt != null) {
         meta.setCustomModelData(this.customModelDataInt);
      }

      boolean componentData = this.customModelDataString != null || this.customModelDataFloat != null;
      if (componentData && MythicLib.plugin.getVersion().isAbove(1, 21, 4)) {
         CustomModelDataComponent component = meta.getCustomModelDataComponent();
         if (this.customModelDataString != null) {
            component.setStrings(Collections.singletonList(this.customModelDataString));
         }

         if (this.customModelDataFloat != null) {
            component.setFloats(Collections.singletonList(this.customModelDataFloat));
         }

         meta.setCustomModelDataComponent(component);
      }

      if (this.itemModel != null && MythicLib.plugin.getVersion().isAbove(1, 21, 2)) {
         meta.setItemModel(this.itemModel);
      }

      if (this.skullTexture != null && meta instanceof SkullMeta) {
         UtilityMethods.setTextureValue((SkullMeta)meta, this.skullTexture);
      }

      if (this.tooltipStyle != null && MythicLib.plugin.getVersion().isAbove(1, 21, 2)) {
         meta.setTooltipStyle(NamespacedKey.fromString(this.tooltipStyle));
      }

      if (this.itemFlags != null) {
         meta.addItemFlags(this.itemFlags);
      }

      if (this.hideTooltip != null && MythicLib.plugin.getVersion().isAbove(1, 20, 5)) {
         meta.setHideTooltip(this.hideTooltip);
      }

      if (this.fakeAttribute != null && this.fakeAttribute) {
         VersionUtils.addEmptyAttributeModifier(meta);
      }

      if (this.unbreakable != null) {
         meta.setUnbreakable(this.unbreakable);
      }
   }

   public ItemStack toItemStack() {
      ItemStack item = new ItemStack(Objects.requireNonNullElse(this.material, Material.BARRIER));
      ItemMeta meta = item.getItemMeta();
      this.applyToItemMeta(meta);
      item.setItemMeta(meta);
      return item;
   }

   @Override
   public String toString() {
      return "IconOptions{material=" + this.material + ", cmd=" + this.customModelDataInt + ", cmdString=" + this.customModelDataString
         + ", cmdFloat=" + this.customModelDataFloat + ", itemModel=" + this.itemModel + ", texture=" + this.skullTexture + "}";
   }

   private static <T> T fallback(T first, T second) {
      return first != null ? first : second;
   }

   /** Every field set in this icon wins, the ones that are not set are taken from the other icon. */
   public IconOptions combine(IconOptions other) {
      return new IconOptions(
         fallback(this.material, other.material),
         fallback(this.customModelDataInt, other.customModelDataInt),
         fallback(this.customModelDataString, other.customModelDataString),
         fallback(this.customModelDataFloat, other.customModelDataFloat),
         fallback(this.itemModel, other.itemModel),
         fallback(this.skullTexture, other.skullTexture),
         fallback(this.tooltipStyle, other.tooltipStyle),
         fallback(this.itemFlags, other.itemFlags),
         fallback(this.hideTooltip, other.hideTooltip),
         fallback(this.fakeAttribute, other.fakeAttribute),
         fallback(this.unbreakable, other.unbreakable)
      );
   }

   private static ItemFlag[] parseItemFlagArray(List<String> list) {
      if (list == null) {
         return null;
      }

      ItemFlag[] flags = new ItemFlag[list.size()];
      for (int i = 0; i < flags.length; i++) {
         flags[i] = UtilityMethods.prettyValueOf(name -> ItemFlag.valueOf(UtilityMethods.enumName(name)), list.get(i), "No item flag with ID '%s'");
      }
      return flags;
   }

   /** Reads an icon from a string ("MATERIAL" or "MATERIAL:modeldata") or from a configuration section. */
   public static IconOptions from(Object object) {
      if (object instanceof String) {
         String[] split = ((String)object).split("[:.,]");
         Material material = Material.valueOf(UtilityMethods.enumName(split[0]));
         int model = split.length == 1 ? 0 : Integer.parseInt(split[1]);
         return new IconOptions(material, model);
      }

      if (object instanceof ConfigurationSection) {
         ConfigurationSection config = (ConfigurationSection)object;
         String materialName = YamlUtils.getString(config, "item", "material");
         Material material = materialName == null
            ? null
            : UtilityMethods.prettyValueOf(name -> Material.valueOf(UtilityMethods.enumName(name)), materialName, "Could not find material with ID '%s'");
         Integer cmdInt = YamlUtils.getInteger(config, "custom_model_data", "custom-model-data", "model-data", "cmd", "model_data");
         String cmdString = YamlUtils.getString(config, "custom_model_data_string", "custom-model-data-string", "cmd-string", "cmd_string", "cmds");
         Float cmdFloat = YamlUtils.getFloatObj(config, "custom_model_data_float", "custom-model-data-float", "cmd-float", "cmd_float", "cmdf");
         String model = YamlUtils.getString(config, "model", "item_model", "item-model");
         NamespacedKey itemModel = model == null ? null : NamespacedKey.fromString(model);
         String texture = YamlUtils.getString(config, "texture", "skull_texture", "skull-texture");
         String tooltip = YamlUtils.getString(config, "tooltip", "tooltip_style", "tooltip-style");
         ItemFlag[] flags = YamlUtils.getBoolean(config, "hide-flags", "hide_flags")
            ? ItemFlag.values()
            : parseItemFlagArray(config.contains("item_flags") || config.contains("item-flags") ? YamlUtils.getStringList(config, "item_flags", "item-flags") : null);
         Boolean hideTooltip = YamlUtils.getBooleanObj(config, "hide_tooltip", "hide-tooltip");
         Boolean fakeAttribute = YamlUtils.getBooleanObj(config, "fake_attribute_modifier", "fake-attribute-modifier");
         Boolean unbreakable = YamlUtils.getBooleanObj(config, "unbreakable");
         return new IconOptions(material, cmdInt, cmdString, cmdFloat, itemModel, texture, tooltip, flags, hideTooltip, fakeAttribute, unbreakable);
      }

      throw new IllegalArgumentException("Could not read icon");
   }

   /** Captures the icon-related data of an item. */
   public static IconOptions from(ItemStack item) {
      ItemMeta meta = item.getItemMeta();
      Integer cmdInt = meta != null && meta.hasCustomModelData() ? meta.getCustomModelData() : null;
      String cmdString = null;
      if (meta != null && MythicLib.plugin.getVersion().isAbove(1, 21, 4)) {
         List<String> strings = meta.getCustomModelDataComponent().getStrings();
         cmdString = strings.isEmpty() ? null : strings.getFirst();
      }

      NamespacedKey itemModel = meta != null && MythicLib.plugin.getVersion().isAbove(1, 21, 2) ? meta.getItemModel() : null;
      return new IconOptions(item.getType(), cmdInt, cmdString, null, itemModel, null, null, null, null, null, null);
   }
}
