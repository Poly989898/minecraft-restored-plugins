package com.Zrips.CMI.Containers;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.bukkit.event.EventPriority;

public enum CMIEventPriority {
  LOWEST,
  LOW,
  NORMAL,
  HIGH,
  HIGHEST,
  MONITOR;

  private EventPriority priority;

  CMIEventPriority() {
    EventPriority[] eventPrioritys;
    int value = (eventPrioritys = EventPriority.values()).length;

    for (int index = 0; index < value; index++) {
      EventPriority eventPriority = eventPrioritys[index];
      if (eventPriority.name().equalsIgnoreCase(this.toString())) {
        this.priority = eventPriority;
        break;
      }
    }
  }

  public static CMIEventPriority getPriority(String string) {
    CMIEventPriority[] cmiEventPrioritys;
    int value = (cmiEventPrioritys = values()).length;

    for (int index = 0; index < value; index++) {
      CMIEventPriority cmiEventPriority = cmiEventPrioritys[index];
      if (cmiEventPriority.name().equalsIgnoreCase(string)) {
        return cmiEventPriority;
      }
    }

    return NORMAL;
  }

  public EventPriority getPriority() {
    return this.priority;
  }

  public static String listPriorities() {
    return Arrays.stream(values()).map(Enum::name).collect(Collectors.joining(", "));
  }

  public static String listGeneralPriorities() {
    return Arrays.stream(values())
        .filter(cmiEventPriority -> !cmiEventPriority.equals(MONITOR))
        .map(Enum::name)
        .collect(Collectors.joining(", "));
  }

  public static String[] generateComments(
      String string, CMIEventPriority cmiEventPriority, boolean flag) {
    return new String[] {
      string != null && !string.isEmpty() ? string : "Priority of listener",
      "Changed priority will only take effect on full server restart. Possible options: "
          + (flag ? listGeneralPriorities() : listPriorities()),
      "Default: " + cmiEventPriority
    };
  }
}
