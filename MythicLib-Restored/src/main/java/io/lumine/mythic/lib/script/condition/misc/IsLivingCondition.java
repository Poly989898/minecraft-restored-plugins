package io.lumine.mythic.lib.script.condition.misc;

import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.entity.LivingEntity;

public class IsLivingCondition extends Condition {
   private final boolean caster;

   public IsLivingCondition(ConfigObject config) {
      super(config);
      this.caster = config.getBoolean("caster", false);
   }

   @Override
   public boolean isMet(SkillMetadata meta) {
      return meta.getSkillEntity(this.caster) instanceof LivingEntity;
   }
}
