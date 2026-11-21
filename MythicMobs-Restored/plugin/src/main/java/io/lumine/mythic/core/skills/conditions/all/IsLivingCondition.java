package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.EntityType;

@MythicCondition(
   author = "Ashijin",
   name = "isliving",
   aliases = {},
   description = "If the target is living"
)
public class IsLivingCondition extends SkillCondition implements IEntityCondition {
   public IsLivingCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      return entity.isLiving() && entity.getBukkitEntity().getType() != EntityType.ARMOR_STAND;
   }
}
