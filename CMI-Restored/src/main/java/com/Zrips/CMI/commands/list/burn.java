package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.timeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class burn implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "feedback", "{gcp}You have put on fire {gcs}[playerDisplayName]{gcp} for {gcs}[time].");
    configReader.get(
        "targetfeedback",
        "{gcp}You have been put on fire by {gcs}[senderDisplayName]{gcp} for {gcs}[time]&e.");
  }

  @CAnnotation(
      info = "Burn a player",
      args = "(playerName) (time) (-s)",
      tab = "playername",
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    String string = null;
    Long durationMillis = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
        flag = true;
      } else {
        try {
          durationMillis = timeModifier.getTimeRangeFromString(arg);
          if (durationMillis != null) {
            continue;
          }
        } catch (Exception exception) {
        }

        string = arg;
      }
    }

    if (durationMillis == null) {
      durationMillis = 3000L;
    } else {
      durationMillis = durationMillis * 1000L;
    }

    if (durationMillis > 2592000000L) {
      durationMillis = 2592000000L;
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    player.setFireTicks((int) (durationMillis / 50L));
    cmi.save(player);
    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    CMILC.info(
        this,
        commandSender,
        "feedback",
        snd,
        "[time]",
        CMITimeManager.to24hourShort(durationMillis));
    if (!player.getName().equalsIgnoreCase(commandSender.getName()) && !flag) {
      CMILC.info(
          this,
          player,
          "targetfeedback",
          snd,
          "[time]",
          CMITimeManager.to24hourShort(durationMillis));
    }

    return true;
  }
}
