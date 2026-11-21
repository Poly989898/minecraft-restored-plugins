package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedInt;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "lightLevel",
   aliases = {},
   description = "Tests the light level at the target location"
)
public class LightLevelCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "level",
      aliases = {"l"},
      description = "The level range to match"
   )
   private RangedInt level;

   public LightLevelCondition(String line, MythicLineConfig mlc) {
      super(line);
      String k = mlc.getString(new String[]{"level", "l"}, "0", this.conditionVar);

      try {
         this.level = new RangedInt(k);
      } catch (Exception e) {
         MythicLogger.errorConditionConfig(this, mlc, "Invalid syntax for level");
      }
   }

   @Override
   public boolean check(AbstractLocation l) {
      int lightLevel = l.getWorld().getLightLevel(l);
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking LightLevel: {0} vs {1}", lightLevel, this.level.toString());
      return this.level.equals(lightLevel);
   }
}
