package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "barRemove",
   version = "4.8",
   description = "Removes a custom bossbar display"
)
public class BarRemoveMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "name",
      aliases = {"n"},
      description = "The name of the bossbar to remove",
      defValue = "infobar"
   )
   protected String barName;

   public BarRemoveMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.barName = mlc.getString(new String[]{"name", "n"}, "infobar");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (data.getCaster() instanceof ActiveMob am) {
         am.removeBar(this.barName);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
