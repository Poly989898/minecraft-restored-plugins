package team.unnamed.creative.atlas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.texture.Texture;
import team.unnamed.creative.util.MoreCollections;

record AtlasImpl(Key key, List<AtlasSource> sources) implements Atlas {
   AtlasImpl(final @NotNull Key key, final @NotNull List<AtlasSource> sources) {
      this.key = (Key)Objects.requireNonNull(key, "key");
      this.sources = MoreCollections.<AtlasSource>immutableListOf((List)Objects.requireNonNull(sources, "sources"));
   }

   public @NotNull Key key() {
      return this.key;
   }

   public @Unmodifiable @NotNull List<AtlasSource> sources() {
      return this.sources;
   }

   public boolean contains(@NotNull Texture texture) {
      return this.contains(texture.key());
   }

   public boolean contains(@NotNull Key textureKey) {
      for(AtlasSource source : this.sources) {
         if (source instanceof SingleAtlasSource single) {
            if (single.resource() == textureKey || single.sprite() == textureKey) {
               return true;
            }
         } else if (source instanceof DirectoryAtlasSource directory) {
            if (textureKey.value().startsWith(directory.prefix())) {
               return true;
            }
         }
      }

      return false;
   }

   public Atlas.@NotNull Builder toBuilder() {
      return Atlas.atlas().key(this.key).sources(this.sources);
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + ", sources=" + String.valueOf(this.sources) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         AtlasImpl atlas = (AtlasImpl)o;
         return !this.key.equals(atlas.key) ? false : this.sources.equals(atlas.sources);
      } else {
         return false;
      }
   }

   static class BuilderImpl implements Atlas.Builder {
      private Key key;
      private List<AtlasSource> sources;

      public @Nullable Key key() {
         return this.key;
      }

      public Atlas.@NotNull Builder key(@NotNull Key key) {
         this.key = (Key)Objects.requireNonNull(key, "key");
         return this;
      }

      public @Nullable List<AtlasSource> sources() {
         return this.sources;
      }

      public Atlas.@NotNull Builder sources(@NotNull List<AtlasSource> sources) {
         Objects.requireNonNull(sources, "sources");
         this.sources = new ArrayList(sources);
         return this;
      }

      public Atlas.@NotNull Builder sources(@NotNull AtlasSource... sources) {
         Objects.requireNonNull(sources, "sources");
         this.sources = new ArrayList(Arrays.asList(sources));
         return this;
      }

      public Atlas.@NotNull Builder addSource(@NotNull AtlasSource source) {
         Objects.requireNonNull(source, "source");
         if (this.sources == null) {
            this.sources = new ArrayList();
         }

         this.sources.add(source);
         return this;
      }

      public Atlas.@NotNull Builder removeSource(@NotNull AtlasSource source) {
         Objects.requireNonNull(source, "source");
         if (this.sources == null) {
            this.sources = new ArrayList();
         }

         this.sources.remove(source);
         return this;
      }

      public @NotNull Atlas build() {
         return new AtlasImpl(this.key, this.sources);
      }
   }
}
