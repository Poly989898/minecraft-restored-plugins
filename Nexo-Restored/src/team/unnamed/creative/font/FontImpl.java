package team.unnamed.creative.font;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

record FontImpl(Key key, List<FontProvider> providers) implements Font {
   FontImpl(final @NotNull Key key, final @NotNull List<FontProvider> providers) {
      this.key = (Key)Objects.requireNonNull(key, "key");
      this.providers = (List)Objects.requireNonNull(providers, "providers");
   }

   public @NotNull Key key() {
      return this.key;
   }

   public @NotNull Font key(@NotNull Key key) {
      Objects.requireNonNull(key, "key");
      return new FontImpl(key, this.providers);
   }

   public @NotNull List<FontProvider> providers() {
      return this.providers;
   }

   public @NotNull Font providers(@NotNull List<FontProvider> providers) {
      Objects.requireNonNull(providers, "providers");
      return new FontImpl(this.key, providers);
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + ", providers=" + String.valueOf(this.providers) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         FontImpl that = (FontImpl)o;
         return this.key.equals(that.key) && this.providers.equals(that.providers);
      } else {
         return false;
      }
   }

   static final class BuilderImpl implements Font.Builder {
      private Key key;
      private List<FontProvider> providers;

      public Font.@NotNull Builder key(@NotNull Key key) {
         this.key = (Key)Objects.requireNonNull(key, "key");
         return this;
      }

      public Font.@NotNull Builder providers(@NotNull List<FontProvider> providers) {
         this.providers = new ArrayList((Collection)Objects.requireNonNull(providers, "providers"));
         return this;
      }

      public Font.@NotNull Builder providers(@NotNull FontProvider... providers) {
         return this.providers(Arrays.asList(providers));
      }

      public Font.@NotNull Builder addProvider(@NotNull FontProvider provider) {
         Objects.requireNonNull(provider, "provider");
         if (this.providers == null) {
            this.providers = new ArrayList();
         }

         this.providers.add(provider);
         return this;
      }

      public @NotNull Font build() {
         return new FontImpl(this.key, this.providers);
      }
   }
}
