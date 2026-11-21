package com.nexomc.nexo.recipes.loaders;

import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.text.StringsKt;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/recipes/loaders/ShapedLoader;", "Lcom/nexomc/nexo/recipes/loaders/RecipeLoader;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "registerRecipe", "", "com.nexomc:core"}
)
public final class ShapedLoader extends RecipeLoader {
   public ShapedLoader(@NotNull ConfigurationSection section) {
      super(section);
   }

   public void registerRecipe() {
      ShapedRecipe recipe = new ShapedRecipe(this.getKey(), this.getResult());
      recipe.setGroup(NexoYamlKt.stringOrEmpty(this.getSection(), "group"));
      List stringList = this.getSection().getStringList("shape");
      Collection collection = (Collection)stringList;
      String[] strs = (String[])collection.toArray(new String[0]);
      recipe.shape((String[])Arrays.copyOf(strs, strs.length));
      ConfigurationSection section = this.getSection();
      String key = "category";
      CraftingBookCategory craftingBookCategory = (CraftingBookCategory)EnumUtils.getEnum(CraftingBookCategory.class, NexoYamlKt.stringOrNull(section, key));
      if (craftingBookCategory == null) {
         craftingBookCategory = recipe.getCategory();
      }

      recipe.setCategory(craftingBookCategory);
      ConfigurationSection configurationSection = NexoYamlKt.section(this.getSection(), "ingredients");
      if (configurationSection != null) {
         Map map = NexoYamlKt.childSections(configurationSection);
         if (map != null) {
            Map map2 = map;

            for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
               int value = 0;
               String letter = (String)element.getKey();
               ConfigurationSection itemSection = (ConfigurationSection)element.getValue();
               char ch = StringsKt.first((CharSequence)letter);
               RecipeChoice recipeChoice = this.recipeChoice(itemSection);
               if (recipeChoice != null) {
                  recipe.setIngredient(ch, recipeChoice);
               }
            }
         }
      }

      this.addToWhitelist((Recipe)recipe);
      this.loadRecipe((Recipe)recipe);
   }
}
