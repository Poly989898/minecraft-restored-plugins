package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.events.CMIPlayerBanEvent;
import java.util.Date;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.timeModifier;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tempban implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("message", "{gcw}Temporarily Banned!");
    configReader.get(
        "messageToAll",
        "{gcs}[playerDisplayName] {gcp}got tempbanned by {gcs}[senderDisplayName] {gcp}for:"
            + " {gcs}[time] {gcp}reason: {gcs}[reason]");
    configReader.get(
        "banedSilent",
        "{gcn}[{gcu}Silent{gcn}]{gcs}[playerDisplayName] {gcp}got tempbanned by"
            + " {gcs}[senderDisplayName] {gcp}for: {gcs}[time] {gcp}reason: {gcs}[reason]");
    configReader.get("time", "{gcp}You will be unbanned after {gcs}[time]");
    configReader.get("badTime", "{gcw}Time is defined incorrectly");
    configReader.get("badTimeRanges", "{gcw}You can't ban for longer than [time] {gcs}([seconds])");
  }

  @CAnnotation(
      priority = 48,
      info = "TempBan player",
      args = "[playerName] [timeValue] (reason) (-s)",
      tab = "playername",
      explanation = {
        "Ban max range depends on cmi.command.tempban.max.[amount]",
        "cmi.command.tempban.max.unlimited - allows to ban for as much as you want",
        "In case player doesn't have any, 60 sec will be max value",
        "Example: ",
        "/cmi tempban zrips 5m For swearing"
      },
      regVar = -100,
      consoleVar = -100,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String reason = "";
    boolean flag = false;
    Long duration = null;
    Boolean hasDuration = null;
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
          Boolean silent = null;
          if (arg.startsWith("-")) {
            arg = arg.substring(1, arg.length());
            silent = false;
          }

          if (arg.startsWith("+")) {
            arg = arg.substring(1, arg.length());
            silent = true;
          }

          try {
            duration = Long.parseLong(arg);
            hasDuration = silent;
            continue;
          } catch (Exception exception) {
            try {
              duration = timeModifier.getTimeRangeFromString(arg);
              hasDuration = silent;
              continue;
            } catch (Exception exception2) {
            }
          }
        }

        if (!reason.isEmpty()) {
          reason = reason + " ";
        }

        reason = reason + arg;
      }
    }

    if (duration == null) {
      duration = 60L;
    }

    if (duration < 1L) {
      CMILC.info(this, commandSender, "badTime");
      return null;
    }

    CMIUser cmiUser = cmi.getPlayerManager().getUser(string);
    if (cmiUser != null) {
      this.privateTempBan(commandSender, cmiUser, duration, reason, flag, hasDuration);
    } else {
      String reasonCopy = reason;
      boolean silentFlag = flag;
      Long durationCopy = duration;
      Boolean hasDurationCopy = hasDuration;
      CMIUser.getOfflinePlayer(string)
          .thenAccept(
              offlinePlayer -> {
                CMIUser targetUser = cmi.getPlayerManager().getUser(offlinePlayer);
                if (targetUser == null) {
                  LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
                } else {
                  this.privateTempBan(
                      commandSender,
                      targetUser,
                      durationCopy,
                      reasonCopy,
                      silentFlag,
                      hasDurationCopy);
                }
              });
    }

    return true;
  }

  private void privateTempBan(
      CommandSender commandSender,
      CMIUser cmiUser,
      Long durationMillis,
      String string,
      boolean flag,
      Boolean silent) {
    if (cmiUser != null && cmiUser.getName() != null) {
      if (cmiUser.getName().equals(commandSender.getName())) {
        CMILC.info(ban.class, commandSender, "noSelfBan");
      } else if (cmiUser.hasPermission(PermissionsManager.CMIPerm.command_tempban_bypass)
          && commandSender instanceof Player) {
        CMILC.info(ban.class, commandSender, "cantBan");
      } else {
        if (commandSender instanceof Player
            && !PermissionsManager.CMIPerm.command_tempban_max_unlimited.hasPermission(
                commandSender)) {
          Long minuteMillis = 60L;
          Player player = (Player) commandSender;
          PermissionInfo permissionInfo =
              CMI.getInstance()
                  .getPermissionsManager()
                  .getPermissionInfo(player, PermissionsManager.CMIPerm.command_tempban_max_$1);
          if (permissionInfo.getMinValue() != null) {
            minuteMillis =
                permissionInfo.getMaxValue() != null
                    ? permissionInfo.getMaxValue().longValue()
                    : minuteMillis;
            if (permissionInfo.getMinValue() == -1.0) {
              minuteMillis = Long.MAX_VALUE;
            }
          }

          if (minuteMillis < durationMillis && !player.isOp()) {
            CMILC.info(
                tempban.class,
                commandSender,
                "badTimeRanges",
                "[seconds]",
                minuteMillis,
                "[time]",
                CMITimeManager.to24hourShort(minuteMillis * 1000L, true));
            return;
          }
        }

        if (silent != null) {
          Long leftTime = cmiUser.getBanEntry().getLeftTime();
          leftTime = leftTime == null ? 0L : leftTime;
          if (silent) {
            durationMillis = leftTime > 0L ? leftTime / 1000L + durationMillis : durationMillis;
          } else {
            durationMillis = leftTime > 0L ? leftTime / 1000L - durationMillis : 0L;
          }

          if (durationMillis <= 0L) {
            if (leftTime > 0L) {
              CMI.getInstance()
                  .getCommandManager()
                  .performCMICommand(
                      commandSender, unban.class, cmiUser.getName(), flag ? "-s" : "");
            }

            return;
          }
        }

        cmiUser.setBanned(
            string, commandSender, new Date(System.currentTimeMillis() + durationMillis * 1000L));
        string = cmiUser.getBanEntry().getReason();
        CMIPlayerBanEvent cmiPlayerBanEvent =
            new CMIPlayerBanEvent(
                commandSender,
                cmiUser.getUniqueId(),
                string,
                System.currentTimeMillis() + durationMillis * 1000L);
        Bukkit.getServer().getPluginManager().callEvent(cmiPlayerBanEvent);
        Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
        if (!flag) {
          for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.getName().equalsIgnoreCase(commandSender.getName())) {
              CMILC.info(
                  tempban.class,
                  onlinePlayer,
                  "messageToAll",
                  snd,
                  "[reason]",
                  string,
                  "[time]",
                  CMITimeManager.to24hourShort(cmiUser.getBanEntry().getLeftTime() + 1000L));
            }
          }

          CMILC.info(
              tempban.class,
              commandSender,
              "messageToAll",
              snd,
              "[reason]",
              string,
              "[time]",
              CMITimeManager.to24hourShort(cmiUser.getBanEntry().getLeftTime() + 1000L));
        } else {
          CMILC.info(
              tempban.class,
              commandSender,
              "banedSilent",
              snd,
              "[reason]",
              string,
              "[time]",
              CMITimeManager.to24hourShort(cmiUser.getBanEntry().getLeftTime() + 1000L));
        }
      }
    } else {
      LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
    }
  }
}
