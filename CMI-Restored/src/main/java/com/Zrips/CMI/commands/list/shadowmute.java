package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.timeModifier;
import org.bukkit.command.CommandSender;

public class shadowmute implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      priority = 240,
      info = "Mute player without telling him that he is muted",
      args = "[playerName] (time) (-s) (reason)",
      tab = {"playername", "1m%%", "-s%%"},
      regVar = -100,
      consoleVar = -100,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    long longValue = 3600L;
    String string = null;
    boolean flag = false;
    String reason = "";
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (string == null && cmi.getPlayerManager().getUserByAproxName(arg) != null) {
        string = arg;
      } else {
        if (strings.length > 0) {
          try {
            Long timeRange = timeModifier.getTimeRangeFromString(arg);
            if (timeRange != null) {
              longValue = timeRange;
              continue;
            }
          } catch (Exception exception) {
          }
        }

        if (arg.equalsIgnoreCase("-s")) {
          flag = true;
        } else if (string == null) {
          string = arg;
        } else {
          if (!reason.isEmpty()) {
            reason = reason + " ";
          }

          reason = reason + arg;
        }
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this, true, false);
    if (cmiUser == null) {
      return null;
    }

    if (commandSender.getName().equals(cmiUser.getName())) {
      return false;
    }

    longValue = longValue > 0L ? longValue : 0L;
    cmiUser.setShadowMutedUntil(System.currentTimeMillis() + longValue * 1000L);
    cmiUser.setMutedUntil(0L);
    if (!reason.isEmpty()) {
      cmiUser.setMutedReason(reason);
    }

    cmiUser.addForDelayedSave();
    Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
    if (!flag) {
      CMILC.sendMessage(
          commandSender,
          CMILC.getIM(
                  "mute",
                  "mutedFor",
                  "[time]",
                  CMITimeManager.to24hourShort(longValue * 1000L),
                  snd)
              + (reason.isEmpty() ? "" : CMILC.getIM("mute", "reason", "[reason]", reason)));
    }

    return true;
  }
}
