package io.lumine.mythic.lib.gui.editable.item;

import io.lumine.mythic.lib.gui.util.IconOptions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/** Index of the displayed entry of an item plus the icon overrides applied on top of the configured icon. */
public class ItemOptions {
   private final int index;
   private final IconOptions icon;

   public ItemOptions(int index, IconOptions icon) {
      this.index = index;
      this.icon = icon;
   }

   public int index() {
      return this.index;
   }

   public IconOptions icon() {
      return this.icon;
   }

   public static ItemOptions index(int index) {
      return new ItemOptions(index, IconOptions.EMPTY);
   }

   public static ItemOptions material(int index, Material material) {
      return new ItemOptions(index, new IconOptions(material));
   }

   public static ItemOptions model(int index, Material material, int customModelData) {
      return new ItemOptions(index, new IconOptions(material, customModelData));
   }

   public static ItemOptions item(int index, ItemStack item) {
      return new ItemOptions(index, IconOptions.from(item));
   }
}
