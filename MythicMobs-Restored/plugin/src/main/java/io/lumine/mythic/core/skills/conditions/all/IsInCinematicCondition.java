package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.cinematic.CinematicManager;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Player;

@MythicCondition(
   author = "Taiyou06",
   name = "isInCinematic",
   aliases = {"incinematic", "incine", "cinematicactive"},
   description = "Checks if the target player is currently in an active cinematic camera path."
)
public class IsInCinematicCondition extends SkillCondition implements IEntityCondition {
   public IsInCinematicCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity target) {
      if (target != null && target.getBukkitEntity() != null) {
         if (target.getBukkitEntity() instanceof Player player) {
            CinematicManager mgr = MythicBukkit.inst().getCinematicManager();
            return mgr == null ? false : mgr.isInCinematic(player.getUniqueId());
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
