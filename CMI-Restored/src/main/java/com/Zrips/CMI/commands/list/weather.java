package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Time.timeModifier;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class weather implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "setTo", "{gcp}Set {gcs}[world] {gcp}weather to {gcs}[weather] {gcp}for next {gcs}[time]");
    configReader.get(
        "currentState", "{gcs}[world] {gcp}-> {gcs}[weather] {gcp}for next {gcs}[time]");
    configReader.get("sunny", "sunny");
    configReader.get("rainy", "rainy");
    configReader.get("thundering", "thundering");
  }

  @CAnnotation(
      priority = 235,
      info = "Controls server weather",
      args = "(sun/rain/storm) (lock/duration) (worldName/all)",
      tab = {"sun%%rain%%storm", "10m%%3600%%lock", "worlds%%all"},
      explanation = {
        "Extra time formats can have: s/m/h/d/w/M/Y",
        "Example:",
        "/sun",
        "/rain",
        "/storm",
        "/sun lock",
        "/sun 120",
        "/sun Lt_Craft",
        "/sun 1h",
        "/sun 10m"
      },
      regVar = {0, 1, 2, 3},
      consoleVar = {1, 2, 3},
      alias = true,
      customAlias = {"!+sun:sun", "!+rain:rain", "!+storm:storm"},
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    CMIScheduler.runTask(
        cmi,
        () -> {
          ArrayList<World> arrayList = new ArrayList();
          WeatherType weatherType = null;
          weather.Action action = weather.Action.INFO;
          boolean flag = false;
          Long duration = 3600L;
          String[] argsCopy = strings;
          int value = strings.length;

          for (int argIndex = 0; argIndex < value; argIndex++) {
            String string = argsCopy[argIndex];
            if (string.equalsIgnoreCase("all")) {
              arrayList.addAll(Bukkit.getWorlds());
            } else {
              if (arrayList.isEmpty()) {
                World world = cmi.getUtilManager().getWorld(string);
                if (world != null) {
                  arrayList.add(world);
                  continue;
                }
              }

              if (string.equalsIgnoreCase("sun")
                  || string.equalsIgnoreCase("clear")
                  || string.equalsIgnoreCase("sunny")) {
                weatherType = WeatherType.CLEAR;
                action = weather.Action.SUN;
              } else if (string.equalsIgnoreCase("rain") || string.equalsIgnoreCase("rainy")) {
                weatherType = WeatherType.DOWNFALL;
                action = weather.Action.RAIN;
              } else if (string.equalsIgnoreCase("storm") || string.equalsIgnoreCase("thunder")) {
                weatherType = WeatherType.DOWNFALL;
                action = weather.Action.STORM;
                flag = true;
              } else if (string.equalsIgnoreCase("lock")) {
                duration = 2147483647L;
                action = weather.Action.LOCK;
              } else {
                try {
                  Long parsedTime = timeModifier.getTimeRangeFromString(string);
                  if (parsedTime != null) {
                    duration = parsedTime;
                  }
                } catch (Exception exception) {
                }
              }
            }
          }

          if (arrayList.isEmpty() && commandSender instanceof Player) {
            arrayList.add(((Player) commandSender).getWorld());
          }

          if (arrayList.isEmpty() && !(commandSender instanceof Player)) {
            LC.info_NoWorld.sendMessage(commandSender, new Object[0]);
          } else {
            if (commandSender instanceof Player) {
              PermissionInfo permissionInfo =
                  cmi.getPermissionsManager()
                      .getPermissionInfo(
                          (Player) commandSender,
                          PermissionsManager.CMIPerm.command_weather_$1_$2.getPermission(
                              weatherType != null && weatherType != WeatherType.CLEAR
                                  ? "rain"
                                  : "sun",
                              ""));
              if (permissionInfo != null
                  && permissionInfo.getMaxValue() != null
                  && duration.longValue() > permissionInfo.getMaxValue()) {
                duration = permissionInfo.getMaxValue().longValue();
              }
            }

            if (action.equals(weather.Action.INFO)
                || PermissionsManager.CMIPerm.command_weather_$1.hasPermission(
                    commandSender, true, action.toString())) {
              if (strings.length == 0 && commandSender instanceof Player
                  || strings.length == 1
                      && !arrayList.isEmpty()
                      && !(commandSender instanceof Player)
                  || weatherType == null) {
                for (World targetWorld : arrayList) {
                  duration = (long) targetWorld.getWeatherDuration();
                  String worldName =
                      targetWorld.hasStorm()
                          ? CMILC.getIM(this, "rainy")
                          : CMILC.getIM(this, "sunny");
                  if (targetWorld.isThundering()) {
                    duration = (long) targetWorld.getThunderDuration();
                    worldName = CMILC.getIM(this, "thundering");
                  }

                  duration = duration / 20L;
                  CMILC.info(
                      this,
                      commandSender,
                      "currentState",
                      "[world]",
                      targetWorld.getName(),
                      "[weather]",
                      worldName,
                      "[time]",
                      CMITimeManager.to24hourShort(duration * 1000L));
                }
              } else if (arrayList.isEmpty()) {
                LC.info_NoWorld.sendMessage(commandSender, new Object[0]);
              } else {
                if (duration != 2147483647L) {
                  duration = duration * 20L;
                }

                if (duration < 0L) {
                  duration = 3600L;
                }

                for (World worldEntry : arrayList) {
                  worldEntry.setStorm(weatherType == WeatherType.DOWNFALL);
                  worldEntry.setWeatherDuration(duration.intValue());
                  if (flag) {
                    worldEntry.setStorm(true);
                    worldEntry.setThundering(true);
                    if (duration != 0L) {
                      worldEntry.setThunderDuration(duration.intValue());
                    }
                  } else {
                    worldEntry.setThundering(false);
                    worldEntry.setThunderDuration(0);
                  }

                  String weatherText =
                      worldEntry.hasStorm()
                          ? CMILC.getIM(this, "rainy")
                          : CMILC.getIM(this, "sunny");
                  if (worldEntry.isThundering()) {
                    duration = (long) worldEntry.getThunderDuration();
                    weatherText = CMILC.getIM(this, "thundering");
                  }

                  duration = duration / 20L;
                  Snd snd = new Snd();
                  snd.setSender(commandSender);
                  snd.setTarget(commandSender);
                  CMILC.info(
                      this,
                      commandSender,
                      "setTo",
                      "[world]",
                      worldEntry.getName(),
                      "[weather]",
                      weatherText,
                      "[time]",
                      CMITimeManager.to24hourShort(duration * 1000L),
                      snd);
                }
              }
            }
          }
        });
    return true;
  }

  private enum Action {
    SUN,
    RAIN,
    STORM,
    LOCK,
    INFO;
  }
}
