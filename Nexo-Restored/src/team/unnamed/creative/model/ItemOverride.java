package team.unnamed.creative.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.util.MoreCollections;

/** @deprecated */
@Deprecated
public class ItemOverride {
   private final Key model;
   private final @Unmodifiable List<ItemPredicate> predicate;

   private ItemOverride(Key model, List<ItemPredicate> predicate) {
      Objects.requireNonNull(model, "model");
      Objects.requireNonNull(predicate, "predicate");
      this.model = model;
      this.predicate = MoreCollections.<ItemPredicate>immutableListOf(predicate);
   }

   public Key model() {
      return this.model;
   }

   public @Unmodifiable List<ItemPredicate> predicate() {
      return this.predicate;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{predicate=" + String.valueOf(this.predicate) + ", model=" + String.valueOf(this.model) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ItemOverride that = (ItemOverride)o;
         return this.predicate.equals(that.predicate) && this.model.equals(that.model);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.predicate, this.model});
   }

   public static ItemOverride of(Key model, List<ItemPredicate> predicates) {
      return new ItemOverride(model, predicates);
   }

   public static ItemOverride of(Key model, ItemPredicate... predicates) {
      return new ItemOverride(model, Arrays.asList(predicates));
   }
}
