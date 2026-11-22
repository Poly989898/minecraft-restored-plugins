package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class more implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback", "{gcp}Filled stack to {gcs}[amount]{gcp} for {gcs}[playerDisplayName]{gcp}.");
  }

  @CAnnotation(
      priority = 65,
      info = "Fills item stack to maximum amount",
      args = "(playerName) (-clone/[amount])",
      tab = "playername",
      explanation = {"Permissions:", " cmi.command.more.oversize - to get oversized stacks"},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    boolean flag = false;
    int value = 0;
    String[] args = strings;
    int count = strings.length;

    for (int argIndex = 0; argIndex < count; argIndex++) {
      String arg = args[argIndex];
      if (!arg.equalsIgnoreCase("-clone") && !arg.equalsIgnoreCase("-c")) {
        try {
          value = Integer.parseInt(arg);
        } catch (Exception exception) {
          if (string == null) {
            string = arg;
          }
        }
      } else {
        flag = true;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    ItemStack itemStack = CMIItemStack.getItemInMainHand(player);
    if (itemStack != null && !CMIMaterial.isAir(itemStack.getType())) {
      if (flag) {
        player.getInventory().addItem(new ItemStack[] {itemStack.clone()});
      } else if (value > 0) {
        count = itemStack.getMaxStackSize();
        if (PermissionsManager.CMIPerm.command_more_oversize.hasPermission(commandSender)) {
          count = 64;
        }

        if (count >= itemStack.getAmount() + value) {
          itemStack.setAmount(itemStack.getAmount() + value);
        } else {
          ItemStack clonedStack = itemStack.clone();
          clonedStack.setAmount(value);
          player.getInventory().addItem(new ItemStack[] {clonedStack});
        }
      } else {
        count = itemStack.getMaxStackSize();
        if (PermissionsManager.CMIPerm.command_more_oversize.hasPermission(commandSender)) {
          count = 64;
        }

        itemStack.setAmount(count);
      }

      if (player.isOnline()) {
        player.updateInventory();
      } else {
        cmi.save(player);
      }

      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      CMILC.info(this, commandSender, "feedback", "[amount]", itemStack.getAmount(), snd);
      return true;
    } else {
      LC.info_nothingInHand.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }
}
