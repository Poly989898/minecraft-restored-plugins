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
import java.util.HashMap;
import java.util.Map.Entry;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ipban implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("message", "{gcw}You are ip banned from this server!");
    configReader.get(
        "messageToAll",
        "{gcs}[playerDisplayName] {gcp}got ip banned by {gcs}[senderDisplayName] {gcp}for:"
            + " {gcs}[reason]");
    configReader.get(
        "banedSilent",
        "{gcn}[{gcu}Silent{gcn}]{gcs}[playerDisplayName] {gcp}got ip banned by"
            + " {gcs}[senderDisplayName] {gcp}for: {gcs}[reason]");
    configReader.get("replacer", "*");
  }

  @CAnnotation(
      info = "Ban ip",
      args = "[ip/playerName] (reason) (-s)",
      tab = "playername",
      explanation = "cmi.command.banip.bypass - to bypass ban",
      regVar = -100,
      consoleVar = -100)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String ip = "";
    boolean flag = false;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (string == null) {
        string = arg;
      } else if (arg.equalsIgnoreCase("-s")) {
        flag = true;
      } else {
        if (!ip.isEmpty()) {
          ip = ip + " ";
        }

        ip = ip + arg;
      }
    }

    HashMap<String, Integer> hashMap = new HashMap();
    CMIUser cmiUser = null;
    String reason = "";
    if (strings.length >= 1 && cmi.getIpManager().ipIsOk(string)) {
      hashMap.put(string, 0);
      reason = string;
    } else {
      String target = null;
      if (strings.length >= 1) {
        target = string;
      }

      cmiUser = cmi.getUser(commandSender, target, this, false);
      if (cmiUser == null) {
        return null;
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

    if (ip.isEmpty()) {
      ip = CMILC.getIM(ban.class.getSimpleName(), "defaultReason");
    }

    for (Entry entry : hashMap.entrySet()) {
      if (entry.getKey() != null) {
        CMIBanRecords.addBanByIP((String) entry.getKey(), ip, null, commandSender.getName());
      }
    }

    if (cmiUser != null) {
      cmiUser.setBanned(ip, commandSender, null);
      CMIPlayerBanEvent cmiPlayerBanEvent =
          new CMIPlayerBanEvent(commandSender, cmiUser.getUniqueId(), ip, null);
      Bukkit.getServer().getPluginManager().callEvent(cmiPlayerBanEvent);
    } else {
      CMIIpBanEvent cmiIpBanEvent = new CMIIpBanEvent(commandSender, reason, ip, null);
      Bukkit.getServer().getPluginManager().callEvent(cmiIpBanEvent);

      for (Player player : Bukkit.getOnlinePlayers()) {
        String playerIp = player.getAddress().getAddress().getHostAddress();
        if (playerIp.equalsIgnoreCase(reason)
            && !PermissionsManager.CMIPerm.command_banip_bypass.hasPermission(player)) {
          BanEntry banEntry = CMIBanRecords.getBanEntryByIP(playerIp);
          String banMessage = CMIBanEntry.getIpBanMessage(banEntry);
          if (!banMessage.isEmpty()) {
            player.kickPlayer(banMessage);
          } else {
            player.kickPlayer(ip.replace("\\n", "\n"));
          }
        }
      }
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
    if (reason != null && reason.contains(".")) {
      StringBuilder stringBuilder = new StringBuilder();
      int octetCount = 0;
      String[] octets;
      int octetTotal = (octets = reason.split("\\.")).length;

      for (int octetIndex = 0; octetIndex < octetTotal; octetIndex++) {
        String octet = octets[octetIndex];
        octetCount++;
        if (!stringBuilder.toString().isEmpty()) {
          stringBuilder.append(".");
        }

        if (octetCount < 3) {
          stringBuilder.append(octet);
        } else {
          for (int charIndex = 0; charIndex < octet.length(); charIndex++) {
            stringBuilder.append(CMILC.getIM(this, "replacer"));
          }
        }
      }

      reason = stringBuilder.toString();
    }

    if (cmiUser == null) {
      snd.setTargetName(reason);
    }

    if (!flag) {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        if (!onlinePlayer.getName().equalsIgnoreCase(commandSender.getName())) {
          CMILC.info(this, onlinePlayer, "messageToAll", snd, "[reason]", ip, "[ip]", reason);
        }
      }

      CMILC.info(this, commandSender, "messageToAll", snd, "[reason]", ip, "[ip]", reason);
    } else {
      CMILC.info(this, commandSender, "banedSilent", snd, "[reason]", ip, "[ip]", reason);
    }

    return true;
  }
}
