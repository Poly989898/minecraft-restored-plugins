package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ifonline implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Perform command only when player is online",
      args = "[playerName] (command)",
      tab = "playername",
      regVar = {-100, -1},
      consoleVar = {-100, -1})
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (strings[0].isEmpty()) {
      return null;
    }

    Player player = CMIUser.getOnlinePlayer(strings[0]);
    if (player == null) {
      return null;
    }

    String string = "";
    if (strings.length > 1) {
      string = "";

      for (int value = 1; value < strings.length; value++) {
        if (!string.isEmpty()) {
          string = string + " ";
        }

        string = string + strings[value];
      }
    }

    Bukkit.dispatchCommand(commandSender, string);
    return true;
  }
}
