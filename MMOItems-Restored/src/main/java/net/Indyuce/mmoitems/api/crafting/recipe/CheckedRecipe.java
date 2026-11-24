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

   public CheckedRecipe(Recipe recipe, PlayerData playerData, IngredientInventory ingredientInventory) {
      this.recipe = recipe;

      for (Ingredient ingredient : recipe.getIngredients()) {
         CheckedIngredient checkedIngredient = ingredient.evaluateIngredient(ingredientInventory);
         this.ingredients.add(checkedIngredient);
         if (!checkedIngredient.isHad()) {
            this.ingredientsHad = false;
         }
      }

      for (Condition condition : recipe.getConditions()) {
         CheckedCondition checkedCondition = condition.evaluateCondition(playerData);
         this.conditions.add(checkedCondition);
         if (!checkedCondition.isMet()) {
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
      return this.conditions.stream().filter(checkedCondition -> checkedCondition.getCondition().getDisplay() != null).collect(Collectors.toSet());
   }

   public Set<CheckedIngredient> getIngredients() {
      return this.ingredients;
   }
}
