package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.timeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class tfly implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "setTimedFly",
        "{gcp}You have set {gcs}[playerDisplayName]{gcp} temporary fly for the next [time]");
    configReader.get(
        "addTimedFly",
        "{gcp}You have added an additional [addTime] ([time]) for {gcs}[playerDisplayName]{gcp}");
    configReader.get(
        "takeTimedFly", "{gcp}You have taken [addTime] ([time]) for {gcs}[playerDisplayName]{gcp}");
    configReader.get(
        "setUntilRelogFly",
        "{gcp}You have set {gcs}[playerDisplayName]{gcp} temporary fly until relog.");
    configReader.get(
        "targetTimedEnabled",
        "{gcp}Your flight was enabled by {gcs}[senderDisplayName]{gcp} for the next [time].");
    configReader.get(
        "targetTimedEnabledAdd", "{gcp}Your flight time was increased by [addTime] ([time]).");
    configReader.get(
        "targetTimedEnabledTake", "{gcp}Your flight time was decreased by [addTime] ([time]).");
    configReader.get(
        "targetUntilRelogEnabled",
        "{gcp}Your flight was enabled by {gcs}[senderDisplayName]{gcp} until you relog.");
    configReader.get("setFor", "{gcp}Temporary fly mode enabled for the next [time]");
    configReader.get("setForRelog", "{gcp}Temporary fly mode enabled until relog");
    configReader.get("notSet", "{gcp}Temporary fly mode is not enabled for player");
    configReader.get("willBeDisabled", "{gcp}Fly mode will be disabled in [time]");
    configReader.get("disabled", "{gcp}Temporary fly mode is disabled!");
  }

  @CAnnotation(
      priority = 75,
      info = "Set temporary players fly mode until relog or until time ends",
      args = "(playerName) (timeInSec) (-s)",
      multiTab = "[playerName] +10m -s",
      explanation = {
        "Examples:",
        "tfly Zrips 30 - fly mode for the next 30 sec",
        "tfly Zrips +30m - adds fly mode for an additional 30 sec",
        "tfly Zrips 0 - fly mode until relog",
        "tfly Zrips - check if player have tfly mode enabled and until when"
      },
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      alias = true,
      modules = "timedcommands",
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    String string = null;
    Long timeRange = null;
    tfly.Action action = tfly.Action.Set;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
        flag = true;
      } else if (string == null && cmi.getPlayerManager().getUser(arg) != null) {
        string = cmi.getPlayerManager().getUser(arg).getName();
      } else {
        try {
          if (arg.startsWith("+")) {
            arg = arg.substring(1);
            action = tfly.Action.Add;
          } else if (arg.startsWith("-")) {
            arg = arg.substring(1);
            action = tfly.Action.Take;
          }

          timeRange = timeModifier.getTimeRangeFromString(arg);
          if (timeRange != null) {
            continue;
          }
        } catch (Exception exception) {
        }

        string = arg;
      }
    }

    CMIUser cmiUser = cmi.getUser(commandSender, string, this);
    if (cmiUser == null) {
      return null;
    }

    if (timeRange != null && timeRange == 0L) {
      action = tfly.Action.UntilRelog;
    }

    if (timeRange != null
        && PermissionsManager.CMIPerm.command_tfly_admin.hasPermission(commandSender)) {
      Player player = cmiUser.getPlayer();
      Long existingTfly = cmiUser.getTfly();
      switch (action) {
        case Set:
          cmiUser.setTfly(System.currentTimeMillis() + timeRange * 1000L);
          break;
        case Add:
          long longValue =
              CMINumber.clamp(cmiUser.getTfly(), System.currentTimeMillis(), Long.MAX_VALUE);
          cmiUser.setTfly(longValue + timeRange * 1000L);
          break;
        case Take:
          long remainingAfterTake =
              cmiUser.getTfly() - System.currentTimeMillis() - timeRange * 1000L;
          if (remainingAfterTake < 1L) {
            cmiUser.setTfly(0L);
          } else {
            cmiUser.setTfly(System.currentTimeMillis() + remainingAfterTake);
          }
          break;
        case UntilRelog:
          cmiUser.setTfly(-1L);
      }

      cmi.getTimedCommandManager().addTimed(cmiUser);
      if (cmiUser.getTfly() == 0L) {
        player.setAllowFlight(false);
      } else {
        player.setAllowFlight(true);
      }

      if (cmiUser.getTfly() > 0L) {
        PermissionInfo permissionInfo =
            CMI.getInstance()
                .getPermissionsManager()
                .getPermissionInfo(
                    player, PermissionsManager.CMIPerm.command_tfly_maxtime_$1, 5000L);
        if (permissionInfo != null
            && permissionInfo.getMaxValue() != null
            && permissionInfo.getMaxValue() * 1000.0
                < cmiUser.getTfly() - System.currentTimeMillis()) {
          long maxEndTime =
              System.currentTimeMillis() + permissionInfo.getMaxValue().longValue() * 1000L;
          if ((existingTfly == null || existingTfly >= maxEndTime) && existingTfly != null) {
            if (existingTfly > maxEndTime) {
              cmiUser.setTfly(existingTfly);
              timeRange = (existingTfly - System.currentTimeMillis()) / 1000L;
            }
          } else {
            cmiUser.setTfly(
                System.currentTimeMillis() + permissionInfo.getMaxValue().longValue() * 1000L);
            timeRange = permissionInfo.getMaxValue().longValue();
          }
        }
      }

      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      String timeText = CMITimeManager.to24hourShort(timeRange * 1000L);
      String remainingText =
          CMITimeManager.to24hourShort(cmiUser.getTfly() - System.currentTimeMillis());
      switch (action) {
        case Set:
          CMILC.info(this, commandSender, "setTimedFly", snd, "[time]", timeText);
          break;
        case Add:
          CMILC.info(
              this,
              commandSender,
              "addTimedFly",
              snd,
              "[addTime]",
              timeText,
              "[time]",
              remainingText);
          break;
        case Take:
          if (cmiUser.getTfly() <= 0L) {
            CMILC.info(this, commandSender, "disabled", snd);
          } else {
            CMILC.info(
                this,
                commandSender,
                "takeTimedFly",
                snd,
                "[addTime]",
                timeText,
                "[time]",
                remainingText);
          }
          break;
        case UntilRelog:
          CMILC.info(this, commandSender, "setUntilRelogFly", snd);
      }

      if (!player.getName().equalsIgnoreCase(commandSender.getName()) && !flag) {
        switch (action) {
          case Set:
            CMILC.info(this, player, "targetTimedEnabled", snd, "[time]", timeText);
            break;
          case Add:
            CMILC.info(
                this,
                player,
                "targetTimedEnabledAdd",
                snd,
                "[addTime]",
                timeText,
                "[time]",
                remainingText);
            break;
          case Take:
            CMILC.info(
                this,
                player,
                "targetTimedEnabledTake",
                snd,
                "[addTime]",
                timeText,
                "[time]",
                remainingText);
            break;
          case UntilRelog:
            CMILC.info(this, player, "targetUntilRelogEnabled", snd);
        }
      }

      return true;
    } else {
      if (cmiUser.getTfly() != 0L) {
        if (cmiUser.getTfly() > 0L) {
          long remainingTime = cmiUser.getTfly() - System.currentTimeMillis() + 1500L;
          if (remainingTime < 0L) {
            cmiUser.setTfly(0L);
            CMILC.info(this, commandSender, "notSet");
            return true;
          }

          CMILC.info(
              this, commandSender, "setFor", "[time]", CMITimeManager.to24hourShort(remainingTime));
        } else {
          CMILC.info(this, commandSender, "setForRelog");
        }
      } else {
        CMILC.info(this, commandSender, "notSet");
      }

      return true;
    }
  }

  enum Action {
    Set,
    Add,
    Take,
    UntilRelog;

    public tfly.Action get(String string) {
      tfly.Action[] actions;
      int value = (actions = values()).length;

      for (int actionIndex = 0; actionIndex < value; actionIndex++) {
        tfly.Action action = actions[actionIndex];
        if (action.toString().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
