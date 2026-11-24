package net.Indyuce.mmocore.util;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.gui.editable.item.ItemOptions;
import java.util.Objects;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Icon {
   private final Material material;
   private final int modelData;

   public Material getMaterial() {
      return this.material;
   }

   public int getModelData() {
      return this.modelData;
   }

   public Icon(@NotNull Material material, int modelData) {
      this.material = material;
      this.modelData = modelData;
   }

   public ItemStack toItem() {
      ItemStack stack = new ItemStack(this.material);
      if (this.modelData > 0) {
         ItemMeta meta = stack.getItemMeta();
         meta.setCustomModelData(this.modelData);
         stack.setItemMeta(meta);
      }

      return stack;
   }

   @NotNull
   public ItemOptions toItemOptions(int index) {
      return ItemOptions.model(index, this.material, this.modelData);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Icon icon = (Icon)o;
         return this.modelData == icon.modelData && this.material == icon.material;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.material, this.modelData);
   }

   @Override
   public String toString() {
      return "Icon{material=" + this.material + ", customModelData=" + this.modelData + "}";
   }

   public static Icon fromItem(@NotNull ItemStack from) {
      Material mat = from.getType();
      int cmd = from.hasItemMeta() ? from.getItemMeta().getCustomModelData() : 0;
      return new Icon(mat, cmd);
   }

   @NotNull
   public static Icon from(@Nullable Object object) {
      Validate.notNull(object, "Could not read icon");
      if (object instanceof ConfigurationSection) {
         ConfigurationSection config = (ConfigurationSection)object;
         Material material = Material.valueOf(UtilityMethods.enumName(((ConfigurationSection)object).getString("item")));
         int modelData = config.getInt("model-data", config.getInt("custom-model-data"));
         return new Icon(material, modelData);
      } else if (object instanceof String) {
         String[] split = ((String)object).split("[:.,]");
         Material mat = Material.valueOf(UtilityMethods.enumName(split[0]));
         int modelData = split.length == 1 ? 0 : Integer.parseInt(split[1]);
         return new Icon(mat, modelData);
      } else {
         throw new RuntimeException("Needs either a string or configuration section");
      }
   }
}
