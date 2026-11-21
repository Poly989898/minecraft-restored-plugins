package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "sudoskill",
   aliases = {"sudo"},
   description = "Forces the inherited target to execute the skill at the targeted entity"
)
public class SudoSkillMechanic extends MetaSkillMechanic {
   private boolean casterAsTrigger = false;
   private boolean chained = false;
   private Optional<SkillTargeter> targetOverride = Optional.empty();

   public SudoSkillMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.casterAsTrigger = mlc.getBoolean(new String[]{"setcasterastrigger", "cat"}, false);
      this.chained = mlc.getBoolean(new String[]{"chained", "chain"}, false);
      String strTarget = mlc.getString(new String[]{"target", "t"}, null);
      if (strTarget != null) {
         this.targetOverride = Optional.of(this.parseSkillTargeter(strTarget));
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      Collection<AbstractEntity> targets = data.getEntityTargets();
      if (targets != null && !targets.isEmpty()) {
         AbstractEntity chainTarget = data.getCaster().getEntity();

         for (AbstractEntity target : targets) {
            SkillCaster caster = this.getPlugin().getSkillManager().getCaster(target);
            SkillMetadata newData = data.deepClone();
            if (this.targetOverride.isPresent()) {
               this.evaluateTargets(newData, this.targetOverride.get());
            } else if (this.chained) {
               newData.setEntityTarget(chainTarget);
               chainTarget = target;
            }

            newData.setCaster(caster);
            if (this.casterAsTrigger) {
               newData.setTrigger(data.getCaster().getEntity());
            }

            if (this.metaskill.isPresent()) {
               Skill ms = this.metaskill.get();
               if (!ms.isUsable(newData)) {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Skill is not usable at this time! Cancelling.");
                  return SkillResult.CONDITION_FAILED;
               }

               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Executing SudoSkill " + this.skillName);
               if (this.forceSync) {
                  newData.setIsAsync(false);
                  Schedulers.of(data.getCaster().getEntity().getBukkitEntity()).run(bindVariablesForLaterRun(newData, () -> {
                     newData.setIsAsync(false);
                     ms.execute(newData);
                  }));
               } else {
                  ms.execute(newData);
               }
            }
         }

         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
