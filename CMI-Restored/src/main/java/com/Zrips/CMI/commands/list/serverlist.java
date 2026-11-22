package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.BungeeCord.BungeeCordServer;
import com.Zrips.CMI.Modules.BungeeCord.BungeePlayer;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class serverlist implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("listOnline", "{gcs}[place]. {gcu}[serverName] {gcs}[count]{gcp}/{gcs}[max]");
    configReader.get("listOffline", "{gcs}[place]. {gcd}[serverName] {gcs}0{gcp}/{gcs}0");
    configReader.get("consoleInfo", " {gcp}({gcs}[ip]{gcp}:{gcs}[port]{gcp})");
  }

  @CAnnotation(
      info = "Show server list",
      args = "",
      tab = {},
      explanation = {},
      regVar = -666,
      consoleVar = -666,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    int value = 0;
    if (!cmi.getBungeeCordManager().isEnabledSupport()) {
      LC.info_FeatureNotEnabled.sendMessage(commandSender, new Object[0]);
      return null;
    }

    LC.info_Spliter.sendMessage(commandSender, new Object[0]);

    for (var entry : cmi.getBungeeCordManager().getServers().entrySet()) {
      value++;
      ((BungeeCordServer) entry.getValue()).update();
      StringBuilder stringBuilder = new StringBuilder();
      int hiddenCount = 0;
      int batchCount = 0;

      for (var playerEntry : ((BungeeCordServer) entry.getValue()).getPlayersMapName().entrySet()) {
        if (!stringBuilder.toString().isEmpty()) {
          stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
        }

        if (((BungeePlayer) playerEntry.getValue()).getVanished()
            && !PermissionsManager.CMIPerm.seevanished.hasPermission(commandSender)) {
          hiddenCount++;
        } else {
          if (++batchCount > 10) {
            stringBuilder.append(" \n");
            batchCount = 0;
          }

          stringBuilder.append(
              (((BungeePlayer) playerEntry.getValue()).getVanished() ? "&7" : "&2")
                  + ((BungeePlayer) playerEntry.getValue()).getPlayerDisplayName());
        }
      }

      if (((BungeeCordServer) entry.getValue()).isOnline()) {
        RawMessage rawMessage = new RawMessage();
        rawMessage
            .addText(
                CMILC.getIM(
                    this,
                    "listOnline",
                    "[place]",
                    value,
                    "[serverName]",
                    ((BungeeCordServer) entry.getValue()).getName(),
                    "[count]",
                    ((BungeeCordServer) entry.getValue()).getPlayersMapName().size() - hiddenCount,
                    "[max]",
                    ((BungeeCordServer) entry.getValue()).getMaxPlayers()))
            .addHover(stringBuilder.toString());
        if (commandSender instanceof ConsoleCommandSender) {
          rawMessage.addText(
              CMILC.getIM(
                  this,
                  "consoleInfo",
                  "[ip]",
                  ((BungeeCordServer) entry.getValue()).getIp(),
                  "[port]",
                  ((BungeeCordServer) entry.getValue()).getPort()));
        }

        rawMessage.show(commandSender);
      } else {
        RawMessage listMessage = new RawMessage();
        listMessage.addText(
            CMILC.getIM(
                this,
                "listOffline",
                "[place]",
                value,
                "[serverName]",
                ((BungeeCordServer) entry.getValue()).getName(),
                "[count]",
                0,
                "[max]",
                ((BungeeCordServer) entry.getValue()).getMaxPlayers()));
        if (commandSender instanceof ConsoleCommandSender) {
          listMessage.addText(
              CMILC.getIM(
                  this,
                  "consoleInfo",
                  "[ip]",
                  ((BungeeCordServer) entry.getValue()).getIp(),
                  "[port]",
                  ((BungeeCordServer) entry.getValue()).getPort()));
        }

        listMessage.show(commandSender);
      }
    }

    return true;
  }
}
