package io.lumine.mythic.bukkit.utils.cache;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class Expiring<T> implements Supplier<T> {
   private final Supplier<T> supplier;
   private final long durationNanos;
   private volatile T value;
   private volatile long expirationNanos;

   public static <T> Expiring<T> suppliedBy(Supplier<T> supplier, long duration, TimeUnit unit) {
      Objects.requireNonNull(supplier, "supplier");
      Preconditions.checkArgument(duration > 0L);
      Objects.requireNonNull(unit, "unit");
      return new Expiring<>(supplier, duration, unit);
   }

   private Expiring(Supplier<T> supplier, long duration, TimeUnit unit) {
      this.supplier = supplier;
      this.durationNanos = unit.toNanos(duration);
   }

   @Override
   public T get() {
      long nanos = this.expirationNanos;
      long now = System.nanoTime();
      if (nanos == 0L || now - nanos >= 0L) {
         synchronized (this) {
            if (nanos == this.expirationNanos) {
               T t = this.supplier.get();
               this.value = t;
               nanos = now + this.durationNanos;
               this.expirationNanos = nanos == 0L ? 1L : nanos;
               return t;
            }
         }
      }

      return this.value;
   }
}
