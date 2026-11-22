package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class pweather implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("check", "{gcp}Personal weather: {gcs}[weather]");
    configReader.get("gotreseted", "{gcs}[playerDisplayName] {gcp}weather has been reset");
    configReader.get(
        "setto", "{gcp}Personal weather set to {gcs}[weather] {gcp}for {gcs}[playerDisplayName]");
    configReader.get("gotset", "{gcp}Your personal weather set to {gcs}[weather]");
    configReader.get("reset", "{gcp}Weather reset");
  }

  @CAnnotation(
      priority = 235,
      info = "Controls player weather",
      args = "[sun/rain/reset] (playerName) (-s)",
      tab = "playername%%sun%%rain%%reset",
      explanation = {},
      regVar = {0, 1, 2, 3},
      consoleVar = {2, 3},
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    WeatherType weatherType = null;
    boolean flag = false;
    String string = null;
    boolean resetWeather = false;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        if (PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
          flag = true;
        }
      } else if (arg.equalsIgnoreCase("sun") || arg.equalsIgnoreCase("clear")) {
        weatherType = WeatherType.CLEAR;
      } else if (arg.equalsIgnoreCase("rain") || arg.equalsIgnoreCase("rainy")) {
        weatherType = WeatherType.DOWNFALL;
      } else if (arg.equalsIgnoreCase("reset")) {
        resetWeather = true;
      } else {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    if (weatherType == null && !resetWeather) {
      weatherType = player.getPlayerWeather();
      if (weatherType == null) {
        CMILC.info(
            this, commandSender, "check", "[weather]", LC.info_notSet.getLocale(new Object[0]));
      } else {
        CMILC.info(
            this,
            commandSender,
            "check",
            "[weather]",
            weatherType.equals(WeatherType.CLEAR)
                ? CMILC.getIM("weather", "sunny")
                : CMILC.getIM("weather", "rainy"));
      }

      return true;
    } else {
      if (resetWeather
          && !PermissionsManager.CMIPerm.command_pweather_$1.hasPermission(
              commandSender, true, "reset")) {
        return null;
      }

      if (weatherType != null
          && weatherType == WeatherType.CLEAR
          && !PermissionsManager.CMIPerm.command_pweather_$1.hasPermission(
              commandSender, true, "sun")) {
        return null;
      }

      if (weatherType != null
          && weatherType == WeatherType.DOWNFALL
          && !PermissionsManager.CMIPerm.command_pweather_$1.hasPermission(
              commandSender, true, "rain")) {
        return null;
      }

      Snd snd = new Snd();
      snd.setSender(commandSender);
      snd.setTarget(player);
      if (!resetWeather && weatherType != null) {
        if (!flag) {
          CMILC.info(
              this,
              player,
              "gotset",
              "[weather]",
              weatherType.equals(WeatherType.CLEAR)
                  ? CMILC.getIM("weather", "sunny")
                  : CMILC.getIM("weather", "rainy"),
              snd);
          if (!commandSender.getName().equalsIgnoreCase(player.getName())) {
            CMILC.info(
                this,
                commandSender,
                "setto",
                "[weather]",
                weatherType.equals(WeatherType.CLEAR)
                    ? CMILC.getIM("weather", "sunny")
                    : CMILC.getIM("weather", "rainy"),
                snd);
          }
        }

        if (player.isOnline()) {
          player.setPlayerWeather(weatherType);
        }
      } else {
        if (!flag) {
          CMILC.info(this, commandSender, "reset", snd);
          if (!commandSender.getName().equalsIgnoreCase(player.getName())) {
            CMILC.info(this, player, "reset", snd);
          }
        }

        if (player.isOnline()) {
          player.resetPlayerWeather();
        }
      }

      return true;
    }
  }
}
