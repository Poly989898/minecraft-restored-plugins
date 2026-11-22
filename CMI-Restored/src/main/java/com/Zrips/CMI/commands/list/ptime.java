package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.TimeInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ptime implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "check", "{gcp}Personal Time: {gcs}[24h] {gcp}or {gcs}[12h] {gcp}or {gcs}[ticks]ticks");
    configReader.get("frozen", " &c(-)");
    configReader.get("gotreseted", "{gcs}[playerDisplayName] {gcp}time has been reset");
    configReader.get("gotfrozen", "{gcs}[playerDisplayName] {gcp}time has been frozen");
    configReader.get("gotunfrozen", "{gcs}[playerDisplayName] {gcp}time has been unfrozen");
    configReader.get(
        "setto",
        "{gcp}Time set to {gcs}[24h] {gcp}or {gcs}[12h] {gcp}or {gcs}[ticks]ticks {gcp}for"
            + " {gcs}[playerDisplayName]");
  }

  @CAnnotation(
      priority = 230,
      info = "Controls player personal time",
      args = "(freeze/unfreeze/day/night/dusk/morning/realtime/reset) (playerName) (-s)",
      tab = {"freeze%%unfreeze%%day%%night%%dusk%%morning%%realtime%%reset", "playername"},
      explanation = {
        "Example:",
        "/ptime 13:00:00",
        "/ptime 1pm",
        "/ptime 13",
        "/ptime 7000ticks",
        "/ptime Zrips 1pm",
        "/ptime freeze",
        "/ptime unfreeze",
        "/ptime realtime",
        "/ptime reset"
      },
      regVar = {0, 1, 2, 3},
      consoleVar = {2, 3},
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    boolean flag = false;
    boolean smooth = false;
    ptime.Action action = null;
    String string = null;
    TimeInfo timeInfo = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (action == null) {
        action = ptime.Action.getByName(arg);
        if (action != null) {
          continue;
        }
      }

      if (arg.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          smooth = true;
        }
      } else if (arg.equalsIgnoreCase("-smooth")) {
        flag = true;
      } else {
        if (timeInfo == null) {
          timeInfo = CMITimeManager.stringToTimeInfo(arg);
          if (timeInfo.isByTicks() && timeInfo.getTicks() >= 0 || timeInfo.getHours() >= 0) {
            action = ptime.Action.na;
            continue;
          }

          timeInfo = null;
        }

        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
      return null;
    }

    try {
      player.getPlayerTime();
    } catch (Throwable throwable) {
      LC.info_NotOnline.sendMessage(commandSender, new Object[0]);
      return null;
    }

    if (action == null) {
      commandSender.sendMessage(
          CMILC.getIM(
                  ptime.class,
                  "check",
                  "[24h]",
                  CMITimeManager.to24hour(player.getPlayerTime() % 24000L),
                  "[12h]",
                  CMITimeManager.to12hour(player.getPlayerTime() % 24000L),
                  "[ticks]",
                  player.getPlayerTime() % 24000L,
                  player)
              + (!player.isPlayerTimeRelative() ? CMILC.getIM(ptime.class, "frozen") : ""));
      return true;
    }

    if (!player.getName().equalsIgnoreCase(commandSender.getName())
        && !PermissionsManager.CMIPerm.command_$1_others.hasPermission(
            commandSender, true, this.getClass().getSimpleName())) {
      return null;
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    switch (action) {
      case freeze:
        player.setPlayerTime(player.getPlayerTime() % 24000L, false);
        if (!smooth) {
          CMILC.info(this, commandSender, "gotfrozen", snd);
        }
        break;
      case unfreeze:
        long longValue = player.getPlayerTime();
        long newTime = longValue - player.getWorld().getTime();
        player.setPlayerTime(newTime, true);
        if (!smooth) {
          CMILC.info(this, commandSender, "gotunfrozen", snd);
        }
        break;
      case day:
        this.timeSet(
            cmi,
            commandSender,
            cmi.getTimeManager().setPTime(player, cmi.getConfigManager().TimeDay, flag),
            player,
            smooth);
        break;
      case morning:
        this.timeSet(
            cmi,
            commandSender,
            cmi.getTimeManager().setPTime(player, cmi.getConfigManager().TimeMorning, flag),
            player,
            smooth);
        break;
      case night:
        this.timeSet(
            cmi,
            commandSender,
            cmi.getTimeManager().setPTime(player, cmi.getConfigManager().TimeNight, flag),
            player,
            smooth);
        break;
      case dusk:
        this.timeSet(
            cmi,
            commandSender,
            cmi.getTimeManager().setPTime(player, cmi.getConfigManager().TimeDusk, flag),
            player,
            smooth);
        break;
      case realtime:
        String currentTimeString =
            new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        this.timeSet(
            cmi,
            commandSender,
            cmi.getTimeManager().setPTime(player, currentTimeString, flag),
            player,
            smooth);
        break;
      case reset:
        player.resetPlayerTime();
        if (!smooth) {
          CMILC.info(this, commandSender, "gotreseted", snd);
        }

        CMIUser.getUser(player).setPlayerTime(-1L);
        break;
      case na:
        this.timeSet(
            cmi,
            commandSender,
            cmi.getTimeManager().setPTime(player, timeInfo, flag),
            player,
            smooth);
    }

    return true;
  }

  private void timeSet(
      CMI cmi, CommandSender commandSender, long longValue, Player player, boolean flag) {
    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    if (longValue == -1L) {
      if (!flag) {
        CMILC.info("time", commandSender, "cantset", snd);
      }
    } else if (!flag) {
      CMILC.info(
          this,
          commandSender,
          "setto",
          "[24h]",
          CMITimeManager.to24hour(longValue),
          "[12h]",
          CMITimeManager.to12hour(longValue),
          "[ticks]",
          longValue < 0L ? longValue + 24000L : longValue,
          snd);
    }
  }

  private enum Action {
    freeze,
    unfreeze,
    day,
    morning,
    night,
    dusk,
    realtime,
    reset,
    na;

    public static ptime.Action getByName(String string) {
      ptime.Action[] actions;
      int value = (actions = values()).length;

      for (int index = 0; index < value; index++) {
        ptime.Action action = actions[index];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
