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
   name = "disguisemodifynew",
   aliases = {"newmodifydisguise"},
   version = "4.8",
   description = "Disguises the target entity"
)
public class DisguiseModifyNewMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "config",
      aliases = {"c"},
      description = "The disguise to set"
   )
   private final PlaceholderString config;

   public DisguiseModifyNewMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.config = mlc.getPlaceholderString(new String[]{"config", "c"}, "");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (CompatibilityManager.LibsDisguises != null) {
         String config = this.config.get(data);
         CompatibilityManager.LibsDisguises.modifyDisguise(data.getCaster().getEntity(), config);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.MISSING_COMPATIBILITY;
      }
   }
}
