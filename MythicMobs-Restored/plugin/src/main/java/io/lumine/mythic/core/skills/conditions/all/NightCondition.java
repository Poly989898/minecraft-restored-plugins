package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "night",
   aliases = {},
   description = "If the time is night, from 13000 to 22999 in-game time"
)
public class NightCondition extends SkillCondition implements ILocationCondition {
   public NightCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractLocation l) {
      long time = BukkitAdapter.adapt(l.getWorld()).getTime();
      return time >= 13000L && time < 23000L;
   }
}
