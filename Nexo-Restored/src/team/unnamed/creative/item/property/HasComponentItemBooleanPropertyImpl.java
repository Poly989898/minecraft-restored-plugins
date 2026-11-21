package team.unnamed.creative.item.property;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record HasComponentItemBooleanPropertyImpl(String component, boolean ignoreDefault) implements HasComponentItemBooleanProperty {
   HasComponentItemBooleanPropertyImpl(final @NotNull String component, final boolean ignoreDefault) {
      this.component = (String)Objects.requireNonNull(component, "component");
      this.ignoreDefault = ignoreDefault;
   }

   public @NotNull String component() {
      return this.component;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{component=" + this.component + ", ignoreDefault=" + this.ignoreDefault + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         HasComponentItemBooleanPropertyImpl that = (HasComponentItemBooleanPropertyImpl)o;
         return this.ignoreDefault == that.ignoreDefault && this.component.equals(that.component);
      } else {
         return false;
      }
   }
}
