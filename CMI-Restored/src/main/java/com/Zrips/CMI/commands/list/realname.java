package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;

public class realname implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcs}[playerDisplayName] {gcp}real name {gcs}[playerName]");
  }

  @CAnnotation(
      info = "Check players real name",
      args = "(playerName/nickName)",
      tab = "nickNames",
      explanation = {},
      regVar = {0, 1},
      consoleVar = 1,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String[] commandArgs = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = commandArgs[argIndex];
      string = arg;
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this, false);
    if (cmiUser == null) {
      return null;
    }

    Snd snd = new Snd();
    snd.setTarget(cmiUser);
    RawMessage rawMessage = new RawMessage();
    rawMessage.addText(CMILC.getIM(this, "feedback", snd));
    rawMessage.addSuggestion(cmiUser.getName());
    rawMessage.addInsertion(cmiUser.getDisplayName());
    rawMessage.show(commandSender);
    return true;
  }
}
