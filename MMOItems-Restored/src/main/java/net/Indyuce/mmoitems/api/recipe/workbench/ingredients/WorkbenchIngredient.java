package net.Indyuce.mmoitems.api.recipe.workbench.ingredients;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

@Deprecated
public abstract class WorkbenchIngredient {
   private final int amount;

   public WorkbenchIngredient(int var1) {
      this.amount = var1;
   }

   public int getAmount() {
      return this.amount;
   }

   public boolean matches(ItemStack var1) {
      return var1 != null && var1.getAmount() >= this.amount && this.corresponds(var1);
   }

   public abstract RecipeChoice toBukkit();

   public abstract ItemStack generateItem();

   protected abstract boolean corresponds(ItemStack var1);
}
