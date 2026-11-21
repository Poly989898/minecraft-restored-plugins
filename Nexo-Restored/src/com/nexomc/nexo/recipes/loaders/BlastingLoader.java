package com.nexomc.nexo.recipes.loaders;

import com.nexomc.nexo.utils.NexoYamlKt;
import kotlin.Metadata;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/recipes/loaders/BlastingLoader;", "Lcom/nexomc/nexo/recipes/loaders/RecipeLoader;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "registerRecipe", "", "com.nexomc:core"}
)
public final class BlastingLoader extends RecipeLoader {
   public BlastingLoader(@NotNull ConfigurationSection section) {
      super(section);
   }

   public void registerRecipe() {
      ConfigurationSection configurationSection = NexoYamlKt.section(this.getSection(), "input");
      if (configurationSection != null) {
         ConfigurationSection inputSection = configurationSection;
         RecipeChoice recipeChoice2 = this.recipeChoice(inputSection);
         if (recipeChoice2 != null) {
            RecipeChoice recipeChoice = recipeChoice2;
            float exp = (float)this.getSection().getInt("experience");
            int cookingTime = this.getSection().getInt("cookingTime");
            BlastingRecipe recipe = new BlastingRecipe(this.getKey(), this.getResult(), recipeChoice, exp, cookingTime);
            recipe.setGroup(NexoYamlKt.stringOrEmpty(this.getSection(), "group"));
            ConfigurationSection section = this.getSection();
            String key = "category";
            CookingBookCategory cookingBookCategory = (CookingBookCategory)EnumUtils.getEnum(CookingBookCategory.class, NexoYamlKt.stringOrNull(section, key));
            if (cookingBookCategory == null) {
               cookingBookCategory = recipe.getCategory();
            }

            recipe.setCategory(cookingBookCategory);
            this.addToWhitelist((Recipe)recipe);
            this.loadRecipe((Recipe)recipe);
         }
      }
   }
}
