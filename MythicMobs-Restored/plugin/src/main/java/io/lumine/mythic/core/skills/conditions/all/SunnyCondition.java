package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "sunny",
   aliases = {"issunny"},
   description = "If the weather is sunny in the target world."
)
public class SunnyCondition extends SkillCondition implements ILocationCondition {
   public SunnyCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractLocation target) {
      return !BukkitAdapter.adapt(target).getWorld().hasStorm();
   }
}
