package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "disguiseold",
   description = "Disguises the target entity"
)
public class DisguiseOldMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "disguise",
      description = "The disguise to set"
   )
   protected PlaceholderString disguise;

   public DisguiseOldMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.disguise = mlc.getPlaceholderString(new String[]{"type", "disguise", "d"}, "player:Xikage");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (CompatibilityManager.LibsDisguises != null) {
         CompatibilityManager.LibsDisguises.setDisguise(data.getCaster().getEntity(), this.config);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.MISSING_COMPATIBILITY;
      }
   }
}
