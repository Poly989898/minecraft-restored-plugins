package com.nexomc.nexo.recipes.builders;

import com.nexomc.nexo.recipes.RecipeType;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Metadata;
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
   d2 = {"Lcom/nexomc/nexo/recipes/builders/ShapedBuilder;", "Lcom/nexomc/nexo/recipes/builders/WorkbenchBuilder;", "player", "Lorg/bukkit/entity/Player;", "<init>", "(Lorg/bukkit/entity/Player;)V", "saveRecipe", "", "name", "", "permission", "com.nexomc:core"}
)
public final class ShapedBuilder extends WorkbenchBuilder {
   public ShapedBuilder(@NotNull Player player) {
      super(player, RecipeType.SHAPED);
   }

   public void saveRecipe(@NotNull String name, @Nullable String permission) {
      Map letterByItem = (Map)(new LinkedHashMap());
      char letter = 'A';
      String[] shapes = new String[3];
      StringBuilder shape = new StringBuilder();
      int i = 1;

      for(int j = this.getInventory().getSize(); i < j; ++i) {
         ItemStack item = this.getInventory().getItem(i);
         if (item == null) {
            shape.append("_");
         } else if (letterByItem.containsKey(item)) {
            shape.append(letterByItem.get(item));
         } else {
            shape.append(letter);
            letterByItem.put(this.getInventory().getItem(i), letter);
            char ch = letter++;
         }

         if (shape.length() == 3) {
            shapes[(i + 1) / 3 - 1] = shape.toString();
            shape = new StringBuilder();
         }
      }

      ConfigurationSection newCraftSection = null;
      ConfigurationSection resultSection = null;
      ConfigurationSection ingredients = null;
      if (this.getConfig().isConfigurationSection(name)) {
         newCraftSection = NexoYamlKt.section((ConfigurationSection)this.getConfig(), name);
         resultSection = NexoYamlKt.section(newCraftSection, "result");
         ingredients = NexoYamlKt.section(newCraftSection, "ingredients");
      } else {
         newCraftSection = this.getConfig().createSection(name);
         resultSection = newCraftSection.createSection("result");
         ingredients = newCraftSection.createSection("ingredients");
      }

      newCraftSection.set("shape", shapes);
      ItemStack itemStack = this.getInventory().getItem(0);
      this.setItemStack(resultSection, itemStack);

      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)letterByItem.entrySet()) {
         ItemStack key = (ItemStack)entry.getKey();
         char value = (Character)entry.getValue();
         ConfigurationSection configurationSection = ingredients.createSection(String.valueOf(value));
         ConfigurationSection ingredientSection = configurationSection;
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
