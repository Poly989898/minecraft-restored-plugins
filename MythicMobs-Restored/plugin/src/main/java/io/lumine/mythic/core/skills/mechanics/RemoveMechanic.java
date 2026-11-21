package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "remove",
   aliases = {"delete"},
   description = "Removes the target entity from existence"
)
public class RemoveMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "onRemove",
      aliases = {"then"},
      version = "5.8",
      description = "Skill to execute when the destination is reached"
   )
   protected Optional<Skill> onCompleteSkill = Optional.empty();

   public RemoveMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String onCompleteSkillName = mlc.getString(new String[]{"onremoveskill", "onremove", "then"});
      this.getManager().queueSecondPass(() -> {
         if (onCompleteSkillName != null) {
            this.onCompleteSkill = this.getManager().getSkill(file, this, onCompleteSkillName);
         }
      });
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         if (this.onCompleteSkill.isPresent()) {
            SkillMetadata summonData = data.deepClone();
            data.getEntityTargets().clear();
            summonData.setTrigger(target);
            summonData.setEntityTarget(target);
            this.onCompleteSkill.get().execute(summonData);
         }

         ActiveMob am = this.getPlugin().getMobManager().getMythicMobInstance(target);
         if (am != null) {
            am.setDespawned();
         }

         target.remove();
         return SkillResult.SUCCESS;
      }
   }
}
