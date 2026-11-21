package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Player;

@MythicCondition(
   author = "Ashijin",
   name = "blocking",
   aliases = {"isblocking"},
   version = "4.4",
   description = "Tests if the target entity is blocking with a shield"
)
public class BlockingCondition extends SkillCondition implements IEntityCondition {
   public BlockingCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      return !e.isPlayer() ? false : ((Player)e.getBukkitEntity()).isBlocking();
   }
}
