package com.nexomc.nexo.recipes.builders;

import com.nexomc.nexo.recipes.RecipeType;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001a\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\tH\u0016¨\u0006\u000b"},
   d2 = {"Lcom/nexomc/nexo/recipes/builders/ShapelessBuilder;", "Lcom/nexomc/nexo/recipes/builders/WorkbenchBuilder;", "player", "Lorg/bukkit/entity/Player;", "<init>", "(Lorg/bukkit/entity/Player;)V", "saveRecipe", "", "name", "", "permission", "com.nexomc:core"}
)
public final class ShapelessBuilder extends WorkbenchBuilder {
   public ShapelessBuilder(@NotNull Player player) {
      super(player, RecipeType.SHAPELESS);
   }

   public void saveRecipe(@NotNull String name, @Nullable String permission) {
      Map items = (Map)(new LinkedHashMap());
      ItemStack[] contents = this.getInventory().getContents();
      ItemStack[] content = contents;
      int i = 1;

      for(int j = content.length; i < j; ++i) {
         if (content[i] != null) {
            items.put(content[i], ((Number)items.getOrDefault(content[i], 0)).intValue() + 1);
         }
      }

      ConfigurationSection configurationSection = this.getConfig().createSection(name);
      ConfigurationSection newCraftSection = configurationSection;
      ConfigurationSection configurationSection2 = newCraftSection.createSection("result");
      ItemStack itemStack = content[0];
      Intrinsics.checkNotNull(content[0]);
      this.setItemStack(configurationSection2, itemStack);
      configurationSection = newCraftSection.createSection("ingredients");
      ConfigurationSection ingredients = configurationSection;
      int i_1 = 0;

      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)items.entrySet()) {
         ItemStack key = (ItemStack)entry.getKey();
         int value = ((Number)entry.getValue()).intValue();
         ++i_1;
         configurationSection = ingredients.createSection(String.valueOf((char)(64 + i_1)));
         ConfigurationSection ingredientSection = configurationSection;
         ingredientSection.set("amount", value);
         this.setItemStack(ingredientSection, key);
      }

      CharSequence charSequence = (CharSequence)permission;
      if (charSequence != null && charSequence.length() != 0) {
         newCraftSection.set("permission", permission);
      }

      this.saveConfig();
      this.close();
   }
}
