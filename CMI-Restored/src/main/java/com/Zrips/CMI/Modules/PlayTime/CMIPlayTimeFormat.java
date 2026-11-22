package com.Zrips.CMI.Modules.PlayTime;

import com.Zrips.CMI.Locale.CMILC;

public class CMIPlayTimeFormat {
  static CMIPlayTimeFormat.showType forceShowDays = CMIPlayTimeFormat.showType.normal;
  static CMIPlayTimeFormat.showType forceShowHours = CMIPlayTimeFormat.showType.normal;
  static CMIPlayTimeFormat.showType forceShowMinutes = CMIPlayTimeFormat.showType.normal;
  static CMIPlayTimeFormat.showType forceShowSeconds = CMIPlayTimeFormat.showType.normal;
  static String daysFormat = "";
  static String hoursFormat = "";
  static String minutesFormat = "";
  static String secondsFormat = "";

  public static void update() {
    String string = CMILC.info_time_days.getLocale();
    forceShowDays =
        string.startsWith("+")
            ? CMIPlayTimeFormat.showType.full
            : (string.startsWith("-")
                ? CMIPlayTimeFormat.showType.partial
                : CMIPlayTimeFormat.showType.normal);
    daysFormat =
        !forceShowDays.equals(CMIPlayTimeFormat.showType.full)
                && !forceShowDays.equals(CMIPlayTimeFormat.showType.partial)
            ? string
            : string.substring(1);
    string = CMILC.info_time_hours.getLocale();
    forceShowHours =
        string.startsWith("+")
            ? CMIPlayTimeFormat.showType.full
            : (string.startsWith("-")
                ? CMIPlayTimeFormat.showType.partial
                : CMIPlayTimeFormat.showType.normal);
    hoursFormat =
        !forceShowHours.equals(CMIPlayTimeFormat.showType.full)
                && !forceShowHours.equals(CMIPlayTimeFormat.showType.partial)
            ? string
            : string.substring(1);
    string = CMILC.info_time_minutes.getLocale();
    forceShowMinutes =
        string.startsWith("+")
            ? CMIPlayTimeFormat.showType.full
            : (string.startsWith("-")
                ? CMIPlayTimeFormat.showType.partial
                : CMIPlayTimeFormat.showType.normal);
    minutesFormat =
        !forceShowMinutes.equals(CMIPlayTimeFormat.showType.full)
                && !forceShowMinutes.equals(CMIPlayTimeFormat.showType.partial)
            ? string
            : string.substring(1);
    string = CMILC.info_time_seconds.getLocale();
    forceShowSeconds =
        string.startsWith("+")
            ? CMIPlayTimeFormat.showType.full
            : (string.startsWith("-")
                ? CMIPlayTimeFormat.showType.partial
                : CMIPlayTimeFormat.showType.normal);
    secondsFormat =
        !forceShowSeconds.equals(CMIPlayTimeFormat.showType.full)
                && !forceShowSeconds.equals(CMIPlayTimeFormat.showType.partial)
            ? string
            : string.substring(1);
  }

  public static String formatPlaytimePlaceholder(long longValue) {
    if (daysFormat.isEmpty()
        && hoursFormat.isEmpty()
        && minutesFormat.isEmpty()
        && secondsFormat.isEmpty()) {
      update();
    }

    StringBuilder stringBuilder = new StringBuilder();
    long days = longValue / 1000L / 60L / 60L / 24L;
    longValue -= days * 1000L * 60L * 60L * 24L;
    long hours = longValue / 1000L / 60L / 60L;
    longValue -= hours * 1000L * 60L * 60L;
    long minutes = longValue / 1000L / 60L;
    longValue -= minutes * 1000L * 60L;
    long seconds = longValue / 1000L;
    longValue -= seconds * 1000L;
    if (days > 0L || forceShowDays.equals(CMIPlayTimeFormat.showType.full)) {
      stringBuilder.append(daysFormat.replace("[d]", String.valueOf(days)));
    }

    if (hours > 0L
        || forceShowHours.equals(CMIPlayTimeFormat.showType.full)
        || forceShowHours.equals(CMIPlayTimeFormat.showType.partial) && days > 0L) {
      stringBuilder.append(hoursFormat.replace("[h]", String.valueOf(hours)));
    }

    if (minutes > 0L
        || forceShowMinutes.equals(CMIPlayTimeFormat.showType.full)
        || forceShowMinutes.equals(CMIPlayTimeFormat.showType.partial)
            && (hours > 0L || days > 0L)) {
      stringBuilder.append(minutesFormat.replace("[m]", String.valueOf(minutes)));
    }

    if (seconds > 0L
        || forceShowSeconds.equals(CMIPlayTimeFormat.showType.full)
        || forceShowSeconds.equals(CMIPlayTimeFormat.showType.partial)
            && (minutes > 0L || hours > 0L || days > 0L)) {
      stringBuilder.append(secondsFormat.replace("[s]", String.valueOf(seconds)));
    }

    if (stringBuilder.toString().isEmpty()) {
      stringBuilder.append(secondsFormat.replace("[s]", String.valueOf(0)));
    }

    return stringBuilder.toString();
  }

  enum showType {
    full,
    partial,
    normal;
  }
}
