package team.unnamed.creative.blockstate;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class Selector {
   private final Condition condition;
   private final @Unmodifiable MultiVariant variant;

   private Selector(Condition condition, MultiVariant variant) {
      this.condition = (Condition)Objects.requireNonNull(condition, "condition");
      this.variant = (MultiVariant)Objects.requireNonNull(variant, "variant");
   }

   public Condition condition() {
      return this.condition;
   }

   public @Unmodifiable MultiVariant variant() {
      return this.variant;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{condition=" + String.valueOf(this.condition) + ", variant=" + String.valueOf(this.variant) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Selector selector = (Selector)o;
         return this.condition.equals(selector.condition) && this.variant.equals(selector.variant);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.condition, this.variant});
   }

   public static Selector of(Condition condition, MultiVariant variant) {
      return new Selector(condition, variant);
   }

   public static Selector of(MultiVariant variant) {
      return new Selector(Condition.NONE, variant);
   }
}
