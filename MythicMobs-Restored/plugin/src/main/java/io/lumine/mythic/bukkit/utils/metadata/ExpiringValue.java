package io.lumine.mythic.bukkit.utils.metadata;

import com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;

public final class ExpiringValue<T> implements TransientValue<T> {
   private final T value;
   private final long expireAt;

   public static <T> ExpiringValue<T> of(T value, long duration, TimeUnit unit) {
      Preconditions.checkArgument(duration >= 0L, "duration must be >= 0");
      Preconditions.checkNotNull(value, "value");
      Preconditions.checkNotNull(unit, "unit");
      long millis = unit.toMillis(duration);
      long expireAt = System.currentTimeMillis() + millis;
      return new ExpiringValue<>(value, expireAt);
   }

   private ExpiringValue(T value, long expireAt) {
      this.value = value;
      this.expireAt = expireAt;
   }

   @Override
   public T getOrNull() {
      return this.shouldExpire() ? null : this.value;
   }

   @Override
   public boolean shouldExpire() {
      return System.currentTimeMillis() > this.expireAt;
   }
}
