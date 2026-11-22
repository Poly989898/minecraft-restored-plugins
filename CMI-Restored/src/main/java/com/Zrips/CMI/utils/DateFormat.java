package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateFormat {
  public static String MiliToDate(long longValue) {
    return MiliToDate(longValue, CMI.getInstance().getConfigManager().getLongDateFormat());
  }

  public static String MiliToDate(long longValue, String string) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(string);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(longValue);
    return simpleDateFormat.format(calendar.getTime());
  }

  public static String MiliToDateShort(long longValue) {
    SimpleDateFormat simpleDateFormat =
        new SimpleDateFormat(CMI.getInstance().getConfigManager().getShortDateFormat());
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(longValue);
    return simpleDateFormat.format(calendar.getTime());
  }

  public static String MiliToHours(long longValue) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(longValue);
    return simpleDateFormat.format(calendar.getTime());
  }

  public static int[] splitToComponentDate(Long timeMillis) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timeMillis);
    int value = calendar.get(1);
    int month = calendar.get(2);
    int day = calendar.get(5);
    return new int[] {value, month, day};
  }

  public static int[] splitToComponentTimes(Long startMillis) {
    int value = (int) (System.currentTimeMillis() - startMillis) / 1000;
    int hours = value / 3600;
    int minutes = value % 3600 / 60;
    int seconds = value % 60;
    return new int[] {hours, minutes, seconds};
  }
}
