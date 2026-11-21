package io.lumine.mythic.bukkit.utils.terminable.composite;

import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CompositeTerminable extends Terminable, TerminableConsumer {
   @Nonnull
   static CompositeTerminable create() {
      return new AbstractCompositeTerminable();
   }

   @Nonnull
   static CompositeTerminable createWeak() {
      return new AbstractWeakCompositeTerminable();
   }

   @Override
   void close() throws CompositeClosingException;

   @Nullable
   default CompositeClosingException closeSilently() {
      try {
         this.close();
         return null;
      } catch (CompositeClosingException e) {
         return e;
      }
   }

   @Override
   default void closeAndReportException() {
      try {
         this.close();
      } catch (CompositeClosingException e) {
         e.printAllStackTraces();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   CompositeTerminable with(AutoCloseable autoCloseable);

   default CompositeTerminable withAll(AutoCloseable... autoCloseables) {
      for (AutoCloseable autoCloseable : autoCloseables) {
         if (autoCloseable != null) {
            this.bind(autoCloseable);
         }
      }

      return this;
   }

   default CompositeTerminable withAll(Iterable<? extends AutoCloseable> autoCloseables) {
      for (AutoCloseable autoCloseable : autoCloseables) {
         if (autoCloseable != null) {
            this.bind(autoCloseable);
         }
      }

      return this;
   }

   @Nonnull
   @Override
   default <T extends AutoCloseable> T bind(@Nonnull T terminable) {
      this.with(terminable);
      return terminable;
   }

   void cleanup();
}
