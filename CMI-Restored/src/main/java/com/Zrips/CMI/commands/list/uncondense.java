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

public class uncondense implements Cmd {
  private static HashMap<ItemStack, BasicRecipe> cache = new HashMap();

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "converted", "{gcp}Converted {gcs}[blocks] {gcp}blocks into {gcs}[items] {gcp}items");
    configReader.get("nothing", "{gcw}There is nothing in your inventory that can be uncondensed.");
  }

  @Override
  @CAnnotation(
      info = "Uncondense items into smaller parts",
      args = "(itemName) (playerName) (-s)",
      multiTab = {"[itemname] [playerName] -s"},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] itemStack) {
    Object object;
    List<ItemStack> results = new ArrayList<ItemStack>();
    Object recipeObj = null;
    boolean bl = false;
    int n = itemStack.length;
    int index = 0;
    while (index < n) {
      object = itemStack[index];
      switch (((String) object).toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            CMIItemStack cmiItemStack =
                CMILib.getInstance().getItemManager().getItem((String) object);
            if (cmiItemStack != null) {
              results.add(cmiItemStack.getItemStack());
              break;
            }
            recipeObj = object;
          }
      }
      ++index;
    }
    object = cMI.getUser(commandSender, (String) recipeObj, this);
    if (object == null) {
      return null;
    }
    if (!((CMIUser) object).isOnline()) {
      LC.info_NotOnline.sendMessage((Object) commandSender, new Object[0]);
      return null;
    }
    if (results.isEmpty()) {
      results =
          ((CMIUser) object)
              .getInventory()
              .getItems(CMIPlayerInventory.CMIInventorySlot.MainInventory);
    }
    CondenseResult condenseResult = new CondenseResult();
    ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
    for (ItemStack resultStack : ((Iterable<ItemStack>) results)) {
      CondenseResult condensed =
          this.uncondenseStack(
              (CMIUser) object, resultStack, cMI.getRecipeManager().isRecipesRequireBackwards());
      if (condensed == null) continue;
      condenseResult.addImputAmount(condensed.getImputAmount());
      condenseResult.addResultAmount(condensed.getResultAmount());
      arrayList.addAll(condensed.getToAdd());
    }
    for (ItemStack leftover : arrayList) {
      ((CMIUser) object).getInventory().addItem(leftover);
    }
    ((CMIUser) object).getPlayer().updateInventory();
    if (condenseResult.getResultAmount() > 0) {
      if (!bl) {
        CMILC.info(
            (Object) this,
            commandSender,
            "converted",
            "[items]",
            condenseResult.getResultAmount(),
            "[blocks]",
            condenseResult.getImputAmount());
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

  private CondenseResult uncondenseStack(CMIUser cMIUser, ItemStack itemStack, boolean bl) {
    BasicRecipe basicRecipe = this.getPossibleUnCondense(itemStack, bl);
    if (basicRecipe == null) {
      return null;
    }
    ItemStack input = basicRecipe.getInput();
    ItemStack output = basicRecipe.getResult();
    int n = 0;
    for (ItemStack extra :
        cMIUser.getInventory().getItems(CMIPlayerInventory.CMIInventorySlot.MainInventory)) {
      if (extra == null || !extra.isSimilar(itemStack)) continue;
      n += extra.getAmount();
    }
    int count = n / input.getAmount() * output.getAmount();
    if ((n -= n % input.getAmount()) <= 0) {
      return null;
    }
    input.setAmount(n);
    output.setAmount(count);
    cMIUser.getPlayer().getInventory().removeItem(new ItemStack[] {input});
    cMIUser.getPlayer().updateInventory();
    CondenseResult condenseResult = new CondenseResult(n, count);
    condenseResult.addToAdd(output);
    return condenseResult;
  }

  private BasicRecipe getPossibleUnCondense(ItemStack itemStack, boolean bl) {
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
    ItemStack match = null;
    Recipe matchRecipe = null;
    ItemStack itemCopy = itemStack.clone();
    itemCopy.setAmount(1);
    CMIItemStack cMIItemStack = new CMIItemStack(itemStack);
    List<Recipe> recipes = cMIItemStack.getRecipesFor();
    for (Recipe recipe : recipes) {
      ItemStack resultItem = recipe.getResult().clone();
      CMIItemStack resultStack = new CMIItemStack(resultItem);
      List<ItemStack> ingredients = CMIRecipe.getIngredientsList(recipe);
      boolean matched = false;
      ItemStack matchItem = null;
      for (ItemStack ingredient : ingredients) {
        if (ingredient == null) continue;
        if ((ingredient = ingredient.clone()).getDurability() == Short.MAX_VALUE) {
          ingredient.setDurability((short) 0);
        }
        if (matchItem == null) {
          matchItem = ingredient.clone();
          continue;
        }
        if (!ingredient.isSimilar(matchItem)) {
          matched = true;
          break;
        }
        itemCopy = ingredient;
      }
      if (matched || ingredients.size() != 4 && ingredients.size() != 9) continue;
      if (bl) {
        boolean valid = true;
        for (Recipe recipeFrom : resultStack.getRecipesFrom()) {
          if (!recipeFrom.getResult().isSimilar((ItemStack) ingredients.get(0))) continue;
          List<ItemStack> fromIngredients = CMIRecipe.getIngredientsList(recipeFrom);
          matchItem = null;
          for (ItemStack fromIngredient : fromIngredients) {
            if (fromIngredient == null) continue;
            if ((fromIngredient = fromIngredient.clone()).getDurability() == Short.MAX_VALUE) {
              fromIngredient.setDurability((short) 0);
            }
            if (matchItem == null) {
              matchItem = fromIngredient.clone();
              continue;
            }
            if (fromIngredient.isSimilar(matchItem)) continue;
            matched = true;
            break;
          }
          if (matched) continue;
          valid = false;
        }
        if (valid) continue;
      }
      itemCopy.setAmount(ingredients.size());
      if (match == null || match.getAmount() < resultItem.getAmount()) {
        match = resultItem;
        matchRecipe = recipe;
      }
      if (ingredients.size() == 9) break;
    }
    if (match != null && matchRecipe != null) {
      BasicRecipe basicRecipe = new BasicRecipe(itemCopy, match);
      cache.put(itemStack, basicRecipe);
      return basicRecipe;
    }
    cache.put(itemStack, null);
    return null;
  }
}
