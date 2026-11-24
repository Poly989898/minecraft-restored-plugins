package net.Indyuce.mmoitems.util;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public abstract class Buildable<B> {
   private boolean lock = true;

   protected abstract B whenBuilt();

   public boolean isBuilt() {
      return !this.lock;
   }

   @NotNull
   public B build() {
      Validate.isTrue(this.lock, "Has already been built");
      this.lock = false;
      return this.whenBuilt();
   }
}
