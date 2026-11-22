package com.Zrips.CMI.Modules.Sheduler;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIDate;
import com.Zrips.CMI.Containers.CMIWeekday;
import java.util.Calendar;
import java.util.Date;
import net.Zrips.CMILib.Time.CMITimeManager;

public class SchedTime {
  private int year = -1;
  private int month = -1;
  private int day = -1;
  private int dayOfWeek = -1;
  private int hour = -1;
  private int minute = -1;
  private int second = -1;
  private boolean done = false;
  private CMIWeekday firstDayOfMonth = null;
  private CMIWeekday lastDayOfMonth = null;
  long next = 0L;

  public Integer getYear() {
    return this.year;
  }

  public void setYear(Integer integer) {
    if (integer < 100) {
      integer = integer + 2000;
    }

    this.year = integer;
  }

  public Integer getMonth() {
    return this.month;
  }

  public void setMonth(Integer integer) {
    this.month = integer;
  }

  public Integer getDay() {
    return this.day;
  }

  public void setDay(Integer integer) {
    this.day = integer;
  }

  public Integer getHour() {
    return this.hour;
  }

  public void setHour(Integer integer) {
    this.hour = integer;
  }

  public Integer getMinute() {
    return this.minute;
  }

  public void setMinute(Integer integer) {
    this.minute = integer;
  }

  public Integer getSecond() {
    return this.second;
  }

  public void setSecond(Integer integer) {
    this.second = integer;
  }

  public boolean isNow() {
    if (this.done) {
      return false;
    } else {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      if (this.year > -1 && this.year != calendar.get(1)) {
        return false;
      } else if (this.month > -1 && this.month != calendar.get(2) + 1) {
        return false;
      } else if (this.day > -1 && this.day != calendar.get(5)) {
        return false;
      } else if (this.dayOfWeek > -1
          && CMI.getInstance().getSchedulerManager().getActualWeekDay(this.dayOfWeek)
              != calendar.get(7)) {
        return false;
      } else if (this.hour > -1 && this.hour != calendar.get(11)) {
        return false;
      } else {
        return this.minute > -1 && this.minute != calendar.get(12)
            ? false
            : this.second <= -1 || this.second == calendar.get(13);
      }
    }
  }

  public long getNextMili() {
    Calendar calendar = Calendar.getInstance();
    Long nowMillis = System.currentTimeMillis() + 1000L;
    calendar.setTimeInMillis(nowMillis);
    if (this.getFirstDayOfMonth() != null) {
      Date date =
          CMIDate.getFirstMonthDay(this.getFirstDayOfMonth(), this.getYear(), this.getMonth());
      Calendar dateCalendar = Calendar.getInstance();
      dateCalendar.setTime(date);
      this.year = dateCalendar.get(1);
      this.month = dateCalendar.get(2) + 1;
      this.day = dateCalendar.get(5);
    }

    if (this.getLastDayOfMonth() != null) {
      Date nextDate =
          CMIDate.getLastMonthDay(this.getLastDayOfMonth(), this.getYear(), this.getMonth());
      Calendar nextCalendar = Calendar.getInstance();
      nextCalendar.setTime(nextDate);
      this.year = nextCalendar.get(1);
      this.month = nextCalendar.get(2) + 1;
      this.day = nextCalendar.get(5);
    }

    if (this.year > -1) {
      calendar.set(1, this.year);
    }

    if (this.month > -1) {
      calendar.set(2, this.month - 1);
    }

    if (this.day > -1) {
      calendar.set(5, this.day);
    } else if (this.month > -1) {
      this.day = 0;
      calendar.set(5, 0);
    }

    if (this.dayOfWeek > -1) {
      calendar.set(7, this.dayOfWeek);
      int value = CMI.getInstance().getSchedulerManager().getActualWeekDay(calendar.get(7));
      if (value > CMI.getInstance().getSchedulerManager().getActualWeekDay(this.dayOfWeek)) {
        calendar.set(5, calendar.get(5) + 7);
      }

      calendar.set(7, this.dayOfWeek);
    }

    if (this.hour > -1) {
      calendar.set(11, this.hour);
    } else if (this.day > -1) {
      this.hour = 0;
      calendar.set(11, 0);
    }

    if (this.minute > -1) {
      calendar.set(12, this.minute);
    } else if (this.hour > -1) {
      this.minute = 0;
      calendar.set(12, 0);
    }

    if (this.second > -1) {
      calendar.set(13, this.second);
    } else {
      calendar.set(13, 0);
    }

    this.next = calendar.getTimeInMillis();
    if (this.next - nowMillis < 0L) {
      if (this.minute == -1) {
        calendar.set(12, calendar.get(12) + 1);
        this.next = calendar.getTimeInMillis();
      }

      if (this.next - nowMillis < 0L) {
        if (this.hour == -1) {
          calendar.set(11, calendar.get(11) + 1);
          this.next = calendar.getTimeInMillis();
        }

        if (this.next - nowMillis < 0L && this.dayOfWeek > -1) {
          calendar.set(5, calendar.get(5) + 7);
          this.next = calendar.getTimeInMillis();
        }

        if (this.next - nowMillis < 0L) {
          if (this.day == -1) {
            calendar.set(5, calendar.get(5) + 1);
            this.next = calendar.getTimeInMillis();
          }

          if (this.next - nowMillis < 0L) {
            if (this.month == -1) {
              calendar.set(2, calendar.get(2) + 1);
              this.next = calendar.getTimeInMillis();
            }

            if (this.next - nowMillis < 0L && this.year == -1) {
              calendar.set(1, calendar.get(1) + 1);
              this.next = calendar.getTimeInMillis();
            }
          }
        }
      }
    }

    return this.next < System.currentTimeMillis() + 1000L
        ? System.currentTimeMillis() + 1000L
        : this.next;
  }

  public boolean isDone() {
    return this.done;
  }

  public void setDone(boolean flag) {
    this.done = flag;
  }

  public Integer getDayOfWeek() {
    return this.dayOfWeek;
  }

  public void setDayOfWeek(Integer integer) {
    this.dayOfWeek = integer;
  }

  public CMIWeekday getFirstDayOfMonth() {
    return this.firstDayOfMonth;
  }

  public void setFirstDayOfMonth(CMIWeekday cmiWeekday) {
    this.firstDayOfMonth = cmiWeekday;
  }

  public CMIWeekday getLastDayOfMonth() {
    return this.lastDayOfMonth;
  }

  public void setLastDayOfMonth(CMIWeekday cmiWeekday) {
    this.lastDayOfMonth = cmiWeekday;
  }

  @Override
  public String toString() {
    return CMITimeManager.MiliToDate(this.getNextMili());
  }
}
