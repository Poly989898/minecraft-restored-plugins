package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.events.CMIPlayerBanEvent;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ban implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.addComment(
        "message", new String[] {"Make sure that OverrideLoginMessage is enabled in config file"});
    configReader.get("message", "{gcw}You are banned from this server!");
    configReader.get("reason", "{gcp}Reason: {gcs}[reason]");
    configReader.get("defaultReason", "{gcp}Violated server rules");
    configReader.get(
        "messageToAll",
        "{gcs}[playerDisplayName] {gcp}got banned by {gcs}[senderDisplayName] {gcp}for:"
            + " {gcs}[reason]");
    configReader.get(
        "banedSilent",
        "{gcp}[{gcn}Silent{gcp}]{gcs}[playerDisplayName] {gcp}got banned by"
            + " {gcs}[senderDisplayName] {gcp}for: {gcs}[reason]");
    configReader.get("noSelfBan", "{gcw}You can't ban yourself");
    configReader.get("cantBan", "{gcw}You can't ban this player");
  }

  @CAnnotation(
      priority = 48,
      info = "Ban player",
      args = "[playerName] (reason) (-s)",
      tab = "playername",
      explanation = "cmi.command.ban.bypass - to bypass ban",
      regVar = -100,
      consoleVar = -100,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    StringBuilder stringBuilder = new StringBuilder();
    boolean flag = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (string == null) {
        string = arg;
      } else if (arg.equalsIgnoreCase("-s")) {
        flag = true;
      } else {
        if (!stringBuilder.toString().isEmpty()) {
          stringBuilder.append(" ");
        }

        stringBuilder.append(arg);
      }
    }

    CMIUser cmiUser = cmi.getPlayerManager().getUser(string);
    boolean silent = flag;
    if (cmiUser == null) {
      CMIUser.getOfflinePlayer(string)
          .thenAccept(
              offlinePlayer -> {
                CMIUser offlineUser = cmi.getPlayerManager().getUser(offlinePlayer);
                if (offlineUser == null) {
                  LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
                } else {
                  this.privateBan(commandSender, offlineUser, stringBuilder.toString(), silent);
                }
              });
    } else {
      this.privateBan(commandSender, cmiUser, stringBuilder.toString(), silent);
    }

    return true;
  }

  private void privateBan(
      CommandSender commandSender, CMIUser cmiUser, String string, boolean flag) {
    if (cmiUser.getName().equals(commandSender.getName())) {
      CMILC.info(this, commandSender, "noSelfBan");
    } else if (cmiUser.hasPermission(PermissionsManager.CMIPerm.command_ban_bypass)
        && commandSender instanceof Player) {
      CMILC.info(this, commandSender, "cantBan");
    } else {
      CMIScheduler.runTask(
          CMI.getInstance(),
          () -> {
            cmiUser.setBanned(string, commandSender, null);
            String banReason = cmiUser.getBanEntry().getReason();
            CMIPlayerBanEvent cmiPlayerBanEvent =
                new CMIPlayerBanEvent(commandSender, cmiUser.getUniqueId(), banReason, null);
            Bukkit.getServer().getPluginManager().callEvent(cmiPlayerBanEvent);
            Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
            if (!flag) {
              for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getName().equalsIgnoreCase(commandSender.getName())) {
                  CMILC.info(ban.class, player, "messageToAll", snd, "[reason]", banReason);
                }
              }

              CMILC.info(ban.class, commandSender, "messageToAll", snd, "[reason]", banReason);
            } else {
              CMILC.info(ban.class, commandSender, "banedSilent", snd, "[reason]", banReason);
            }
          });
    }
  }
}
