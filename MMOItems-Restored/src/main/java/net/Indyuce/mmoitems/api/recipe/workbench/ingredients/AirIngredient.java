package net.Indyuce.mmoitems.api.recipe.workbench.ingredients;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

@Deprecated
public class AirIngredient extends WorkbenchIngredient {
   public AirIngredient() {
      super(0);
   }

   @Override
   public boolean matches(ItemStack item) {
      return item == null || item.getType() == Material.AIR;
   }

   @Override
   public boolean corresponds(ItemStack item) {
      return true;
   }

   @Override
   public ItemStack generateItem() {
      return new ItemStack(Material.AIR);
   }

   @Override
   public RecipeChoice toBukkit() {
      return new MaterialChoice(Material.AIR);
   }
}
