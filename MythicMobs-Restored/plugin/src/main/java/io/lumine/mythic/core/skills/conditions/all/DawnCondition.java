package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "dawn",
   aliases = {},
   description = "If the time is dawn, from 23000 to 23999 in-game time"
)
public class DawnCondition extends SkillCondition implements ILocationCondition {
   public DawnCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractLocation l) {
      long time = BukkitAdapter.adapt(l.getWorld()).getTime();
      return time >= 23000L && time < 24000L;
   }
}
