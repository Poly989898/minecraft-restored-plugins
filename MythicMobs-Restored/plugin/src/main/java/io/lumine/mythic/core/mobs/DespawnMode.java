package io.lumine.mythic.core.mobs;

import lombok.Getter;


@Getter
public enum DespawnMode {
   NORMAL(true, false, true, true),
   CHUNK(false, false, true, true),
   NEVER(false, true, false, true),
   PERSISTENT(false, true, false, false),
   NPC(false, false, true, false);

   private final Boolean despawnWithoutNearbyPlayers;
   private final Boolean savesToDisk;
   private final Boolean despawnsOnUnload;
   private final Boolean obeysKillCommand;

   private DespawnMode(boolean despawnWithoutNearbyPlayers, boolean savesToDisk, boolean despawnsOnUnload, boolean obeysKillCommand) {
      this.despawnWithoutNearbyPlayers = despawnWithoutNearbyPlayers;
      this.savesToDisk = savesToDisk;
      this.despawnsOnUnload = despawnsOnUnload;
      this.obeysKillCommand = obeysKillCommand;
   }

   public static DespawnMode get(String in) {
      String toUpperCase = in.toUpperCase();
      switch (toUpperCase) {
         case "NORMAL":
         case "TRUE":
         case "YES":
            return NORMAL;
         case "NEVER":
         case "FALSE":
         case "NO":
            return NEVER;
         case "CHUNK":
            return CHUNK;
         case "PERSISTENT":
            return PERSISTENT;
         case "NPC":
            return NPC;
         default:
            return NORMAL;
      }
   }
}
