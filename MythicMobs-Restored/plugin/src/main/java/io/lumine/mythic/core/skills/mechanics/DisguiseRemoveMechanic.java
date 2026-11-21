package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "undisguise",
   aliases = {"disguiseRemove"},
   description = "Removes a disguise from the target entity"
)
public class DisguiseRemoveMechanic extends SkillMechanic implements INoTargetSkill {
   public DisguiseRemoveMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (CompatibilityManager.LibsDisguises != null) {
         CompatibilityManager.LibsDisguises.removeDisguise(data.getCaster().getEntity());
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.MISSING_COMPATIBILITY;
      }
   }
}
