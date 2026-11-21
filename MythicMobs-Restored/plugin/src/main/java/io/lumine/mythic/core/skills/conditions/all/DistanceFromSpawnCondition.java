package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "distanceFromSpawn",
   aliases = {},
   description = "Whether the distance from the world's spawn point to the target is within the given range"
)
public class DistanceFromSpawnCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "distance",
      aliases = {"d"},
      description = "The distance to match"
   )
   private final PlaceholderString distance;

   public DistanceFromSpawnCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.distance = mlc.getPlaceholderString(new String[]{"distance", "d"}, this.conditionVar);
   }

   @Override
   public boolean check(AbstractLocation l) {
      double distanceSq = l.getWorld().getSpawnLocation().distanceSquared(l);
      return new RangedDouble(this.distance.get()).equalsSquared(distanceSq);
   }
}
