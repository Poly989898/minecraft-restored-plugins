/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class feed implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("feedback", "{gcp}You have fed {gcs}[playerDisplayName]{gcp}.");
    configReader.get("feedbackMulti", "{gcp}You have fed {gcs}[count] {gcp}players.");
    configReader.get("targetfeedback", "{gcp}You have been fed by {gcs}[senderDisplayName]{gcp}.");
  }

  @Override
  @CAnnotation(
      info = "Feed player",
      args = "(playerName/all) (-s)",
      tab = {"playername%%all", "-s%%"},
      regVar = {0, 1, 2},
      consoleVar = {1, 2},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    String arg;
    boolean bl = false;
    String playerName = null;
    ArrayList<Player> arrayList = new ArrayList<Player>();
    String[] args = stringArray;
    int n = stringArray.length;
    int argIndex = 0;
    while (argIndex < n) {
      arg = args[argIndex];
      switch (arg.toLowerCase()) {
        case "-s":
          {
            if (!PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) break;
            bl = true;
            break;
          }
        default:
          {
            playerName = arg;
          }
      }
      ++argIndex;
    }
    if (playerName != null
        && (playerName.equalsIgnoreCase("*") || playerName.equalsIgnoreCase("all"))) {
      if (!PermissionsManager.CMIPerm.command_feed_all.hasPermission(
          commandSender, true, new String[0])) {
        return null;
      }
      for (Player online : Bukkit.getOnlinePlayers()) {
        if (!(commandSender instanceof Player) || !((Player) commandSender).canSee(online))
          continue;
        arrayList.add(online);
      }
    } else {
      Player target = cMI.getTarget(commandSender, playerName, this);
      if (target == null) {
        return null;
      }
      arrayList.add(target);
    }
    if (arrayList.isEmpty()) {
      return null;
    }
    for (Player player : arrayList) {
      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      if (!bl && arrayList.size() == 1) {
        CMILC.info((Object) this, commandSender, "feedback", snd);
      }
      if (!player.getName().equalsIgnoreCase(commandSender.getName()) && !bl) {
        CMILC.info((Object) this, (CommandSender) player, "targetfeedback", snd);
      }
      if (player.getSaturation() < 10.0f) {
        player.setSaturation(10.0f);
      }
      player.setFoodLevel(20);
      cMI.save(player);
    }
    if (!bl && arrayList.size() > 1) {
      CMILC.info((Object) this, commandSender, "feedbackMulti", "[count]", arrayList.size());
    }
    return true;
  }
}
