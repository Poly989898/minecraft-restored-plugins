package team.unnamed.creative.metadata.language;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record LanguageEntryImpl(String name, String region, boolean bidirectional) implements LanguageEntry {
   LanguageEntryImpl(final @NotNull String name, final @NotNull String region, final boolean bidirectional) {
      this.name = (String)Objects.requireNonNull(name, "name");
      this.region = (String)Objects.requireNonNull(region, "region");
      this.bidirectional = bidirectional;
      this.validate();
   }

   private void validate() {
      if (this.name.isEmpty()) {
         throw new IllegalArgumentException("'name' is empty!");
      } else if (this.region.isEmpty()) {
         throw new IllegalArgumentException("'region' is empty!");
      }
   }

   public @NotNull String name() {
      return this.name;
   }

   public @NotNull String region() {
      return this.region;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{name=" + this.name + ", region=" + this.region + ", bidirectional=" + this.bidirectional + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         LanguageEntryImpl entry = (LanguageEntryImpl)o;
         return this.bidirectional == entry.bidirectional && this.name.equals(entry.name) && this.region.equals(entry.region);
      } else {
         return false;
      }
   }

   static final class BuilderImpl implements LanguageEntry.Builder {
      private String name;
      private String region;
      private boolean bidirectional = false;

      public LanguageEntry.@NotNull Builder name(@NotNull String name) {
         this.name = (String)Objects.requireNonNull(name, "name");
         return this;
      }

      public LanguageEntry.@NotNull Builder region(@NotNull String region) {
         this.region = (String)Objects.requireNonNull(region, "region");
         return this;
      }

      public LanguageEntry.@NotNull Builder bidirectional(boolean bidirectional) {
         this.bidirectional = bidirectional;
         return this;
      }

      public @NotNull LanguageEntry build() {
         return new LanguageEntryImpl(this.name, this.region, this.bidirectional);
      }
   }
}
