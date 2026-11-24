package io.lumine.mythic.lib.util;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.commons.lang.Validate;

public class Lazy<T> implements Supplier<T> {
   private final boolean persistent;
   @Nullable
   private Supplier<T> expression;
   private boolean evaluated;
   @Nullable
   private T value;

   private Lazy(@Nullable Supplier<T> expression, boolean persistent) {
      this.expression = expression;
      this.persistent = persistent;
   }

   public void flush() {
      Validate.isTrue(this.persistent, "Non persistent lazy value");
      this.value = null;
      this.evaluated = false;
   }

   public static <T> Lazy<T> persistent(Supplier<T> expression) {
      return new Lazy<>(expression, true);
   }

   public static <T> Lazy<T> of(Supplier<T> expression) {
      return new Lazy<>(expression, true);
   }

   @Override
   public T get() {
      if (this.evaluated) {
         return this.value;
      }

      Validate.notNull(this.expression, "Non persistent lazy value");
      this.value = this.expression.get();
      this.evaluated = true;
      if (!this.persistent) {
         this.expression = null;
      }

      return this.value;
   }
}
