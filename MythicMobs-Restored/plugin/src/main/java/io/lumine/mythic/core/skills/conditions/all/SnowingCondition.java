package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.Location;

@MythicCondition(
   author = "Taiyouh",
   name = "snowing",
   aliases = {"issnowing"},
   description = "If it's snowing at the target location"
)
public class SnowingCondition extends SkillCondition implements ILocationCondition {
   public SnowingCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractLocation target) {
      Location location = BukkitAdapter.adapt(target);
      return location.getWorld().hasStorm() && location.getBlock().getTemperature() < 0.15;
   }
}
