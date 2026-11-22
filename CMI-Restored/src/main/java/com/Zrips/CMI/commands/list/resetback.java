package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class resetback implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}Back location got a reset!");
  }

  @CAnnotation(
      info = "Resets players back location",
      args = "(playerName) (reason) (-death) (-s)",
      tab = {"playername", "-s%%-death"},
      regVar = -666,
      consoleVar = -100,
      customAlias = "rback",
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    boolean flag = false;
    boolean deathOnly = false;
    StringBuilder stringBuilder = new StringBuilder();
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        flag = true;
      } else if (arg.equalsIgnoreCase("-death")) {
        deathOnly = true;
      } else if (string == null && cmi.getPlayerManager().getUser(arg) != null) {
        string = arg;
      } else {
        if (!stringBuilder.toString().isEmpty()) {
          stringBuilder.append(" ");
        }

        stringBuilder.append(arg);
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    cmiUser.setLastTeleportLocation(null);
    cmiUser.setBungeeBackLocation(null);
    if (deathOnly) {
      cmiUser.setDeathLoc(null);
    }

    if (!flag) {
      if (stringBuilder.toString().isEmpty()) {
        CMILC.info(this, cmiUser, "feedback");
      } else {
        CMILC.sendMessage(cmiUser, stringBuilder.toString());
      }
    }

    return true;
  }
}
