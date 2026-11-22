package com.Zrips.CMI.Modules.Worth;

import com.Zrips.CMI.CMI;
import java.util.HashSet;
import java.util.Set;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Recipes.CMIRecipe;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class WorthItem {
  private Material material;
  private Integer data = null;
  private Double sellPrice = 0.0;
  private Double buyPrice = null;
  private ItemStack item;
  private Recipe recipe = null;
  private Recipe exploitRecipe = null;
  private Set<Recipe> recipesFor;
  private Set<Recipe> recipesFrom;
  private Integer modelData;

  @Deprecated
  public WorthItem(ItemStack itemStack, Integer integer, double number) {
    this.item = itemStack;
    this.material = itemStack.getType();
    this.data = integer;
    this.sellPrice = number;
  }

  public WorthItem(ItemStack itemStack, double number) {
    this.item = itemStack;
    this.material = itemStack.getType();
    this.sellPrice = number;
  }

  public WorthItem(ItemStack itemStack) {
    this(itemStack, 0.0);
  }

  public Integer getData() {
    return this.data;
  }

  public void setData(Integer integer) {
    this.data = integer;
  }

  public Double getSellPrice() {
    return this.sellPrice;
  }

  public Double getPlayerSellPrice(ItemStack itemStack, boolean flag, boolean useDurability) {
    double number = this.sellPrice;
    if (useDurability) {
      number = CMI.getInstance().getWorthManager().getWorthByDurability(itemStack, number);
    }

    if (flag) {
      number += getEnchantSellPrice(itemStack);
    }

    return number;
  }

  public static double getEnchantSellPrice(ItemStack itemStack) {
    if (itemStack == null) {
      return 0.0;
    }

    double number = 0.0;

    for (var entry : itemStack.getEnchantments().entrySet()) {
      if (entry != null) {
        number +=
            CMI.getInstance()
                .getWorthManager()
                .getEnchantSellPrice((Enchantment) entry.getKey(), (Integer) entry.getValue());
      }
    }

    return number;
  }

  public void setSellPrice(double number) {
    this.sellPrice = Math.round(number * 100.0) / 100.0;
    if (this.sellPrice < 0.0) {
      this.sellPrice = 0.0;
    }
  }

  public ItemStack getItem() {
    return this.item;
  }

  public void setItem(ItemStack itemStack) {
    if (this.item == null) {
      this.item = itemStack.clone();
    }
  }

  public Double getBuyPrice() {
    return this.buyPrice == null ? this.sellPrice : this.buyPrice;
  }

  public boolean isBuyPriceSet() {
    return this.buyPrice != null;
  }

  public void setBuyPrice(Double price) {
    this.buyPrice = price == null ? null : Math.round(price * 100.0) / 100.0;
    if (this.buyPrice != null && this.buyPrice < 0.0) {
      this.buyPrice = null;
    }
  }

  public Recipe getExploitRecipe() {
    return this.exploitRecipe;
  }

  public Recipe updateExploitRecipe() {
    if (this.recipesFor == null || this.recipesFrom == null) {
      this.updateRecipes();
    }

    label62:
    for (Recipe recipeFor : this.recipesFor) {
      var list = CMIRecipe.getIngredientsList(recipeFor);
      double number = 0.0;

      for (ItemStack itemStack : list) {
        WorthItem worthItem = CMI.getInstance().getWorthManager().getWorth(itemStack, true);
        if (worthItem == null || !worthItem.isBuyPriceSet()) {
          continue label62;
        }

        number += itemStack.getAmount() * worthItem.getBuyPrice();
      }

      if (number > 0.0 && this.getSellPrice() * recipeFor.getResult().getAmount() > number) {
        this.exploitRecipe = recipeFor;
        return recipeFor;
      }
    }

    label47:
    for (Recipe recipeFrom : this.recipesFrom) {
      var fromIngredients = CMIRecipe.getIngredientsList(recipeFrom);
      double ingredientCost = 0.0;

      for (ItemStack ingredient : fromIngredients) {
        WorthItem ingredientWorth = CMI.getInstance().getWorthManager().getWorth(ingredient, true);
        if (ingredientWorth == null || !ingredientWorth.isBuyPriceSet()) {
          continue label47;
        }

        ingredientCost += ingredient.getAmount() * ingredientWorth.getBuyPrice();
      }

      WorthItem resultWorth =
          CMI.getInstance().getWorthManager().getWorth(recipeFrom.getResult(), true);
      if (resultWorth != null
          && ingredientCost > 0.0
          && resultWorth.getSellPrice() * recipeFrom.getResult().getAmount() > ingredientCost) {
        this.exploitRecipe = recipeFrom;
        return recipeFrom;
      }
    }

    this.exploitRecipe = null;
    return null;
  }

  public void resetRecipes() {
    this.exploitRecipe = null;
    this.recipe = null;
    this.recipesFor = null;
    this.recipesFrom = null;
  }

  public void setExploitRecipe(Recipe newRecipe) {
    this.exploitRecipe = newRecipe;
  }

  public void updateRecipes() {
    this.recipesFor = new HashSet<>();
    this.recipesFrom = new HashSet<>();

    for (Recipe recipeFor :
        CMILib.getInstance().getItemManager().getItem(this.getItem()).getRecipesFor()) {
      if (this.recipe == null) {
        this.recipe = recipeFor;
      }

      var list = CMIRecipe.getIngredientsList(recipeFor);
      boolean flag = false;

      for (ItemStack itemStack : list) {
        WorthItem worthItem = CMI.getInstance().getWorthManager().getWorth(itemStack, true);
        if (worthItem == null || !worthItem.isBuyPriceSet()) {
          flag = true;
          break;
        }
      }

      if (!flag) {
        this.recipesFor.add(recipeFor);
      }
    }

    for (Recipe recipeFrom :
        CMILib.getInstance().getItemManager().getItem(this.getItem()).getRecipesFrom()) {
      var fromIngredients = CMIRecipe.getIngredientsList(recipeFrom);
      boolean missingWorth = false;

      for (ItemStack ingredient : fromIngredients) {
        WorthItem ingredientWorth = CMI.getInstance().getWorthManager().getWorth(ingredient, true);
        if (ingredientWorth == null || !ingredientWorth.isBuyPriceSet()) {
          missingWorth = true;
          break;
        }
      }

      if (!missingWorth) {
        this.recipesFrom.add(recipeFrom);
      }
    }
  }

  public Recipe getRecipe() {
    return this.recipe;
  }

  public void setRecipe(Recipe newRecipe) {
    this.recipe = newRecipe;
  }

  public void setModelData(Integer integer) {
    this.modelData = integer;
  }

  public Integer getModelData() {
    return this.modelData;
  }
}
