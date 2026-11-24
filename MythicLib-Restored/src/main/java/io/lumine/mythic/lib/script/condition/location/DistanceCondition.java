package io.lumine.mythic.lib.script.condition.location;

import io.lumine.mythic.lib.script.condition.type.LocationCondition;
import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.Optional;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;

public class DistanceCondition extends LocationCondition {
   private final LocationTargeter center;
   private final double distanceMax;

   public DistanceCondition(ConfigObject config) {
      super(config, true);
      config.validateKeys("location", "max");
      this.center = config.getLocationTargeter("location");
      this.distanceMax = config.getDouble("max");
   }

   @Override
   public boolean isMet(SkillMetadata meta, Location loc) {
      Optional<Location> target = this.center.findTargets(meta).stream().findAny();
      Validate.isTrue(target.isPresent());
      return target.get().distance(loc) <= this.distanceMax;
   }
}
