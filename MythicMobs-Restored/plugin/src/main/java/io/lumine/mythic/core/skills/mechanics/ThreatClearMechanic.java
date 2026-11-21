package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "threatclear",
   aliases = {"clearthreat"},
   description = "Clears the caster's threat table"
)
public class ThreatClearMechanic extends SkillMechanic implements INoTargetSkill {
   public ThreatClearMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (!(data.getCaster() instanceof ActiveMob)) {
         return SkillResult.INVALID_TARGET;
      } else {
         ActiveMob am = (ActiveMob)data.getCaster();
         if (am.getThreatTable() == null) {
            return SkillResult.INVALID_CONFIG;
         } else {
            am.getThreatTable().dropCombat();
            return SkillResult.SUCCESS;
         }
      }
   }
}
