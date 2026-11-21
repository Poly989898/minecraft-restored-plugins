package team.unnamed.creative.item.property;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

record TimeItemNumericPropertyImpl(boolean wobble, TimeItemNumericProperty.Source source) implements TimeItemNumericProperty {
   TimeItemNumericPropertyImpl(final boolean wobble, final TimeItemNumericProperty.@NotNull Source source) {
      this.wobble = wobble;
      this.source = (TimeItemNumericProperty.Source)Objects.requireNonNull(source, "source");
   }

   public TimeItemNumericProperty.@NotNull Source source() {
      return this.source;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{wobble=" + this.wobble + ", source=" + String.valueOf(this.source) + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         TimeItemNumericPropertyImpl that = (TimeItemNumericPropertyImpl)o;
         return this.wobble == that.wobble && this.source == that.source;
      } else {
         return false;
      }
   }
}
