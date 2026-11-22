package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;

public class switchplayerdata implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("same", "{gcp}Provided target and source users are same already");
    configReader.get("transferred", "{gcs}[sourceName] {gcp}data got switched with {gcs}[Name]");
    configReader.get("failed", "{gcw}Failed to switch player data");
  }

  @CAnnotation(
      priority = 48,
      info = "Switch all data from one player to another",
      args = "[sourcePlayerName/uuid] [targetPlayerName/uuid]",
      explanation =
          "Use UUID for more accurate transfers, especially when usernames matching each other",
      tab = "playername",
      regVar = 2,
      consoleVar = 2,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMIUser cmiUser = cmi.getPlayerManager().getUser(strings[0]);
    if (cmiUser == null) {
      LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
      return null;
    } else {
      CMIUser targetUser = cmi.getPlayerManager().getUser(strings[1]);
      if (targetUser == null) {
        LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
        return null;
      } else {
        Snd snd = new Snd();
        snd.setSource(cmiUser).setTarget(targetUser);
        if (!cmi.getPlayerManager().switchPlayerData(cmiUser, targetUser)) {
          CMILC.info(this, commandSender, "failed");
          return null;
        } else {
          CMILC.info(this, commandSender, "transferred", snd);
          return true;
        }
      }
    }
  }
}
