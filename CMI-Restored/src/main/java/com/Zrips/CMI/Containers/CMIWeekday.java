package com.Zrips.CMI.Containers;

public enum CMIWeekday {
  Monday(1),
  Tuesday(2),
  Wednesday(3),
  Thursday(4),
  Friday(5),
  Saturday(6),
  Sunday(7);

  private final int day;

  CMIWeekday(int value) {
    this.day = value;
  }

  public int getDayID() {
    return this.day;
  }

  public static CMIWeekday get(String string) {
    CMIWeekday[] cmiWeekdays;
    int value = (cmiWeekdays = values()).length;

    for (int index = 0; index < value; index++) {
      CMIWeekday cmiWeekday = cmiWeekdays[index];
      if (cmiWeekday.toString().equalsIgnoreCase(string)) {
        return cmiWeekday;
      }
    }

    return null;
  }

  public static CMIWeekday get(int value) {
    CMIWeekday[] cmiWeekdays;
    int count = (cmiWeekdays = values()).length;

    for (int index = 0; index < count; index++) {
      CMIWeekday cmiWeekday = cmiWeekdays[index];
      if (cmiWeekday.getDayID() == value) {
        return cmiWeekday;
      }
    }

    return null;
  }
}
