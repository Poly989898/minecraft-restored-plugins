package net.Indyuce.mmocore.manager;

import io.lumine.mythic.lib.util.FileUtils;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.waypoint.Waypoint;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class WaypointManager implements MMOCoreManager {
   private final Map<String, Waypoint> waypoints = new LinkedHashMap<>();

   public Collection<Waypoint> getAll() {
      return this.waypoints.values();
   }

   public boolean has(String id) {
      return this.waypoints.containsKey(id);
   }

   @Nullable
   public Waypoint get(String id) {
      return this.waypoints.get(id);
   }

   public void register(Waypoint waypoint) {
      Validate.isTrue(
         !this.waypoints.containsKey(Objects.requireNonNull(waypoint, "Waypoint cannot be null").getId()),
         "There is already a waypoint with ID '" + waypoint.getId() + "'"
      );
      this.waypoints.put(waypoint.getId(), waypoint);
   }

   @Nullable
   public Waypoint getCurrentWaypoint(Player player) {
      for (Waypoint waypoint : this.getAll()) {
         if (waypoint.isOnWaypoint(player)) {
            return waypoint;
         }
      }

      return null;
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.waypoints.clear();
      }

      FileUtils.loadObjectsFromFolder(
         MMOCore.plugin, "waypoints", false, (key, config) -> this.register(new Waypoint(config)), "Could not load waypoint '%s' from file '%s': %s"
      );

      for (Waypoint waypoint : this.waypoints.values()) {
         try {
            waypoint.getPostLoadAction().performAction();
         } catch (RuntimeException exception) {
            MMOCore.log(Level.WARNING, "Could not post-load waypoint '" + waypoint.getId() + "': " + exception.getMessage());
         }
      }
   }
}
