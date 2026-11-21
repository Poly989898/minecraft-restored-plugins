package team.unnamed.creative.texture;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.metadata.Metadata;

record TextureImpl(Key key, Writable data, Metadata meta) implements Texture {
   TextureImpl(final @NotNull Key key, final @NotNull Writable data, final @NotNull Metadata meta) {
      this.key = (Key)Objects.requireNonNull(key, "key");
      this.data = (Writable)Objects.requireNonNull(data, "data");
      this.meta = (Metadata)Objects.requireNonNull(meta, "meta");
   }

   public @NotNull Key key() {
      return this.key;
   }

   public @NotNull Writable data() {
      return this.data;
   }

   public @NotNull Metadata meta() {
      return this.meta;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + ", data=" + String.valueOf(this.data) + ", meta=" + String.valueOf(this.meta) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TextureImpl texture = (TextureImpl)o;
         return this.key.equals(texture.key) && this.data.equals(texture.data) && this.meta.equals(texture.meta);
      } else {
         return false;
      }
   }

   static final class BuilderImpl implements Texture.Builder {
      private Key key;
      private Writable data;
      private Metadata meta = Metadata.empty();

      public Texture.@NotNull Builder key(@NotNull Key key) {
         this.key = (Key)Objects.requireNonNull(key, "key");
         return this;
      }

      public @Nullable Key key() {
         return this.key;
      }

      public Texture.@NotNull Builder data(@NotNull Writable data) {
         this.data = (Writable)Objects.requireNonNull(data, "data");
         return this;
      }

      public @Nullable Writable data() {
         return this.data;
      }

      public Texture.@NotNull Builder meta(@NotNull Metadata meta) {
         this.meta = (Metadata)Objects.requireNonNull(meta, "meta");
         return this;
      }

      public @NotNull Metadata meta() {
         return this.meta;
      }

      public @NotNull Texture build() {
         return new TextureImpl(this.key, this.data, this.meta);
      }
   }
}
