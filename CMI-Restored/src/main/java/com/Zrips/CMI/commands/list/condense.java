/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.CMILib
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Items.CMIItemStack
 *  net.Zrips.CMILib.Locale.LC
 *  net.Zrips.CMILib.Recipes.CMIRecipe
 *  org.bukkit.command.CommandSender
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.Recipe
 *  org.bukkit.inventory.ShapedRecipe
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.BasicRecipe;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CondenseResult;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Recipes.CMIRecipe;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class condense implements Cmd {
  private static HashMap<ItemStack, BasicRecipe> cache = new HashMap();

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "converted", "{gcp}Converted {gcs}[items] {gcp}items into {gcs}[blocks] {gcp}blocks");
    configReader.get("nothing", "{gcw}There is nothing in your inventory that can be condensed.");
  }

  @Override
  @CAnnotation(
      info = "Condense items into blocks",
      args = "(itemName) (playerName) (-s)",
      multiTab = {"[itemname] [playerName] -s"},
      regVar = {0, 1, 2},
      consoleVar = {1, 2, 3},
      others = true,
      customAlias = {"compact"})
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] args) {
    List<ItemStack> items = new ArrayList<ItemStack>();
    String targetName = null;
    boolean bl = false;
    for (String arg : args) {
      switch (arg.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            CMIItemStack cmiItemStack = CMILib.getInstance().getItemManager().getItem(arg);
            if (cmiItemStack != null) {
              items.add(cmiItemStack.getItemStack());
              break;
            }
            targetName = arg;
          }
      }
    }
    CMIUser user = cMI.getUser(commandSender, targetName, this);
    if (user == null) {
      return null;
    }
    if (!user.isOnline()) {
      LC.info_NotOnline.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    if (items.isEmpty()) {
      items = user.getInventory().getItems(CMIPlayerInventory.CMIInventorySlot.MainInventory);
    }
    CondenseResult condenseResult = new CondenseResult();
    for (ItemStack itemStack : items) {
      CondenseResult stackResult =
          this.condenseStack(user, itemStack, cMI.getRecipeManager().isRecipesRequireBackwards());
      if (stackResult == null) continue;
      condenseResult.addImputAmount(stackResult.getImputAmount());
      condenseResult.addResultAmount(stackResult.getResultAmount());
    }
    user.getPlayer().updateInventory();
    if (condenseResult.getResultAmount() > 0) {
      if (!bl) {
        CMILC.info(
            (Object) this,
            commandSender,
            "converted",
            "[items]",
            condenseResult.getImputAmount(),
            "[blocks]",
            condenseResult.getResultAmount());
      }
      return true;
    }
    if (!bl) {
      CMILC.info((Object) this, commandSender, "nothing", new Object[0]);
    }
    return null;
  }

  public static void clearCache() {
    cache.clear();
  }

  private CondenseResult condenseStack(CMIUser cMIUser, ItemStack itemStack, boolean bl) {
    BasicRecipe basicRecipe = this.getPossibleCondense(itemStack, bl);
    if (basicRecipe == null) {
      return null;
    }
    ItemStack item = basicRecipe.getInput();
    ItemStack itemSecond = basicRecipe.getResult();
    int n = 0;
    for (ItemStack itemThird :
        cMIUser.getInventory().getItems(CMIPlayerInventory.CMIInventorySlot.MainInventory)) {
      if (itemThird == null || !itemThird.isSimilar(itemStack)) continue;
      n += itemThird.getAmount();
    }
    int count = n / item.getAmount() * itemSecond.getAmount();
    if ((n -= n % item.getAmount()) <= 0) {
      return null;
    }
    item.setAmount(n);
    itemSecond.setAmount(count);
    cMIUser.getPlayer().getInventory().removeItem(new ItemStack[] {item});
    cMIUser.getPlayer().updateInventory();
    cMIUser.getInventory().addItem(itemSecond);
    return new CondenseResult(n, count);
  }

  private BasicRecipe getPossibleCondense(ItemStack itemStack, boolean bl) {
    if (itemStack.hasItemMeta()) {
      if (itemStack.getItemMeta().hasDisplayName()) {
        return null;
      }
      if (itemStack.getItemMeta().hasLore()) {
        return null;
      }
    }
    if (cache.containsKey(itemStack)) {
      return cache.get(itemStack);
    }
    List<ItemStack> bestIngredients = null;
    Recipe bestRecipe = null;
    CMIItemStack cMIItemStack = new CMIItemStack(itemStack);
    List<Recipe> recipes = cMIItemStack.getRecipesFrom();
    for (Recipe recipeValue : recipes) {
      List<ItemStack> items = CMIRecipe.getIngredientsList(recipeValue);
      boolean blValue = false;
      for (ItemStack ingredient : items) {
        if (ingredient == null) continue;
        if ((ingredient = ingredient.clone()).getDurability() == Short.MAX_VALUE) {
          ingredient.setDurability((short) 0);
        }
        if (ingredient.isSimilar(itemStack)) continue;
        blValue = true;
        break;
      }
      if (recipeValue instanceof ShapedRecipe && isShapedMissingSlots((Recipe) recipeValue)
          || blValue
          || items == null
          || items.isEmpty()
          || !CMI.getInstance().getRecipeManager().isRecipes4Sized() && items.size() == 4
          || items.size() != 4 && items.size() != 9
          || items.size() < recipeValue.getResult().getAmount()) continue;
      if (bl) {
        boolean blSecond = true;
        for (Recipe recipeSecond : cMIItemStack.getRecipesFor()) {
          List<ItemStack> itemsSecond = CMIRecipe.getIngredientsList(recipeSecond);
          boolean blThird = false;
          for (ItemStack item : itemsSecond) {
            if (item == null) continue;
            if ((item = item.clone()).getDurability() == Short.MAX_VALUE) {
              item.setDurability((short) 0);
            }
            if (item.isSimilar(recipeValue.getResult())) continue;
            blThird = true;
            break;
          }
          if (blThird) continue;
          blSecond = false;
          break;
        }
        if (blSecond) continue;
      }
      if (bestIngredients == null || bestIngredients.size() < items.size()) {
        bestIngredients = items;
        bestRecipe = recipeValue;
      }
      if (items.size() == 9) break;
    }
    if (bestIngredients != null && bestRecipe != null) {
      ItemStack resultAmount = itemStack.clone();
      resultAmount.setAmount(bestIngredients.size());
      BasicRecipe basicRecipe = new BasicRecipe(bestRecipe.getResult(), resultAmount);
      cache.put(itemStack, basicRecipe);
      return basicRecipe;
    }
    cache.put(itemStack, null);
    return null;
  }

  private static boolean isShapedMissingSlots(Recipe recipe) {
    HashMap ingredientsMap = (HashMap) CMIRecipe.getIngredientsMap(recipe);
    return ingredientsMap.get(1) == null && ingredientsMap.get(4) == null
        || ingredientsMap.get(4) == null && ingredientsMap.get(7) == null;
  }
}
