package net.Zrips.CMILib.Recipes;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

public enum CMIRecipeChoice {
  byMaterial,
  byItemStack;

  public static List<ItemStack> getChoices(RecipeChoice inputChoice) {
    List<ItemStack> items = new ArrayList<>();
    if (inputChoice instanceof ExactChoice) {
      items = ((ExactChoice) inputChoice).getChoices();
    } else if (inputChoice instanceof MaterialChoice) {
      for (Material material : ((MaterialChoice) inputChoice).getChoices()) {
        if (!material.equals(Material.AIR)) {
          items.add(new ItemStack(material));
        }
      }
    }

    return items;
  }
}
