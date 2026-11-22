package com.Zrips.CMI.Modules.PlayTime;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import net.Zrips.CMILib.Container.CMINumber;

public class CMIPlayDay {
  Long[] map = new Long[24];
  private long lastUpdate = 0L;
  private CMIUser user;
  private int date;
  private int id = 0;
  private static final Long maxValue = 3600000L;

  public CMIPlayDay(CMIUser cmiUser, int value) {
    this.user = cmiUser;
    this.date = value;
  }

  public long getLastUpdate() {
    return this.lastUpdate;
  }

  public Long getTotalTime() {
    long longValue = 0L;

    for (int value = 0; value < 24; value++) {
      longValue += this.getHourPlaytime(value);
    }

    return longValue;
  }

  public void setLastUpdate(Long newLastUpdate) {
    this.lastUpdate = newLastUpdate == null ? 0L : newLastUpdate;
  }

  public void updatePlayTime() {
    if (this.lastUpdate != 0L
        && (!CMI.getInstance().getAfkManager().isStopPlayTime() || !this.user.isAfk())) {
      int value = PlayTimeManager.PlaytimeRange.thishour.getStartTime();
      long longValue =
          System.currentTimeMillis() - this.lastUpdate > PlayTimeManager.checkIntervalMs
              ? PlayTimeManager.checkIntervalMs
              : System.currentTimeMillis() - this.lastUpdate;
      Long playtime = this.map[value];
      if (playtime == null || playtime == 0L) {
        this.checkPreviousHour(value - 1);
      }

      playtime = CMINumber.clamp(playtime == null ? longValue : playtime + longValue, 0L, maxValue);
      this.setLastUpdate(System.currentTimeMillis());
      this.map[value] = playtime;
    } else {
      this.lastUpdate = System.currentTimeMillis();
    }
  }

  public void checkPreviousHour(int value) {
    if (value >= 0) {
      Long playtime = this.map[value];
      if (playtime != null) {
        if (maxValue - playtime < 60000L) {
          this.map[value] = maxValue;
        }

        this.user.addForDelayedSave();
      }
    }
  }

  public Long getCurrentHourPlaytime() {
    return this.getHourPlaytime(PlayTimeManager.PlaytimeRange.thishour.getStartTime());
  }

  public Long getHourPlaytime(int value) {
    Long playtime = this.map[value];
    return playtime == null ? 0L : playtime;
  }

  public void setHourPlaytime(int value, Long playtime, boolean flag) {
    if (!flag) {
      playtime = playtime > maxValue ? maxValue : playtime;
    }

    playtime = playtime <= 0L ? null : playtime;
    this.map[value] = playtime;
  }

  public int getDate() {
    return this.date;
  }

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer integer) {
    this.id = integer;
  }
}
