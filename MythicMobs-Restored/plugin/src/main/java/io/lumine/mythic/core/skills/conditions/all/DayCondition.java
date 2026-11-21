package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "day",
   aliases = {},
   description = "If the time is day, from 0 to 11999 in-game time"
)
public class DayCondition extends SkillCondition implements ILocationCondition {
   public DayCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractLocation l) {
      long time = BukkitAdapter.adapt(l.getWorld()).getTime();
      return time >= 0L && time < 12000L;
   }
}
