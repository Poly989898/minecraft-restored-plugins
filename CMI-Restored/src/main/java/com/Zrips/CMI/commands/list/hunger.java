package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class hunger implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have set {gcs}[hunger]{gcp} hunger for {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetfeedback",
        "{gcp}Your hunger was set to {gcs}[hunger]{gcp} by {gcs}[senderDisplayName]{gcp}.");
  }

  @CAnnotation(
      info = "Set players hunger",
      args = "[playerName] [amount] (-s)",
      tab = "playername",
      regVar = {1, 2, 3},
      consoleVar = {2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Boolean silent = false;
    String string = null;
    int value = -1;
    String[] args = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
        silent = true;
      } else {
        if (string == null) {
          Player player = CMIUser.getOnlinePlayer(commandSender, arg, true);
          if (player != null) {
            string = arg;
            continue;
          }
        }

        try {
          value = Integer.parseInt(arg);
        } catch (NumberFormatException numberFormatException) {
          string = arg;
        }
      }
    }

    Player targetPlayer = cmi.getTarget(commandSender, string, this);
    if (targetPlayer == null) {
      return null;
    }

    if (value == -1) {
      LC.info_UseInteger.sendMessage(commandSender, new Object[0]);
      return null;
    }

    byte b = 0;
    if (value < b) {
      LC.info_NoLessThan.sendMessage(commandSender, new Object[] {"[amount]", Integer.valueOf(b)});
      return null;
    }

    b = 20;
    if (!cmi.getConfigManager().hungeroveride && value > b) {
      LC.info_NoMoreThan.sendMessage(commandSender, new Object[] {"[amount]", Integer.valueOf(b)});
      return null;
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(targetPlayer);
    targetPlayer.setFoodLevel(value);
    CMILC.info(this, commandSender, "feedback", snd, "[amount]", value);
    if (!targetPlayer.getName().equalsIgnoreCase(commandSender.getName()) && !silent) {
      CMILC.info(this, targetPlayer, "targetfeedback", snd, "[amount]", value);
    }

    cmi.save(targetPlayer);
    return true;
  }
}
