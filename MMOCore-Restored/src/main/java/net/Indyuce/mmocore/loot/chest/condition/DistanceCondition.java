package net.Indyuce.mmocore.loot.chest.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class DistanceCondition extends Condition {
   private final Location location;
   private final double distance;

   public DistanceCondition(MMOLineConfig config) {
      super(config);
      Validate.isTrue(config.contains("world"));
      Validate.isTrue(config.contains("x"));
      Validate.isTrue(config.contains("y"));
      Validate.isTrue(config.contains("z"));
      Validate.isTrue(config.contains("distance"));
      Validate.isTrue(Bukkit.getWorld(config.getString("world")) != null, "This world doesn't exist");
      this.location = new Location(Bukkit.getWorld(config.getString("world")), config.getDouble("x"), config.getDouble("y"), config.getDouble("z"));
      this.distance = config.getDouble("distance");
   }

   @Override
   public boolean isMet(ConditionInstance instance) {
      return instance.getLocation().getWorld().equals(this.location.getWorld()) && this.location.distance(instance.getLocation()) < this.distance;
   }
}
