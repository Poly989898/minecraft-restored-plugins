package team.unnamed.creative.base;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public enum CubeFace {
   WEST(Axis3D.X, -1),
   EAST(Axis3D.X, 1),
   DOWN(Axis3D.Y, -1),
   UP(Axis3D.Y, 1),
   NORTH(Axis3D.Z, -1),
   SOUTH(Axis3D.Z, 1);

   private final Axis3D axis;
   private final int factor;

   private CubeFace(final Axis3D axis, final int factor) {
      this.axis = (Axis3D)Objects.requireNonNull(axis, "axis");
      this.factor = factor;
   }

   public @NotNull Axis3D axis() {
      return this.axis;
   }

   public int factor() {
      return this.factor;
   }
}
