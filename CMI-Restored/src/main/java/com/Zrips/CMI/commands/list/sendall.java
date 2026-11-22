package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.BungeeCord.BungeeCordServer;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class sendall implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      priority = 48,
      info = "Send all online players to target server",
      args = "[serverName]",
      tab = "bungeeserver",
      explanation = {},
      regVar = 1,
      consoleVar = 1,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (!cmi.getBungeeCordManager().isBungeeCord()) {
      LC.info_bungee_not.sendMessage(commandSender, new Object[0]);
      return null;
    }

    String string = null;
    String[] commandArgs = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = commandArgs[argIndex];
      if (string == null) {
        string = arg;
      }
    }

    BungeeCordServer bungeeCordServer = cmi.getBungeeCordManager().getServer(string);
    if (bungeeCordServer == null) {
      LC.info_bungee_noserver.sendMessage(commandSender, new Object[0]);
      return null;
    }

    for (Player player : Bukkit.getOnlinePlayers()) {
      CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
      if (cmiUser != null
          && !cmiUser.isSame(commandSender)
          && !PermissionsManager.CMIPerm.command_sendall_bypass.hasPermission(player)) {
        cmi.getBungeeCordManager().connectToServer(player, bungeeCordServer.getName());
      }
    }

    return true;
  }
}
