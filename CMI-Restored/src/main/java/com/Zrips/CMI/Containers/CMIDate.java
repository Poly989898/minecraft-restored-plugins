package com.Zrips.CMI.Containers;

import java.util.Calendar;
import java.util.Date;

public class CMIDate {
  public static Date getFirstMonthDay(CMIWeekday cmiWeekday) {
    return getFirstMonthDay(cmiWeekday, -1, -1);
  }

  public static Date getFirstMonthDay(CMIWeekday cmiWeekday, int value, int month) {
    Calendar calendar = Calendar.getInstance();
    Calendar monthCalendar = Calendar.getInstance();
    boolean flag = month < 1 || month > 12;
    if (value > 0) {
      monthCalendar.set(1, value);
    }

    if (!flag) {
      monthCalendar.set(2, month - 1);
    } else if (value > 0 && value > calendar.get(1)) {
      monthCalendar.set(2, 0);
    }

    monthCalendar.set(5, 1);
    int weekdayId = cmiWeekday.getDayID() % 7 + 1;

    while (monthCalendar.get(7) != weekdayId) {
      monthCalendar.add(5, 1);
    }

    if (flag && value <= 0 && monthCalendar.before(calendar)) {
      monthCalendar = Calendar.getInstance();
      monthCalendar.add(2, 1);
      monthCalendar.set(5, 1);

      while (monthCalendar.get(7) != weekdayId) {
        monthCalendar.add(5, 1);
      }
    }

    return monthCalendar.getTime();
  }

  public static Date getLastMonthDay(CMIWeekday cmiWeekday) {
    return getLastMonthDay(cmiWeekday, -1, -1);
  }

  public static Date getLastMonthDay(CMIWeekday cmiWeekday, int value, int month) {
    Calendar calendar = Calendar.getInstance();
    Calendar monthCalendar = Calendar.getInstance();
    boolean flag = month < 1 || month > 12;
    if (value > 0) {
      monthCalendar.set(1, value);
    }

    if (!flag) {
      monthCalendar.set(2, month - 1);
    } else if (value > 0 && value > calendar.get(1)) {
      monthCalendar.set(2, 0);
    }

    monthCalendar.set(5, monthCalendar.getActualMaximum(5));
    int weekdayId = cmiWeekday.getDayID() % 7 + 1;

    while (monthCalendar.get(7) != weekdayId) {
      monthCalendar.add(5, -1);
    }

    if (flag && value <= 0 && monthCalendar.before(calendar)) {
      monthCalendar = Calendar.getInstance();
      monthCalendar.add(2, 1);
      monthCalendar.set(5, monthCalendar.getActualMaximum(5));

      while (monthCalendar.get(7) != weekdayId) {
        monthCalendar.add(5, -1);
      }
    }

    return monthCalendar.getTime();
  }
}
