package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.BungeeCord.BungeeCordServer;
import com.Zrips.CMI.Modules.CustomText.CTextManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.Arrays;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class bbroadcast implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("format", "{gcp}[{gcw}Broadcast{gcp}] {gcn}[message]");
  }

  @CAnnotation(
      priority = 48,
      info = "Sends special message to all players on all servers",
      args = "(!) [message] (-s:[serverName,serverName])",
      explanation = {
        "If message starts with ! then clean message will be shown",
        "Servers can be defined to broadcast messages only in those servers"
      },
      regVar = -100,
      consoleVar = -100,
      ignoreHelpPage = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = "";
    ArrayList<BungeeCordServer> arrayList = new ArrayList();
    if (commandSender instanceof Player) {
      CMIUser cmiUser = CMIUser.getUser((Player) commandSender);
      if (cmiUser.isMuted()
          && Config.MuteNoPm
          && !PermissionsManager.CMIPerm.command_mute_bypass.hasPermission(commandSender)) {
        CMILC.info(
            "mute",
            commandSender,
            "muted",
            "[time]",
            CMITimeManager.to24hourShort(cmiUser.getMutedUntil() - System.currentTimeMillis())
                + (cmiUser.getMutedReason() == null
                    ? ""
                    : CMILC.getIM("mute", "reason", "[reason]", cmiUser.getMutedReason())));
        return null;
      }
    }

    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.startsWith("-s:")) {
        arg = arg.substring("-s:".length());
        ArrayList<String> serverNames = new ArrayList();
        if (arg.contains(",")) {
          serverNames.addAll(Arrays.asList(arg.split(",")));
        } else {
          serverNames.add(arg);
        }

        for (String serverName : serverNames) {
          BungeeCordServer bungeeCordServer = cmi.getBungeeCordManager().getServer(serverName);
          if (bungeeCordServer != null) {
            arrayList.add(bungeeCordServer);
          }
        }
      } else {
        if (!string.isEmpty()) {
          string = string + " ";
        }

        string = string + arg;
      }
    }

    if (PermissionsManager.CMIPerm.command_broadcast_colors.hasPermission(commandSender)) {
      string = CMIChatColor.translate(string);
    }

    boolean flag = false;
    if (string.startsWith("!")) {
      string = string.substring(1, string.length());
      flag = true;
    }

    if (arrayList.isEmpty()) {
      arrayList.addAll(cmi.getBungeeCordManager().getServers().values());
    }

    string = CTextManager.clean(string);

    for (BungeeCordServer onlineServer : arrayList) {
      if (onlineServer.getCurrentPlayers() > 0) {
        cmi.getBungeeCordManager()
            .sendBroadcastMessage(
                commandSender.getName(), onlineServer, (flag ? "!" : "") + string);
      }
    }

    return true;
  }
}
