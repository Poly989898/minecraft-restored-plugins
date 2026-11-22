package com.Zrips.CMI.Modules.PlayTime;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class CMIPlayTime {
  TreeMap<Integer, CMIPlayDay> map = new TreeMap<>(Collections.reverseOrder());
  private Set<CMIPlayDay> forSave = new HashSet<>();
  private CMIUser user;

  public CMIPlayTime(CMIUser cmiUser) {
    this.user = cmiUser;
  }

  public TreeMap<Integer, CMIPlayDay> getDays() {
    return this.map;
  }

  public CMIPlayDay getPlayDay(Integer integer) {
    CMIPlayDay cmiPlayDay = this.map.get(integer);
    if (cmiPlayDay == null) {
      cmiPlayDay = new CMIPlayDay(this.user, integer);
      this.map.put(integer, cmiPlayDay);
    }

    return cmiPlayDay;
  }

  public CMIPlayDay getPlayDayOfToday() {
    Integer integer = PlayTimeManager.PlaytimeRange.today.getStartTime();
    CMIPlayDay cmiPlayDay = this.map.get(integer);
    if (cmiPlayDay == null) {
      cmiPlayDay = new CMIPlayDay(this.user, integer);
      this.map.put(integer, cmiPlayDay);
      this.checkYestarday();
    }

    return cmiPlayDay;
  }

  private void checkYestarday() {
    Integer integer = PlayTimeManager.PlaytimeRange.yesterday.getStartTime();
    CMIPlayDay cmiPlayDay = this.map.get(integer);
    if (cmiPlayDay != null) {
      cmiPlayDay.checkPreviousHour(23);
      this.forSave.add(cmiPlayDay);
    }
  }

  public void addDayForSave(CMIPlayDay cmiPlayDay) {
    this.forSave.add(cmiPlayDay);
  }

  public void updatePlayTime() {
    this.updatePlayTime(true);
  }

  public void updatePlayTime(boolean flag) {
    if (CMI.getInstance().getConfigManager().isCMIPlayTimeTracking()) {
      if (!this.map.containsKey(0)) {
        long longValue = this.user.getTotalPlayTime(true);
        if (longValue > 0L) {
          this.getPlayDay(0).setHourPlaytime(0, longValue, true);
          this.forSave.add(this.getPlayDay(0));
        }
      }

      if (flag && this.user.isOnline() || !flag) {
        CMIPlayDay cmiPlayDay = this.getPlayDayOfToday();
        cmiPlayDay.updatePlayTime();
        this.forSave.add(cmiPlayDay);
      }
    }
  }

  public Long getPlayTime(PlayTimeManager.PlaytimeRange playtimeRange) {
    Integer integer = 0;
    switch (playtimeRange) {
      case thishour:
        return this.getPlayDay(PlayTimeManager.PlaytimeRange.today.getStartTime())
            .getHourPlaytime(PlayTimeManager.PlaytimeRange.thishour.getStartTime());
      case hourback:
        Long time = playtimeRange.getCache(this.user.getUniqueId());
        if (time == null) {
          Integer count;
          if (PlayTimeManager.PlaytimeRange.hourback.equals(23)) {
            count = PlayTimeManager.PlaytimeRange.yesterday.getStartTime();
          } else {
            count = PlayTimeManager.PlaytimeRange.today.getStartTime();
          }

          CMIPlayDay cmiPlayDay = this.getPlayDay(count);
          Long timeSecond =
              cmiPlayDay.getHourPlaytime(PlayTimeManager.PlaytimeRange.hourback.getStartTime());
          time = timeSecond == null ? 0L : timeSecond;
          playtimeRange.setCache(this.user.getUniqueId(), time);
        }

        return time;
      case today:
        return this.getPlayDay(PlayTimeManager.PlaytimeRange.today.getStartTime()).getTotalTime();
      case yesterday:
        Long timeThird = playtimeRange.getCache(this.user.getUniqueId());
        if (timeThird == null) {
          timeThird =
              this.getPlayDay(PlayTimeManager.PlaytimeRange.yesterday.getStartTime())
                  .getTotalTime();
          timeThird = timeThird == null ? 0L : timeThird;
          playtimeRange.setCache(this.user.getUniqueId(), timeThird);
          return timeThird;
        }

        return timeThird;
      case thisweek:
      case week:
      case thismonth:
      case month:
      case thisyear:
      case year:
      case total:
        integer = playtimeRange.getStartTime();
      default:
        switch (playtimeRange) {
          case thisweek:
          case week:
          case thismonth:
          case month:
          case thisyear:
          case year:
          case total:
            Long timeFourth = playtimeRange.getCache(this.user.getUniqueId());
            if (timeFourth == null) {
              int value = 0;

              for (var entry : this.getDays().entrySet()) {
                if (value == 0) {
                  value++;
                } else {
                  if ((Integer) entry.getKey() < integer) {
                    break;
                  }

                  if (timeFourth == null) {
                    timeFourth = 0L;
                  }

                  timeFourth = timeFourth + ((CMIPlayDay) entry.getValue()).getTotalTime();
                  value++;
                }
              }

              timeFourth = timeFourth == null ? 0L : timeFourth;
              playtimeRange.setCache(this.user.getUniqueId(), timeFourth);
            }

            return timeFourth + this.getPlayTime(PlayTimeManager.PlaytimeRange.today);
          default:
            return 0L;
        }
    }
  }

  public Long getTotalPlayTime() {
    Long time = 0L;

    for (var entry : this.map.entrySet()) {
      time = time + ((CMIPlayDay) entry.getValue()).getTotalTime();
    }

    return time;
  }

  public Set<CMIPlayDay> getForSave() {
    return this.forSave;
  }
}
