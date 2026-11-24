package io.lumine.mythic.lib.util;

public interface Autosaveable extends Closeable {
   void prepareSaving(boolean flag);

   @Override
   default void close() {
      this.prepareSaving(false);
   }
}
