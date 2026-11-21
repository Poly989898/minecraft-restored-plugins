package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.volatilecode.handlers.VolatileEntityHandler;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Frog;

@MythicMechanic(
   author = "Ashijin",
   name = "settonguetarget",
   aliases = {"tonguetarget"},
   description = "Sets the casting frog's tongue to target the given entity"
)
public class SetTongueTargetMechanic extends SkillMechanic implements ITargetedEntitySkill {
   public SetTongueTargetMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!(data.getCaster().getEntity().getBukkitEntity() instanceof Frog)) {
         return SkillResult.INVALID_CONFIG;
      } else {
         VolatileEntityHandler handler = this.getPlugin().getVolatileCodeHandler().getEntityHandler();
         return handler != null && handler.shootFrogTongue(data.getCaster().getEntity(), target) ? SkillResult.SUCCESS : SkillResult.ERROR;
      }
   }
}
