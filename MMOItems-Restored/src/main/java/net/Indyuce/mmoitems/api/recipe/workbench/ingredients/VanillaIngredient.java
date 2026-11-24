package net.Indyuce.mmoitems.api.recipe.workbench.ingredients;

import io.lumine.mythic.lib.api.item.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

@Deprecated
public class VanillaIngredient extends WorkbenchIngredient {
   private final Material material;

   public VanillaIngredient(Material var1, int var2) {
      super(var2);
      this.material = var1;
   }

   @Override
   public boolean corresponds(ItemStack var1) {
      return !NBTItem.get(var1).hasType() && var1.getType() == this.material;
   }

   @Override
   public ItemStack generateItem() {
      return new ItemStack(this.material);
   }

   @Override
   public RecipeChoice toBukkit() {
      return new MaterialChoice(this.material);
   }
}
