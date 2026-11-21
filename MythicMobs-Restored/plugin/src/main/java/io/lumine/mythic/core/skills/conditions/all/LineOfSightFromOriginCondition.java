package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "lineOfSightFromOrigin",
   aliases = {"inlineofsightfromorigin"},
   description = "Tests if the target is within line of sight of the origin"
)
public class LineOfSightFromOriginCondition extends SkillCondition implements ISkillMetaComparisonCondition {
   public LineOfSightFromOriginCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      return target.hasLineOfSight(meta.getOrigin());
   }
}
