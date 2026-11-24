package net.Indyuce.mmocore.waypoint;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.util.PostLoadAction;
import io.lumine.mythic.lib.util.PreloadedObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.loot.chest.condition.Condition;
import net.Indyuce.mmocore.loot.chest.condition.ConditionInstance;
import net.Indyuce.mmocore.player.Unlockable;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Waypoint implements Unlockable, PreloadedObject {
   private final String id;
   private final String name;
   private final Location loc;
   private final List<String> lore;
   private final double radiusSquared;
   private final int warpTime;
   private final Map<Waypoint, Double> destinations = new HashMap<>();
   private final Map<WaypointOption, Boolean> options = new HashMap<>();
   private final double dynamicCost;
   private final double setSpawnCost;
   private final double normalCost;
   private final List<Condition> dynamicUseConditions = new ArrayList<>();
   private final PostLoadAction postLoadAction = new PostLoadAction(configx -> {
      if (configx.contains("linked")) {
         ConfigurationSection section = configx.getConfigurationSection("linked");

         for (String key : section.getKeys(false)) {
            this.destinations.put(MMOCore.plugin.waypointManager.get(key), section.getDouble(key));
         }
      }

      if (MMOCore.plugin.configManager.waypointLinkReciprocity) {
         this.destinations.forEach((neighbor, cost) -> neighbor.destinations.put(this, cost));
      }
   });

   public Waypoint(ConfigurationSection config) {
      this.postLoadAction.cacheConfig(config);
      this.id = Objects.requireNonNull(config, "Could not load config section").getName();
      this.name = Objects.requireNonNull(config.getString("name"), "Could not load waypoint name");
      this.lore = Objects.requireNonNullElse(config.getStringList("lore"), new ArrayList<>());
      this.loc = MMOCoreUtils.readLocation(Objects.requireNonNull(config.getString("location"), "Could not read location"));
      this.radiusSquared = Math.pow(config.getDouble("radius"), 2.0);
      this.warpTime = Math.max(0, config.getInt("warp-time", MMOCore.plugin.configManager.waypointWarpTime));
      this.dynamicCost = config.getDouble("cost.dynamic-use");
      this.normalCost = config.getDouble("cost.normal-use");
      this.setSpawnCost = config.getDouble("cost.set-spawnpoint");

      for (WaypointOption option : WaypointOption.values()) {
         this.options.put(option, config.getBoolean("option." + option.getPath(), option.getDefaultValue()));
      }

      if (config.contains("dynamic-conditions")) {
         for (String condition : config.getStringList("dynamic-conditions")) {
            try {
               this.dynamicUseConditions.add(MMOCore.plugin.loadManager.loadCondition(new MMOLineConfig(condition)));
            } catch (RuntimeException exception) {
               MMOCore.plugin
                  .getLogger()
                  .log(Level.WARNING, "Could not load condition '" + condition + "' from waypoint '" + this.id + "': " + exception.getMessage());
            }
         }
      }
   }

   @NotNull
   public PostLoadAction getPostLoadAction() {
      return this.postLoadAction;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public List<String> getLore() {
      return this.lore;
   }

   public Location getLocation() {
      return this.loc;
   }

   public int getWarpTime() {
      return this.warpTime;
   }

   public double getDynamicCost() {
      return this.dynamicCost;
   }

   @Deprecated
   public double getSetSpawnCost() {
      return this.setSpawnCost;
   }

   public boolean mayBeUsedDynamically(Player player) {
      if (!this.options.get(WaypointOption.DYNAMIC)) {
         return false;
      }

      for (Condition condition : this.dynamicUseConditions) {
         if (!condition.isMet(new ConditionInstance(player))) {
            return false;
         }
      }

      return true;
   }

   public double getDirectCost(@NotNull Waypoint waypoint) {
      return this.destinations.isEmpty() ? this.normalCost : this.destinations.getOrDefault(waypoint, Double.POSITIVE_INFINITY);
   }

   public boolean hasOption(WaypointOption option) {
      return this.options.get(option);
   }

   public boolean isOnWaypoint(Player player) {
      return player.getWorld().equals(this.loc.getWorld()) && player.getLocation().distanceSquared(this.loc) < this.radiusSquared;
   }

   @NotNull
   public Map<Waypoint, Double> getDestinations() {
      return this.destinations;
   }

   @Override
   public String toString() {
      return this.id;
   }

   @Override
   public String getUnlockNamespacedKey() {
      return "waypoint:" + this.getId();
   }

   @Override
   public boolean isUnlockedByDefault() {
      return this.hasOption(WaypointOption.DEFAULT);
   }

   @Override
   public void whenLocked(PlayerData playerData) {
   }

   @Override
   public void whenUnlocked(PlayerData playerData) {
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Waypoint waypoint = (Waypoint)o;
         return this.id.equals(waypoint.id);
      } else {
         return false;
      }
   }
}
