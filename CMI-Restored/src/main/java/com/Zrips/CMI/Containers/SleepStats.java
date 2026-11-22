package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.utils.TimeManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SleepStats {
  private World world;
  private int speed;
  private double sleeping;
  private double total;
  private double percent;
  private int online;
  CMITask task = null;
  public static List<String> timeSpeedUpWorlds = new ArrayList<>();
  private static boolean SleepingPercentage;
  public static boolean SleepingExcludeAfk;
  public static boolean SleepingSpeedup;
  public static boolean SleepingOnlyDurringNight;
  public static boolean SleepingInform;
  private static int SleepingBaseSpeed;
  private static int SleepingMinBeforeSpeeding;
  private static int SleepingMinSpeed;
  public static int SleepingInformDelay;
  public static int SleepingNightStartsAt;
  public static String SleepingSpeedupInfoType;
  public static HashMap<UUID, Long> informMap = new HashMap<>();

  public static void loadConfig() {
    ConfigReader configReader = CMI.getInstance().getConfigManager().getConfig();
    configReader.addComment(
        "Sleeping.Speedup.Enabled",
        new String[] {
          "When set to true, players can speedup night by sleeping in bed",
          "This will allow to speed up night in percentage depending how many players are sleeping"
              + " in beds in that world"
        });
    SleepingSpeedup = configReader.get("Sleeping.Speedup.Enabled", true);
    configReader.addComment(
        "Sleeping.Speedup.Worlds",
        new String[] {
          "List of worlds where this should be applied.",
          "Keep in mind that time speed up only works on a normal type world and by default you"
              + " will have only one",
          "Set this list to [] if you want to include all possible worlds"
        });
    timeSpeedUpWorlds = configReader.get("Sleeping.Speedup.Worlds", new ArrayList());
    CMIList.toLowerCase(timeSpeedUpWorlds);
    configReader.addComment(
        "Sleeping.Speedup.OnlyDurringNight",
        new String[] {
          "When this set to true time will be speed up only between 12542 and 24000 ticks of the"
              + " day",
          "When having this set to false players can speed up day durring storms or other events"
        });
    SleepingOnlyDurringNight = configReader.get("Sleeping.Speedup.OnlyDurringNight", true);
    configReader.addComment(
        "Sleeping.Speedup.NightStartsAt",
        new String[] {"Time when night starts", "This is in ticks, so 12542 will be 18:32:00"});
    SleepingNightStartsAt = configReader.get("Sleeping.Speedup.NightStartsAt", 12542);
    configReader.addComment(
        "Sleeping.Speedup.InfoType",
        new String[] {"Type of speedup information, can be: none, title, bossbar"});
    SleepingSpeedupInfoType = configReader.get("Sleeping.Speedup.InfoType", "title");
    configReader.addComment(
        "Sleeping.Speedup.ExcludeAfk",
        new String[] {
          "When set to true, players who are in afk mode will be excluded from speed calculations"
        });
    SleepingExcludeAfk = configReader.get("Sleeping.Speedup.ExcludeAfk", true);
    configReader.addComment(
        "Sleeping.Speedup.BaseSpeed",
        new String[] {
          "Defines speed to go throw night, bigger numbers will make it go faster and less players"
              + " you will need to go throw night",
          "100 will result in 100 times faster time"
        });
    SleepingBaseSpeed = configReader.get("Sleeping.Speedup.BaseSpeed", 100);
    configReader.addComment(
        "Sleeping.Speedup.MinSpeed",
        new String[] {
          "Defines minimal speed to go throw night, this is in case there are more players than"
              + " base speed and calculation return default speed"
        });
    SleepingMinSpeed = configReader.get("Sleeping.Speedup.MinSpeed", 5);
    configReader.addComment(
        "Sleeping.Speedup.MinBeforeSpeeding",
        new String[] {
          "Minimal amount of players sleeping in beds before speeding it up",
          "Can be defined in 2 formats. When using clean number like 3, then 3 players will have to"
              + " be sleeping before speedup kicks in",
          "If amount is defined with % like 50% then half of server population will have to be"
              + " sleeping before speedup kicks in"
        });
    String string = configReader.get("Sleeping.Speedup.MinBeforeSpeeding", "50%");
    if (string.contains("%")) {
      try {
        SleepingMinBeforeSpeeding = Integer.parseInt(string.replace("%", ""));
        SleepingPercentage = true;
      } catch (Exception exception) {
        SleepingMinBeforeSpeeding = 1;
        SleepingPercentage = false;
      }
    } else {
      SleepingPercentage = false;

      try {
        SleepingMinBeforeSpeeding = Integer.parseInt(string);
      } catch (Exception exception2) {
        SleepingMinBeforeSpeeding = 1;
      }
    }

    configReader.addComment(
        "Sleeping.Speedup.Inform",
        new String[] {
          "When set to true online players will be informed about missing sleeping people count"
        });
    SleepingInform = configReader.get("Sleeping.Speedup.Inform", true);
    configReader.addComment(
        "Sleeping.Speedup.InformDelay",
        new String[] {
          "Time in seconds between information messages can be shown when player starts or stops"
              + " sleeping"
        });
    SleepingInformDelay = configReader.get("Sleeping.Speedup.InformDelay", 30);
  }

  public SleepStats(World sleepWorld) {
    this.world = sleepWorld;
    this.sleeping = 0.0;
    this.total = 0.0;
    this.online = 0;

    for (Player player : Bukkit.getOnlinePlayers()) {
      CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
      if (player.getWorld().equals(sleepWorld)
          && !PermissionsManager.CMIPerm.sleepignore.hasSetPermission(player)) {
        boolean flag = cmiUser.isAfk();
        if (!cmiUser.isVanished() && flag && !SleepingExcludeAfk) {
          this.online++;
        }

        if (!flag && !cmiUser.isVanished()
            || flag && !SleepingExcludeAfk && !cmiUser.isVanished()) {
          this.total++;
        }

        if (player.isSleeping()
            && !cmiUser.isVanished()
            && (!flag || flag && !SleepingExcludeAfk)) {
          this.sleeping++;
        }
      }
    }

    this.percent = this.sleeping / this.total;
    this.speed = (int) (SleepingBaseSpeed * this.percent);
    if (this.speed < SleepingMinSpeed) {
      this.speed = SleepingMinSpeed;
    }
  }

  public World getWorld() {
    return this.world;
  }

  public void setWorld(World newWorld) {
    this.world = newWorld;
  }

  public int getSpeed() {
    return this.speed;
  }

  public void setSpeed(int value) {
    this.speed = value;
  }

  public double getSleeping() {
    return this.sleeping;
  }

  public int needToBeSleeping() {
    int value = SleepingMinBeforeSpeeding;
    if (SleepingPercentage) {
      double number = value / 100.0;
      return (int) Math.ceil(this.getTotal() * number);
    } else {
      return value;
    }
  }

  public void setSleeping(double number) {
    this.sleeping = number;
    this.percent = number / this.total;
    this.speed = (int) (SleepingBaseSpeed * this.percent);
    if (this.speed < SleepingMinSpeed) {
      this.speed = SleepingMinSpeed;
    }
  }

  public double getTotal() {
    return this.total;
  }

  public void setTotal(double number) {
    this.total = number;
  }

  public double getPercent() {
    return this.percent;
  }

  public void setPercent(double number) {
    this.percent = number;
  }

  public int getOnline() {
    return this.online;
  }

  public int getWorldOnline() {
    int value = 0;

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getWorld().equals(this.world)) {
        value++;
      }
    }

    return value;
  }

  public void setOnline(int value) {
    this.online = value;
  }

  public void onBedEnter(Player player) {
    if (!SleepingOnlyDurringNight || player.getWorld().getTime() > SleepingNightStartsAt) {
      CMIScheduler.runTask(
          CMI.getInstance(),
          () -> {
            if (!PermissionsManager.CMIPerm.sleepignore.hasSetPermission(player)) {
              this.setSleeping(this.getSleeping() + 1.0);
            }

            if (this.getSleeping() >= this.getWorldOnline()) {
              if (!CMIGameRule.ADVANCE_TIME.get(player.getWorld())) {
                CMISunMover cmiSunMover =
                    TimeManager.movingmap.getOrDefault(player.getWorld(), new CMISunMover());
                cmiSunMover.setMoving(CMIGameRule.ADVANCE_TIME.get(player.getWorld()));
                TimeManager.movingmap.put(player.getWorld(), cmiSunMover);
                CMIGameRule.ADVANCE_TIME.set(player.getWorld(), true);
              }

              CMI.getInstance().getTimeManager().cancelSunMove(player.getWorld(), false);

              for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getWorld().equals(player.getWorld())) {
                  CMILC.info_skippingNight.sendMessage(onlinePlayer);
                }
              }
            } else if (!(this.needToBeSleeping() > this.getSleeping())) {
              if (this.getSleeping() > 0.0) {
                if (CMIGameRule.ADVANCE_TIME.get(player.getWorld())) {
                  CMI.getInstance()
                      .getTimeManager()
                      .moveSun(player.getWorld(), this.getSpeed(), 1, 23450, true);
                } else {
                  CMI.getInstance()
                      .getTimeManager()
                      .moveSun(player.getWorld(), this.getSpeed(), 1, 23550, true);
                }
              }
            } else {
              Long lastInform = informMap.get(player.getUniqueId());
              if (SleepingInform
                  && (lastInform == null
                      || lastInform + 1000 * SleepingInformDelay < System.currentTimeMillis())) {
                CMIMessages.broadcastMessage(
                    Bukkit.getConsoleSender(),
                    CMILC.info_sleepersRequired.getLocale(
                        "[required]",
                        this.needToBeSleeping(),
                        "[sleeping]",
                        (int) this.getSleeping()),
                    false);
                informMap.put(player.getUniqueId(), System.currentTimeMillis());
              }

              CMI.getInstance().getTimeManager().cancelSunMove(player.getWorld());
            }
          });
    }
  }

  public void onBedLeave(Player player) {
    this.setSleeping(this.getSleeping() - 1.0);
    CMIScheduler.runTask(
        CMI.getInstance(),
        () -> {
          if (this.getSleeping() <= this.getWorldOnline()
              && TimeManager.movingmap.containsKey(player.getWorld())) {
            CMIGameRule.ADVANCE_TIME.set(
                player.getWorld(), TimeManager.movingmap.get(player.getWorld()).getMoving());
          }

          if (this.getSleeping() <= 1.0) {
            CMI.getInstance().getTimeManager().cancelSunMove(player.getWorld());
          } else if (!(this.needToBeSleeping() > this.getSleeping())) {
            if (player.getWorld().getTime() > 10000L) {
              CMI.getInstance()
                  .getTimeManager()
                  .moveSun(player.getWorld(), this.getSpeed(), 1, 23500, true);
            }
          } else {
            if (this.getSleeping() > 0.0 && SleepingInform) {
              Long lastInform = informMap.get(player.getUniqueId());
              if (lastInform == null
                  || lastInform + 1000 * SleepingInformDelay < System.currentTimeMillis()) {
                CMIMessages.broadcastMessage(
                    player,
                    CMILC.info_sleepersRequired.getLocale(
                        "[required]",
                        this.needToBeSleeping(),
                        "[sleeping]",
                        (int) this.getSleeping()),
                    false);
                informMap.put(player.getUniqueId(), System.currentTimeMillis());
              }
            }

            CMI.getInstance().getTimeManager().cancelSunMove(player.getWorld());
          }
        });
  }
}
