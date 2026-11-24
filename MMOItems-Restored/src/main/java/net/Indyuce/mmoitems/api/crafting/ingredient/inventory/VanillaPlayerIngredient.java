package net.Indyuce.mmoitems.api.crafting.ingredient.inventory;

import io.lumine.mythic.lib.api.item.NBTItem;
import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class VanillaPlayerIngredient extends PlayerIngredient {
   @NotNull
   final ItemStack sourceItem;
   private final Material material;
   @Nullable
   private final String displayName;

   @NotNull
   public ItemStack getSourceItem() {
      return this.sourceItem;
   }

   public VanillaPlayerIngredient(NBTItem nbtItem) {
      super(nbtItem);
      this.sourceItem = nbtItem.toItem();
      this.material = nbtItem.getItem().getType();
      ItemMeta itemMeta = nbtItem.getItem().getItemMeta();
      if (itemMeta != null) {
         this.displayName = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : null;
      } else {
         this.displayName = null;
      }
   }

   public Material getType() {
      return this.material;
   }

   @Nullable
   public String getDisplayName() {
      return this.displayName;
   }
}
