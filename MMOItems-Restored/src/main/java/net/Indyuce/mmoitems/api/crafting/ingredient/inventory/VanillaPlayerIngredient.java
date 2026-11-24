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

   public VanillaPlayerIngredient(NBTItem var1) {
      super(var1);
      this.sourceItem = var1.toItem();
      this.material = var1.getItem().getType();
      ItemMeta var2 = var1.getItem().getItemMeta();
      if (var2 != null) {
         this.displayName = var2.hasDisplayName() ? var2.getDisplayName() : null;
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
