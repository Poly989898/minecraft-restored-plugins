package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Worth.WorthItem;
import com.Zrips.CMI.Modules.Worth.WorthManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Recipes.CMIRecipe;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class generateworth implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(info = "Auto generate possible item worth values", regVar = 666, consoleVar = 0)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    HashSet<WorthItem> hashSet = new HashSet<>();
    CompletableFuture completableFuture = this.checkAsync(5, null, hashSet);
    completableFuture.whenComplete(
        (object, throwable) -> {
          if (!hashSet.isEmpty()) {
            try {
              new ConfigReader(cmi, "Saves" + File.separator + WorthManager.fileName)
                  .saveToBackup(false);
            } catch (Exception exception) {
              exception.printStackTrace();
            }
          }

          if (!hashSet.isEmpty()) {
            cmi.getWorthManager().updatePriceInFile(null, null);
          }

          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("\n");
          if (!hashSet.isEmpty()) {
            stringBuilder.append("Old Worth.yml file got placed into backup folder\n");
          }

          stringBuilder.append("Added new ").append(hashSet.size()).append(" worth values:\n");
          int value = 0;

          for (WorthItem worthItem : hashSet) {
            stringBuilder
                .append(++value)
                .append(". ")
                .append(CMIMaterial.get(worthItem.getItem()))
                .append(" -> ")
                .append(worthItem.getSellPrice())
                .append("\n");
          }

          CMIMessages.consoleMessage(stringBuilder.toString());
        });
    return true;
  }

  private CompletableFuture<Void> checkAsync(
      int value, Set<CMIMaterial> set, Set<WorthItem> setSecond) {
    CMIMessages.consoleMessage("Checking!");
    return CompletableFuture.runAsync(() -> this.check(value, set, setSecond));
  }

  private void check(int value, Set<CMIMaterial> set, Set<WorthItem> setSecond) {
    if (value > 0) {
      CMIMessages.consoleMessage("Pass " + (6 - value));
      if (set == null || set.isEmpty()) {
        set = new HashSet<>();
        CMIMaterial[] cmiMaterials;
        int count = (cmiMaterials = CMIMaterial.values()).length;

        for (int countSecond = 0; countSecond < count; countSecond++) {
          CMIMaterial cmiMaterial = cmiMaterials[countSecond];
          if (cmiMaterial.getMaterial() != null && !cmiMaterial.isLegacy()) {
            set.add(cmiMaterial);
          }
        }
      }

      int countThird = setSecond.size();
      double number =
          1.0 + CMI.getInstance().getWorthManager().getAutoGeneratePriceIncrease() / 100.0;

      for (CMIMaterial cmiMaterialValue : new HashSet<>(set)) {
        CMIItemStack cmiItemStack = cmiMaterialValue.newCMIItemStack();
        WorthItem worthItem =
            CMI.getInstance().getWorthManager().getWorth(cmiItemStack.getItemStack());
        if (worthItem != null && worthItem.getSellPrice() != null) {
          set.remove(cmiMaterialValue);
        } else {
          var list = cmiItemStack.getRecipesFor();
          Double amount = null;
          if (list != null && !list.isEmpty()) {
            for (Recipe recipe : list) {
              var varSecond = CMIRecipe.getIngredientsList(recipe);
              Double amountSecond = null;

              for (ItemStack itemStack : varSecond) {
                WorthItem worthItemValue = CMI.getInstance().getWorthManager().getWorth(itemStack);
                if (worthItemValue == null || worthItemValue.getSellPrice() == null) {
                  amountSecond = null;
                  break;
                }

                if (amountSecond == null) {
                  amountSecond = 0.0;
                }

                amountSecond =
                    amountSecond
                        + worthItemValue.getSellPrice()
                            * itemStack.getAmount()
                            / recipe.getResult().getAmount();
              }

              if (amount == null || amountSecond != null && amount > amountSecond) {
                amount = amountSecond;
              }
            }

            if (amount != null) {
              worthItem =
                  new WorthItem(
                      cmiItemStack.getItemStack(), (int) (amount * number * 100.0) / 100.0);
              CMI.getInstance().getWorthManager().addWorth(worthItem);
              setSecond.add(worthItem);
              set.remove(cmiMaterialValue);
            }
          } else {
            set.remove(cmiMaterialValue);
          }
        }
      }

      value--;
      if (countThird < setSecond.size()) {
        this.check(value, set, setSecond);
      }
    }
  }
}
