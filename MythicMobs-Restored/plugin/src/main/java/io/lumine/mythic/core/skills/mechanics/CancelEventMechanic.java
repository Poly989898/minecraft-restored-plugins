package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.mobs.MobType;
import io.lumine.mythic.core.skills.AbstractSkill;
import io.lumine.mythic.core.skills.MetaSkill;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@MythicMechanic(
   author = "Ashijin",
   name = "cancelEvent",
   description = "Cancels the calling event"
)
public class CancelEventMechanic extends SkillMechanic implements INoTargetSkill {
   public CancelEventMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.EITHER;
      this.getPlugin().getClock().queueAfterLoad(() -> this.setParentSync(this));
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      data.cancelEvent();
      return SkillResult.SUCCESS;
   }

   private void setParentSync(AbstractSkill skill) {
      Set<AbstractSkill> visitedSkills = Sets.newConcurrentHashSet();
      this.setParentSyncInternal(skill, visitedSkills);
   }

   private void setParentSyncInternal(AbstractSkill skill, Collection<AbstractSkill> visitedSkills) {
      if (!visitedSkills.contains(skill)) {
         visitedSkills.add(skill);
         Collection<SkillHolder> parents = Lists.newArrayList();
         if (skill instanceof MetaSkill metaSkill) {
            parents.addAll(metaSkill.getParents());
         } else if (skill instanceof SkillMechanic skillMechanic) {
            Optional<SkillHolder> maybeParent = skillMechanic.getParent();
            if (maybeParent.isEmpty()) {
               return;
            }

            parents.add(maybeParent.get());
         }

         for (SkillHolder parent : parents) {
            if (parent instanceof AbstractSkill abstractSkill) {
               abstractSkill.setThreadSafetyLevel(ThreadSafetyLevel.SYNC_ONLY);
               this.setParentSyncInternal(abstractSkill, visitedSkills);
            } else if (parent instanceof MobType var7) {
               ;
            }
         }
      }
   }
}
