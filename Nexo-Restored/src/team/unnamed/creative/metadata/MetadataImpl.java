package team.unnamed.creative.metadata;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.util.MoreCollections;

final class MetadataImpl implements Metadata {
   static final Metadata EMPTY = new MetadataImpl(Collections.emptyMap());
   private final Map<Class<?>, MetadataPart> parts;

   MetadataImpl(@NotNull Map<Class<?>, MetadataPart> parts) {
      this.parts = MoreCollections.<Class<?>, MetadataPart>immutableMapOf((Map)Objects.requireNonNull(parts, "parts"));
   }

   public @Unmodifiable @NotNull Collection<MetadataPart> parts() {
      return this.parts.values();
   }

   public <T extends MetadataPart> @Nullable T meta(@NotNull Class<T> type) {
      MetadataPart metadataPart = (MetadataPart)this.parts.get(type);
      return (T)(metadataPart == null ? null : (MetadataPart)type.cast(metadataPart));
   }

   public Metadata.@NotNull Builder toBuilder() {
      Metadata.Builder builder = Metadata.metadata();

      for(Map.Entry<Class<?>, MetadataPart> entry : this.parts.entrySet()) {
         builder.addPart((MetadataPart)entry.getValue());
      }

      return builder;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{parts=" + String.valueOf(this.parts) + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MetadataImpl metadata = (MetadataImpl)o;
         return this.parts.equals(metadata.parts);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.parts});
   }

   static final class BuilderImpl implements Metadata.Builder {
      private final Map<Class<?>, MetadataPart> parts = new LinkedHashMap();

      public Metadata.@NotNull Builder parts(@NotNull Collection<MetadataPart> parts) {
         Objects.requireNonNull(parts, "parts");
         this.parts.clear();

         for(MetadataPart part : parts) {
            this.parts.put(part.type(), part);
         }

         return this;
      }

      public Metadata.@NotNull Builder addPart(@NotNull MetadataPart part) {
         Objects.requireNonNull(part, "part");
         this.parts.put(part.type(), part);
         return this;
      }

      public @NotNull Metadata build() {
         return new MetadataImpl(this.parts);
      }
   }
}
