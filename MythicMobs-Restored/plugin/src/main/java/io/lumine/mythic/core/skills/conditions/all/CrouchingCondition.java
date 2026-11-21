package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Player;

@MythicCondition(
   author = "Ashijin",
   name = "crouching",
   aliases = {"sneaking", "iscrouching", "issneaking"},
   description = "Whether or not the target entity is crouching"
)
public class CrouchingCondition extends SkillCondition implements IEntityCondition {
   public CrouchingCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      if (e.getBukkitEntity() instanceof Player player) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Player {0} sneaking {1}", e.getName(), player.isSneaking());
         return player.isSneaking();
      } else {
         return false;
      }
   }
}
