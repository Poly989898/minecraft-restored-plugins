package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.HashSet;
import java.util.Set;

@MythicCondition(
   author = "Ashijin",
   name = "lunarPhase",
   aliases = {},
   description = "Checks the target world's lunar phase"
)
public class LunarPhaseCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "phases",
      aliases = {"phase", "p"},
      description = "A list of lunar phases"
   )
   private final Set<Integer> phases = new HashSet<>();

   public LunarPhaseCondition(String line, MythicLineConfig mlc) {
      super(line);
      String phase = mlc.getString(new String[]{"phases", "phase", "p"}, "0", this.conditionVar);
      String[] split = phase.split(",");

      for (String s : split) {
         this.phases.add(Integer.valueOf(s));
      }
   }

   @Override
   public boolean check(AbstractLocation l) {
      int phase = (int)(l.getWorld().getFullTime() / 24000L % 8L);
      return this.phases.contains(phase);
   }
}
