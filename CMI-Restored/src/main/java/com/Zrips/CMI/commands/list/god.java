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
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class god implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback",
        "{gcp}You have set god to {gcs}[boolean]{gcp} for {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetfeedback",
        "{gcp}Your god mode was set to {gcs}[boolean]{gcp} by {gcs}[senderDisplayName]{gcp}.");
  }

  @CAnnotation(
      info = "Set players god mode to true or false",
      args = "[playerName] (true/false) (-s)",
      tab = {"playername", "true%%false"},
      regVar = {0, 1, 2, 3},
      consoleVar = {2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    Boolean godModeEnabled = null;
    String string = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
        flag = true;
      } else if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("on")) {
        godModeEnabled = true;
      } else if (!arg.equalsIgnoreCase("false") && !arg.equalsIgnoreCase("off")) {
        string = arg;
      } else {
        godModeEnabled = false;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    if (godModeEnabled == null) {
      godModeEnabled = !cmi.getNMS().getGodMode(player);
    }

    if (!godModeEnabled
        && player.getGameMode() != GameMode.ADVENTURE
        && player.getGameMode() != GameMode.SURVIVAL) {
      LC.info_cantDoInGamemode.sendMessage(commandSender, new Object[0]);
      return null;
    }

    cmi.getNMS().changeGodMode(player, godModeEnabled);
    cmi.save(player);
    CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      cmiUser.setTgod(0L);
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    if (!flag) {
      CMILC.info(this, commandSender, "feedback", snd, "[boolean]", godModeEnabled);
    }

    if (!player.getName().equalsIgnoreCase(commandSender.getName()) && !flag) {
      CMILC.info(this, player, "targetfeedback", snd, "[boolean]", godModeEnabled);
    }

    return true;
  }
}
