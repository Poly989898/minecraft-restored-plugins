package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.events.CMIPlayerKickEvent;
import java.util.ArrayList;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class kick implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("defaultMessage", "{gcp}Kicked from server");
    configReader.get("cantKick", "{gcp}Can't kick this player");
    configReader.get("messageToAll", "{gcs}[playerDisplayName] {gcp}got kicked from server.");
    configReader.get("kickReason", " {gcp}Reason: {gcs}[reason]");
    configReader.get("all", "{gcp}Kicked {gcs}[amount] {gcp}players from server");
  }

  @CAnnotation(
      info = "Kick player with custom message",
      args = "[playerName/all] (message) (-s)",
      tab = "playername%%all",
      explanation = "cmi.command.kick.bypass - prevents from being kicked out",
      regVar = -100,
      consoleVar = -100,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    ArrayList<Player> arrayList = new ArrayList();
    boolean flag = false;
    if (strings[0].equalsIgnoreCase("all")) {
      flag = true;

      for (Player player : Bukkit.getOnlinePlayers()) {
        if ((!(commandSender instanceof Player)
                || !PermissionsManager.CMIPerm.command_kick_bypass.hasPermission(player))
            && !commandSender.getName().equals(player.getName())) {
          arrayList.add(player);
        }
      }
    } else {
      Player playerSecond = CMIUser.getOnlinePlayer(strings[0]);
      if (playerSecond == null) {
        LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
        return null;
      }

      if (PermissionsManager.CMIPerm.command_kick_bypass.hasPermission(playerSecond)
          && commandSender instanceof Player) {
        CMILC.info(this, commandSender, "cantKick");
        return null;
      }

      arrayList.add(playerSecond);
    }

    if (arrayList.isEmpty() && !flag) {
      LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (arrayList.isEmpty() && flag) {
      LC.info_NobodyOnline.sendMessage(commandSender, new Object[0]);
      return null;
    }

    boolean flagSecond = false;
    String string = CMILC.getIM(this, "defaultMessage");
    String text = "";
    if (strings.length > 1) {
      for (int value = 1; value < strings.length; value++) {
        if (strings[value].equalsIgnoreCase("-s")) {
          flagSecond = true;
        } else {
          text = text + strings[value];
          if (value < strings.length) {
            text = text + " ";
          }
        }
      }

      if (!text.isEmpty()) {
        string = text;
      }
    }

    if (string.endsWith(" ")) {
      string = string.substring(0, string.length() - 1);
    }

    string = CMIChatColor.translate(string);
    if (arrayList.size() == 1) {
      Player playerThird = (Player) arrayList.get(0);
      Snd snd = new Snd().setSender(commandSender).setTarget(playerThird);
      string = cmi.getLM().updateSnd(snd, string.replace("\\n", "\n"));
      CMIPlayerKickEvent cmiPlayerKickEvent =
          new CMIPlayerKickEvent(commandSender, playerThird.getUniqueId(), string);
      Bukkit.getPluginManager().callEvent(cmiPlayerKickEvent);
      if (cmiPlayerKickEvent.isCancelled()) {
        return null;
      }

      playerThird.kickPlayer(string);
      if (!flagSecond) {
        for (Player playerFourth : Bukkit.getOnlinePlayers()) {
          String textSecond = CMILC.getIM(this, "messageToAll", snd);
          textSecond =
              textSecond
                  + (text.isEmpty() ? "" : CMILC.getIM(this, "kickReason", "[reason]", string));
          CMILC.sendMessage(playerFourth, textSecond);
        }
      }
    } else {
      int count = 0;

      for (Player playerFifth : arrayList) {
        Snd sndSecond = new Snd().setSender(commandSender).setTarget(playerFifth);
        String textThird = cmi.getLM().updateSnd(sndSecond, string.replace("\\n", "\n"));
        CMIPlayerKickEvent cmiPlayerKickEventValue =
            new CMIPlayerKickEvent(commandSender, playerFifth.getUniqueId(), textThird);
        Bukkit.getPluginManager().callEvent(cmiPlayerKickEventValue);
        if (!cmiPlayerKickEventValue.isCancelled()) {
          count++;
          playerFifth.kickPlayer(textThird.replace("\\n", "\n"));
        }
      }

      if (count > 0) {
        CMILC.info(this, commandSender, "all", "[amount]", arrayList.size());
      }
    }

    return true;
  }
}
