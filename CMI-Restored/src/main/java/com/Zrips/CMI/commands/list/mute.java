package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Arrays;
import java.util.HashSet;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.timeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class mute implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("mutedFor", "{gcs}[playerDisplayName] {gcp}muted for {gcs}[time]");
    configReader.get("muted", "{gcp}You are muted for {gcs}[time]");
    configReader.get("reason", "{gcp}Reason: {gcs}[reason]");
    configReader.get(
        "badTimeRanges",
        "{gcw}You can't mute for longer than {gcs}[time] {gcp}({gcs}[seconds]{gcp})");
  }

  @CAnnotation(
      priority = 240,
      info = "Mute player",
      args = "[playerName] (time) (-s) (reason)",
      tab = {"playername", "1m%%", "-s%%"},
      explanation = {
        "Examples: ",
        "/cmi mute zrips 1m",
        "/cmi mute zrips +30s",
        "/cmi mute zrips -15m",
        "/cmi mute zrips 1h",
        "/cmi mute zrips 1h For swearing"
      },
      regVar = -100,
      consoleVar = -100,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Long duration = null;
    String string = null;
    boolean flag = false;
    String reason = "";
    Boolean silentFlag = null;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (string == null && cmi.getPlayerManager().getUserByAproxName(arg) != null) {
        string = arg;
      } else if (arg.equalsIgnoreCase("-s")) {
        flag = true;
      } else {
        Boolean silent = null;
        if (arg.startsWith("-")) {
          arg = arg.substring(1, arg.length());
          silent = false;
        }

        if (arg.startsWith("+")) {
          arg = arg.substring(1, arg.length());
          silent = true;
        }

        if (duration == null) {
          try {
            Long parsedTime = timeModifier.getTimeRangeFromString(arg);
            if (parsedTime != null) {
              duration = parsedTime;
              silentFlag = silent;
              continue;
            }
          } catch (Exception exception) {
          }
        }

        if (string == null) {
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

    duration = duration == null ? 3600L : duration;
    duration = CMINumber.clamp(duration, 0L, Long.MAX_VALUE);
    Long defaultDuration = 3600L;
    Player player = null;
    if (commandSender instanceof Player) {
      player = (Player) commandSender;
    }

    PermissionInfo permissionInfo =
        cmi.getPermissionsManager()
            .getPermissionInfo(player, PermissionsManager.CMIPerm.command_mute_max_$1);
    if (permissionInfo.getMinValue() != null) {
      defaultDuration =
          permissionInfo.getMaxValue() != null
              ? permissionInfo.getMaxValue().longValue()
              : defaultDuration;
      if (permissionInfo.getMinValue() == -1.0) {
        defaultDuration = Long.MAX_VALUE;
      }
    }

    if (defaultDuration < duration && player != null && !player.isOp()) {
      CMILC.info(
          this,
          commandSender,
          "badTimeRanges",
          "[seconds]",
          defaultDuration,
          "[time]",
          CMITimeManager.to24hourShort(defaultDuration * 1000L, true));
      return null;
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
    reason = cmi.getLM().updateSnd(snd, reason);
    if (silentFlag != null) {
      long longValue =
          cmiUser.getMutedUntil() == 0L ? 0L : cmiUser.getMutedUntil() - System.currentTimeMillis();
      if (silentFlag) {
        duration = longValue > 0L ? longValue / 1000L + duration : duration;
      } else {
        duration = longValue > 0L ? longValue / 1000L - duration : 0L;
      }

      if (duration <= 0L) {
        if (longValue > 0L) {
          cmi.getCommandManager()
              .performCMICommand(commandSender, unmute.class, cmiUser.getName(), flag ? "-s" : "");
        }

        return true;
      }
    }

    cmiUser.setMutedUntil(System.currentTimeMillis() + duration * 1000L);
    cmiUser.setShadowMutedUntil(0L);
    if (!reason.isEmpty()) {
      cmiUser.setMutedReason(reason);
    }

    cmiUser.addForDelayedSave();
    if (!flag) {
      CMILC.sendMessage(
          commandSender,
          CMILC.getIM(
                  this, "mutedFor", "[time]", CMITimeManager.to24hourShort(duration * 1000L), snd)
              + (reason.isEmpty() ? "" : CMILC.getIM(this, "reason", "[reason]", reason)));
    }

    if (!flag && cmiUser.isOnline()) {
      CMILC.sendMessage(
          cmiUser,
          CMILC.getIM(this, "muted", "[time]", CMITimeManager.to24hourShort(duration * 1000L), snd)
              + (reason.isEmpty() ? "" : CMILC.getIM(this, "reason", "[reason]", reason)));
    }

    if (!flag) {
      CMILC.broadcastMessage(
          commandSender,
          CMILC.getIM(
                  this, "mutedFor", "[time]", CMITimeManager.to24hourShort(duration * 1000L), snd)
              + (reason.isEmpty() ? "" : CMILC.getIM(this, "reason", "[reason]", reason)),
          false,
          cmiUser.isOnline() ? new HashSet<>(Arrays.asList(cmiUser.getPlayer())) : null);
    }

    return true;
  }
}
