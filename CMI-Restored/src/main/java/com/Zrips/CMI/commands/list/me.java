package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class me implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("format", "{gcp}* [senderDisplayName] {gcs}[message]");
  }

  @CAnnotation(
      info = "Sends special message to all players",
      args = "[message]",
      regVar = -100,
      consoleVar = -100,
      others = false,
      ignoreHelpPage = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = "";
    String[] commandArgs = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = commandArgs[argIndex];
      if (!string.isEmpty()) {
        string = string + " ";
      }

      string = string + arg;
    }

    if (commandSender instanceof Player) {
      CMIUser cmiUser = cmi.getPlayerManager().getUser((Player) commandSender);
      if (cmiUser.isMuted()
          && Config.MuteNoPm
          && !PermissionsManager.CMIPerm.command_mute_bypass.hasPermission(commandSender)) {
        CMILC.info(
            "mute",
            commandSender,
            "muted",
            "[time]",
            CMITimeManager.to24hourShort(cmiUser.getMutedUntil() - System.currentTimeMillis())
                + (cmiUser.getMutedReason() == null
                    ? ""
                    : CMILC.getIM("mute", "reason", "[reason]", cmiUser.getMutedReason())));
        return null;
      }
    }

    if (cmi.getChatManager().isColorsMe()) {
      string = CMITextCleaner.cleanFromColorCodes(commandSender, string, CMIColorTypes.me, false);
    }

    Snd snd = new Snd().setSender(commandSender);
    string = CMILC.getIM(this, "format", "[message]", string);

    for (Player player : Bukkit.getOnlinePlayers()) {
      snd.setTarget(player);
      CMILC.sendMessage(player, cmi.getLM().updateSnd(snd, string));
    }

    return true;
  }
}
