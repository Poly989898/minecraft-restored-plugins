package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CounterInfo;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.TitleMessages.CMITitleMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class counter implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("join", "{gcp}Joined counter");
    configReader.get("leave", "{gcp}Left counter");
    configReader.get("go", "{gcp}GO!");
    configReader.get("tooFast", "{gcw}Cant repeat command that fast, wait: {gcs}[sec]{gcp}sec");
    configReader.get("noOne", "&cNo one seeing this counter!");
  }

  @CAnnotation(
      priority = 230,
      info = "Starts counter for surrounding players",
      args =
          "[join/leave/start] (t:time) (r:[range/-1]) (c:[world:x:y:z]) (msg:custom_message) (-f)",
      tab = {"join%%leave%%start", "%%-f"},
      explanation = {
        "Permissions:",
        "cmi.command.counter.force - to force counter for everyone in range ",
        "cmi.command.counter.time - define custom time range ",
        "cmi.command.counter.range - to define custom range",
        "cmi.command.counter.msg - to define custom message",
        "cmi.command.counter.autojoin - joins counter automatically",
        "-f will force counter to all players in range",
        "Example: /cmi counter start r:30 t:7 msg:&eCustom_message -f"
      },
      regVar = -100,
      consoleVar = -100,
      alias = true,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = null;
    if (commandSender instanceof Player) {
      player = (Player) commandSender;
    }

    Location location = null;
    if (player != null) {
      location = player.getLocation();
    }

    if (strings.length > 0) {
      String string;
      switch ((string = strings[0].toLowerCase()).hashCode()) {
        case 3267882:
          if (string.equals("join")) {
            if (player != null) {
              CMIUser cmiUser = cmi.getPlayerManager().getUser(player);
              cmiUser.setJoinedCounter(true);
              CMILC.info(this, commandSender, "join");
            }

            return true;
          }
          break;
        case 102846135:
          if (string.equals("leave")) {
            if (player != null) {
              CMIUser counterUser = cmi.getPlayerManager().getUser(player);
              counterUser.setJoinedCounter(false);
              CMILC.info(this, commandSender, "leave");
            }

            return true;
          }
          break;
        case 109757538:
          if (string.equals("start")) {
            if (player != null) {
              CMIUser counterUserAlt = cmi.getPlayerManager().getUser(player);
              Long startValue = counterUserAlt.getCounter("counter start");
              if (startValue != null
                  && startValue > System.currentTimeMillis()
                  && !PermissionsManager.CMIPerm.command_counter_force.hasPermission(
                      commandSender)) {
                CMILC.info(
                    this,
                    commandSender,
                    "tooFast",
                    "[sec]",
                    (startValue - System.currentTimeMillis()) / 1000L + 1L);
                return null;
              }
            }

            boolean flag = false;
            int value = 3;
            int range = cmi.getConfigManager().CounterRange;
            String goText = CMILC.getIM(this, "go");
            if (strings.length > 1) {
              for (int argIndex = 1; argIndex < strings.length; argIndex++) {
                if (strings[argIndex].equalsIgnoreCase("-f")) {
                  if (PermissionsManager.CMIPerm.command_counter_force.hasPermission(
                      commandSender)) {
                    flag = true;
                  }
                } else {
                  if (strings[argIndex].contains("t:")) {
                    try {
                      if (PermissionsManager.CMIPerm.command_counter_time.hasPermission(
                          commandSender)) {
                        value = Integer.parseInt(strings[argIndex].split("t:")[1]);
                        continue;
                      }
                    } catch (NumberFormatException numberFormatException) {
                    }
                  }

                  if (strings[argIndex].contains("r:")) {
                    try {
                      if (PermissionsManager.CMIPerm.command_counter_range.hasPermission(
                          commandSender)) {
                        range = Integer.parseInt(strings[argIndex].split("r:")[1]);
                        continue;
                      }
                    } catch (NumberFormatException numberFormatException2) {
                    }
                  }

                  if (strings[argIndex].contains("c:")) {
                    try {
                      if (PermissionsManager.CMIPerm.command_counter_range.hasPermission(
                          commandSender)) {
                        String arg = strings[argIndex].substring("c:".length());
                        World world = cmi.getUtilManager().getWorld(arg.split(":")[0]);
                        Double startDouble = Double.parseDouble(arg.split(":")[1]);
                        Double stepDouble = Double.parseDouble(arg.split(":")[2]);
                        Double endDouble = Double.parseDouble(arg.split(":")[3]);
                        if (world == null) {
                          return false;
                        }

                        location = new Location(world, startDouble, stepDouble, endDouble);
                        continue;
                      }
                    } catch (NumberFormatException numberFormatException3) {
                    }
                  }

                  if (strings[argIndex].toLowerCase().startsWith("msg:")) {
                    try {
                      if (PermissionsManager.CMIPerm.command_counter_msg.hasPermission(
                          commandSender)) {
                        goText = strings[argIndex].substring("msg:".length()).replace("_", " ");
                      }
                    } catch (NumberFormatException numberFormatException4) {
                    }
                  }
                }
              }
            }

            if (location == null) {
              return false;
            }

            if (value > 30) {
              value = 30;
            }

            List<Player> list = new ArrayList();
            if (range > 0) {
              list = cmi.getUtilManager().getPlayersFromRangeForCounter(location, range, flag);
            } else {
              for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                list.add(onlinePlayer);
              }
            }

            if (player != null) {
              list.add(player);
            }

            if (!list.isEmpty() && (list.size() != 1 || !(commandSender instanceof Player))) {
              if (player != null) {
                CMIUser startingUser = cmi.getPlayerManager().getUser(player);
                startingUser.addCounter("counter start", System.currentTimeMillis() + value * 1000);
              }

              CounterInfo counterInfo = new CounterInfo(goText, list, value, cmi);
              this.Timer(counterInfo);
              return true;
            }

            CMILC.info(this, commandSender, "noOne");
            return null;
          }
      }
    }

    return false;
  }

  private void Timer(CounterInfo counterInfo) {
    if (counterInfo.getLeftTime() > 0) {
      for (Player player : counterInfo.getList()) {
        if (player.isOnline()) {
          CMITitleMessage.send(player, String.valueOf(counterInfo.getLeftTime()), null);
        }
      }
    } else if (counterInfo.getLeftTime() == -1) {
      for (Player titlePlayer : counterInfo.getList()) {
        if (titlePlayer.isOnline()) {
          CMITitleMessage.send(titlePlayer, "", null);
        }
      }
    }

    CMIScheduler.runTaskLater(
        CMI.getInstance(),
        () -> {
          if (counterInfo.getLeftTime() == 1) {
            for (Player titlePlayer : counterInfo.getList()) {
              if (titlePlayer.isOnline()) {
                CMITitleMessage.send(titlePlayer, counterInfo.getMessage(), null);
              }
            }
          }

          if (counterInfo.getLeftTime() >= 0) {
            counterInfo.lowerTime();
            this.Timer(counterInfo);
          }
        },
        20L);
  }
}
