package net.Indyuce.mmoitems.api.crafting.recipe;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.Indyuce.mmoitems.api.crafting.condition.CheckedCondition;
import net.Indyuce.mmoitems.api.crafting.condition.Condition;
import net.Indyuce.mmoitems.api.crafting.ingredient.CheckedIngredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.Ingredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.IngredientInventory;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.inventory.ItemStack;

public class CheckedRecipe {
   private final Recipe recipe;
   private final Set<CheckedCondition> conditions = new LinkedHashSet<>();
   private final Set<CheckedIngredient> ingredients = new LinkedHashSet<>();
   private boolean ingredientsHad = true;
   private boolean conditionsMet = true;

   public CheckedRecipe(Recipe var1, PlayerData var2, IngredientInventory var3) {
      this.recipe = var1;

      for (Ingredient var5 : var1.getIngredients()) {
         CheckedIngredient var6 = var5.evaluateIngredient(var3);
         this.ingredients.add(var6);
         if (!var6.isHad()) {
            this.ingredientsHad = false;
         }
      }

      for (Condition var8 : var1.getConditions()) {
         CheckedCondition var9 = var8.evaluateCondition(var2);
         this.conditions.add(var9);
         if (!var9.isMet()) {
            this.conditionsMet = false;
         }
      }
   }

   public Recipe getRecipe() {
      return this.recipe;
   }

   @Deprecated
   public boolean isUnlocked() {
      return this.ingredientsHad && this.conditionsMet;
   }

   public boolean areConditionsMet() {
      return this.conditionsMet;
   }

   public boolean allIngredientsHad() {
      return this.ingredientsHad;
   }

   public ItemStack display() {
      return this.recipe.display(this);
   }

   public Set<CheckedCondition> getConditions() {
      return this.conditions;
   }

   public Set<CheckedCondition> getDisplayableConditions() {
      return this.conditions.stream().filter(var0 -> var0.getCondition().getDisplay() != null).collect(Collectors.toSet());
   }

   public Set<CheckedIngredient> getIngredients() {
      return this.ingredients;
   }
}
