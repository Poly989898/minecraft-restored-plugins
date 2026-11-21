package io.lumine.mythic.bukkit.utils.tasks;

import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;

@NonnullByDefault
public interface Task extends Terminable {
   int getTimesRan();

   boolean stop();

   int getBukkitId();

   @Override
   default void close() {
      this.stop();
   }
}
