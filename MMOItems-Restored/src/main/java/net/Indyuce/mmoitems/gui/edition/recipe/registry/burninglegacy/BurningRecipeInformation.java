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

   public BurningRecipeInformation(@NotNull ConfigurationSection var1) {
      String var2 = var1.getString("item");
      if (var2 == null) {
         throw new IllegalArgumentException("Invalid input ingredient");
      }

      this.choice = RecipeManager.getWorkbenchIngredient(var2);
      this.exp = (float)var1.getDouble("exp", 0.35);
      this.burnTime = var1.getInt("time", 200);
   }

   public BurningRecipeInformation(@NotNull WorkbenchIngredient var1, float var2, int var3) {
      this.choice = var1;
      this.exp = var2;
      this.burnTime = var3;
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
