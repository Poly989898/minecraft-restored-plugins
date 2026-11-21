package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setParent",
   description = "Sets the caster's parent to the target entity"
)
public class SetParentMechanic extends SkillMechanic implements ITargetedEntitySkill {
   public SetParentMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!(data.getCaster() instanceof ActiveMob am)) {
         return SkillResult.INVALID_TARGET;
      } else if (target.isPlayer()) {
         try {
            PlayerData playerData = this.getPlugin().getPlayerManager().getProfile(target.asPlayer());
            am.setParent(playerData);
            return SkillResult.SUCCESS;
         } catch (Error | Exception e) {
            return SkillResult.INVALID_TARGET;
         }
      } else {
         ActiveMob mob = this.getPlugin().getMobManager().getMythicMobInstance(target);
         if (mob != null) {
            am.setParent(mob);
            return SkillResult.SUCCESS;
         } else {
            return SkillResult.INVALID_TARGET;
         }
      }
   }
}
