/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMINumber
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Messages.CMIMessages
 *  net.Zrips.CMILib.Version.Schedulers.CMIScheduler
 *  net.Zrips.CMILib.Version.Schedulers.CMITask
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.Zrips.CMI.Modules.Sheduler;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIWeekday;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SchedulerManager {
  private HashMap<String, Schedule> map = new HashMap();
  private CMI plugin;
  private CMITask autoTimerBukkitId = null;
  public static final String randomPlayerLabel = "[randomPlayer]";
  private String fileName = "Schedules.yml";
  private Runnable autoTimer =
      () -> {
        try {
          this.checkSchedulers();
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
        this.runTimer();
      };

  public SchedulerManager(CMI cMI) {
    this.plugin = cMI;
  }

  public Schedule getSchedule(String string) {
    return this.getSchedule(string, false);
  }

  public Schedule getSchedule(String string, boolean bl) {
    Schedule schedule = this.map.get(string.toLowerCase());
    if (schedule == null || !schedule.isEnabled() && !bl) {
      return null;
    }
    return schedule;
  }

  public void stop() {
    if (this.autoTimerBukkitId != null) {
      this.autoTimerBukkitId.cancel();
      this.autoTimerBukkitId = null;
    }
  }

  public void load() {
    Long l = System.currentTimeMillis();
    this.stop();
    CMI.getInstance()
        .safeRenameFile(this.fileName.toLowerCase(), "Settings" + File.separator + this.fileName);
    File file = new File(this.plugin.getDataFolder(), "Settings" + File.separator + this.fileName);
    ConfigReader configReader = null;
    try {
      configReader =
          new ConfigReader((Plugin) CMI.getInstance(), "Settings" + File.separator + this.fileName);
      configReader.load();
      configReader.getC().options().copyDefaults(true);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    if (configReader != null) {
      configReader.header(
          new ArrayList<String>(
              Arrays.asList(
                  "Scheduler file where you can set commands to be performed in regular intervals"
                      + " or in particular time",
                  "Enabled: (true/false) only schedulers which are enabled will be included",
                  "  There can be 2 types, performed in regular time intervals (in seconds) or on"
                      + " particular time",
                  "Delay: (number) defines how long to wait between each action, 600 means that"
                      + " actions will be performed every 10 minutes",
                  "Randomize: (true/false) if set to true, then only one and random of defined"
                      + " commands will be used. Commands will not repeat untill all of them got"
                      + " used up. Usefull for repeating random announcements",
                  "DuplicateRandomize: (true/false) if set to true, then only one and random of"
                      + " defined commands will be used. Commands might repeat. Usefull for"
                      + " repeating random announcements",
                  "SingleLinear: (true/false) if set to true, then only one of defined commands"
                      + " will be used in a row without repeating until all of them have been"
                      + " executed. Usefull in cases when it needs to be shown in exact order one"
                      + " by one",
                  "PerformOn: section will define particular times when perform commands. First of"
                      + " name should be defined and then additional time frames should be given",
                  "  It can have: Month, Day, Hour, Minute, Second sections. All of them in number"
                      + " format and hours uses 24 hour format",
                  "  Example",
                  "  PerformOn:",
                  "    FirstTimeFrame:",
                  "      Hour: 4",
                  "    SecondTimeFrame:",
                  "      Hour: 22",
                  "      Minute: 30",
                  "  You can use week day names to indicate at which day in a week this sheduler"
                      + " should be performed. Example - Day: Monday",
                  "  This will set commands to be performed on 4 a clock in morning and at 22:30."
                      + " Good way to control server backups when there is less players online",
                  "Repeat: (true/false) if set to false, action will be performed only once other"
                      + " wise it will be repeated all the time in intervals or on particular time",
                  "MinPlayers: (amount) Will limit minimum player amount there should be online"
                      + " when command performe",
                  "MinPlayersCommands: List of commands to be performed when online player amount"
                      + " is under specified limit",
                  "MaxPlayers: (amount) Will skip scheduler if there is more than defined amount of"
                      + " players",
                  "Feedback: (true/false) if set to false will not show feedback message in console"
                      + " in case there is not enough players to perform this schedule",
                  "DontTranslatePlaceholders: (true/false) if set to true we will not translate"
                      + " placeholders inside commands before sending them to later processing",
                  "  This defaults to false and placeholders will be translated before hand",
                  "  Exception for delay! specialized command variable which always gets translated"
                      + " before hand",
                  "Commands: List of commands to be performed when time is correct. ",
                  "  You can multiple commands defined in single line, separate each one with ;;"
                      + " for example: command1;;command2;;command3",
                  "  If command starts with msg! and then player name is given, simple message will"
                      + " be sent to that player if he is online.",
                  "  If command starts with broadcast! then message will be sent to everyone on"
                      + " server in simple manear",
                  "  If command starts with actionbar! then all players will get action bar message"
                      + " defined after this variable",
                  "  If command starts with title! then all players will get title message defined"
                      + " after this variable",
                  "  If command starts with subtitle! then all players will get subtitle message"
                      + " defined after this variable",
                  "  If command starts with kickall! then all players will be kicked from server"
                      + " with defined message. Usefull before server stop.",
                  "  If command starts with perm:[permissionNode]! then command will get performed"
                      + " if player has permission node. This will not work when we don't know who"
                      + " is target player. In example: perm:cmi.announce.vip! broadcast! Hello vip"
                      + " guys",
                  "  If command starts with moneycost:[amount]! then command will get performed if"
                      + " player has enough money. ",
                  "  If command starts with expcost:[amount]! then command will get performed if"
                      + " player has enough exp.",
                  "  If command starts with allPlayers! then command will get performed by using"
                      + " everyones who is online name. Command itself need to contain [allPlayers]"
                      + " to indicate where player name needs to be added",
                  "  Insert line with 'delay! 5' to perform rest of commands after 5 seconds from"
                      + " scheduler trigger. This allows to create in example counter before server"
                      + " stop. Check example.",
                  "  [randomPlayer] placeholder can be used to get random online player name who"
                      + " don't have cmi.scheduler.exclude permission node. This can be used to"
                      + " give rewards for random players on particular time. In example: - cmi"
                      + " give [randomPlayer] diamond %rand/1-5% will give rando mamount from 1 to"
                      + " 5 diamonds to random online player",
                  "Supports PlaceHolderAPI variables",
                  "More information about specialized commands can be found at"
                      + " https://www.zrips.net/cmi/commands/specialized/")));
      configReader.save();
    }
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException iOException) {
        iOException.printStackTrace();
      }
      return;
    }
    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((File) file);
    ArrayList<String> arrayList = new ArrayList<String>();
    for (Map.Entry<String, Schedule> entry : this.map.entrySet()) {
      arrayList.add(entry.getKey());
    }
    if (yamlConfiguration.getKeys(false).isEmpty()) {
      return;
    }
    Map map = yamlConfiguration.getValues(false);
    for (Map.Entry entry : ((java.util.Map<?, ?>) map).entrySet()) {
      Schedule schedule;
      block81:
      {
        schedule = this.getSchedule((String) entry.getKey());
        if (schedule == null) {
          schedule = new Schedule((String) entry.getKey());
        } else {
          schedule.reset();
        }
        try {
          Map scheduleMap;
          block82:
          {
            List<Map<?, ?>> mapList = null;
            scheduleMap =
                yamlConfiguration.getConfigurationSection((String) entry.getKey()).getValues(false);
            if (!scheduleMap.containsKey("Enabled")
                || !((Boolean) scheduleMap.get("Enabled")).booleanValue()) {
              schedule.setEnabled(false);
            } else if (scheduleMap.containsKey("Enabled")
                || ((Boolean) scheduleMap.get("Enabled")).booleanValue()) {
              schedule.setEnabled(true);
            }
            if (scheduleMap.containsKey("Delay")) {
              if (scheduleMap.get("Delay") instanceof Integer) {
                schedule.setDelay(((Integer) scheduleMap.get("Delay")).intValue());
              } else if (scheduleMap.get("Delay") instanceof Double) {
                schedule.setDelay(((Double) scheduleMap.get("Delay")).longValue());
              }
              schedule.setLastPerformedOn();
            }
            if (scheduleMap.containsKey("Repeat")) {
              schedule.setRepeatable((Boolean) scheduleMap.get("Repeat"));
            }
            if (scheduleMap.containsKey("Randomize")) {
              schedule.setRandomize((Boolean) scheduleMap.get("Randomize"));
            }
            if (scheduleMap.containsKey("DuplicateRandomize")) {
              schedule.setDuplicateRandomize((Boolean) scheduleMap.get("DuplicateRandomize"));
            }
            if (scheduleMap.containsKey("SingleLinear")) {
              schedule.setSingleLinear((Boolean) scheduleMap.get("SingleLinear"));
            }
            if (scheduleMap.containsKey("MinPlayers")) {
              schedule.setMinPlayer((int) ((Integer) scheduleMap.get("MinPlayers")));
            }
            if (scheduleMap.containsKey("MaxPlayers")) {
              schedule.setMaxPlayer((int) ((Integer) scheduleMap.get("MaxPlayers")));
            }
            if (scheduleMap.containsKey("FeedBack")) {
              schedule.setPlayerAmountFeedback((boolean) ((Boolean) scheduleMap.get("FeedBack")));
            }
            if (scheduleMap.containsKey("Feedback")) {
              schedule.setPlayerAmountFeedback((boolean) ((Boolean) scheduleMap.get("Feedback")));
            }
            if (scheduleMap.containsKey("DontTranslatePlaceholders")) {
              schedule.setDontTranslatePlaceholders(
                  (Boolean) scheduleMap.get("DontTranslatePlaceholders"));
            }
            if (!scheduleMap.containsKey("PerformOn")) break block82;
            ConfigurationSection configurationSection =
                yamlConfiguration.getConfigurationSection(
                    String.valueOf((String) entry.getKey()) + ".PerformOn");
            ArrayList<Map> performOnList = new ArrayList<Map>();
            if (configurationSection != null) {
              for (Map.Entry sectionEntry : configurationSection.getValues(false).entrySet()) {
                if (!(sectionEntry.getValue() instanceof ConfigurationSection)) continue;
                performOnList.add(
                    ((ConfigurationSection) sectionEntry.getValue()).getValues(false));
              }
            }
            if (performOnList.isEmpty()
                && (mapList =
                        yamlConfiguration.getMapList(
                            String.valueOf((String) entry.getKey()) + ".PerformOn"))
                    != null) {
              performOnList.addAll((Collection) mapList);
            }
            List<SchedTime> schedTimes = new ArrayList<>();
            for (Map dayMap : performOnList) {
              SchedTime schedTime;
              block83:
              {
                int n;
                block85:
                {
                  block84:
                  {
                    schedTime = new SchedTime();
                    if (dayMap.containsKey("Year")) {
                      schedTime.setYear((int) ((Integer) dayMap.get("Year")));
                    }
                    if (dayMap.containsKey("Month")) {
                      schedTime.setMonth((int) ((Integer) dayMap.get("Month")));
                    }
                    if (!dayMap.containsKey("Day")) break block83;
                    n = -1;
                    Object v = dayMap.get("Day");
                    if (!(v instanceof Integer)) break block84;
                    n = (Integer) v;
                    break block85;
                  }
                  int dayCount = 2;
                  switch (((String) dayMap.get("Day")).toLowerCase()) {
                    case "monday":
                      {
                        dayCount = 2;
                        break;
                      }
                    case "tuesday":
                      {
                        dayCount = 3;
                        break;
                      }
                    case "wednesday":
                      {
                        dayCount = 4;
                        break;
                      }
                    case "thursday":
                      {
                        dayCount = 5;
                        break;
                      }
                    case "friday":
                      {
                        dayCount = 6;
                        break;
                      }
                    case "saturday":
                      {
                        dayCount = 7;
                        break;
                      }
                    case "sunday":
                      {
                        dayCount = 1;
                      }
                  }
                  schedTime.setDayOfWeek(dayCount);
                }
                if (n > 0) {
                  schedTime.setDay(n);
                }
              }
              if (dayMap.containsKey("FirstMonthDay")) {
                Object v = dayMap.get("FirstMonthDay");
                if (v instanceof Integer) {
                  schedTime.setFirstDayOfMonth(CMIWeekday.get((Integer) v));
                } else {
                  schedTime.setFirstDayOfMonth(CMIWeekday.get((String) v));
                }
              }
              if (dayMap.containsKey("LastMonthDay")) {
                Object v = dayMap.get("LastMonthDay");
                if (v instanceof Integer) {
                  schedTime.setLastDayOfMonth(CMIWeekday.get((Integer) v));
                } else {
                  schedTime.setLastDayOfMonth(CMIWeekday.get((String) v));
                }
              }
              if (dayMap.containsKey("Hour")) {
                if (dayMap.get("Hour") instanceof Integer) {
                  schedTime.setHour((int) ((Integer) dayMap.get("Hour")));
                } else {
                  schedTime.setHour(((Double) dayMap.get("Hour")).intValue());
                }
              }
              if (dayMap.containsKey("Minute")) {
                if (dayMap.get("Minute") instanceof Integer) {
                  schedTime.setMinute((int) ((Integer) dayMap.get("Minute")));
                } else {
                  schedTime.setMinute(((Double) dayMap.get("Minute")).intValue());
                }
              }
              if (dayMap.containsKey("Second")) {
                if (dayMap.get("Second") instanceof Integer) {
                  schedTime.setSecond((int) ((Integer) dayMap.get("Second")));
                } else {
                  schedTime.setSecond(((Double) dayMap.get("Second")).intValue());
                }
              }
              schedTimes.add(schedTime);
            }
            schedule.setTime(schedTimes);
          }
          if (scheduleMap.containsKey("Commands")) {
            schedule.setCommands((List) scheduleMap.get("Commands"));
          }
          if (!scheduleMap.containsKey("MinPlayersCommands")) break block81;
          schedule.setMinPlayerCommands((List) scheduleMap.get("MinPlayersCommands"));
        } catch (Exception exception) {
          CMIMessages.consoleMessage(
              (String) ("Cant load schedule (" + (String) entry.getKey() + ") "));
          exception.printStackTrace();
          continue;
        }
      }
      if (schedule.getCommands().isEmpty()) {
        CMIMessages.consoleMessage(
            (String) ("Cant load schedule (" + (String) entry.getKey() + ") missing commands"));
        continue;
      }
      this.map.put(schedule.getName().toLowerCase(), schedule);
      arrayList.remove(schedule.getName().toLowerCase());
    }
    for (String string : arrayList) {
      this.map.remove(string);
    }
    if (!this.map.isEmpty()) {
      this.plugin.loadMessage(this.map.size(), "schedules", System.currentTimeMillis() - l);
      if (CMIModule.schedule.isEnabled()) {
        this.runTimer();
      }
    }
  }

  private void runTimer() {
    Long l = SchedulerManager.nextIn();
    if (l == null) {
      return;
    }
    if (this.autoTimerBukkitId != null) {
      this.autoTimerBukkitId.cancel();
    }
    l = CMINumber.clamp((long) l, (long) -9223372036854775807L, (long) 1800000L);
    this.autoTimerBukkitId =
        CMIScheduler.runTaskLater(
            (JavaPlugin) this.plugin,
            (Runnable) this.autoTimer,
            (long) ((Long) CMINumber.clamp(Long.valueOf(l / 50L), Long.valueOf(1L))));
  }

  private static Long nextIn() {
    Long l = System.currentTimeMillis();
    Long earliest = Long.MAX_VALUE;
    for (Map.Entry<String, Schedule> entry :
        CMI.getInstance().getSchedulerManager().getMap().entrySet()) {
      Schedule schedule = entry.getValue();
      if (!schedule.isEnabled()) continue;
      Long next = schedule.getPerformOn();
      if (earliest <= next) continue;
      earliest = next;
    }
    return earliest == Long.MAX_VALUE ? null : Long.valueOf(earliest - l);
  }

  public int getActualWeekDay(int n) {
    switch (n) {
      case 1:
        {
          return 7;
        }
      case 2:
        {
          return 1;
        }
      case 3:
        {
          return 2;
        }
      case 4:
        {
          return 3;
        }
      case 5:
        {
          return 4;
        }
      case 6:
        {
          return 5;
        }
      case 7:
        {
          return 6;
        }
    }
    return 1;
  }

  private void checkSchedulers() {
    for (Map.Entry<String, Schedule> entry : this.map.entrySet()) {
      Schedule schedule = entry.getValue();
      if (!schedule.isEnabled() || !schedule.itsTimeToPerform()) continue;
      schedule.safePerform();
    }
  }

  public HashMap<String, Schedule> getMap() {
    return this.map;
  }
}
