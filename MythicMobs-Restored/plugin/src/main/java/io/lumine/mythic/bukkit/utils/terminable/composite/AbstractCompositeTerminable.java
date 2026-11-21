package io.lumine.mythic.bukkit.utils.terminable.composite;

import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

public class AbstractCompositeTerminable implements CompositeTerminable {
   private final Deque<AutoCloseable> closeables = new ConcurrentLinkedDeque<>();

   protected AbstractCompositeTerminable() {
   }

   @Override
   public CompositeTerminable with(AutoCloseable autoCloseable) {
      Objects.requireNonNull(autoCloseable, "autoCloseable");
      this.closeables.push(autoCloseable);
      return this;
   }

   @Override
   public void close() throws CompositeClosingException {
      List<Exception> caught = new ArrayList<>();

      AutoCloseable ac;
      while ((ac = this.closeables.poll()) != null) {
         try {
            ac.close();
         } catch (Exception e) {
            caught.add(e);
         }
      }

      if (!caught.isEmpty()) {
         throw new CompositeClosingException(caught);
      }
   }

   @Override
   public void cleanup() {
      this.closeables.removeIf(ac -> {
         if (!(ac instanceof Terminable)) {
            return false;
         } else {
            if (ac instanceof CompositeTerminable) {
               ((CompositeTerminable)ac).cleanup();
            }

            return ((Terminable)ac).isClosed();
         }
      });
   }
}
