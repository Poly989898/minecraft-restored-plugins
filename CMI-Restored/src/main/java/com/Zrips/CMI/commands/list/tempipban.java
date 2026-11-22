package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIBanEntry;
import com.Zrips.CMI.Containers.CMIBanRecords;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.events.CMIIpBanEvent;
import com.Zrips.CMI.events.CMIPlayerBanEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.timeModifier;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tempipban implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("message", "{gcw}Temporarily IP Banned!");
    configReader.get(
        "messageToAll",
        "{gcs}[playerDisplayName] {gcp}got temp ip banned for {gcs}[time]{gcp}by"
            + " {gcs}[senderDisplayName]{gcp}: {gcs}[reason]");
    configReader.get(
        "banedSilent",
        "{gcp}[{gcu}Silent{gcp}]{gcs}[playerDisplayName] {gcp}got temp ip banned for"
            + " {gcs}[time]{gcp}: {gcs}[reason]");
  }

  @CAnnotation(
      priority = 48,
      info = "Temp ban ip",
      args = "[ip/playerName] [time] (reason) (-s)",
      tab = "playername",
      explanation = "cmi.command.banip.bypass - to bypass ban",
      regVar = -100,
      consoleVar = -100,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String reason = "";
    boolean flag = false;
    Long duration = null;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (string == null) {
        string = arg;
      } else if (arg.equalsIgnoreCase("-s")) {
        flag = true;
      } else {
        if (duration == null) {
          try {
            Long parsedTime = timeModifier.getTimeRangeFromString(arg);
            if (parsedTime != null) {
              duration = parsedTime;
            }
            continue;
          } catch (Exception exception) {
          }
        }

        if (!reason.isEmpty()) {
          reason = reason + " ";
        }

        reason = reason + arg;
      }
    }

    if (duration == null) {
      return false;
    }

    HashMap<String, Integer> hashMap = new HashMap();
    CMIUser cmiUser = null;
    String ipReason = "";
    if (strings.length >= 1 && cmi.getIpManager().ipIsOk(string)) {
      hashMap.put(string, 0);
      ipReason = string;
    } else {
      String target = null;
      if (strings.length >= 1) {
        target = string;
      }

      cmiUser = cmi.getUser(commandSender, target, this, false);
      if (cmiUser == null) {
        return true;
      }

      hashMap.put(cmiUser.getLastIp(), 1);
    }

    if (hashMap.isEmpty()) {
      LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (cmiUser != null && cmiUser.getName().equals(commandSender.getName())) {
      CMILC.info("ban", commandSender, "noSelfBan");
      return null;
    }

    if (cmiUser != null
        && cmiUser.hasPermission(PermissionsManager.CMIPerm.command_banip_bypass)
        && commandSender instanceof Player) {
      CMILC.info("ban", commandSender, "cantBan");
      return null;
    }

    if (reason.isEmpty()) {
      reason = CMILC.getIM(ban.class.getSimpleName(), "defaultReason");
    }

    for (Entry entry : hashMap.entrySet()) {
      if (entry.getKey() != null) {
        CMIBanRecords.addBanByIP(
            (String) entry.getKey(),
            reason,
            new Date(System.currentTimeMillis() + duration * 1000L),
            commandSender.getName());
      }
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
    if (cmiUser == null) {
      snd.setTargetName(ipReason);
    }

    if (cmiUser != null) {
      cmiUser.setBanned(
          reason, commandSender, new Date(System.currentTimeMillis() + duration * 1000L));
      CMIPlayerBanEvent cmiPlayerBanEvent =
          new CMIPlayerBanEvent(
              commandSender,
              cmiUser.getUniqueId(),
              reason,
              System.currentTimeMillis() + duration * 1000L);
      Bukkit.getServer().getPluginManager().callEvent(cmiPlayerBanEvent);
    } else {
      for (Player player : Bukkit.getOnlinePlayers()) {
        String playerIp = player.getAddress().getAddress().getHostAddress();
        if (playerIp.equalsIgnoreCase(ipReason)
            && !PermissionsManager.CMIPerm.command_banip_bypass.hasPermission(player)) {
          var banEntry = Bukkit.getBanList(Type.IP).getBanEntry(playerIp);
          String banMessage = CMIBanEntry.getIpBanMessage(banEntry);
          if (!banMessage.isEmpty()) {
            player.kickPlayer(banMessage);
          } else {
            player.kickPlayer(reason.replace("\\n", "\n"));
          }
        }
      }

      CMIIpBanEvent cmiIpBanEvent =
          new CMIIpBanEvent(
              commandSender, ipReason, reason, System.currentTimeMillis() + duration * 1000L);
      Bukkit.getServer().getPluginManager().callEvent(cmiIpBanEvent);
    }

    if (!flag) {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        if (!onlinePlayer.getName().equalsIgnoreCase(commandSender.getName())) {
          CMILC.info(
              this,
              onlinePlayer,
              "messageToAll",
              snd,
              "[reason]",
              reason,
              "[ip]",
              ipReason,
              "[time]",
              CMITimeManager.to24hourShort(duration * 1000L));
        }
      }

      CMILC.info(
          this,
          commandSender,
          "messageToAll",
          snd,
          "[reason]",
          reason,
          "[ip]",
          ipReason,
          "[time]",
          CMITimeManager.to24hourShort(duration * 1000L));
    } else {
      CMILC.info(
          this,
          commandSender,
          "banedSilent",
          snd,
          "[reason]",
          reason,
          "[ip]",
          ipReason,
          "[time]",
          CMITimeManager.to24hourShort(duration * 1000L));
    }

    return true;
  }
}
