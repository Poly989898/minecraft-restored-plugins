package team.unnamed.creative.metadata.filter;

import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.base.KeyPattern;
import team.unnamed.creative.metadata.MetadataPart;
import team.unnamed.creative.util.MoreCollections;

record FilterMetaImpl(List<KeyPattern> patterns) implements FilterMeta {
   FilterMetaImpl(final @NotNull List<KeyPattern> patterns) {
      this.patterns = MoreCollections.<KeyPattern>immutableListOf((List)Objects.requireNonNull(patterns, "patterns"));
      this.validate();
   }

   private void validate() {
      if (this.patterns.isEmpty()) {
         throw new IllegalArgumentException("Patterns list is empty!");
      } else {
         for(KeyPattern pattern : this.patterns) {
            if (pattern == null) {
               throw new NullPointerException("An element in the patterns list is null");
            }
         }

      }
   }

   public @NotNull Class<? extends MetadataPart> type() {
      return FilterMeta.class;
   }

   public @NotNull @Unmodifiable List<KeyPattern> patterns() {
      return this.patterns;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{patterns=" + String.valueOf(this.patterns) + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         FilterMetaImpl that = (FilterMetaImpl)o;
         return this.patterns.equals(that.patterns);
      } else {
         return false;
      }
   }
}
