package io.lumine.mythic.lib.script.condition.misc;

import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;

public class OnFireCondition extends Condition {
   private final boolean caster;

   public OnFireCondition(ConfigObject config) {
      super(config);
      this.caster = config.getBoolean("caster", false);
   }

   @Override
   public boolean isMet(SkillMetadata meta) {
      return meta.getSkillEntity(this.caster).getFireTicks() > 0;
   }
}
