package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ticxo",
   name = "isUsingSpyglass",
   aliases = {"isScoping"},
   description = "If the target is using a spyglass"
)
public class IsUsingSpyglassCondition extends SkillCondition implements IEntityCondition {
   public IsUsingSpyglassCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      return entity.isPlayer() && getPlugin().getVolatileCodeHandler().getEntityHandler().isUsingSpyglass(entity.asPlayer());
   }
}
