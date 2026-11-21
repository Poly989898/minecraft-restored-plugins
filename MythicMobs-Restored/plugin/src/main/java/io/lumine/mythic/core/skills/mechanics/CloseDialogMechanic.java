package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Player;

@MythicMechanic(
   name = "closeDialog",
   aliases = {"hideDialog"},
   premium = true,
   description = "Closes any open dialog for the target player"
)
public class CloseDialogMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
   public CloseDialogMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.target_creative = true;
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      return this.closeDialogFor(data.getCaster().getEntity());
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.closeDialogFor(target);
   }

   private SkillResult closeDialogFor(AbstractEntity target) {
      if (!ServerVersion.isPaper()) {
         return SkillResult.INVALID_VERSION;
      } else if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         DialogTracker.close((Player)target.getBukkitEntity());
         return SkillResult.SUCCESS;
      }
   }

   @Override
   public boolean getTargetsSpectators() {
      return true;
   }

   @Override
   public boolean getTargetsCreatives() {
      return true;
   }
}
