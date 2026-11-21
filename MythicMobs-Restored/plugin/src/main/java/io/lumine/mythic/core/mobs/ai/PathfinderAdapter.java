package io.lumine.mythic.core.mobs.ai;

public interface PathfinderAdapter {
   boolean isValid();

   default boolean requiresUpdateEveryTick() {
      return false;
   }

   default boolean isInterruptable() {
      return true;
   }
}
