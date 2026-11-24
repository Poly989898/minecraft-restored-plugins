package net.Indyuce.mmocore.waypoint;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map.Entry;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WaypointPathCalculation {
   private final PlayerData playerData;
   private final Map<Waypoint, WaypointPath> paths = new HashMap<>();

   public WaypointPathCalculation(PlayerData playerData) {
      this.playerData = playerData;
   }

   @NotNull
   public WaypointPathCalculation run(@Nullable Waypoint source) {
      if (MMOCore.plugin.configManager.waypointAutoPathCalculation) {
         this.runDijkstra(source);
      } else {
         this.runSimple(source);
      }

      return this;
   }

   public void runSimple(@Nullable Waypoint source) {
      if (source != null) {
         for (Entry<Waypoint, Double> adjacent : source.getDestinations().entrySet()) {
            this.replaceIfLower(adjacent.getKey(), adjacent.getValue());
         }
      }

      for (Waypoint waypoint : MMOCore.plugin.waypointManager.getAll()) {
         if (waypoint.hasOption(WaypointOption.DYNAMIC)) {
            this.replaceIfLower(waypoint, waypoint.getDynamicCost());
         }
      }
   }

   private void replaceIfLower(@NotNull Waypoint adjacent, double cost) {
      this.paths.compute(adjacent, (ignored, path) -> (WaypointPath)(path != null && !(cost < path.getCost()) ? path : new WaypointPath(cost)));
   }

   public void runDijkstra(@Nullable Waypoint source) {
      PriorityQueue<Pair<Waypoint, Double>> queue = new PriorityQueue<>(Comparator.comparingDouble(Pair::getRight));
      Set<Waypoint> visited = new HashSet<>();

      for (Waypoint waypoint : MMOCore.plugin.waypointManager.getAll()) {
         if (waypoint.hasOption(WaypointOption.DYNAMIC) && waypoint.mayBeUsedDynamically(this.playerData.getPlayer())) {
            this.init(queue, waypoint, waypoint.getDynamicCost());
         }
      }

      if (source != null) {
         this.init(queue, source, 0.0);
      }

      do {
         Waypoint currentNode = queue.remove().getLeft();
         visited.add(currentNode);
         WaypointPath nodePath = this.path(currentNode);

         for (Entry<Waypoint, Double> adjacentData : currentNode.getDestinations().entrySet()) {
            Waypoint adjacentNode = adjacentData.getKey();
            if (!adjacentNode.isUnlockedByDefault() && !adjacentNode.isOnWaypoint(this.playerData.getPlayer()) && !this.playerData.hasWaypoint(adjacentNode)) {
               return;
            }

            double edgeWeight = adjacentData.getValue();
            WaypointPath adjacentPath = this.path(adjacentNode);
            if (nodePath.getCost() + edgeWeight < adjacentPath.getCost()) {
               this.paths.put(adjacentNode, nodePath.push(currentNode, edgeWeight));
            }

            if (!visited.contains(adjacentNode)) {
               queue.add(Pair.of(adjacentNode, edgeWeight));
            }
         }
      } while (!queue.isEmpty());
   }

   private void init(@NotNull PriorityQueue<Pair<Waypoint, Double>> queue, @NotNull Waypoint waypoint, double cost) {
      queue.add(Pair.of(waypoint, cost));
      this.paths.put(waypoint, new WaypointPath(cost));
   }

   private WaypointPath path(Waypoint waypoint) {
      return this.paths.getOrDefault(waypoint, WaypointPath.INFINITE);
   }

   @NotNull
   public Map<Waypoint, WaypointPath> getPaths() {
      return this.paths;
   }
}
