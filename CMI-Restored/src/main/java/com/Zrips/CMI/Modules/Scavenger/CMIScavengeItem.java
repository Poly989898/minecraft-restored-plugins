package com.Zrips.CMI.Modules.Scavenger;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Worth.WorthEnchantment;
import com.Zrips.CMI.Modules.Worth.WorthItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Recipes.CMIRecipe;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class CMIScavengeItem {
  private ItemStack item;
  private Player player;
  private Map<Enchantment, Integer> enchants = new HashMap<>();
  int maxDurability = 1;
  int durability = 1;
  private int recipeResultAmount = 1;
  Recipe recipe = null;

  CMIScavengeItem(ItemStack itemStack) {
    this(itemStack, null);
  }

  CMIScavengeItem(ItemStack itemStack, Player playerSecond) {
    this.item = itemStack;
    this.player = playerSecond;
    if (itemStack != null) {
      this.maxDurability = itemStack.getType().getMaxDurability();
      this.durability = this.maxDurability - itemStack.getDurability();
      this.maxDurability = this.maxDurability < 1 ? 1 : this.maxDurability;
      this.durability = this.durability < 1 ? 1 : this.durability;
      this.enchants = itemStack.getEnchantments();
    }
  }

  public boolean canScavenge() {
    if (this.getItem() == null
        || CMIMaterial.isAir(this.getItem().getType())
        || this.isBlackListedItem()) {
      return false;
    } else if (this.getItem() == null || !this.getIngredients().isEmpty()) {
      return this.recipeResultAmount <= this.item.getAmount();
    } else {
      return ScavengeManager.WithoutIngredients ? !this.enchants.isEmpty() : false;
    }
  }

  public boolean isBlackListedItem() {
    if (ScavengeManager.InvertBlackList) {
      if (!ScavengeManager.blackList.contains(CMIMaterial.get(this.getItem()))) {
        return true;
      }
    } else if (ScavengeManager.blackList.contains(CMIMaterial.get(this.getItem()))) {
      return true;
    }

    return false;
  }

  public ItemStack getItem() {
    return this.item;
  }

  public Player getPlayer() {
    return this.player;
  }

  public void setPlayer(Player playerSecond) {
    this.player = playerSecond;
  }

  public Map<Enchantment, Integer> getEnchants() {
    return this.enchants;
  }

  public double getEnchantExtractionFailChance(Enchantment enchantment) {
    Integer integer = this.enchants.get(enchantment);
    if (integer == null) {
      integer = 1;
    }

    Double amount = ScavengeManager.baseEnchantFailPercentage;
    int value = enchantment.getMaxLevel();
    amount = amount + integer.intValue() * ScavengeManager.levelEnchantFailPercentage / value;
    amount =
        amount > ScavengeManager.levelEnchantFailMaxChance
            ? ScavengeManager.levelEnchantFailMaxChance
            : amount;
    return this.format(amount);
  }

  private double format(double number) {
    return (int) (number * 100.0) / 100.0;
  }

  public double getExtractionCost() {
    if (this.item != null && !CMIMaterial.isAir(this.item.getType())) {
      Double amount = ScavengeManager.EnchantBaseCost;

      for (var entry : this.enchants.entrySet()) {
        WorthEnchantment worthEnchantment =
            CMI.getInstance()
                .getWorthManager()
                .getEnchantWorth((Enchantment) entry.getKey(), (Integer) entry.getValue());
        if (worthEnchantment != null && worthEnchantment.getSellPrice() > 0.0) {
          amount =
              amount
                  + worthEnchantment.getSellPrice()
                      * (ScavengeManager.EnchantBaseCostExtra / 100.0);
        }
      }

      WorthItem worthItem = CMI.getInstance().getWorthManager().getWorth(this.item);
      if (worthItem != null && worthItem.getSellPrice() > 0.0) {
        amount = amount + worthItem.getSellPrice() * (ScavengeManager.EnchantBaseCostExtra / 100.0);
      }

      return this.format(amount);
    } else {
      return 0.0;
    }
  }

  public double getIngredientReturnChance() {
    double number = ScavengeManager.IngredientReturnBase;
    if (this.getIngredients().isEmpty()) {
      number = 0.0;
    }

    return this.format(number);
  }

  public double getEnchantLevelLowerChance(Enchantment enchantment) {
    Integer integer = this.enchants.get(enchantment);
    if (integer == null) {
      integer = 1;
    }

    Double amount = ScavengeManager.LowerLevelChanceBase;
    int value = enchantment.getMaxLevel();
    amount = amount + integer.intValue() * ScavengeManager.LowerLevelForEachLevel / value;
    amount =
        amount > ScavengeManager.LowerLevelMaxChance ? ScavengeManager.LowerLevelMaxChance : amount;
    return this.format(amount);
  }

  public double getItemBreakChance() {
    if (this.enchants.isEmpty()) {
      return 100.0;
    }

    double number = ScavengeManager.baseBreakPercentage;
    if (!this.enchants.isEmpty()) {
      number += this.enchants.size() * ScavengeManager.eachBreakPercentage;

      for (var entry : this.enchants.entrySet()) {
        int value = (Integer) entry.getValue();
        int count = ((Enchantment) entry.getKey()).getMaxLevel();
        number += value * ScavengeManager.eachBreakPercentageLevel / count;
      }
    }

    number += this.getBreakChanceByItemDurability();
    if (number > 0.0) {
      number =
          number > ScavengeManager.itemBreakMaxBreakChance
              ? ScavengeManager.itemBreakMaxBreakChance
              : number;
    }

    return this.format(number);
  }

  public double getBreakChanceByItemDurability() {
    return ScavengeManager.ItemBreakDurabilityChange > 0.0 && this.maxDurability != this.durability
        ? this.format(
            ScavengeManager.ItemBreakDurabilityChange
                - Math.ceil(
                    this.getLeftDurabilityPercentage()
                        * ScavengeManager.ItemBreakDurabilityChange
                        / 100.0))
        : 0.0;
  }

  public List<ItemStack> getIngredients() {
    return this.getIngredients(ScavengeManager.ItemMaterialDurabilityCheck, false);
  }

  public List<ItemStack> getIngredients(boolean flag) {
    return this.getIngredients(flag, false);
  }

  public List<ItemStack> getIngredients(boolean flag, boolean flagSecond) {
    CMIItemStack cmiItemStack = new CMIItemStack(this.item);
    var list = cmiItemStack.getRecipesFor();
    if (list != null && !list.isEmpty()) {
      Collections.shuffle(list);
      ItemMeta itemMeta = this.item.hasItemMeta() ? this.item.getItemMeta() : null;
      if (this.recipe == null) {
        Recipe recipeValue = (Recipe) list.get(0);
        Recipe recipeSecond = null;
        Recipe recipeThird = null;
        Recipe recipeFourth = null;

        while (!list.isEmpty()) {
          recipeValue = (Recipe) list.remove(0);
          if (recipeThird == null
              || recipeThird.getResult().getAmount() > recipeValue.getResult().getAmount()) {
            if (!CMIMaterial.isTool(recipeValue.getResult().getType())) {
              var varSecond = CMIRecipe.getIngredientsList(recipeValue);
              if (varSecond.isEmpty()
                  || CMIMaterial.isTool(((ItemStack) varSecond.get(0)).getType())) {
                continue;
              }
            }

            recipeThird = recipeValue;
          }

          if (itemMeta != null && recipeValue.getResult().hasItemMeta()) {
            ItemMeta itemMetaSecond = recipeValue.getResult().getItemMeta();
            if ((itemMetaSecond.hasDisplayName()
                        && itemMeta.hasDisplayName()
                        && itemMetaSecond.getDisplayName().equals(itemMeta.getDisplayName())
                    || !itemMetaSecond.hasDisplayName() && !itemMeta.hasDisplayName())
                && (itemMetaSecond.hasLore()
                        && itemMeta.hasLore()
                        && itemMetaSecond.getLore().equals(itemMeta.getLore())
                    || !itemMetaSecond.hasLore() && !itemMeta.hasLore())) {
              recipeFourth = recipeValue;
            }
          }

          if ((itemMeta == null && recipeValue.getResult().hasItemMeta()
                  || !recipeValue.getResult().hasItemMeta())
              && recipeValue.getResult().getAmount() == 1
              && !CMIRecipe.getIngredientsList(recipeValue).isEmpty()) {
            if (!CMIMaterial.isTool(recipeValue.getResult().getType())) {
              var varThird = CMIRecipe.getIngredientsList(recipeValue);
              if (varThird.isEmpty()
                  || CMIMaterial.isTool(((ItemStack) varThird.get(0)).getType())) {
                continue;
              }
            }

            if (!recipeValue.getResult().hasItemMeta()) {
              recipeSecond = recipeValue;
            } else {
              ItemMeta itemMetaThird = recipeValue.getResult().getItemMeta();
              if (!itemMetaThird.hasDisplayName() && !itemMetaThird.hasLore()) {
                recipeSecond = recipeValue;
              }
            }
          }

          if (recipeValue.getResult().getAmount() == 1
              && !CMIRecipe.getIngredientsList(recipeValue).isEmpty()) {
            break;
          }
        }

        while (!list.isEmpty()) {
          recipeValue = (Recipe) list.remove(0);
          if (itemMeta != null && recipeValue.getResult().hasItemMeta()) {
            ItemMeta itemMetaFourth = recipeValue.getResult().getItemMeta();
            if ((itemMetaFourth.hasDisplayName()
                        && itemMeta.hasDisplayName()
                        && itemMetaFourth.getDisplayName().equals(itemMeta.getDisplayName())
                    || !itemMetaFourth.hasDisplayName() && !itemMeta.hasDisplayName())
                && (itemMetaFourth.hasLore()
                        && itemMeta.hasLore()
                        && itemMetaFourth.getLore().equals(itemMeta.getLore())
                    || !itemMetaFourth.hasLore() && !itemMeta.hasLore())) {
              recipeFourth = recipeValue;
            }
          }

          if ((itemMeta == null && recipeValue.getResult().hasItemMeta()
                  || !recipeValue.getResult().hasItemMeta())
              && recipeValue.getResult().getAmount() == 1
              && !CMIRecipe.getIngredientsList(recipeValue).isEmpty()) {
            if (!recipeValue.getResult().hasItemMeta()) {
              recipeSecond = recipeValue;
            } else {
              ItemMeta itemMetaFifth = recipeValue.getResult().getItemMeta();
              if (!itemMetaFifth.hasDisplayName() && !itemMetaFifth.hasLore()) {
                recipeSecond = recipeValue;
              }
            }
          }
        }

        if (recipeThird != null) {
          if (recipeFourth != null) {
            recipeValue = recipeFourth;
          } else if (recipeSecond != null) {
            recipeValue = recipeSecond;
          } else {
            for (ItemStack itemStack : CMIRecipe.getIngredientsList(recipeThird)) {
              if (this.item.getType().equals(itemStack.getType())) {
                return new ArrayList<>();
              }
            }

            recipeValue = recipeThird;
          }
        }

        this.recipeResultAmount = recipeValue.getResult().getAmount();
        this.recipe = recipeValue;
      }

      var varFourth = CMIRecipe.getIngredientsList(this.recipe);
      if (flagSecond) {
        double number = 0.0;
        if (this.recipeResultAmount > this.item.getAmount()) {
          number = this.item.getAmount() * 100.0 / Double.valueOf((double) this.recipeResultAmount);
        }
      }

      if (flag) {
        Collections.shuffle(varFourth);
        int value = (int) Math.ceil(this.getLeftDurabilityPercentage() * varFourth.size() / 100.0);

        while (varFourth.size() > value) {
          varFourth.remove(0);
        }
      }

      return varFourth;
    } else {
      return new ArrayList<>();
    }
  }

  public double getLeftDurabilityPercentage() {
    return this.format(this.durability * 100 / this.maxDurability);
  }

  public List<ItemStack> enchantsToBooks() {
    ArrayList<ItemStack> arrayList = new ArrayList();
    var map = this.item.getEnchantments();
    if (map.isEmpty()) {
      return arrayList;
    }

    for (Entry entry : map.entrySet()) {
      ItemStack itemStack = CMIMaterial.ENCHANTED_BOOK.newItemStack();
      if (itemStack.getItemMeta() instanceof EnchantmentStorageMeta) {
        EnchantmentStorageMeta enchantmentStorageMeta =
            (EnchantmentStorageMeta) itemStack.getItemMeta();
        enchantmentStorageMeta.addStoredEnchant(
            (Enchantment) entry.getKey(), (Integer) entry.getValue(), true);
        itemStack.setItemMeta(enchantmentStorageMeta);
      }

      arrayList.add(itemStack);
    }

    return arrayList;
  }

  public ItemStack createBook(Enchantment enchantment) {
    Integer integer = this.enchants.get(enchantment);
    if (integer == null) {
      integer = 1;
    }

    return this.createBook(enchantment, integer);
  }

  public ItemStack createBook(Enchantment enchantment, int value) {
    ItemStack itemStack = CMIMaterial.ENCHANTED_BOOK.newItemStack();
    if (itemStack.getItemMeta() instanceof EnchantmentStorageMeta) {
      EnchantmentStorageMeta enchantmentStorageMeta =
          (EnchantmentStorageMeta) itemStack.getItemMeta();
      enchantmentStorageMeta.addStoredEnchant(enchantment, value, true);
      itemStack.setItemMeta(enchantmentStorageMeta);
    }

    return itemStack;
  }

  public int getRecipeResultAmount() {
    return this.recipeResultAmount;
  }
}
