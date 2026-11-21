package team.unnamed.creative.waypoint;

import java.util.List;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;

public interface WaypointStyle extends ResourcePackPart, Keyed {
   @Internal
   int DEFAULT_NEAR_DISTANCE = 128;
   @Internal
   int DEFAULT_FAR_DISTANCE = 332;

   @NotNull Key key();

   int nearDistance();

   int farDistance();

   @NotNull List<Key> sprites();

   default void addTo(@NotNull ResourceContainer resourceContainer) {
      resourceContainer.waypointStyle(this);
   }

   @Contract("-> new")
   static @NotNull Builder waypointStyle() {
      return new WaypointStyleImpl.BuilderImpl();
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder key(@NotNull Key var1);

      @Contract("_ -> this")
      @NotNull Builder nearDistance(int var1);

      @Contract("_ -> this")
      @NotNull Builder farDistance(int var1);

      @Contract("_ -> this")
      @NotNull Builder sprite(@NotNull Key var1);

      @Contract("-> new")
      @NotNull WaypointStyle build();
   }
}
