package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   name = "canBeHitByProjectile",
   aliases = {"hitByProjectile"},
   description = "Whether the target can be hit by a projectile"
)
public class CanBeHitByProjectileCondition extends SkillCondition implements IEntityCondition {
   public CanBeHitByProjectileCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      return getPlugin().getVolatileCodeHandler().getEntityHandler().canBeHitByProjectile(e);
   }
}
