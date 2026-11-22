package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Worth.WorthItem;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class setworth implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("setSell", "{gcp}Set item sell price {gcs}[amount]");
    configReader.get("setBuy", "{gcp}Set item buy price {gcs}[amount]");
    configReader.get("notInUse", "{gcp}Mainly used for possible exploit detection");
    configReader.get("sellPrice", "{gcp}Sell price: {gcp}[amount]");
    configReader.get("buyPrice", "{gcp}Buy price: {gcs}[amount]");
    configReader.get("exploit", "{gce}Possible EXPLOIT!");
    configReader.get("ingredient", "{gcn}Ingredients");
    configReader.get("ingredients", "{gcs}[ingredient]{gcp}({gcs}[amount]{gcp}) = {gcs}[price]");
    configReader.get("notSet", "{gcp}price not set");
    configReader.get("notSetBuy", "{gcd}Buy price not set");
    configReader.get("result", "{gcp}Crafts into {gcs}[result]{gcp}({gcs}[amount]{gcp})");
    configReader.get("ingredientsPrice", "{gcp}Ingredient price: {gcs}[price]");
    configReader.get("resultPrice", "{gcp}Can be sold for: {gcs}[price]");
    configReader.get("explanation", "{gcn}Buy price is only used in detecting possible exploits");
  }

  @CAnnotation(
      info = "Change item worth",
      args = "(itemname) -s:(sellPrice)",
      multiTab = {"[itemname],-s:1", "-s:1,-b:0.5"},
      regVar = {0, 1, 2, 3},
      consoleVar = {2, 3},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMIItemStack cmiItemStack = null;
    Double amount = null;
    Double amountSecond = null;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String string = parts[count];

      try {
        if (string.startsWith("-s:")) {
          amount = Double.parseDouble(string.substring("-s:".length()));
        }
      } catch (Exception exception) {
      }

      try {
        if (string.startsWith("-b:")) {
          amountSecond = Double.parseDouble(string.substring("-b:".length()));
        }
      } catch (Exception exception2) {
      }

      if (cmiItemStack == null) {
        cmiItemStack = CMILib.getInstance().getItemManager().getItem(string);
      }
    }

    if (cmiItemStack == null && commandSender instanceof Player) {
      cmiItemStack = new CMIItemStack(CMIItemStack.getItemInMainHand((Player) commandSender));
    }

    if (cmiItemStack == null || cmiItemStack.getType().equals(Material.AIR)) {
      return false;
    }

    if (amount != null || amountSecond != null) {
      WorthItem worthItem = cmi.getWorthManager().getWorth(cmiItemStack.getItemStack());
      if (worthItem == null) {
        worthItem = new WorthItem(cmiItemStack.getItemStack());
      }

      if (amount != null) {
        amount = CMINumber.clamp(amount, 0.0, amount);
        if (worthItem.getBuyPrice() != null && worthItem.getBuyPrice() > 0.0) {
          amount = Math.min(amount, worthItem.getBuyPrice());
        }

        worthItem.setSellPrice(amount);
        CMILC.info(this, commandSender, "setSell", "[amount]", amount);
      }

      if (amountSecond != null) {
        amountSecond = CMINumber.clamp(amountSecond, 0.0, amountSecond);
        if (amount != null) {
          amountSecond = Math.max(amountSecond, amount);
        } else if (worthItem.getSellPrice() != null && worthItem.getSellPrice() > 0.0) {
          amountSecond = Math.max(amountSecond, worthItem.getSellPrice());
        }

        worthItem.setBuyPrice(amountSecond);
        CMILC.info(this, commandSender, "setBuy", "[amount]", amountSecond);
      }

      CMI.getInstance().getWorthManager().updatePriceInFile(worthItem);
      return true;
    } else {
      if (!(commandSender instanceof Player)) {
        return false;
      }

      cmi.getWorthManager().openWorthGui((Player) commandSender, cmiItemStack);
      return true;
    }
  }
}
