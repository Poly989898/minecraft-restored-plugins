package io.lumine.mythic.bukkit.utils.terminable;

import io.lumine.mythic.bukkit.utils.terminable.composite.CompositeTerminable;
import java.util.function.Consumer;

public interface TerminableRegistry extends Consumer<Terminable>, Terminable {
   static TerminableRegistry create() {
      return new SimpleTerminableRegistry();
   }

   <T extends CompositeTerminable> T bindTerminable(T t);

   void cleanup();
}
