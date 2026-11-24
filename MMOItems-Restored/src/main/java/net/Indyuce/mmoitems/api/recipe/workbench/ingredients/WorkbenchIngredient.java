package net.Indyuce.mmoitems.api.recipe.workbench.ingredients;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

@Deprecated
public abstract class WorkbenchIngredient {
   private final int amount;

   public WorkbenchIngredient(int count) {
      this.amount = count;
   }

   public int getAmount() {
      return this.amount;
   }

   public boolean matches(ItemStack item) {
      return item != null && item.getAmount() >= this.amount && this.corresponds(item);
   }

   public abstract RecipeChoice toBukkit();

   public abstract ItemStack generateItem();

   protected abstract boolean corresponds(ItemStack item);
}
