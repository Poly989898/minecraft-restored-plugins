package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.PlayTime.CMIPlayTimeFormat;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.DateFormat;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;

public class playtime implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("total", "{gcp}* Total play time: {gcs}[time]");
    configReader.get("joined", "{gcp}* Joined on: {gcs}[time]");
  }

  @CAnnotation(
      priority = 110,
      info = "Shows player total play time",
      args = "(playername)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1},
      consoleVar = 1,
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String[] commandArgs = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = commandArgs[argIndex];
      string = arg;
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    long longValue = cmiUser.getTotalPlayTime(true);
    if (cmiUser.getPlayer() == null) {
      return false;
    }

    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    CMILC.info(
        this,
        commandSender,
        "total",
        "[time]",
        CMIPlayTimeFormat.formatPlaytimePlaceholder(longValue));
    CMILC.info(
        this,
        commandSender,
        "joined",
        "[time]",
        DateFormat.MiliToDate(cmiUser.getPlayer().getFirstPlayed()));
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    return true;
  }
}
