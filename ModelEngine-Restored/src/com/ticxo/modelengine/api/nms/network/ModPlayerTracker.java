package com.ticxo.modelengine.api.nms.network;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ModPlayerTracker {
   public static final String CHANNEL = "modelengine:bulk_data";
   private static final Set<UUID> MOD_PLAYERS = ConcurrentHashMap.newKeySet();

   public static void addPlayer(UUID uuid) {
      MOD_PLAYERS.add(uuid);
   }

   public static void removePlayer(UUID uuid) {
      MOD_PLAYERS.remove(uuid);
   }

   public static boolean hasClientMod(UUID uuid) {
      return MOD_PLAYERS.contains(uuid);
   }

   public static Set<UUID> getModPlayers() {
      return MOD_PLAYERS;
   }
}
