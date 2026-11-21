package team.unnamed.creative.waypoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public record WaypointStyleImpl(@NotNull Key key, int nearDistance, int farDistance, @NotNull List<Key> sprites) implements WaypointStyle {
   public @NotNull List<Key> sprites() {
      return Collections.unmodifiableList(this.sprites);
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + ", nearDistance=" + this.nearDistance + ", farDistance=" + this.farDistance + ", sprites=" + String.valueOf(this.sprites) + "}";
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof WaypointStyleImpl)) {
         return false;
      } else {
         WaypointStyleImpl that = (WaypointStyleImpl)obj;
         return this.nearDistance == that.nearDistance && this.farDistance == that.farDistance && this.key.equals(that.key) && this.sprites.equals(that.sprites);
      }
   }

   static final class BuilderImpl implements WaypointStyle.Builder {
      private Key key;
      private int nearDistance = 128;
      private int farDistance = 332;
      private final @NotNull ArrayList<Key> sprites = new ArrayList();

      public WaypointStyle.@NotNull Builder key(@NotNull Key key) {
         this.key = key;
         return this;
      }

      public WaypointStyle.@NotNull Builder nearDistance(int nearDistance) {
         this.nearDistance = nearDistance;
         return this;
      }

      public WaypointStyle.@NotNull Builder farDistance(int farDistance) {
         this.farDistance = farDistance;
         return this;
      }

      public WaypointStyle.@NotNull Builder sprite(@NotNull Key sprite) {
         this.sprites.add(sprite);
         return this;
      }

      public @NotNull WaypointStyle build() {
         if (this.key == null) {
            throw new IllegalStateException("Key cannot be null");
         } else if (this.sprites.isEmpty()) {
            throw new IllegalStateException("Sprites cannot be empty");
         } else if (this.nearDistance < 0) {
            throw new IllegalStateException("Near distance cannot be negative");
         } else if (this.farDistance < 0) {
            throw new IllegalStateException("Far distance cannot be negative");
         } else if (this.nearDistance > this.farDistance) {
            throw new IllegalStateException("Near distance cannot be greater than far distance");
         } else {
            return new WaypointStyleImpl(this.key, this.nearDistance, this.farDistance, this.sprites);
         }
      }
   }
}
