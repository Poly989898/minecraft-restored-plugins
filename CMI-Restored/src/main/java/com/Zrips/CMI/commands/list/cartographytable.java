package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class cartographytable implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback", "{gcp}You have opened cartography table for {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetfeedback", "{gcs}[senderDisplayName]{gcp} opened cartography table for you.");
  }

  @CAnnotation(
      priority = 121,
      info = "Open cartography table",
      args = "(playerName) (-s)",
      tab = {"playername", "%%-s"},
      explanation = {},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isPaper() && !Version.isCurrentLower(Version.v1_14_R1)) {
      String string = null;
      boolean flag = false;
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

      if (!player.isOnline()) {
        LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
        return null;
      }

      if (!flag) {
        Snd snd = new Snd().setSender(commandSender).setTarget(player);
        CMILC.info(this, commandSender, "feedback", snd);
        if (!player.getName().equalsIgnoreCase(commandSender.getName())) {
          CMILC.info(this, player, "targetfeedback", snd);
        }
      }

      cmi.getReflectionManager().openCartographyTable(player);
      return true;
    } else {
      LC.info_versionNotSupported.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }
}
