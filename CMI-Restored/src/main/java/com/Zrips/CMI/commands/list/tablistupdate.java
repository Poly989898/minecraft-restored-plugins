package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tablistupdate implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("updated", "{gcp}Tablist updated");
  }

  @CAnnotation(
      priority = 48,
      info = "Force tablist update for all or specific player",
      args = "(playerName) (-s)",
      tab = {"playerName", "-s"},
      regVar = {0, 1, 2},
      consoleVar = {0, 1, 2},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          flag = true;
        }
      } else {
        string = arg;
      }
    }

    if (string != null) {
      Player player = CMIUser.getOnlinePlayer(string);
      if (player == null) {
        LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
        return null;
      }

      CMIUser.getUser(player).updateDisplayName();
      cmi.getTabListManager().updateTabList(player);
    } else {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        CMIUser.getUser(onlinePlayer).updateDisplayName();
      }

      cmi.getTabListManager().updateTabList();
    }

    if (!flag) {
      CMILC.info(this, commandSender, "updated");
    }

    return true;
  }
}
