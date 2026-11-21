package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.cinematic.CinematicManager;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Taiyou06",
   name = "cinematicCancel",
   aliases = {"cinecancel", "stopcinematic"},
   description = "Cancels an active cinematic camera for the target player."
)
public class CinematicCancelMechanic extends SkillMechanic implements ITargetedEntitySkill {
   public CinematicCancelMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target != null && target.getBukkitEntity() != null) {
         if (target.getBukkitEntity() instanceof Player player) {
            CinematicManager mgr = MythicBukkit.inst().getCinematicManager();
            if (mgr == null) {
               return SkillResult.ERROR;
            } else {
               mgr.cancel(player.getUniqueId());
               return SkillResult.SUCCESS;
            }
         } else {
            return SkillResult.INVALID_TARGET;
         }
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
