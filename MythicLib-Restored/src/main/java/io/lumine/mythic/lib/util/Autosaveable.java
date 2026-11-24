package io.lumine.mythic.lib.util;

public interface Autosaveable extends Closeable {
   void prepareSaving(boolean var1);

   @Override
   default void close() {
      this.prepareSaving(false);
   }
}
