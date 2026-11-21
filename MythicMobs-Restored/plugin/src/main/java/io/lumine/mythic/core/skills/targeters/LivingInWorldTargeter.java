package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "LivingInWorld",
   aliases = {"EntitiesInWorld", "LivingEntitiesInWorld", "AllInWorld", "EIW"},
   description = "Targets all living entities in the caster's world"
)
public class LivingInWorldTargeter extends IEntitySelector {
   public LivingInWorldTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      return data.getCaster().getEntity().getWorld().getLivingEntities();
   }
}
