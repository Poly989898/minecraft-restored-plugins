package io.lumine.mythic.bukkit.utils.timingslib;

class EmptyTiming extends MCTiming {
   @Override
   public final MCTiming startTiming() {
      return this;
   }

   @Override
   public final void stopTiming() {
   }
}
