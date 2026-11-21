package team.unnamed.creative.blockstate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class MultiVariant {
   private final List<Variant> variants;

   private MultiVariant(List<Variant> variants) {
      this.variants = (List)Objects.requireNonNull(variants, "variants");
      this.validate();
   }

   private void validate() {
      if (this.variants.isEmpty()) {
         throw new IllegalArgumentException("No variants provided!");
      }
   }

   public List<Variant> variants() {
      return this.variants;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{variants=" + String.valueOf(this.variants) + "}";
   }

   public static MultiVariant of(List<Variant> variants) {
      return new MultiVariant(variants);
   }

   public static MultiVariant of(Variant... variants) {
      return new MultiVariant(Arrays.asList(variants));
   }
}
