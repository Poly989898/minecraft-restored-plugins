package io.lumine.mythic.api.volatilecode.handlers;

import org.bukkit.entity.Player;

public interface VolatilePacketHandler {
   void injectPlayer(Player player);

   void ejectPlayer(Player player);

   default void enablePlayerActionEvent() {
   }

   default void enablePacketItemDataEvent() {
   }

   default void enablePlayerBlockDestructionEvent() {
   }

   default void setPlayerPositionSilent(Player viewer, double x, double y, double z) {
   }

   default int spawnFakeCamera(Player viewer, double x, double y, double z, float yaw, float pitch, int teleportDuration) {
      return -1;
   }

   default void moveFakeCamera(Player viewer, int entityId, double x, double y, double z, float yaw, float pitch) {
   }

   default void removeFakeCamera(Player viewer, int entityId) {
   }
}
