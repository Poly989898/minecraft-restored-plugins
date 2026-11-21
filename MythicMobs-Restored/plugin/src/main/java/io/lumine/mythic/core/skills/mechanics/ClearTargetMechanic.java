package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ticxo",
   name = "clearTarget",
   aliases = {"resetTarget"},
   description = "Force the target to reset its current target."
)
public class ClearTargetMechanic extends SkillMechanic implements ITargetedEntitySkill {
   public ClearTargetMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.getPlugin().getMobManager().getActiveMob(target.getUniqueId()).ifPresent(ActiveMob::resetTarget);
      return SkillResult.SUCCESS;
   }
}
