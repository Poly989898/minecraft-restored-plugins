package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;

public class MPMeta extends SkillTargeter implements IPathSelector {
   public MPMeta(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public SelectorType getType() {
      return SelectorType.META;
   }
}
