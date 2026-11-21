package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@MythicCondition(
   author = "Ashijin",
   name = "isInSurvivalMode",
   aliases = {"inSurvivalMode"},
   description = "If the target is in survival mode"
)
public class InSurvivalModeCondition extends SkillCondition implements IEntityCondition {
   public InSurvivalModeCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      return ((Player)entity.getBukkitEntity()).getGameMode() == GameMode.SURVIVAL;
   }
}
