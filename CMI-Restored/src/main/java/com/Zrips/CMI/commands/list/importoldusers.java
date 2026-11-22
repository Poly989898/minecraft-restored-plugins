package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class importoldusers implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("starting", "{gcp}Starting import");
    configReader.get("feedback", "{gcp}Imported {gcs}[amount] {gcp}new users");
  }

  @CAnnotation(
      info =
          "Imports users from playerdata folder in main world folder. Server can suffer lag spike"
              + " during import",
      regVar = 0,
      consoleVar = 0)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    int value = 0;
    CMILC.info(this, commandSender, "starting");
    OfflinePlayer[] offlinePlayers;
    int userCount = (offlinePlayers = Bukkit.getOfflinePlayers()).length;

    for (int index = 0; index < userCount; index++) {
      OfflinePlayer offlinePlayer = offlinePlayers[index];
      if (!cmi.getPlayerManager().isUserExist(offlinePlayer.getUniqueId())) {
        CMIUser cmiUser = new CMIUser(offlinePlayer);
        if (cmi.getPlayerManager().checkForExistingSimilarUser(cmiUser, true)) {
          cmi.getPlayerManager().addUser(cmiUser);

          try {
            cmiUser.updatePermissions();
          } catch (Exception | Error value4) {
            value4.printStackTrace();
          }

          cmiUser.addForDelayedSave();
          value++;
        }
      }
    }

    cmi.getDbManager().startRapidSave();
    CMILC.info(this, commandSender, "feedback", "[amount]", value);
    return true;
  }
}
