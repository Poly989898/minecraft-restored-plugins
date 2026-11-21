package io.lumine.mythic.bukkit.utils.metadata;

import com.google.common.base.Preconditions;
import java.lang.ref.WeakReference;

public final class WeakValue<T> implements TransientValue<T> {
   private final WeakReference<T> value;

   public static <T> WeakValue<T> of(T value) {
      Preconditions.checkNotNull(value, "value");
      return new WeakValue<>(value);
   }

   private WeakValue(T value) {
      this.value = new WeakReference<>(value);
   }

   @Override
   public T getOrNull() {
      return this.value.get();
   }

   @Override
   public boolean shouldExpire() {
      return this.value.get() == null;
   }
}
