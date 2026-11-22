package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class balance implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("balance", "{gcp}Balance: {gcs}[money]");
    configReader.get("balanceOther", "{gcs}[playerDisplayName] {gcp}balance: {gcs}[money]");
  }

  @CAnnotation(
      priority = 48,
      info = "Check money balance",
      args = "(playerName)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1},
      consoleVar = 1,
      customAlias = "bal",
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

    Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
    if (commandSender.getName().equalsIgnoreCase(cmiUser.getName())) {
      CMILC.info(this, commandSender, "balance", "[money]", cmiUser.getFormatedBalance());
    } else {
      CMILC.info(this, commandSender, "balanceOther", "[money]", cmiUser.getFormatedBalance(), snd);
    }

    return true;
  }
}
