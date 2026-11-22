package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class unmutechat implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("unmuted", "{gcp}Unmuted public chat");
  }

  @CAnnotation(
      info = "Unmute public chat",
      args = "(-s)",
      tab = {},
      regVar = {0, 1},
      consoleVar = {0, 1},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    String[] commandArgs = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = commandArgs[argIndex];
      if (string.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender, false)) {
        flag = true;
      }
    }

    cmi.getChatFormatManager().setChatMutedUntil(null);
    if (!flag) {
      CMILC.broadcastMessage(CMILC.getIM(this, "unmuted"));
    }

    return true;
  }
}
