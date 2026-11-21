package team.unnamed.creative.item.property;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record BlockStateItemStringPropertyImpl(String property) implements BlockStateItemStringProperty {
   BlockStateItemStringPropertyImpl(final @NotNull String property) {
      this.property = (String)Objects.requireNonNull(property, "property");
   }

   public @NotNull String property() {
      return this.property;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{property=" + this.property + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         BlockStateItemStringPropertyImpl that = (BlockStateItemStringPropertyImpl)o;
         return this.property.equals(that.property);
      } else {
         return false;
      }
   }
}
