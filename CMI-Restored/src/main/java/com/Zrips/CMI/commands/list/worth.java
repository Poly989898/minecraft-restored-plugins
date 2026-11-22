package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Worth.WorthItem;
import com.Zrips.CMI.Modules.Worth.WorthItemCheck;
import com.Zrips.CMI.Modules.Worth.WorthManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashMap;
import java.util.Map.Entry;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class worth implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("worthless", "{gcp}Worthless");
    configReader.get("damaged", " {gcp}({gcs}[percent]% {gcp}damaged)");
    configReader.get("shulker", "{gcp}Remove items from shulker box before selling");
    configReader.get("repair", "{gcp}Repair before selling");
    configReader.get("basePrice", "{gcs}[itemName]{gcp}({gcs}[amount]{gcp}) -> {gcs}[price]");
    configReader.get("fullPrice", "{gcs}[price]");
    configReader.get("total", "{gcp}Total worth: {gcs}[total]");
  }

  @CAnnotation(
      info = "Check item worth",
      args = "(all/blocks/hand/material)",
      tab = "all%%blocks%%hand%%[itemname]",
      explanation = {},
      regVar = {0, 1},
      consoleVar = 666,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    CMIItemStack cmiItemStack = null;
    WorthManager.worthType worthType = WorthManager.worthType.hand;
    if (strings.length > 0 && WorthManager.worthType.getByName(strings[0]) != null) {
      worthType = WorthManager.worthType.getByName(strings[0]);
    }

    if (strings.length > 0) {
      cmiItemStack = CMILib.getInstance().getItemManager().getItem(strings[0]);
    }

    var list = cmi.getWorthManager().getItems(player, worthType);
    if (cmiItemStack != null && !cmiItemStack.getCMIType().isAir()) {
      list.clear();
      list.add(cmiItemStack.getItemStack());
    }

    double number = 0.0;
    HashMap<WorthItemCheck, Integer> hashMap = new HashMap();
    boolean flag = false;

    for (ItemStack itemStack : list) {
      if (itemStack != null) {
        if (CMI.getInstance().getWorthManager().isRepairBeforeSell(itemStack)) {
          flag = true;
        }

        if (!CMI.getInstance().getWorthManager().isShulkerWithItems(itemStack)) {
          WorthItem worthItem = CMI.getInstance().getWorthManager().getWorth(itemStack);
          if (worthItem != null) {
            WorthItemCheck worthItemCheck = new WorthItemCheck(itemStack);
            worthItemCheck.setWorth(worthItem);
            hashMap.put(worthItemCheck, itemStack.getAmount());
          }
        }
      }
    }

    LC.info_Spliter.sendMessage(commandSender, new Object[0]);

    for (Entry entry : hashMap.entrySet()) {
      int value = (Integer) entry.getValue();
      WorthItemCheck worthCheck = (WorthItemCheck) entry.getKey();
      number += worthCheck.getSellPrice(true, true) * value;
      cmiItemStack = CMILib.getInstance().getItemManager().getItem(worthCheck.getItem());
      Double enchantSellPrice = worthCheck.getEnchantSellPrice();
      RawMessage rawMessage = new RawMessage();
      String string =
          CMILC.getIM(
              this,
              "basePrice",
              "[itemName]",
              cmiItemStack.getRealName(),
              "[amount]",
              value,
              "[price]",
              cmi.getEconomyManager().format(worthCheck.getSellPrice(true) * value));
      rawMessage
          .addText(string)
          .addHover(
              CMILC.getIM(
                      this,
                      "fullPrice",
                      "[price]",
                      cmi.getEconomyManager().format(worthCheck.getSellPrice(false)))
                  + (enchantSellPrice > 0.0
                      ? " \nEnchant price: " + cmi.getEconomyManager().format(enchantSellPrice)
                      : ""));
      double damagePercent =
          CMI.getInstance().getWorthManager().damagePercentage(worthCheck.getItem());
      if (CMI.getInstance().getWorthManager().damagePercentage(worthCheck.getItem()) > 0.0) {
        rawMessage.addText(CMILC.getIM(this, "damaged", "[percent]", damagePercent));
      }

      rawMessage.show(commandSender);
    }

    if (number == 0.0) {
      if (flag) {
        CMILC.info(this, commandSender, "repair");
      }

      if (list.size() == 1
          && CMI.getInstance().getWorthManager().isShulkerWithItems((ItemStack) list.get(0))) {
        CMILC.info(this, commandSender, "shulker");
      }

      CMILC.info(this, commandSender, "worthless");
      return true;
    } else {
      if (list.size() > 1) {
        CMILC.info(this, commandSender, "total", "[total]", cmi.getEconomyManager().format(number));
      }

      return true;
    }
  }
}
