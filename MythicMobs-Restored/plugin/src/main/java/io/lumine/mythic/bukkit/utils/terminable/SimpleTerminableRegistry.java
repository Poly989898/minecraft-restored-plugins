package io.lumine.mythic.bukkit.utils.terminable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.terminable.composite.CompositeTerminable;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;

final class SimpleTerminableRegistry implements TerminableRegistry {
   private final List<Terminable> terminables = Collections.synchronizedList(new ObjectArrayList());
   private boolean terminated = false;

   public void accept(Terminable terminable) {
      Preconditions.checkNotNull(terminable, "terminable");
      this.terminables.add(terminable);
   }

   @Override
   public <T extends CompositeTerminable> T bindTerminable(T terminable) {
      Preconditions.checkNotNull(terminable, "terminable");
      terminable.bind(this);
      return terminable;
   }

   @Override
   public void close() {
      Lists.reverse(this.terminables).forEach(terminable -> {
         if (terminable != null) {
            try {
               terminable.terminate();
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
      this.terminables.clear();
      this.terminated = true;
   }

   @Override
   public boolean hasTerminated() {
      return this.terminated;
   }

   @Override
   public void cleanup() {
      this.terminables.removeIf(Terminable::hasTerminated);
   }
}
