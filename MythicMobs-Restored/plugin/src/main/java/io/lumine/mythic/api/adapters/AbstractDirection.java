package io.lumine.mythic.api.adapters;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AbstractDirection {
   DOWN("down", AbstractDirection.Axis.Y, AbstractDirection.AxisDirection.NEGATIVE, 1, new AbstractVector(0, -1, 0)),
   UP("up", AbstractDirection.Axis.Y, AbstractDirection.AxisDirection.POSITIVE, 0, new AbstractVector(0, 1, 0)),
   NORTH("north", AbstractDirection.Axis.Z, AbstractDirection.AxisDirection.NEGATIVE, 3, new AbstractVector(0, 0, -1)),
   SOUTH("south", AbstractDirection.Axis.Z, AbstractDirection.AxisDirection.POSITIVE, 2, new AbstractVector(0, 0, 1)),
   WEST("west", AbstractDirection.Axis.X, AbstractDirection.AxisDirection.NEGATIVE, 5, new AbstractVector(-1, 0, 0)),
   EAST("east", AbstractDirection.Axis.X, AbstractDirection.AxisDirection.POSITIVE, 4, new AbstractVector(1, 0, 0));

   private final String name;
   private final AbstractDirection.Axis axis;
   private final AbstractDirection.AxisDirection axisDirection;
   private final int oppositeDirectionIndex;
   private final AbstractVector normalization;

   public double getStepX() {
      return this.normalization.getX();
   }

   public double getStepY() {
      return this.normalization.getY();
   }

   public double getStepZ() {
      return this.normalization.getZ();
   }

   public static AbstractDirection getRelative(AbstractLocation from, AbstractLocation to) {
      int dx = to.getBlockX() - from.getBlockX();
      int dy = to.getBlockY() - from.getBlockY();
      int dz = to.getBlockZ() - from.getBlockZ();
      if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > Math.abs(dz)) {
         return dx > 0 ? EAST : WEST;
      } else if (Math.abs(dy) <= Math.abs(dx) || Math.abs(dy) <= Math.abs(dz)) {
         return dz > 0 ? SOUTH : NORTH;
      } else {
         return dy > 0 ? UP : DOWN;
      }
   }

   public static enum Axis {
      X,
      Y,
      Z;
   }

   public static enum AxisDirection {
      POSITIVE,
      NEGATIVE;
   }
}
