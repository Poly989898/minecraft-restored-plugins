package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "dusk",
   aliases = {},
   description = "If the time is dusk, from 12000 to 12999 in-game time."
)
public class DuskCondition extends SkillCondition implements ILocationCondition {
   public DuskCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractLocation l) {
      long time = BukkitAdapter.adapt(l.getWorld()).getTime();
      return time >= 12000L && time < 13000L;
   }
}
