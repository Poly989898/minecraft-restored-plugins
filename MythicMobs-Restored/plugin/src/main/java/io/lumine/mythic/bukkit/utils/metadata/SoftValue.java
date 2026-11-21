package io.lumine.mythic.bukkit.utils.metadata;

import com.google.common.base.Preconditions;
import java.lang.ref.SoftReference;

public final class SoftValue<T> implements TransientValue<T> {
   private final SoftReference<T> value;

   public static <T> SoftValue<T> of(T value) {
      Preconditions.checkNotNull(value, "value");
      return new SoftValue<>(value);
   }

   private SoftValue(T value) {
      this.value = new SoftReference<>(value);
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
