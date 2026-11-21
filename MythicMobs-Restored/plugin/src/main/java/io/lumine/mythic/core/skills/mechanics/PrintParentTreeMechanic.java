package io.lumine.mythic.core.skills.mechanics;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.MobType;
import io.lumine.mythic.core.skills.AbstractSkill;
import io.lumine.mythic.core.skills.MetaSkill;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "printParentTree",
   description = ""
)
public class PrintParentTreeMechanic extends SkillMechanic implements INoTargetSkill {
   public PrintParentTreeMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      this.printParent(this);
      return SkillResult.SUCCESS;
   }

   private void printParent(AbstractSkill skill) {
      Collection<SkillHolder> parents = Lists.newArrayList();
      if (skill instanceof MetaSkill metaSkill) {
         MythicLogger.log("[PT] | {0} (MetaSkill) {1}", metaSkill.getInternalName(), metaSkill.getPack().getName());
         parents.addAll(metaSkill.getParents());
      } else if (skill instanceof SkillMechanic skillMechanic) {
         String packName = skillMechanic.getPack() != null ? skillMechanic.getPack().getName() : "NO.PACK";
         MythicLogger.log("[PT] | {0} - {1} ** {2}", skillMechanic.getTypeName(), skillMechanic.getConfigLine(), packName);
         Optional<SkillHolder> maybeParent = skillMechanic.getParent();
         if (maybeParent.isEmpty()) {
            MythicLogger.log("[PT] | MECHANIC NO PARENT");
            return;
         }

         parents.add(maybeParent.get());
      }

      for (SkillHolder parent : parents) {
         if (parent instanceof AbstractSkill abstractSkill) {
            this.printParent(abstractSkill);
         } else if (parent instanceof MobType mobType) {
            MythicLogger.log("[PT] | Holder is Mob {0}", mobType.getInternalName());
         } else {
            MythicLogger.log("[PT] | Holder is OTHER {0} {1}", parent.getInternalName(), parent.getClass().getCanonicalName());
         }
      }
   }
}
