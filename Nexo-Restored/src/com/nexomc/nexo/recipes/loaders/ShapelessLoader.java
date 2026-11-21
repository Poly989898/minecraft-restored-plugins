package com.nexomc.nexo.recipes.loaders;

import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.Collection;
import java.util.Map;
import kotlin.Metadata;
import kotlin.ranges.RangesKt;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/recipes/loaders/ShapelessLoader;", "Lcom/nexomc/nexo/recipes/loaders/RecipeLoader;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "registerRecipe", "", "com.nexomc:core"}
)
public final class ShapelessLoader extends RecipeLoader {
   public ShapelessLoader(@NotNull ConfigurationSection section) {
      super(section);
   }

   public void registerRecipe() {
      ShapelessRecipe recipe = new ShapelessRecipe(this.getKey(), this.getResult());
      recipe.setGroup(NexoYamlKt.stringOrEmpty(this.getSection(), "group"));
      ConfigurationSection section = this.getSection();
      String key = "category";
      CraftingBookCategory craftingBookCategory = (CraftingBookCategory)EnumUtils.getEnum(CraftingBookCategory.class, NexoYamlKt.stringOrNull(section, key));
      if (craftingBookCategory == null) {
         craftingBookCategory = recipe.getCategory();
      }

      Collection collection;
      block: {
         recipe.setCategory(craftingBookCategory);
         ConfigurationSection configurationSection = NexoYamlKt.section(this.getSection(), "ingredients");
         if (configurationSection != null) {
            Map map = NexoYamlKt.childSections(configurationSection);
            if (map != null) {
               collection = map.values();
               if (collection != null) {
                  break block;
               }
            }
         }

         Collection collection2 = (Collection)NexoYamlKt.sectionListOrSingle(this.getSection(), "ingredients");
         if (collection2.isEmpty()) {
            int value = 0;
            return;
         }

         collection = collection2;
      }

      Collection ingredients = collection;
      Iterable iterable = (Iterable)ingredients;

      for(Object element : iterable) {
         ConfigurationSection itemSection = (ConfigurationSection)element;
         int value2 = 0;
         RecipeChoice recipeChoice = this.recipeChoice(itemSection);
         if (recipeChoice != null) {
            RecipeChoice choice = recipeChoice;
            int value3 = RangesKt.coerceIn(itemSection.getInt("amount", 1), 1, 9);

            for(int i = 0; i < value3; ++i) {
               int value4 = 0;
               recipe.addIngredient(choice);
            }
         }
      }

      this.addToWhitelist((Recipe)recipe);
      this.loadRecipe((Recipe)recipe);
   }
}
