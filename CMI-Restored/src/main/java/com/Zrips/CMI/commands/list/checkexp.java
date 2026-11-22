package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.command.CommandSender;

public class checkexp implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "current",
        "{gcp}Level: {gcs}[currentLevel] {gcp}Exp: {gcs}[levelExp] {gcp}Total exp: {gcs}[exp]");
    configReader.get("need", "{gcp}Need exp until next level: {gcs}[missingExp]");
  }

  @CAnnotation(
      info = "Check players exp",
      args = "(playerName)",
      tab = "playername",
      regVar = {0, 1},
      consoleVar = 1,
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
    LC.info_PlayerSpliter.sendMessage(commandSender, new Object[] {snd});
    CMILC.info(
        this,
        commandSender,
        "current",
        "[currentLevel]",
        cmiUser.getLevel(),
        "[levelExp]",
        cmiUser.getExpForCurrentLevel(),
        "[exp]",
        cmiUser.getTotalExp());
    CMILC.info(this, commandSender, "need", "[missingExp]", cmiUser.getMissingExp());
    CMIMessages.sendMessage(
        commandSender,
        cmi.getUtilManager()
            .createTextProbgressBar(
                200,
                (float) (cmiUser.getExpForCurrentLevel() + cmiUser.getMissingExp()),
                (float) cmiUser.getExpForCurrentLevel()));
    return true;
  }
}
