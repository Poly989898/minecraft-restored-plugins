package io.lumine.mythic.lib.module;

import java.util.function.Supplier;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModuleWrapper<T extends Module> {
   private final Supplier<T> supplier;
   @Nullable
   private T current;

   private ModuleWrapper(Supplier<T> supplier) {
      this.supplier = supplier;
   }

   public void initialize() {
      Validate.isTrue(this.current == null, "Module is already open");
      this.current = this.supplier.get();
   }

   public void close() {
      Validate.notNull(this.current, "Module is closed");
   }

   @NotNull
   public static <T extends Module> ModuleWrapper<T> from(@NotNull Supplier<T> supplier) {
      return new ModuleWrapper<>(supplier);
   }
}
