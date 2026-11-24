package net.Indyuce.mmoitems.api.crafting.recipe;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.CraftingStation;
import net.Indyuce.mmoitems.api.crafting.condition.Condition;
import net.Indyuce.mmoitems.api.crafting.ingredient.Ingredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.IngredientInventory;
import net.Indyuce.mmoitems.api.crafting.trigger.Trigger;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public abstract class Recipe {
   private final String id;
   private final Map<Recipe.RecipeOption, Boolean> options = new HashMap<>();
   private final Set<Ingredient> ingredients = new LinkedHashSet<>();
   private final Set<Condition> conditions = new LinkedHashSet<>();
   private final Set<Trigger> onUse = new LinkedHashSet<>();
   private final Set<Trigger> onClaim = new LinkedHashSet<>();
   private final Set<Trigger> onCancel = new LinkedHashSet<>();

   public Recipe(ConfigurationSection section) {
      this(section.getName());
      if (section.contains("options")) {
         for (Recipe.RecipeOption recipeOption : Recipe.RecipeOption.values()) {
            if (section.getConfigurationSection("options").contains(recipeOption.getConfigPath())) {
               this.options.put(recipeOption, section.getBoolean("options." + recipeOption.getConfigPath()));
            }
         }
      }

      for (String text : section.getStringList("ingredients")) {
         try {
            Ingredient ingredient = MMOItems.plugin.getCrafting().getIngredient(new MMOLineConfig(text));
            this.ingredients.add(ingredient);
         } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Could not load ingredient '" + text + "': " + exception.getMessage());
         }
      }

      for (String text : section.getStringList("conditions")) {
         try {
            Condition condition = MMOItems.plugin.getCrafting().getCondition(new MMOLineConfig(text));
            this.conditions.add(condition);
         } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Could not load condition '" + text + "': " + exception.getMessage());
         }
      }

      if (this.conditions.isEmpty() && this.ingredients.isEmpty()) {
         throw new IllegalArgumentException("No conditions or ingredients set.");
      }

      this.loadTriggerList(section.getStringList("on-use"), "on-use", this.onUse);
      this.loadTriggerList(section.getStringList("triggers"), "on-claim", this.onClaim);
      this.loadTriggerList(section.getStringList("on-cancel"), "on-cancel", this.onCancel);
   }

   private void loadTriggerList(List<String> texts, String text, Set<Trigger> triggers) {
      for (String line : texts) {
         try {
            Trigger trigger = MMOItems.plugin.getCrafting().getTrigger(new MMOLineConfig(line));
            Validate.notNull(trigger, "Could not match trigger");
            triggers.add(trigger);
         } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Could not load " + text + " trigger '" + line + "': " + exception.getMessage());
         }
      }
   }

   private Recipe(String text) {
      Validate.notNull(text, "Recipe ID must not be null");
      this.id = text;
   }

   public String getId() {
      return this.id;
   }

   public Set<Ingredient> getIngredients() {
      return this.ingredients;
   }

   public Set<Condition> getConditions() {
      return this.conditions;
   }

   public Set<Trigger> whenUsed() {
      return this.onUse;
   }

   public Set<Trigger> whenClaimed() {
      return this.onClaim;
   }

   public Set<Trigger> whenCanceled() {
      return this.onCancel;
   }

   public Condition getCondition(String text) {
      for (Condition condition : this.conditions) {
         if (condition.getId().equals(text)) {
            return condition;
         }
      }

      return null;
   }

   public boolean hasOption(Recipe.RecipeOption recipeOption) {
      return this.options.getOrDefault(recipeOption, recipeOption.getDefault());
   }

   public void addIngredient(Ingredient ingredient) {
      this.ingredients.add(ingredient);
   }

   public void registerCondition(Condition condition) {
      this.conditions.add(condition);
   }

   public void setOption(Recipe.RecipeOption recipeOption, boolean flag) {
      this.options.put(recipeOption, flag);
   }

   public abstract CheckedRecipe evaluateRecipe(PlayerData playerData, IngredientInventory ingredientInventory);

   public abstract boolean whenUsed(PlayerData playerData, IngredientInventory ingredientInventory, CheckedRecipe checkedRecipe, CraftingStation craftingStation);

   public abstract boolean canUse(PlayerData playerData, IngredientInventory ingredientInventory, CheckedRecipe checkedRecipe, CraftingStation craftingStation);

   public abstract ItemStack display(CheckedRecipe checkedRecipe);

   @Override
   public boolean equals(Object obj) {
      return obj instanceof Recipe && ((Recipe)obj).id.equals(this.id);
   }

   public enum RecipeOption {
      HIDE_WHEN_LOCKED(false),
      HIDE_WHEN_NO_INGREDIENTS(false),
      OUTPUT_ITEM(true),
      SILENT_CRAFT(false);

      private final boolean def;

      RecipeOption(boolean nullxx) {
         this.def = nullxx;
      }

      public boolean getDefault() {
         return this.def;
      }

      public String getConfigPath() {
         return this.name().toLowerCase().replace("_", "-");
      }
   }
}
