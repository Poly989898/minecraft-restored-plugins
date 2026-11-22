package com.Zrips.CMI.Modules.PlayTime;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayTimeManager {
  private CMITask autoTimerBukkitId = null;
  private CMI plugin;
  public static final Long checkIntervalMs = 60000L;
  protected Map<UUID, CMIPlayTime> playtimeCache = new HashMap<>();
  SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
  Calendar calendar = Calendar.getInstance();
  private Runnable autoTimer =
      new Runnable() {
        @Override
        public void run() {
          try {
            PlayTimeManager.this.updatePlayTimes();
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
      };

  public CMIPlayTime getCMIPlayTime(UUID uuid) {
    return this.playtimeCache.computeIfAbsent(uuid, key -> new CMIPlayTime(CMIUser.getUser(uuid)));
  }

  public void setCMIPlayTime(UUID uuid, CMIPlayTime cmiPlayTime) {
    this.playtimeCache.put(uuid, cmiPlayTime);
  }

  public PlayTimeManager(CMI cmi) {
    this.plugin = cmi;
    if (CMIModule.cmiPlaytime.isEnabled()) {
      this.autoTimerBukkitId =
          CMIScheduler.scheduleSyncRepeatingTask(cmi, this.autoTimer, 0L, checkIntervalMs / 50L);
    }
  }

  public void stop() {
    if (this.autoTimerBukkitId != null) {
      this.autoTimerBukkitId.cancel();
      this.autoTimerBukkitId = null;
    }
  }

  private Integer getTodaysDate() {
    this.calendar.setTimeInMillis(System.currentTimeMillis());
    return Integer.parseInt(this.formatter.format(this.calendar.getTime()));
  }

  private Integer getYeastardayDate() {
    this.calendar.setTimeInMillis(System.currentTimeMillis());
    this.calendar.add(5, -1);
    return Integer.parseInt(this.formatter.format(this.calendar.getTime()));
  }

  Integer getWeekStartDate() {
    this.calendar.setTimeInMillis(System.currentTimeMillis());
    this.calendar.setFirstDayOfWeek(2);

    while (this.calendar.get(7) != 2) {
      this.calendar.add(5, -1);
    }

    return Integer.parseInt(this.formatter.format(this.calendar.getTime()));
  }

  Integer getMonthStartDate() {
    this.calendar.setTimeInMillis(System.currentTimeMillis());

    while (this.calendar.get(5) != 1) {
      this.calendar.add(5, -1);
    }

    return Integer.parseInt(this.formatter.format(this.calendar.getTime()));
  }

  private static int currentHour() {
    Date date = new Date();
    Calendar hourCalendar = Calendar.getInstance();
    hourCalendar.setTime(date);
    return hourCalendar.get(11);
  }

  static Integer hourBack(int value) {
    Date date = new Date();
    Calendar hourCalendar = Calendar.getInstance();
    hourCalendar.setTime(date);
    hourCalendar.add(11, -value);
    return hourCalendar.get(11);
  }

  Integer getYearStartDate() {
    this.calendar.setTimeInMillis(System.currentTimeMillis());
    this.calendar.set(2, 1);
    this.calendar.set(6, 1);
    return Integer.parseInt(this.formatter.format(this.calendar.getTime()));
  }

  public Integer getDaysBackDate(int value) {
    this.calendar.setTimeInMillis(System.currentTimeMillis());
    this.calendar.add(5, -value);
    return Integer.parseInt(this.formatter.format(this.calendar.getTime()));
  }

  public void updatePlayTimes() {
    PlayTimeManager.PlaytimeRange[] playtimeRanges;
    int value = (playtimeRanges = PlayTimeManager.PlaytimeRange.values()).length;

    for (int index = 0; index < value; index++) {
      PlayTimeManager.PlaytimeRange playtimeRange = playtimeRanges[index];
      playtimeRange.update();
    }

    for (Player player : Bukkit.getOnlinePlayers()) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      cmiUser.updatePlayTime();
    }
  }

  public String formatDate(int value) {
    int year = value / 10000;
    int month = (value - year * 10000) / 100;
    int day = value % 100;
    return day + "/" + month + "/" + year;
  }

  public enum PlaytimeRange {
    thishour(10, false, CMIMaterial.LIME_WOOL),
    hourback(19, false, CMIMaterial.GREEN_WOOL),
    today(11, true, CMIMaterial.LIGHT_GRAY_WOOL),
    yesterday(20, true, CMIMaterial.GRAY_WOOL),
    thisweek(13, true, CMIMaterial.YELLOW_WOOL),
    week(22, true, CMIMaterial.BROWN_WOOL),
    thismonth(15, true, CMIMaterial.PINK_WOOL),
    month(24, true, CMIMaterial.RED_WOOL),
    thisyear(16, false, CMIMaterial.LIGHT_BLUE_WOOL),
    year(25, false, CMIMaterial.BLUE_WOOL),
    total(40, false, CMIMaterial.WHITE_WOOL);

    private Integer StartTime;
    private HashMap<UUID, Long> cache = new HashMap<>();
    private HashMap<UUID, Integer> cacheLastUpdate = new HashMap<>();
    private int slot;
    private boolean detailed;
    private CMIMaterial color;

    PlaytimeRange(int value, boolean flag, CMIMaterial cmiMaterial) {
      this.slot = value;
      this.detailed = flag;
      this.color = cmiMaterial;
    }

    public void update() {
      Integer integer = 0;
      switch (this) {
        case thishour:
          integer = PlayTimeManager.currentHour();
          break;
        case hourback:
          integer = PlayTimeManager.hourBack(1);
          break;
        case today:
          integer = CMI.getInstance().getPlayTimeManager().getDaysBackDate(0);
          if (!integer.equals(this.StartTime)) {
            total.cache.clear();
            total.cacheLastUpdate.clear();
          }
          break;
        case yesterday:
          integer = CMI.getInstance().getPlayTimeManager().getDaysBackDate(1);
          break;
        case thisweek:
          integer = CMI.getInstance().getPlayTimeManager().getWeekStartDate();
          break;
        case week:
          integer = CMI.getInstance().getPlayTimeManager().getDaysBackDate(7);
          break;
        case thismonth:
          integer = CMI.getInstance().getPlayTimeManager().getMonthStartDate();
          break;
        case month:
          integer = CMI.getInstance().getPlayTimeManager().getDaysBackDate(30);
          break;
        case thisyear:
          integer = CMI.getInstance().getPlayTimeManager().getYearStartDate();
          break;
        case year:
          integer = CMI.getInstance().getPlayTimeManager().getDaysBackDate(365);
          break;
        case total:
          integer = 0;
      }

      if (!integer.equals(this.StartTime)) {
        this.cache.clear();
        this.cacheLastUpdate.clear();
      }

      this.StartTime = integer;
    }

    public Integer getStartTime() {
      if (this.StartTime == null) {
        this.update();
      }

      return this.StartTime;
    }

    public Long getCache(UUID uuid) {
      return this.cache.get(uuid);
    }

    public void setCache(UUID uuid, Long cachedTime) {
      this.cache.put(uuid, cachedTime);
    }

    public Integer getCacheLastUpdate(UUID uuid) {
      return this.cacheLastUpdate.get(uuid);
    }

    public void setCacheLastUpdate(UUID uuid, Integer integer) {
      this.cacheLastUpdate.put(uuid, integer);
    }

    public static PlayTimeManager.PlaytimeRange getByName(String string) {
      PlayTimeManager.PlaytimeRange[] playtimeRanges;
      int value = (playtimeRanges = values()).length;

      for (int index = 0; index < value; index++) {
        PlayTimeManager.PlaytimeRange playtimeRange = playtimeRanges[index];
        if (playtimeRange.name().equalsIgnoreCase(string)) {
          return playtimeRange;
        }
      }

      return null;
    }

    public int getSlot() {
      return this.slot;
    }

    public void setSlot(int value) {
      this.slot = value;
    }

    public boolean isDetailed() {
      return this.detailed;
    }

    public CMIMaterial getColor() {
      return this.color;
    }

    public void setColor(CMIMaterial cmiMaterial) {
      this.color = cmiMaterial;
    }
  }
}
