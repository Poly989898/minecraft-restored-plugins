package io.lumine.mythic.lib.util;

import org.jetbrains.annotations.NotNull;

public class OneTimeLock {
   private boolean lock;

   public boolean isLocked() {
      return this.lock;
   }

   public void lock(@NotNull String errorMessage) {
      if (this.lock) {
         throw new RuntimeException(errorMessage);
      }

      this.lock = true;
   }
}
