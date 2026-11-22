package com.Zrips.CMI.Containers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class CMITabComplete {
  public ConcurrentHashMap<String, Object> tabs = new ConcurrentHashMap<>();
  public static final String externalSeparator = ",";
  public static final String internalSeparator = "><";

  public List<Object> getTabCompleteList(String[] strings) {
    StringBuilder stringBuilder = new StringBuilder();

    for (int value = 0; value < strings.length - 1; value++) {
      if (!stringBuilder.toString().isEmpty()) {
        stringBuilder.append(" ");
      }

      stringBuilder.append(strings[value]);
    }

    String string = this.getTabComplete(stringBuilder.toString());
    ArrayList arrayList = new ArrayList();
    if (string != null) {
      if (strings.length > 2) {
        if (stringBuilder.toString().contains(" ")) {
          arrayList.addAll(Arrays.asList(stringBuilder.toString().split(" ", 2)[1].split(" ")));
        } else {
          arrayList.addAll(Arrays.asList(stringBuilder.toString()));
        }
      }

      arrayList.addAll(Arrays.asList(string.replace(",", "><").split(" ")));
      ArrayList copy = new ArrayList(arrayList);

      for (int index = arrayList.size() - 1; index >= 0; index--) {
        Object object = arrayList.get(index);
        if (object instanceof String) {
          String token = (String) object;
          StringBuilder builder = new StringBuilder();
          String[] parts;
          int partCount = (parts = token.split("><")).length;

          for (int partIndex = 0; partIndex < partCount; partIndex++) {
            String part = parts[partIndex];
            if (!builder.toString().isEmpty()) {
              builder.append("><");
            }

            if (part.startsWith("!")) {
              part = part.substring(1);
              if (notRepeating(part, strings)) {
                builder.append(part);
              }
            } else {
              builder.append(part);
            }
          }

          copy.set(index, builder.toString());
        }
      }

      arrayList = copy;
    }

    return arrayList;
  }

  private static boolean notRepeating(String string, String[] strings) {
    String[] argsCopy = strings;
    int value = strings.length;

    for (int index = 0; index < value; index++) {
      String arg = argsCopy[index];
      if (arg.equalsIgnoreCase(string)) {
        return false;
      }
    }

    return true;
  }

  public String getTabComplete(String string) {
    String tabName = string.split(" ", 2)[0];
    Object object = this.tabs.get(tabName.toLowerCase());
    if (object == null) {
      String[] strings = string.split(" ");
      tabName = strings[strings.length - 1];
      object = this.tabs.get(tabName.toLowerCase());
      if (object != null) {
        Object tab = this.getTab(tabName.toLowerCase(), this.tabs);
        return tab == null ? null : (String) tab;
      }
    }

    if (object == null && !tabName.isEmpty() && this.tabs.size() >= 100) {
      return null;
    }

    Object tab = this.getTab(string.toLowerCase(), this.tabs);
    return tab == null ? null : (String) tab;
  }

  private Object getTab(String string, Map<String, Object> map) {
    StringBuilder stringBuilder = new StringBuilder();
    if (string.isEmpty()) {
      for (var entry : map.entrySet()) {
        if (!stringBuilder.toString().isEmpty()) {
          stringBuilder.append(",");
        }

        stringBuilder.append((String) entry.getKey());
      }

      return stringBuilder.toString();
    } else {
      String tabName = string.split(" ", 2)[0];
      Object object = map.get(tabName.toLowerCase());
      if (object instanceof HashMap && string.contains(" ")) {
        return this.getTab(string.split(" ", 2)[1], (Map<String, Object>) object);
      }

      if (object == null) {
        for (var tabEntry : map.entrySet()) {
          if (tabEntry.getKey() != null
              && ((String) tabEntry.getKey()).contains("[")
              && ((String) tabEntry.getKey()).contains("]")) {
            object = tabEntry.getValue();
            break;
          }
        }

        if (object instanceof HashMap && string.contains(" ")) {
          return this.getTab(string.split(" ", 2)[1], (HashMap) object);
        }
      }

      if (object != null) {
        for (Entry entry : ((Iterable<Entry>) ((HashMap) object).entrySet())) {
          if (!stringBuilder.toString().isEmpty()) {
            stringBuilder.append(",");
          }

          stringBuilder.append((String) entry.getKey());
        }

        return stringBuilder.toString();
      } else {
        if (map.containsKey(tabName.toLowerCase())) {
          return null;
        }

        for (var outerEntry : map.entrySet()) {
          if (outerEntry.getValue() != null) {
            for (Entry innerEntry :
                ((Iterable<Entry>) ((HashMap) outerEntry.getValue()).entrySet())) {
              if (!stringBuilder.toString().isEmpty()) {
                stringBuilder.append(",");
              }

              stringBuilder.append((String) innerEntry.getKey());
            }
          }
        }

        return stringBuilder.toString();
      }
    }
  }

  public void addTabComplete(String string) {
    string = string.replace("><", ",");
    this.addTab(string, this.tabs);
  }

  private Object addTab(String string, Map<String, Object> map) {
    if (string.contains(" ")) {
      String[] strings = string.split(" ", 2);
      String[] parts;
      int value = (parts = strings[0].split(",")).length;

      for (int index = 0; index < value; index++) {
        String part = parts[index];
        Object object = map.get(part.toLowerCase());
        if (object == null) {
          object = new HashMap();
          map.put(part.toLowerCase(), object);
        }

        this.addTab(strings[1], (Map<String, Object>) object);
      }

      return map;
    } else {
      generalAdd(string, map);
      return map;
    }
  }

  private static void generalAdd(String string, Map<String, Object> map) {
    String[] strings;
    int value = (strings = string.split(",")).length;

    for (int index = 0; index < value; index++) {
      String arg = strings[index];
      Object object = map.get(arg.toLowerCase());
      if (object == null) {
        object = new HashMap();
        map.put(arg.toLowerCase(), object);
      }
    }
  }
}
