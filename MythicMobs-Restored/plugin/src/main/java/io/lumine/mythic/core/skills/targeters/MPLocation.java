package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;

public class MPLocation extends SkillTargeter implements IPathSelector {
   public MPLocation(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public SelectorType getType() {
      return SelectorType.LOCATION;
   }
}
