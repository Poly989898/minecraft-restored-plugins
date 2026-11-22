package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIGameRule;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.TimeInfo;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class time implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "check",
        "{gcp}Time: {gcs}[24h] {gcp}or {gcs}[12h] {gcp}or {gcs}[ticks]ticks {gcp}in {gcs}[world]");
    configReader.get("frozen", " {gcn}(-)");
    configReader.get("frozenTime", "{gcp}Frozen time");
    configReader.get("real", " {gcs}(+)");
    configReader.get("realTime", "{gcp}Automatic real time");
    configReader.get(
        "autorealstart", "{gcp}Automatic time adjustment for {gcs}[world] {gcp}started");
    configReader.get(
        "autorealstop", "{gcp}Automatic time adjustment for {gcs}[world] {gcp}stopped");
    configReader.get("gotfrozen", "{gcs}[world] {gcp}time have has been frozen");
    configReader.get("gotunfrozen", "{gcs}[world] {gcp}time have has been unfrozen");
    configReader.get("cantset", "{gcp}Can't set time to this value");
    configReader.get(
        "setto",
        "{gcp}Time set to {gcs}[24h] {gcp}or {gcs}[12h] {gcp}or {gcs}[ticks]ticks {gcp}for"
            + " {gcs}[world]");
  }

  @CAnnotation(
      priority = 230,
      info = "Controls server time",
      args = "(time) (world) (alter [value]) (-smooth)",
      multiTab = {
        "1pm,7000ticks [worlds],all,-smooth -smooth",
        "day,night,dusk,morning -smooth",
        "freeze",
        "unfreeze",
        "add,take 0:30 [worlds],all",
        "realtime [worlds]",
        "autorealtime start,stop"
      },
      explanation = {
        "Example:",
        "/day",
        "/night",
        "/time 13:00:00",
        "/time 1pm",
        "/time 13",
        "/time 7000ticks",
        "/time 1pm Lt_Craft",
        "/time 1pm all",
        "/time add 0:30",
        "/time take 0:30",
        "/time freeze",
        "/time unfreeze",
        "/time realtime",
        "/time autorealtime start/stop"
      },
      regVar = -66,
      consoleVar = -66,
      alias = true,
      customAlias = {"!+day:day", "!+night:night", "!+morning:morning", "!+dusk:dusk"},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    ArrayList<World> arrayList = new ArrayList();
    boolean flag = cmi.getConfigManager().isAutoTimeSmooth();
    boolean silent = false;
    time.Action action = null;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = argsCopy[argIndex];
      if (action == null) {
        action = time.Action.getByName(string);
      } else if (string.equalsIgnoreCase("-smooth")) {
        flag = !cmi.getConfigManager().isAutoTimeSmooth();
      } else if (string.equalsIgnoreCase("all")) {
        silent = true;
      } else if (CMIWorld.getWorld(string) != null) {
        arrayList.add(CMIWorld.getWorld(string));
      }
    }

    if (strings.length == 0
        || strings.length == 1 && CMIWorld.getWorld(strings[0]) != null
        || strings.length == 1 && silent) {
      if (commandSender instanceof Player && strings.length == 0) {
        arrayList.add(((Player) commandSender).getWorld());
      }

      if (strings.length == 1 && CMIWorld.getWorld(strings[0]) != null) {
        arrayList.add(CMIWorld.getWorld(strings[0]));
      }

      if (strings.length == 0 && !(commandSender instanceof Player)
          || strings.length == 1 && silent) {
        arrayList.addAll(Bukkit.getWorlds());
      }

      if (arrayList.isEmpty()) {
        return false;
      }

      for (World world : arrayList) {
        RawMessage rawMessage = new RawMessage();
        rawMessage.addText(
            CMILC.getIM(
                this,
                "check",
                "[24h]",
                CMITimeManager.to24hour(world.getTime()),
                "[12h]",
                CMITimeManager.to12hour(world.getTime()),
                "[ticks]",
                world.getTime(),
                "[world]",
                world.getName()));
        if (!CMIGameRule.ADVANCE_TIME.get(world)) {
          rawMessage.addText(CMILC.getIM(this, "frozen")).addHover(CMILC.getIM(this, "frozenTime"));
        }

        if (cmi.getTimeManager().getWorlds().contains(world.getName())) {
          rawMessage.addText(CMILC.getIM(this, "real")).addHover(CMILC.getIM(this, "realTime"));
        }

        rawMessage.show(commandSender);
      }

      return true;
    } else {
      if (action == null) {
        return false;
      }

      if (!PermissionsManager.CMIPerm.command_time_$1.hasPermission(
          commandSender, true, action.toString())) {
        return null;
      }

      if (silent) {
        arrayList.addAll(Bukkit.getWorlds());
      } else {
        World targetWorld = CMIWorld.getWorld(strings[strings.length - 1]);
        if (targetWorld != null) {
          arrayList.add(targetWorld);
        }
      }

      if (arrayList.isEmpty() && commandSender instanceof Player) {
        Player player = (Player) commandSender;
        arrayList.add(player.getWorld());
      }

      if (arrayList.isEmpty()) {
        LC.info_NoWorld.sendMessage(commandSender, new Object[0]);
        return null;
      }

      if (silent && !(commandSender instanceof Player)) {
        flag = false;
      }

      for (World worldEntry : arrayList) {
        switch (action) {
          case freeze:
            CMILC.info(this, commandSender, "gotfrozen", "[world]", worldEntry.getName());
            CMIScheduler.runTask(cmi, () -> CMIGameRule.ADVANCE_TIME.set(worldEntry, false));
            cmi.getTimeManager().addFroozenWorlds(worldEntry);
            break;
          case unfreeze:
            CMILC.info(this, commandSender, "gotunfrozen", "[world]", worldEntry.getName());
            CMIScheduler.runTask(cmi, () -> CMIGameRule.ADVANCE_TIME.set(worldEntry, true));
            cmi.getTimeManager().removeFroozenWorlds(worldEntry);
            break;
          case day:
            cmi.getTimeManager().cancelSunMove(worldEntry, false);
            this.timeSet(
                cmi,
                commandSender,
                cmi.getTimeManager().setTime(worldEntry, cmi.getConfigManager().TimeDay, flag),
                worldEntry);
            break;
          case morning:
            cmi.getTimeManager().cancelSunMove(worldEntry, false);
            this.timeSet(
                cmi,
                commandSender,
                cmi.getTimeManager().setTime(worldEntry, cmi.getConfigManager().TimeMorning, flag),
                worldEntry);
            break;
          case night:
            cmi.getTimeManager().cancelSunMove(worldEntry, false);
            this.timeSet(
                cmi,
                commandSender,
                cmi.getTimeManager().setTime(worldEntry, cmi.getConfigManager().TimeNight, flag),
                worldEntry);
            break;
          case dusk:
            cmi.getTimeManager().cancelSunMove(worldEntry, false);
            this.timeSet(
                cmi,
                commandSender,
                cmi.getTimeManager().setTime(worldEntry, cmi.getConfigManager().TimeDusk, flag),
                worldEntry);
            break;
          case add:
          case take:
            if (strings.length < 2) {
              return false;
            }

            String timeText = CMITimeManager.to24hour(worldEntry.getTime());
            TimeInfo timeInfo = CMITimeManager.stringToTimeInfo(timeText);
            TimeInfo argTimeInfo = CMITimeManager.stringToTimeInfo(strings[1]);
            if (action == time.Action.add) {
              argTimeInfo.setHours(argTimeInfo.getHours() + argTimeInfo.getHours());
              argTimeInfo.setMinutes(argTimeInfo.getMinutes() + argTimeInfo.getMinutes());
              argTimeInfo.setSeconds(argTimeInfo.getSeconds() + argTimeInfo.getSeconds());
            } else {
              argTimeInfo.setHours(argTimeInfo.getHours() - argTimeInfo.getHours());
              argTimeInfo.setMinutes(argTimeInfo.getMinutes() - argTimeInfo.getMinutes());
              argTimeInfo.setSeconds(argTimeInfo.getSeconds() - argTimeInfo.getSeconds());
            }

            this.timeSet(
                cmi,
                commandSender,
                cmi.getTimeManager().setTime(worldEntry, argTimeInfo, flag),
                worldEntry);
            break;
          case realtime:
            String worldLabel =
                new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
            this.timeSet(
                cmi,
                commandSender,
                cmi.getTimeManager().setTime(worldEntry, worldLabel, false),
                worldEntry);
            break;
          case autorealtime:
            if ((strings.length <= 1 || !strings[1].equalsIgnoreCase("start"))
                && strings.length != 1) {
              if (strings.length > 1 && strings[1].equalsIgnoreCase("stop")) {
                cmi.getTimeManager().getWorlds().remove(worldEntry.getName());
                cmi.getConfigManager()
                    .ChangeConfig("Time.AutoTime.Worlds", cmi.getTimeManager().getWorlds());
                CMIGameRule.ADVANCE_TIME.set(worldEntry, true);
                CMILC.info(this, commandSender, "autorealstop", "[world]", worldEntry.getName());
              }
            } else {
              String timeLabel =
                  new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
              CMIGameRule.ADVANCE_TIME.set(worldEntry, false);
              cmi.getTimeManager().setTime(worldEntry, timeLabel, false);
              if (!cmi.getTimeManager().getWorlds().contains(worldEntry.getName())) {
                cmi.getTimeManager().getWorlds().add(worldEntry.getName());
                cmi.getConfigManager()
                    .ChangeConfig("Time.AutoTime.Worlds", cmi.getTimeManager().getWorlds());
              }

              cmi.getTimeManager().runTimer();
              CMILC.sendMessageByPath(
                  commandSender,
                  "command.time.info.autorealstart",
                  "[world]",
                  worldEntry.getName());
            }
            break;
          default:
            this.timeSet(
                cmi,
                commandSender,
                cmi.getTimeManager().setTime(worldEntry, strings[0], flag),
                worldEntry);
        }
      }

      return true;
    }
  }

  private void timeSet(CMI cmi, CommandSender commandSender, long longValue, World world) {
    if (longValue == -1L) {
      CMILC.info(this, commandSender, "cantset");
    } else {
      Snd snd = new Snd();
      snd.setSender(commandSender);
      snd.setTarget(commandSender);
      Boolean advanceTime = CMIGameRule.ADVANCE_TIME.get(world);
      CMILC.sendMessage(
          commandSender,
          CMILC.getIM(
                  this,
                  "setto",
                  "[24h]",
                  CMITimeManager.to24hour(longValue),
                  "[12h]",
                  CMITimeManager.to12hour(longValue),
                  "[ticks]",
                  longValue,
                  "[world]",
                  world.getName(),
                  snd)
              + (!advanceTime ? CMILC.getIM(this, "frozen") : "")
              + (cmi.getTimeManager().getWorlds().contains(world.getName())
                  ? CMILC.getIM(this, "real")
                  : ""));
    }
  }

  private enum Action {
    freeze,
    unfreeze,
    day,
    morning,
    night,
    dusk,
    add,
    take,
    realtime,
    autorealtime,
    na;

    public static time.Action getByName(String string) {
      time.Action[] actions;
      int value = (actions = values()).length;

      for (int index = 0; index < value; index++) {
        time.Action action = actions[index];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return na;
    }
  }
}
