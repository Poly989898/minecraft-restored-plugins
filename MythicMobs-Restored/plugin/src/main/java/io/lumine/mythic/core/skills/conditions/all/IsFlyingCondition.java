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
   name = "isflying",
   aliases = {"flying"},
   description = "If the target is flying"
)
public class IsFlyingCondition extends SkillCondition implements IEntityCondition {
   public IsFlyingCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      if (e.getBukkitEntity() instanceof Player player) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Player is flying? {0}", player.isFlying());
         return player.isFlying();
      } else {
         return e.isFlyingMob();
      }
   }
}
