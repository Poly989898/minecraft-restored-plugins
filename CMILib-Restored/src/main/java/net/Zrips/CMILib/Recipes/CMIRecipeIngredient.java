package net.Zrips.CMILib.Recipes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

public class CMIRecipeIngredient {
  private CMIRecipeChoice choice = CMIRecipeChoice.byMaterial;
  private ItemStack item = null;

  public CMIRecipeIngredient(ItemStack item, CMIRecipeChoice choice) {
    this.item = item;
    this.choice = choice;
  }

  public CMIRecipeIngredient(ItemStack item) {
    this.item = item;
  }

  public CMIRecipeChoice getChoice() {
    return this.choice;
  }

  public CMIRecipeIngredient setChoice(CMIRecipeChoice choice) {
    this.choice = choice;
    return this;
  }

  public ItemStack getItem() {
    return this.item;
  }

  public CMIRecipeIngredient setItem(ItemStack item) {
    this.item = item;
    return this;
  }

  public Object generateChoice() {
    return this.choice.equals(CMIRecipeChoice.byMaterial)
        ? new MaterialChoice(this.item.getType())
        : new ExactChoice(this.item);
  }
}
