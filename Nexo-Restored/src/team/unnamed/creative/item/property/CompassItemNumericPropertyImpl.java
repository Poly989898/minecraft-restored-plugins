package team.unnamed.creative.item.property;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

record CompassItemNumericPropertyImpl(CompassItemNumericProperty.Target target, boolean wobble) implements CompassItemNumericProperty {
   CompassItemNumericPropertyImpl(final CompassItemNumericProperty.@NotNull Target target, final boolean wobble) {
      this.target = (CompassItemNumericProperty.Target)Objects.requireNonNull(target, "target");
      this.wobble = wobble;
   }

   public CompassItemNumericProperty.@NotNull Target target() {
      return this.target;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{target=" + String.valueOf(this.target) + ", wobble=" + this.wobble + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         CompassItemNumericPropertyImpl that = (CompassItemNumericPropertyImpl)o;
         return this.wobble == that.wobble && this.target == that.target;
      } else {
         return false;
      }
   }
}
