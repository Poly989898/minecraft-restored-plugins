package io.lumine.mythic.bukkit.utils.terminable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@FunctionalInterface
public interface Terminable extends AutoCloseable {
   Terminable EMPTY = () -> {};

   default boolean terminate() {
      this.closeAndReportException();
      return true;
   }

   @Override
   void close() throws Exception;

   default boolean isClosed() {
      return this.hasTerminated();
   }

   default boolean hasTerminated() {
      return false;
   }

   @Nullable
   default Exception closeSilently() {
      try {
         this.close();
         return null;
      } catch (Exception e) {
         return e;
      }
   }

   default void closeAndReportException() {
      try {
         this.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   default void bindWith(@Nonnull TerminableConsumer consumer) {
      consumer.bind(this);
   }
}
