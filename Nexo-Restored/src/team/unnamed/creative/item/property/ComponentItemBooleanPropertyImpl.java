package team.unnamed.creative.item.property;

import com.google.gson.JsonElement;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

record ComponentItemBooleanPropertyImpl(String predicate, JsonElement value) implements ComponentItemBooleanProperty {
   ComponentItemBooleanPropertyImpl(final @NotNull String predicate, final @NotNull JsonElement value) {
      this.predicate = (String)Objects.requireNonNull(predicate, "predicate");
      this.value = (JsonElement)Objects.requireNonNull(value, "value");
   }

   public @NotNull String predicate() {
      return this.predicate;
   }

   public @NotNull JsonElement value() {
      return this.value;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{predicate=" + this.predicate + ", value=" + String.valueOf(this.value) + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         ComponentItemBooleanPropertyImpl that = (ComponentItemBooleanPropertyImpl)o;
         return this.predicate.equals(that.predicate) && this.value.equals(that.value);
      } else {
         return false;
      }
   }
}
