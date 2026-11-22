package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class oplist implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}* {gcs}[number]{gcp}. {gcs}[playerName]");
  }

  @CAnnotation(info = "Check operator player list", regVar = 0, consoleVar = 0, others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    int value = 0;
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);

    for (OfflinePlayer offlinePlayer : Bukkit.getOperators()) {
      CMILC.info(
          this,
          commandSender,
          "feedback",
          "[number]",
          ++value,
          "[playerName]",
          offlinePlayer.getName());
    }

    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    return true;
  }
}
