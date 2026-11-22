package com.Zrips.CMI.Containers;

import java.util.HashMap;
import java.util.UUID;

public class CMICounter {
  private static HashMap<UUID, HashMap<String, Long>> counters = new HashMap<>();

  public static boolean isJoinedCounter(UUID uuid) {
    return counters.containsKey(uuid);
  }

  public static void setJoinedCounter(UUID uuid, boolean flag) {
    if (!flag || !isJoinedCounter(uuid)) {
      if (flag) {
        counters.put(uuid, new HashMap<>());
      } else {
        counters.remove(uuid);
      }
    }
  }

  public static HashMap<String, Long> getCounter(UUID uuid) {
    return counters.computeIfAbsent(uuid, playerUuid -> new HashMap<>());
  }
}
