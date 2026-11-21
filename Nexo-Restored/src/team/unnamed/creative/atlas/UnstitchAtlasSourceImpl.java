package team.unnamed.creative.atlas;

import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.util.MoreCollections;

record UnstitchAtlasSourceImpl(Key resource, List<UnstitchAtlasSource.Region> regions, Vector2Float divisor) implements UnstitchAtlasSource {
   UnstitchAtlasSourceImpl(final @NotNull Key resource, final @NotNull List<UnstitchAtlasSource.Region> regions, final @NotNull Vector2Float divisor) {
      this.resource = (Key)Objects.requireNonNull(resource, "resource");
      this.regions = MoreCollections.<UnstitchAtlasSource.Region>immutableListOf((List)Objects.requireNonNull(regions, "regions"));
      this.divisor = (Vector2Float)Objects.requireNonNull(divisor, "divisor");
   }

   public @NotNull Key resource() {
      return this.resource;
   }

   public @NotNull @Unmodifiable List<UnstitchAtlasSource.Region> regions() {
      return this.regions;
   }

   public @NotNull Vector2Float divisor() {
      return this.divisor;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{resource=" + String.valueOf(this.resource) + ", regions=" + String.valueOf(this.regions) + ", divisor=" + String.valueOf(this.divisor) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         UnstitchAtlasSourceImpl that = (UnstitchAtlasSourceImpl)o;
         if (!this.divisor.equals(that.divisor)) {
            return false;
         } else {
            return !this.resource.equals(that.resource) ? false : this.regions.equals(that.regions);
         }
      } else {
         return false;
      }
   }

   static record RegionImpl(Key sprite, Vector2Float position, Vector2Float dimensions) implements UnstitchAtlasSource.Region {
      RegionImpl(final @NotNull Key sprite, final @NotNull Vector2Float position, final @NotNull Vector2Float dimensions) {
         this.sprite = (Key)Objects.requireNonNull(sprite, "sprite");
         this.position = (Vector2Float)Objects.requireNonNull(position, "position");
         this.dimensions = (Vector2Float)Objects.requireNonNull(dimensions, "dimensions");
      }

      public @NotNull Key sprite() {
         return this.sprite;
      }

      public @NotNull Vector2Float position() {
         return this.position;
      }

      public @NotNull Vector2Float dimensions() {
         return this.dimensions;
      }

      public @NotNull String toString() {
         String var10000 = this.getClass().getSimpleName();
         return var10000 + "{sprite=" + String.valueOf(this.sprite) + ", position=" + String.valueOf(this.position) + ", dimensions=" + String.valueOf(this.dimensions) + "}";
      }
   }
}
