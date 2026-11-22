package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Jail.CMIJail;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashSet;
import net.Zrips.CMILib.Container.CMIArray;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.timeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class jail implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "jailed", "{gcp}You put {gcs}[playerDisplayName] {gcp}into jail for the next [time]");
    configReader.get("jailedPlayer", "{gcs}[playerDisplayName] {gcp}jailed for the next [time]");
    configReader.get("reason", " {gcp}For: {gcs}[reason]");
    configReader.get("leftTime", "!title!{gcn}Jail time:!subtitle![time]");
    configReader.get("jailedTarget", "{gcp}You have been jailed! [time]");
    configReader.get("jailedTimeChange", "{gcp}Jail time changed to [time]");
    configReader.get("cantTalk", "{gcd}You cant talk while you are jailed");
    configReader.get(
        "cantTalkRange", "!actionbar!{gcn}Your chat range is limited to {gcs}[range] {gcn}blocks");
    configReader.get(
        "failed", "{gcw}Failed to jail player. Check target jail name, cell id or time format");
    configReader.get("bossbar", "{gcn}Jail time: [autoTimeLeft]");
    configReader.get("noCommand", "{gcw}You can't use this command while jailed!");
  }

  @CAnnotation(
      info = "Jail player for time period",
      args = "[playerName] (time) (jailName) (cellId) (-s) (r:jail_reason)",
      tab = {"playername", "5m%%", "jail", "cellId", "r:"},
      explanation = {},
      regVar = -100,
      consoleVar = -100,
      modules = "jail",
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMIJail cmiJail = null;
    Integer integer = null;
    Long duration = null;
    Object object = null;
    Boolean silent = null;
    String string = null;
    String reason = null;
    boolean flag = false;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (!arg.startsWith("r:") && reason == null) {
        if (arg.equalsIgnoreCase("-s")) {
          flag = true;
        } else if (string == null) {
          string = arg;
        } else {
          if (duration == null) {
            String jailName = arg;
            if (jailName.startsWith("-") || jailName.startsWith("+")) {
              silent = jailName.startsWith("+");
              jailName = jailName.substring(1);
            }

            Long parsedTime = timeModifier.getTimeRangeFromString(jailName);
            if (parsedTime != null) {
              duration = parsedTime;
              continue;
            }

            silent = null;
          }

          if (cmiJail == null) {
            CMIJail jail = cmi.getJailManager().getByName(arg);
            if (jail != null) {
              cmiJail = jail;
              continue;
            }
          }

          if (integer == null) {
            try {
              integer = Integer.parseInt(arg);
            } catch (Exception exception) {
            }
          }
        }
      } else {
        reason = reason == null ? "" : reason + " ";
        if (arg.startsWith("r:")) {
          reason = reason + arg.substring("r:".length()).replace("_", " ");
        } else {
          reason = reason + arg;
        }
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this, false);
    Snd snd = new Snd();
    snd.setSender(commandSender);
    snd.setTarget(cmiUser);
    if (cmiUser == null) {
      return null;
    }

    if (!cmiUser.getName().equals(commandSender.getName())
        && !cmiUser.hasPermission(PermissionsManager.CMIPerm.command_jail_bypass)) {
      if (commandSender instanceof Player) {
        Player player = (Player) commandSender;
        PermissionInfo permissionInfo =
            cmi.getPermissionsManager()
                .getPermissionInfo(
                    player, PermissionsManager.CMIPerm.command_jail_maxtime_$1, 5000L);
        if (permissionInfo != null
            && permissionInfo.getMaxValue() != null
            && duration != null
            && duration.longValue() > permissionInfo.getMaxValue()) {
          object = permissionInfo.getMaxValue().longValue();
          duration = permissionInfo.getMaxValue().longValue();
        }
      }

      if (duration == null) {
        duration = cmi.getJailManager().getDefaultTime().longValue();
      }

      if (cmiJail == null) {
        var list = cmi.getJailManager().getJailsByDistance(cmiUser.getLocation());
        if (list != null && !list.isEmpty()) {
          cmiJail = (CMIJail) list.get(0);
        }
      }

      if (silent == null || (cmiUser.isJailed() || silent) && !cmiUser.isJailed()) {
        boolean hasFlag =
            cmiUser.jail(
                duration,
                cmiJail,
                integer,
                reason,
                commandSender instanceof Player ? ((Player) commandSender).getUniqueId() : null);
        if (!hasFlag) {
          CMILC.info(this, commandSender, "failed");
          return null;
        }

        cmiUser.addForDelayedSave();
        reason = reason == null ? "" : CMILC.getIM(this, "reason", "[reason]", reason);
        if (!commandSender.getName().equalsIgnoreCase(cmiUser.getName()) && !flag) {
          CMILC.sendMessage(
              commandSender,
              CMILC.getIM(
                      this, "jailed", snd, "[time]", CMITimeManager.to24hourShort(duration * 1000L))
                  + reason);
        }

        if (cmiUser.isOnline() && cmiUser.isJailed() && !flag) {
          CMILC.info(
              this,
              cmiUser,
              "jailedTarget",
              snd,
              "[time]",
              CMITimeManager.to24hourShort(duration * 1000L) + reason);
        }

        if (!flag) {
          HashSet<Player> hashSet = new HashSet();
          if (cmiUser.isOnline()) {
            hashSet.add(cmiUser.getPlayer(false));
          }

          CMILC.broadcastMessage(
              commandSender,
              CMILC.getIM(
                  this,
                  "jailedPlayer",
                  snd,
                  "[time]",
                  CMITimeManager.to24hourShort(duration * 1000L) + reason),
              false,
              hashSet);
        }

        return true;
      } else {
        if (!cmiUser.isJailed() && !silent) {
          return true;
        }

        long longValue = cmiUser.getJailedUntil() + (silent ? duration * 1000L : -duration * 1000L);
        if (longValue < System.currentTimeMillis()) {
          String[] targets = new String[] {cmiUser.getName()};
          if (flag) {
            targets = CMIArray.addLast(targets, "-s");
          }

          cmi.getCommandManager().performCMICommand(commandSender, unjail.class, targets);
          return null;
        } else {
          cmiUser.setJailedForTime(longValue);
          if (cmiUser.isOnline()) {
            cmi.getJailManager().refreshBossBar(cmiUser);
          }

          if (cmiUser.isOnline() && cmiUser.isJailed() && !flag) {
            CMILC.info(
                this,
                cmiUser,
                "jailedTimeChange",
                snd,
                "[time]",
                CMITimeManager.to24hourShort(cmiUser.getJailedForTime()));
          }

          return true;
        }
      }
    } else {
      LC.info_cantDoForPlayer.sendMessage(commandSender, new Object[] {snd});
      return null;
    }
  }
}
