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
   name = "lightLevelfromblocks",
   aliases = {"blocklightlevel"},
   description = "Tests the light level at the target location"
)
public class LightLevelBlockCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "level",
      aliases = {"l"},
      description = "The level range to match"
   )
   private final RangedInt level;

   public LightLevelBlockCondition(String line, MythicLineConfig mlc) {
      super(line);
      String k = mlc.getString(new String[]{"level", "l"}, "0", this.conditionVar);
      this.level = new RangedInt(k);
   }

   @Override
   public boolean check(AbstractLocation l) {
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Checking LightLevel: {0} vs {1}", l.getWorld().getLightLevelFromBlocks(l), this.level.toString());
      int level = l.getWorld().getLightLevelFromBlocks(l);
      return this.level.equals(level);
   }
}
