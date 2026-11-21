package io.lumine.mythic.bukkit.utils.timingslib;

import org.bukkit.Bukkit;
import org.spigotmc.CustomTimingsHandler;

class SpigotTiming extends MCTiming {
   private final CustomTimingsHandler timing;

   SpigotTiming(String name) {
      this.timing = new CustomTimingsHandler(name);
   }

   @Override
   public MCTiming startTiming() {
      if (Bukkit.isPrimaryThread()) {
         this.timing.startTiming();
      }

      return this;
   }

   @Override
   public void stopTiming() {
      if (Bukkit.isPrimaryThread()) {
         this.timing.stopTiming();
      }
   }
}
