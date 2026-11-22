package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Worth.WorthItem;
import com.Zrips.CMI.Modules.Worth.WorthManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.events.CMIPlayerItemsSellEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.GUIManager;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public class sell implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "total",
        "{gcp}Earned {gcs}[total] {gcp}by selling {gcs}[amount] {gcp}items. New balance"
            + " {gcs}[balance]");
    configReader.get("close", "{gcp}Place items you want to sell");
    configReader.get("sellInfo", "{gcp}Sell for: {gcs}[price]");
  }

  @Override
  @CAnnotation(
      priority = 125,
      info = "Sell items from inventory",
      args = "(all/blocks/hand/same/gui/material) (playerName) (-s)",
      tab = {"all%%blocks%%hand%%same%%gui%%playeritems%%playername", "playername", "-s"},
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] args) {
    String targetName = null;
    String typeName = "";
    int amount = 0;
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
            if (targetName == null && CMIUser.getOnlinePlayer(commandSender, arg, true) != null) {
              targetName = arg;
              break;
            }
            switch (arg.toLowerCase()) {
              case "blocks":
              case "all":
              case "gui":
              case "hand":
              case "same":
              case "material":
                {
                  typeName = arg.toLowerCase();
                  continue;
                }
            }
            try {
              amount = Integer.parseInt(arg);
            } catch (Throwable throwable) {
              typeName = arg.toLowerCase();
            }
          }
      }
    }
    Player player = cMI.getTarget(commandSender, targetName, this);
    if (player == null) {
      if (!bl) {
        LC.info_NotOnline.sendMessage((Object) commandSender, new Object[0]);
      }
      return null;
    }
    if (player.getGameMode().equals(GameMode.CREATIVE)) {
      if (!bl) {
        CMILC.Information_BadGameMode.sendMessage(commandSender, new Object[0]);
      }
      return null;
    }
    if (args.length > 0 && typeName.equalsIgnoreCase("gui")) {
      if (!PermissionsManager.CMIPerm.command_sell_$1.hasPermission(commandSender, true, "gui")) {
        return null;
      }
      CMIGui cMIGui = new CMIGui(player);
      cMIGui.setCmiInventoryType(GUIManager.CmiInventoryType.SellHand);
      cMIGui.setInvSize(GUIManager.GUIRows.r4);
      cMIGui.setAllowShift(true);
      cMIGui.setAllowPickUpAll(true);
      cMIGui.setAllowItemPickup(false);
      cMIGui.setTitle(CMILC.getIM(this, "close", new Object[0]));
      cMIGui.open();
      return true;
    }
    WorthManager.worthType worthType = WorthManager.worthType.hand;
    CMIMaterial material = null;
    if (args.length > 0 && WorthManager.worthType.getByName(typeName) != null) {
      worthType = WorthManager.worthType.getByName(typeName);
    } else {
      if (args.length > 0) {
        material = CMIMaterial.get(typeName);
      }
      if (material != null) {
        worthType = WorthManager.worthType.material;
      }
    }
    if (worthType.equals(WorthManager.worthType.gui)) {
      worthType = WorthManager.worthType.hand;
    }
    if (!worthType.equals(WorthManager.worthType.hand)
        && !PermissionsManager.CMIPerm.command_sell_$1.hasPermission(
            commandSender, true, worthType.name())) {
      return null;
    }
    List<ItemStack> items = cMI.getWorthManager().getItems(player, worthType, material, amount);
    sell.sellingItems(commandSender, player, worthType, items, amount, bl, true);
    return true;
  }

  public static boolean sellingItems(
      CommandSender commandSender,
      Player player,
      WorthManager.worthType worthType,
      List<ItemStack> itemList,
      int amount,
      boolean bl,
      boolean sellFromHand) {
    double total = 0.0;
    int soldAmount = 0;
    int counted = 0;
    List<ItemStack> sold = new ArrayList<ItemStack>();
    HashMap<WorthItem, Integer> hashMap = new HashMap<WorthItem, Integer>();
    HashMap<CMIMaterial, Integer> materialAmounts = new HashMap<CMIMaterial, Integer>();
    HashMap<CMIMaterial, Double> materialPrices = new HashMap<CMIMaterial, Double>();
    for (ItemStack itemStack : itemList) {
      if (itemStack == null) continue;
      if (CMI.getInstance().getWorthManager().isShulkerWithItems(itemStack)) {
        if (!WorthManager.worthType.gui.equals(worthType)) continue;
        player.getInventory().addItem(new ItemStack[] {itemStack});
        continue;
      }
      WorthItem worthItem = CMI.getInstance().getWorthManager().getWorth(itemStack);
      if (worthItem == null) {
        if (!WorthManager.worthType.gui.equals(worthType)) continue;
        player.getInventory().addItem(new ItemStack[] {itemStack});
        continue;
      }
      int stackAmount = itemStack.getAmount();
      if (amount > 0 && counted + stackAmount > amount) {
        stackAmount = amount - counted;
        itemStack = itemStack.clone();
        itemStack.setAmount(stackAmount);
      }
      counted += stackAmount;
      double price = worthItem.getPlayerSellPrice(itemStack, true, true) * (double) stackAmount;
      total += price;
      soldAmount += stackAmount;
      sold.add(itemStack);
      Integer count = hashMap.get(worthItem);
      if (count == null) {
        count = 0;
      }
      hashMap.put(worthItem, count + stackAmount);
      CMIMaterial material = CMIMaterial.get(itemStack);
      materialAmounts.put(
          material, materialAmounts.getOrDefault(material, 0) + itemStack.getAmount());
      materialPrices.put(material, materialPrices.getOrDefault(material, 0.0) + price);
      if (amount > 0 && counted >= amount) break;
    }
    if (!bl) {
      LC.info_Spliter.sendMessage((Object) commandSender, new Object[0]);
    }
    if (total == 0.0) {
      if (!bl) {
        CMILC.info("worth", commandSender, "worthless", new Object[0]);
      }
      return true;
    }
    CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
    if (sellFromHand) {
      if (worthType.equals(WorthManager.worthType.hand)) {
        ItemStack handItem = CMIItemStack.getItemInMainHand(player);
        if (amount > 0 && handItem.getAmount() > amount) {
          handItem.setAmount(handItem.getAmount() - amount);
        } else {
          CMILib.getInstance().getReflectionManager().setItemInMainHand(player, null);
        }
      } else {
        for (ItemStack soldItem : sold) {
          Map<Integer, ItemStack> leftover =
              player.getInventory().removeItem(new ItemStack[] {soldItem});
          ItemStack offHand;
          if (leftover.isEmpty()
              || (offHand = CMIItemStack.getItemInOffHand(player)) == null
              || !offHand.isSimilar(soldItem)) continue;
          offHand.setAmount(offHand.getAmount() - soldItem.getAmount());
          player.getInventory().setItemInOffHand(offHand.getAmount() > 0 ? offHand : null);
        }
      }
    }
    CMIPlayerItemsSellEvent sellEvent =
        new CMIPlayerItemsSellEvent(
            player, worthType, soldAmount, total, materialAmounts, materialPrices);
    Bukkit.getPluginManager().callEvent((Event) sellEvent);
    total = sellEvent.getTotalPayment();
    cmiUser.deposit(total);
    if (!bl) {
      CMILC.info(
          "sell",
          player,
          "total",
          "[total]",
          CMI.getInstance().getEconomyManager().format(total),
          "[amount]",
          soldAmount,
          "[balance]",
          cmiUser.getFormatedBalance());
    }
    if (CMI.getInstance().getConfigManager().isSellLog()) {
      CMI.getInstance().getWorthManager().sellLog(player, hashMap);
    }
    return true;
  }
}
