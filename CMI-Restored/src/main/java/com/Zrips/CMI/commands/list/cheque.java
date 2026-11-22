package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.AttachedCommands.CustomNBTManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.events.CMIChequeCreationEvent;
import com.Zrips.CMI.utils.CMIEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class cheque implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "transferred",
        "{gcp}Transferred {gcp}[amount] {gcp}to your account. New balance: {gcp}[balance]");
    configReader.get("chequeName", "{gcp}Cheque for {gcp}[amount]");
    configReader.get(
        "chequeLore",
        Arrays.asList(
            "&r{gcn}Issued by {gcs}[senderDisplayName]", "&r{gcn}Right click to deposit"));
    configReader.get("holdPaper", "{gcp}Hold paper in your hand");
    configReader.get("cantUse", "{gcp}Can't use this piece of paper. It is a cheque already!");
    configReader.get(
        "clarification",
        "{gcw}This type of cheque is no longer accepted. Please contact with staff member to update"
            + " it");
  }

  @CAnnotation(
      priority = 48,
      info = "Convert money into cheque",
      args = "(playerName) [amount]",
      tab = "playername",
      explanation = {},
      regVar = {1, 2},
      consoleVar = 2,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Long time = null;
    String string = null;
    String[] parts = strings;
    int value = strings.length;

    for (int count = 0; count < value; count++) {
      String text = parts[count];
      if (time == null) {
        try {
          Double amount = CMI.getInstance().getEconomyManager().translateMoney(text);
          if (amount != null) {
            time = amount.longValue();
            continue;
          }
        } catch (Throwable throwable) {
        }
      }

      string = text;
    }

    if (string != null
        && !PermissionsManager.CMIPerm.command_cheque_admin.hasPermission(commandSender, true)) {
      return null;
    }

    if (time == null) {
      return false;
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    Player player = cmiUser.getPlayer();
    if (cmi.getEconomyManager().getMaxChequeValue() > 0.0
        && time.longValue() > cmi.getEconomyManager().getMaxChequeValue()) {
      time = cmi.getEconomyManager().getMaxChequeValue().longValue();
    }

    if (time <= 0L) {
      return false;
    }

    if (!cmiUser.hasMoney(time.doubleValue()) && commandSender instanceof Player) {
      LC.econ_noMoney.sendMessage(commandSender, new Object[0]);
      return null;
    }

    ItemStack itemStack =
        cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.MainHand);
    if (string == null && cmi.getEconomyManager().isChequePaperRequired()) {
      if (itemStack == null || !itemStack.getType().equals(Material.PAPER)) {
        CMILC.info(this, commandSender, "holdPaper");
        return null;
      }
    } else {
      itemStack = new ItemStack(Material.PAPER);
    }

    ItemStack item = itemStack.clone();
    CMINBT cminbt = new CMINBT(item);
    if (!cminbt.hasNBT(CustomNBTManager.CustomNBTType.MoneyCheque.name())
        && !cminbt.hasNBT(CustomNBTManager.CustomNBTType.CMICheque.name())) {
      ItemStack itemSecond =
          (ItemStack)
              cminbt.setString(
                  CustomNBTManager.CustomNBTType.CMICheque.name(),
                  CMIEncoder.encode(String.valueOf(time)));
      ItemStack itemThird = updateLore(commandSender, player, itemSecond, time);
      CMIChequeCreationEvent cmiChequeCreationEvent =
          new CMIChequeCreationEvent(player, itemThird, time.longValue());
      Bukkit.getPluginManager().callEvent(cmiChequeCreationEvent);
      if (cmiChequeCreationEvent.isCancelled()) {
        return null;
      }

      ItemStack itemFourth = cmiChequeCreationEvent.getCheque();
      time = (long) cmiChequeCreationEvent.getPrice();
      if (string == null) {
        cmiUser.withdraw(time.doubleValue());
      }

      if (itemStack.getAmount() > 1) {
        itemStack.setAmount(itemFourth.getAmount() - 1);
        itemFourth.setAmount(1);
        CMIItemStack.setItemInMainHand(player, itemStack);
        cmiUser.getInventory().addItem(itemFourth);
        cmiUser.updateInventory();
      } else if (string == null && cmi.getEconomyManager().isChequePaperRequired()) {
        CMIItemStack.setItemInMainHand(player, itemFourth);
      } else {
        cmiUser.getInventory().addItem(itemFourth);
        cmiUser.updateInventory();
      }

      cmi.save(player);
      return true;
    } else {
      CMILC.info(this, commandSender, "cantUse");
      return null;
    }
  }

  private static ItemStack updateLore(
      CommandSender commandSender, Player player, ItemStack itemStack, Long time) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(
        CMILC.getIM(
            "cheque",
            "chequeName",
            "[amount]",
            CMI.getInstance().getEconomyManager().format(time.doubleValue())));
    var list = itemMeta.getLore();
    if (list == null) {
      list = new ArrayList();
    }

    list.clear();
    Snd snd = new Snd().setSender(commandSender).setTarget(player);

    for (String string :
        CMILC.getIML(
            "cheque",
            "chequeLore",
            "[amount]",
            CMI.getInstance().getEconomyManager().format(time.doubleValue()))) {
      String text = CMI.getInstance().getLM().updateSnd(snd, string);
      list.add(CMIChatColor.translate(text));
    }

    itemMeta.setLore(list);
    itemStack.setItemMeta(itemMeta);
    return itemStack;
  }
}
