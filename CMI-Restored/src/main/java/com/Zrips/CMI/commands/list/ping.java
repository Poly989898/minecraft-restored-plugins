package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.CustomText.CTextManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ping implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcs}[playerName] {gcp}ping: {gcs}[ping]ms");
    configReader.get("selffeedback", "{gcp}Your ping is {gcs}[ping]ms");
  }

  @CAnnotation(
      priority = 255,
      info = "Shows players ping",
      args = "(playerName/message)",
      tab = "playername",
      explanation = {},
      regVar = -666,
      consoleVar = -100,
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String playerNameInput = "";
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (playerNameInput.isEmpty()) {
        CMIUser cmiUser = cmi.getPlayerManager().getUser(arg);
        if (cmiUser != null && strings.length == 1) {
          string = cmiUser.getName();
          break;
        }
      }

      if (!playerNameInput.isEmpty()) {
        playerNameInput = playerNameInput + " ";
      }

      playerNameInput = playerNameInput + arg;
    }

    CMIUser targetUser =
        cmi.getUser(commandSender, string, this, commandSender instanceof Player, true);
    if (targetUser == null && commandSender instanceof Player) {
      return null;
    }

    if (targetUser != null && !targetUser.isOnline()) {
      LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
      return null;
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(targetUser);
    if (playerNameInput.isEmpty() && targetUser != null) {
      if (targetUser.getName().equals(commandSender.getName())) {
        CMILC.info(
            this,
            commandSender,
            "selffeedback",
            "[ping]",
            cmi.getReflectionManager().getPing(targetUser.getPlayer(false)),
            snd);
      } else {
        CMILC.info(
            this,
            commandSender,
            "feedback",
            "[ping]",
            cmi.getReflectionManager().getPing(targetUser.getPlayer(false)),
            snd);
      }
    } else {
      playerNameInput = CTextManager.clean(playerNameInput.replace("%", ""));
      commandSender.sendMessage(CMIChatColor.translate(playerNameInput));
    }

    return true;
  }
}
