package team.unnamed.creative.item.property;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record ComponentItemStringPropertyImpl(String component) implements ComponentItemStringProperty {
   ComponentItemStringPropertyImpl(final @NotNull String component) {
      this.component = (String)Objects.requireNonNull(component, "component");
   }

   public @NotNull String component() {
      return this.component;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{component=" + this.component + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         ComponentItemStringPropertyImpl that = (ComponentItemStringPropertyImpl)o;
         return this.component.equals(that.component);
      } else {
         return false;
      }
   }
}
