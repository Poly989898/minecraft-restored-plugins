package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ext implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}You have extinguished {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetfeedback", "{gcp}You have been extinguished by {gcs}[senderDisplayName]{gcp}.");
  }

  @CAnnotation(
      priority = 120,
      info = "Extinguish a player",
      args = "(playerName) (-s)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    String string = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
        flag = true;
      } else {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    player.setFireTicks(0);
    cmi.save(player);
    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    CMILC.info(this, commandSender, "feedback", snd);
    if (!player.getName().equalsIgnoreCase(commandSender.getName()) && !flag) {
      CMILC.info(this, player, "targetfeedback", snd);
    }

    return true;
  }
}
