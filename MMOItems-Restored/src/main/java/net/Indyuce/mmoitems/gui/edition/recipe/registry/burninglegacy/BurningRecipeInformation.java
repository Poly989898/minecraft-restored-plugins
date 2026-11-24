package net.Indyuce.mmoitems.gui.edition.recipe.registry.burninglegacy;

import net.Indyuce.mmoitems.api.recipe.workbench.ingredients.WorkbenchIngredient;
import net.Indyuce.mmoitems.manager.RecipeManager;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class BurningRecipeInformation {
   private final WorkbenchIngredient choice;
   private final float exp;
   private final int burnTime;

   public BurningRecipeInformation(@NotNull ConfigurationSection section) {
      String item = section.getString("item");
      if (item == null) {
         throw new IllegalArgumentException("Invalid input ingredient");
      }

      this.choice = RecipeManager.getWorkbenchIngredient(item);
      this.exp = (float)section.getDouble("exp", 0.35);
      this.burnTime = section.getInt("time", 200);
   }

   public BurningRecipeInformation(@NotNull WorkbenchIngredient workbenchIngredient, float value, int count) {
      this.choice = workbenchIngredient;
      this.exp = value;
      this.burnTime = count;
   }

   public int getBurnTime() {
      return this.burnTime;
   }

   public WorkbenchIngredient getChoice() {
      return this.choice;
   }

   public float getExp() {
      return this.exp;
   }
}
