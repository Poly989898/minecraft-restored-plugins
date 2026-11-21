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
   name = "disguisemodify",
   aliases = {"modifydisguise"},
   version = "4.8",
   description = "Disguises the target entity"
)
public class DisguiseModifyMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "disguise",
      description = "The disguise to set"
   )
   protected PlaceholderString disguise;

   public DisguiseModifyMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.disguise = mlc.getPlaceholderString(new String[]{"type", "disguise", "d"}, "player Ashijin");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (CompatibilityManager.LibsDisguises != null) {
         CompatibilityManager.LibsDisguises.modifyDisguise(data.getCaster().getEntity(), this.disguise.get(data));
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.MISSING_COMPATIBILITY;
      }
   }
}
