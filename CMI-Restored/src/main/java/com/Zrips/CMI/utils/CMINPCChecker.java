package com.Zrips.CMI.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.entity.Player;

public class CMINPCChecker {
  private static final Map<UUID, Boolean> npcMap =
      Collections.synchronizedMap(
          new LinkedHashMap<UUID, Boolean>(16, 0.75F, false) {
            private static final int MAX_SIZE = 250;

            @Override
            protected boolean removeEldestEntry(Entry<UUID, Boolean> entry) {
              return this.size() > 250;
            }
          });

  public static boolean isNPC(Player player) {
    if (player == null) {
      return false;
    }

    UUID uuid = player.getUniqueId();
    synchronized (npcMap) {
      return npcMap.computeIfAbsent(uuid, playerUuid -> player.hasMetadata("NPC"));
    }
  }

  public static boolean isNPC(UUID uuid) {
    synchronized (npcMap) {
      return npcMap.getOrDefault(uuid, false);
    }
  }

  public static void remove(UUID uuid) {
    synchronized (npcMap) {
      npcMap.remove(uuid);
    }
  }

  public static void remove(Player player) {
    if (player != null) {
      remove(player.getUniqueId());
    }
  }
}
