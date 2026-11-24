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

   public Recipe(ConfigurationSection var1) {
      this(var1.getName());
      if (var1.contains("options")) {
         for (Recipe.RecipeOption var5 : Recipe.RecipeOption.values()) {
            if (var1.getConfigurationSection("options").contains(var5.getConfigPath())) {
               this.options.put(var5, var1.getBoolean("options." + var5.getConfigPath()));
            }
         }
      }

      for (String var10 : var1.getStringList("ingredients")) {
         try {
            Ingredient var12 = MMOItems.plugin.getCrafting().getIngredient(new MMOLineConfig(var10));
            this.ingredients.add(var12);
         } catch (IllegalArgumentException var7) {
            throw new IllegalArgumentException("Could not load ingredient '" + var10 + "': " + var7.getMessage());
         }
      }

      for (String var11 : var1.getStringList("conditions")) {
         try {
            Condition var13 = MMOItems.plugin.getCrafting().getCondition(new MMOLineConfig(var11));
            this.conditions.add(var13);
         } catch (IllegalArgumentException var6) {
            throw new IllegalArgumentException("Could not load condition '" + var11 + "': " + var6.getMessage());
         }
      }

      if (this.conditions.isEmpty() && this.ingredients.isEmpty()) {
         throw new IllegalArgumentException("No conditions or ingredients set.");
      }

      this.loadTriggerList(var1.getStringList("on-use"), "on-use", this.onUse);
      this.loadTriggerList(var1.getStringList("triggers"), "on-claim", this.onClaim);
      this.loadTriggerList(var1.getStringList("on-cancel"), "on-cancel", this.onCancel);
   }

   private void loadTriggerList(List<String> var1, String var2, Set<Trigger> var3) {
      for (String var5 : var1) {
         try {
            Trigger var6 = MMOItems.plugin.getCrafting().getTrigger(new MMOLineConfig(var5));
            Validate.notNull(var6, "Could not match trigger");
            var3.add(var6);
         } catch (IllegalArgumentException var7) {
            throw new IllegalArgumentException("Could not load " + var2 + " trigger '" + var5 + "': " + var7.getMessage());
         }
      }
   }

   private Recipe(String var1) {
      Validate.notNull(var1, "Recipe ID must not be null");
      this.id = var1;
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

   public Condition getCondition(String var1) {
      for (Condition var3 : this.conditions) {
         if (var3.getId().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public boolean hasOption(Recipe.RecipeOption var1) {
      return this.options.getOrDefault(var1, var1.getDefault());
   }

   public void addIngredient(Ingredient var1) {
      this.ingredients.add(var1);
   }

   public void registerCondition(Condition var1) {
      this.conditions.add(var1);
   }

   public void setOption(Recipe.RecipeOption var1, boolean var2) {
      this.options.put(var1, var2);
   }

   public abstract CheckedRecipe evaluateRecipe(PlayerData var1, IngredientInventory var2);

   public abstract boolean whenUsed(PlayerData var1, IngredientInventory var2, CheckedRecipe var3, CraftingStation var4);

   public abstract boolean canUse(PlayerData var1, IngredientInventory var2, CheckedRecipe var3, CraftingStation var4);

   public abstract ItemStack display(CheckedRecipe var1);

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof Recipe && ((Recipe)var1).id.equals(this.id);
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
