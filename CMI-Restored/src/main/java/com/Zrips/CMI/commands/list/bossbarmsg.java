package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Time.timeModifier;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class bossbarmsg implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      priority = 48,
      info = "Sends boss bar message to player",
      args =
          "[playerName/all] (-sec:[seconds])(-t:[timeToKeepFor]) (-n:nameOfBar)"
              + " (-p:[maxValue/current]) (-c:[color]) (-s:[1,6,10,12,20])"
              + " (-cmd:\"command;;command2\") (-pcmd:\"command;;command2\") (-a:[ticks]) [message]"
              + " (-cancel:nameOfBar)",
      tab = "playername",
      explanation = {
        "PlaceHolderAPI is supported for progression and text it self",
        "Time is defined in regular way, like 1s is one second and 1m is one minute. This will"
            + " determine for how long to keep bar shown for player before automatically hiding",
        "nameOfBar is option and when defining same one, then current bar will update with new"
            + " information",
        "colors can be: red, green, pink, purple, white, yellow",
        "max and current values determines bar fillup, PlaceHolderAPI variables can be used for"
            + " dynamic updates",
        "Examples:",
        "/cmi bossbar Zrips BossBarTitle",
        "/cmi bossbarmsg all -t:1s -p:%server_max_players%/%server_online% -n:testbar -c:red"
            + " &2Title of bar %server_online%/%server_max_players% &e%cmi_user_name%",
        "/cmi bossbarmsg all -p:+1 -n:testbar - will add one percent towards current progresion bar"
            + " by name",
        "/cmi bossbarmsg all -p:-1 -n:testbar - will take out one percent from current progresion"
            + " bar by name",
        "/cmi bossbarmsg all -s:10 -n:testbar - will set bossbar style to 10 segments by bar name",
        "/cmi bossbarmsg all -sec:5 - will count for 5 seconds from 0 to 5",
        "/cmi bossbarmsg all -sec:-5 - will count for 5 seconds from 5 to 0",
        "-cmd: will perform commands either for single player if target is specific player or only"
            + " once if its for everyone (all). This will not translated variables lie [playerName]"
            + " if used with 'all' variable. Use -pcmd: instead",
        "-pcmd: will perform commands on each player if target is 'all'",
        "https://www.zrips.net/cmi/commands/bossbarmsg-handling/"
      },
      regVar = {-100, -1},
      consoleVar = {-100, -1},
      ignoreHelpPage = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentLower(Version.v1_9_R1)) {
      CMIMessages.sendMessage(
          commandSender, "&cCant use this command on your server version! Only 1.9 and above");
      return null;
    }

    String targetName = strings[0];
    Player player = null;
    boolean toAll = false;
    if (!targetName.equalsIgnoreCase("all")) {
      player = CMIUser.getOnlinePlayer(targetName, true);
      if (player == null) {
        LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
        return null;
      }
    } else {
      toAll = true;
    }

    List<String> tokens = Arrays.asList(Arrays.copyOfRange(strings, 1, strings.length));
    StringBuilder message = new StringBuilder();
    Integer keepForTicks = null;
    String barName = null;
    double secondsCount = 0.0;
    String currentValue = "";
    String maxValue = "";
    Double adjustPerc = null;
    Integer autoTicks = null;
    BarColor barColor = BarColor.GREEN;
    BarStyle barStyle = null;
    ArrayList<String> commands = new ArrayList<String>();
    ArrayList<String> playerCommands = new ArrayList<String>();
    ArrayList<CMIChatColor> colors = new ArrayList<CMIChatColor>();
    int colorChangeTicks = -1;
    Pattern commandPattern = Pattern.compile("(-cmd:\")([^\"]*)(\")");
    Pattern playerCommandPattern = Pattern.compile("(-pcmd:\")([^\"]*)(\")");
    String joined = "";

    for (String token : tokens) {
      if (!joined.isEmpty()) {
        joined = joined + " ";
      }

      joined = joined + token;
    }

    Matcher commandMatcher = commandPattern.matcher(joined);
    if (commandMatcher.find()) {
      String commandList = commandMatcher.group(2);
      if (commandList.contains(";;")) {
        commands.addAll(Arrays.asList(commandList.split(";;")));
      } else {
        commands.add(commandList);
      }

      joined = joined.replace(commandMatcher.group(0), "");
      tokens = new ArrayList<String>();
      if (joined.contains(" ")) {
        tokens.addAll(Arrays.asList(joined.split(" ")));
      } else {
        tokens.add(commandList);
      }
    }

    Matcher playerCommandMatcher = playerCommandPattern.matcher(joined);
    if (playerCommandMatcher.find()) {
      String playerCommandList = playerCommandMatcher.group(2);
      if (playerCommandList.contains(";;")) {
        playerCommands.addAll(Arrays.asList(playerCommandList.split(";;")));
      } else {
        playerCommands.add(playerCommandList);
      }

      joined = joined.replace(playerCommandMatcher.group(0), "");
      tokens = new ArrayList<String>();
      if (joined.contains(" ")) {
        tokens.addAll(Arrays.asList(joined.split(" ")));
      } else {
        tokens.add(playerCommandList);
      }
    }

    for (String token : tokens) {
      if (token.startsWith("-cancel:")) {
        cancelBossbar(token, toAll, player);
        return true;
      }

      if (token.startsWith("-t:")) {
        try {
          Double seconds = timeModifier.getDoubleTimeRangeFromString(token.replace("-t:", ""));
          if (seconds != null) {
            keepForTicks = (int) (seconds * 20.0);
          }
        } catch (Exception exception) {
        }
      } else if (token.startsWith("-n:")) {
        try {
          barName = token.replace("-n:", "");
        } catch (Exception exception2) {
        }
      } else if (token.startsWith("-s:")) {
        barStyle = getStyle(token);
      } else if (token.startsWith("-p:") && token.contains("/")) {
        try {
          currentValue = token.replace("-p:", "").split("/")[0];
          maxValue = token.replace("-p:", "").split("/")[1];
          if (autoTicks == null) {
            autoTicks = 20;
          }
        } catch (Exception exception3) {
        }
      } else if (token.startsWith("-p:") && !token.contains("/")) {
        try {
          adjustPerc = Double.parseDouble(token.replace("-p:", "").replace("+", ""));
          adjustPerc = adjustPerc / 100.0;
          if (currentValue.isEmpty()) {
            currentValue = "100";
          }

          if (maxValue.isEmpty()) {
            if (adjustPerc < 0.0) {
              maxValue = "100";
            } else {
              maxValue = "0";
            }
          }

          if (autoTicks == null) {
            autoTicks = 20;
          }
        } catch (Exception exception4) {
        }
      } else if (token.startsWith("-sec:")) {
        try {
          secondsCount = Double.parseDouble(token.replace("-sec:", "").replace("+", ""));
          adjustPerc = 100.0 / secondsCount / 100.0;
          if (adjustPerc < 0.0) {
            currentValue = "100";
            maxValue = "100";
          }

          if (autoTicks == null) {
            autoTicks = 20;
          } else {
            adjustPerc = adjustPerc * (autoTicks.intValue() / 20.0);
          }
        } catch (Exception exception5) {
          exception5.printStackTrace();
        }
      } else if (token.startsWith("-a:")) {
        try {
          autoTicks = Integer.parseInt(token.replace("-a:", ""));
          if (secondsCount != 0.0 && adjustPerc != null) {
            adjustPerc = adjustPerc * (autoTicks.intValue() / 20.0);
          }
        } catch (Exception exception6) {
        }
      } else if (token.startsWith("-c:")) {
        barColor = getBarColor(token);
      } else if (token.startsWith("-cc:")) {
        String[] colorNames = token.substring(4).split(",");
        for (String colorName : colorNames) {
          if (colorChangeTicks < 0) {
            colorChangeTicks = (int) (Double.parseDouble(colorName) * 20.0);
          } else {
            CMIChatColor color = CMIChatColor.getColor(colorName);
            if (color != null && color.isValid()) {
              colors.add(color);
            }
          }
        }
      } else if (token.startsWith("-cw:")) {
        try {
          int steps = 0;
          String[] parts = token.substring(4).split(",");
          for (String part : parts) {
            if (colorChangeTicks < 0) {
              colorChangeTicks = (int) (Double.parseDouble(part) * 20.0);
            } else {
              if (steps == 0) {
                try {
                  steps = Integer.parseInt(part);
                  continue;
                } catch (Throwable throwable) {
                }
              }

              CMIChatColor color = CMIChatColor.getColor(part);
              if (color != null && color.isValid()) {
                colors.add(color);
              }
            }
          }

          colors.add(colors.get(0));
          ArrayList<CMIChatColor> gradient = new ArrayList<CMIChatColor>();
          CMIChatColor fromColor = colors.get(0);
          CMIChatColor toColor = colors.get(1);
          int count = steps < 1 ? 5 : steps;
          count = CMINumber.clamp(count, 1, 30);
          double stepPercent = 100 / count;

          for (int index = 1; index < colors.size(); index++) {
            CMIChatColor next = colors.get(index);

            for (int step = 1; step < count; step++) {
              gradient.add(CMIChatColor.mixColors(fromColor, next, step * stepPercent));
            }

            fromColor = next;
          }

          colors.clear();
          colors.addAll(gradient);
        } catch (Exception exception7) {
        }
      } else {
        if (!message.toString().isEmpty()) {
          message.append(" ");
        }

        message.append(token);
      }
    }

    if (!commands.isEmpty()
        && !PermissionsManager.CMIPerm.command_bossbarmsg_admin.hasPermission(
            commandSender, true)) {
      commands.clear();
    }

    if (autoTicks == null && !colors.isEmpty()) {
      autoTicks = 2;
    }

    if (keepForTicks != null && keepForTicks < 0) {
      adjustPerc = 0.0;
    }

    if (player != null) {
      Snd snd = new Snd().setSender(commandSender).setTarget(player);
      String title = cmi.getLM().updateSnd(snd, message.toString());
      Double progress = getProgress(player, currentValue, maxValue);
      CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
      BossBarInfo bossBarInfo = new BossBarInfo(cmiUser.getPlayer(), barName);
      if (!PermissionsManager.CMIPerm.bossbar_colors.hasPermission(commandSender)) {
        bossBarInfo.setTranslateColors(false);
      }

      bossBarInfo.setKeepForTicks(keepForTicks);
      bossBarInfo.setAdjustPerc(adjustPerc);
      bossBarInfo.setColor(barColor);
      bossBarInfo.setTitleOfBar(title);
      bossBarInfo.setPercentage(progress);
      bossBarInfo.setStyle(barStyle);
      bossBarInfo.setAuto(autoTicks);

      try {
        bossBarInfo.setColors(colors);
        bossBarInfo.setColorChangeIntervalTicks(colorChangeTicks < 0 ? 20 : colorChangeTicks);
      } catch (Throwable throwable2) {
        throwable2.printStackTrace();
      }

      commands.addAll(playerCommands);
      bossBarInfo.setCmds(commands);
      cmiUser.addBossBar(bossBarInfo);
    } else {
      if (!toAll) {
        return false;
      }

      Double progress = null;

      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        Snd snd = new Snd().setSender(commandSender).setTarget(onlinePlayer);
        progress = getProgress(onlinePlayer, currentValue, maxValue);
        String title = cmi.getLM().updateSnd(snd, message.toString());
        CMIUser cmiUser = cmi.getPlayerManager().getUser(onlinePlayer);
        progress = getProgress(onlinePlayer, currentValue, maxValue);
        BossBarInfo bossBarInfo = new BossBarInfo(cmiUser.getPlayer(), barName);
        bossBarInfo.setKeepForTicks(keepForTicks);
        bossBarInfo.setAdjustPerc(adjustPerc);
        bossBarInfo.setColor(barColor);
        if (!PermissionsManager.CMIPerm.bossbar_colors.hasPermission(commandSender)) {
          bossBarInfo.setTranslateColors(false);
        }

        bossBarInfo.setTitleOfBar(title);
        bossBarInfo.setPercentage(progress);
        bossBarInfo.setStyle(barStyle);
        bossBarInfo.setAuto(autoTicks);

        try {
          bossBarInfo.setColors(new ArrayList<CMIChatColor>(colors));
          bossBarInfo.setColorChangeIntervalTicks(colorChangeTicks < 0 ? 20 : colorChangeTicks);
        } catch (Throwable throwable3) {
        }

        bossBarInfo.setCmds(playerCommands);
        bossBarInfo.setGlobal(true);
        cmiUser.addBossBar(bossBarInfo);
      }

      BossBarInfo globalBar = new BossBarInfo(null, barName);
      globalBar.setKeepForTicks(keepForTicks);
      globalBar.setAdjustPerc(adjustPerc);
      globalBar.setColor(barColor);
      if (!PermissionsManager.CMIPerm.bossbar_colors.hasPermission(commandSender)) {
        globalBar.setTranslateColors(false);
      }

      globalBar.setTitleOfBar(message.toString());
      globalBar.setPercentage(progress);
      globalBar.setStyle(barStyle);
      globalBar.setAuto(autoTicks);

      try {
        globalBar.setColors(colors);
        globalBar.setColorChangeIntervalTicks(colorChangeTicks < 0 ? 20 : colorChangeTicks);
      } catch (Throwable throwable4) {
      }

      globalBar.setCmds(commands);
      globalBar.setGlobal(true);
      CMILib.getInstance().getBossBarManager().addGlobalBar(globalBar);
    }

    return true;
  }

  private static Double getProgress(Player player, String firstValue, String secondValue) {
    Double progress = 0.0;
    if (!firstValue.isEmpty() && !secondValue.isEmpty()) {
      Double divisor = 100.0;

      try {
        divisor =
            Double.parseDouble(
                CMI.getInstance()
                    .getPlaceholderAPIManager()
                    .updatePlaceHolders(player, firstValue));
      } catch (Exception exception) {
        exception.printStackTrace();
      }

      Double numerator = 0.0;

      try {
        numerator =
            Double.parseDouble(
                CMI.getInstance()
                    .getPlaceholderAPIManager()
                    .updatePlaceHolders(player, secondValue));
      } catch (Exception exception2) {
        exception2.printStackTrace();
      }

      if (numerator <= 0.0) {
        numerator = 0.0;
      }

      if (divisor <= 0.0) {
        divisor = numerator + 1.0;
      }

      progress = numerator / divisor;
      if (progress > 1.0) {
        progress = 1.0;
      }

      if (progress < 0.0) {
        progress = 0.0;
      }

      return progress;
    } else {
      return null;
    }
  }

  private static void cancelBossbar(String token, boolean toAll, Player player) {
    if (token != null) {
      String barName = token.replace("-cancel:", "");
      if (!barName.equals("*")) {
        if (toAll) {
          for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CMIUser cmiUser = CMIUser.getUser(onlinePlayer);
            BossBarInfo bossBarInfo = cmiUser.getBossBar(barName);
            if (bossBarInfo != null) {
              bossBarInfo.remove();
            }
          }

          CMILib.getInstance().getBossBarManager().removeGlobalBossbar(barName);
        } else {
          CMIUser cmiUser = CMIUser.getUser(player);
          BossBarInfo bossBarInfo = cmiUser.getBossBar(barName);
          if (bossBarInfo != null) {
            bossBarInfo.remove();
          }
        }
      } else if (!toAll) {
        CMIUser.getUser(player).hideBossBars();
      } else {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          CMIUser.getUser(onlinePlayer).hideBossBars();
        }
      }
    }
  }

  private static BarStyle getStyle(String token) {
    if (token == null) {
      return null;
    }

    String value = token.replace("-s:", "");
    switch (value.hashCode()) {
      case 49:
        if (value.equals("1")) {
          return BarStyle.SOLID;
        }
        break;
      case 54:
        if (value.equals("6")) {
          return BarStyle.SEGMENTED_6;
        }
        break;
      case 1567:
        if (value.equals("10")) {
          return BarStyle.SEGMENTED_10;
        }
        break;
      case 1569:
        if (value.equals("12")) {
          return BarStyle.SEGMENTED_12;
        }
        break;
      case 1598:
        if (value.equals("20")) {
          return BarStyle.SEGMENTED_20;
        }
    }

    return null;
  }

  private static BarColor getBarColor(String token) {
    if (token == null) {
      return null;
    }

    String colorName = token.replace("-c:", "");

    try {
      for (BarColor barColor : BarColor.values()) {
        if (barColor.name().equalsIgnoreCase(colorName)) {
          return barColor;
        }
      }
    } catch (Exception exception) {
    }

    return null;
  }
}
