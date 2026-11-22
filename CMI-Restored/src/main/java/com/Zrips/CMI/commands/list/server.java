package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.BungeeCord.BungeeCordServer;
import com.Zrips.CMI.Modules.BungeeCord.BungeePlayer;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.command.CommandSender;

public class server implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      priority = 48,
      info = "Connect to bungeecord server",
      args = "[serverName] (playerName) (-f)",
      multiTab = "[bungeeserver] [playername] -f",
      explanation = {},
      regVar = {1, 2, 3},
      consoleVar = {2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (!cmi.getBungeeCordManager().isBungeeCord()) {
      LC.info_bungee_not.sendMessage(commandSender, new Object[0]);
      return null;
    }

    String string = null;
    String serverName = null;
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-f")) {
        flag = true;
      } else if (serverName == null) {
        serverName = arg;
      } else {
        string = arg;
      }
    }

    BungeeCordServer bungeeCordServer = cmi.getBungeeCordManager().getServer(serverName);
    serverName = bungeeCordServer == null ? serverName : bungeeCordServer.getName();
    if (bungeeCordServer == null && !flag) {
      LC.info_bungee_noserver.sendMessage(commandSender, new Object[0]);
      return null;
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    string = cmiUser.getName();
    BungeePlayer bungeePlayer = cmi.getBungeeCordManager().getBungeePlayer(string);
    if (commandSender.getName().equalsIgnoreCase(string)
        || (string == null || bungeePlayer != null || cmiUser.isOnline())
            && (bungeePlayer == null
                || !bungeePlayer.getVanished()
                || PermissionsManager.CMIPerm.seevanished.hasPermission(commandSender))) {
      if (cmiUser.isOnline()) {
        cmi.getBungeeCordManager().connectToServer(cmiUser.getPlayer(false), serverName);
      } else {
        cmi.getBungeeCordManager().connectOther(string, serverName);
      }

      cmi.getBungeeCordManager()
          .recordBackLocation(serverName, cmiUser.getUniqueId(), cmiUser.getLocation());
      return true;
    } else {
      LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }
}
