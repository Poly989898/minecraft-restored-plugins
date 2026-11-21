package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "isplayer",
   aliases = {},
   description = "If the target is a player"
)
public class IsPlayerCondition extends SkillCondition implements IEntityCondition {
   public IsPlayerCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      return entity.isPlayer() && getPlugin().getBootstrap().getOnlinePlayers().contains(entity.asPlayer());
   }
}
