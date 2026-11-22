package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class saturation implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have set {gcs}[amount]{gcp} saturation for {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetfeedback",
        "{gcp}Your saturation was set to {gcs}[amount]{gcp} by {gcs}[senderDisplayName]{gcp}.");
  }

  @CAnnotation(
      info = "Set players saturation",
      args = "(playerName) [amount]",
      tab = "playername",
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    String string = null;
    int value = -1;
    String[] args = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          flag = true;
        }
      } else {
        try {
          value = Integer.parseInt(arg);
        } catch (NumberFormatException numberFormatException) {
          string = arg;
        }
      }
    }

    if (value == -1) {
      value = 20;
    }

    int limit = 0;
    if (value < limit) {
      LC.info_NoLessThan.sendMessage(commandSender, new Object[] {"[amount]", limit});
      return null;
    }

    limit = Integer.MAX_VALUE;
    if (value > limit) {
      LC.info_NoMoreThan.sendMessage(commandSender, new Object[] {"[amount]", limit});
      return null;
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    CMILC.info(this, commandSender, "feedback", snd, "[amount]", value);
    if (!player.getName().equalsIgnoreCase(commandSender.getName()) && !flag) {
      CMILC.info(this, player, "targetfeedback", snd, "[amount]", value);
    }

    player.setSaturation(value);
    cmi.save(player);
    return true;
  }
}
