package net.Indyuce.mmocore.waypoint;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class WaypointPath {
   private final List<Waypoint> waypoints = new ArrayList<>();
   private final double cost;
   public static final WaypointPath INFINITE = new WaypointPath(Double.POSITIVE_INFINITY);

   public WaypointPath() {
      this(0.0);
   }

   public WaypointPath(double cost) {
      this.cost = cost;
   }

   @NotNull
   public List<Waypoint> getWaypoints() {
      return this.waypoints;
   }

   public double getCost() {
      return this.cost;
   }

   @NotNull
   public String displayIntermediaryWayPoints(String splitter, String none) {
      if (this.waypoints.isEmpty()) {
         return none;
      }

      boolean b = false;
      StringBuilder result = new StringBuilder();

      for (Waypoint waypoint : this.waypoints) {
         if (b) {
            result.append(splitter);
         }

         result.append(waypoint.getName());
         if (!b) {
            b = true;
         }
      }

      return result.toString();
   }

   @NotNull
   public WaypointPath push(@NotNull Waypoint waypoint, double extraCost) {
      WaypointPath clone = new WaypointPath(this.cost + extraCost);
      clone.waypoints.add(waypoint);
      return clone;
   }
}
