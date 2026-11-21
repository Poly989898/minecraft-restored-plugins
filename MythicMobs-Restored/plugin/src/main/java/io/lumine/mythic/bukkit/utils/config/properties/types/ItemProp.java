package io.lumine.mythic.bukkit.utils.config.properties.types;

import io.lumine.mythic.bukkit.utils.config.properties.PropertyType;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ItemProp extends PropertyType<ItemStack> {
   private ItemStack defaultValue;
   private MaterialProp MATERIAL;
   private StringProp ITEM_MODEL;
   private IntProp AMOUNT;
   private StringProp DISPLAY;
   private StringListProp LORE;
   private BooleanProp HIDE_FLAGS;
   private StringProp COLOR;
   private StringProp SKULL_OWNER;
   private StringProp SKULL_TEXTURE;

   public ItemProp(LuminePlugin plugin, Object file, String node) {
      super(plugin, file, node);
      if (node != null && !node.isEmpty()) {
         node = node + ".";
      } else {
         node = "";
      }

      this.MATERIAL = new MaterialProp(plugin, file, node + "Material", Material.AIR);
      this.ITEM_MODEL = new StringProp(plugin, file, node + "ItemModel", null);
      this.AMOUNT = new IntProp(plugin, file, node + "Amount", 1);
      this.DISPLAY = new StringProp(plugin, file, node + "Display", " ");
      this.LORE = new StringListProp(plugin, file, node + "Lore");
      this.HIDE_FLAGS = new BooleanProp(plugin, file, node + "HideFlags", false);
      this.COLOR = new StringProp(plugin, file, node + "Color", null);
      this.SKULL_OWNER = new StringProp(plugin, file, node + "SkullOwner", null);
      this.SKULL_TEXTURE = new StringProp(plugin, file, node + "SkullTexture", null);
      this.defaultValue = new ItemStack(Material.STONE);
   }

   public ItemProp(LuminePlugin plugin, Object file, String node, ItemStack defaultValue) {
      super(plugin, file, node);
      this.defaultValue = defaultValue;
   }

   protected ItemStack compute(ConfigurationSection section, String node) {
      if (section == null) {
         return this.defaultValue;
      } else if (!section.isConfigurationSection(node)) {
         try {
            String str = section.getString(node);
            Material mat = Material.valueOf(str.toUpperCase());
            return new ItemStack(mat);
         } catch (Throwable e) {
            return new ItemStack(Material.STONE);
         }
      } else {
         Material material = this.MATERIAL.get(section);
         String modelNode = node + "Model";
         if (material == Material.AIR) {
            if (!section.isSet(modelNode)) {
               return this.defaultValue;
            }

            material = Material.PAPER;
         }

         int amount = this.AMOUNT.get(section);
         String display = this.DISPLAY.get(section);
         List<String> lore = this.LORE.get(section);
         String itemModel = this.ITEM_MODEL.get(section);
         boolean hideFlags = this.HIDE_FLAGS.get(section);
         String color = this.COLOR.get(section);
         String skullOwner = this.SKULL_OWNER.get(section);
         String skullTexture = this.SKULL_TEXTURE.get(section);
         ItemFactory factory = ItemFactory.of(material).name(display).lore(lore).amount(amount);
         if (itemModel != null) {
            try {
               factory.itemModel(itemModel);
            } catch (IllegalArgumentException ex) {
               Log.error("Error while setting ItemProp item model " + itemModel + ": " + ex.getMessage());
            }
         }

         if (section.isSet(modelNode)) {
            if (section.isInt(modelNode)) {
               factory.model(section.getInt(modelNode));
            } else if (section.isString(modelNode)) {
               factory.itemModel(section.getString(modelNode));
            }
         }

         if (color != null) {
            factory.color(color);
         }

         if (hideFlags) {
            factory.hideAttributes();
         }

         if (skullOwner != null) {
            factory.skullOwner(skullOwner);
         }

         if (skullTexture != null) {
            factory.skullTexture(skullTexture);
         }

         return factory.build();
      }
   }

   public void set(String holder, ItemStack value) {
      if (this.config != null) {
         ;
      }
   }

   public ItemStack getDefaultValue() {
      return this.defaultValue;
   }
}
