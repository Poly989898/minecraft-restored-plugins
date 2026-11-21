package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Player;

@MythicCondition(
   author = "Joshinn",
   name = "sprinting",
   aliases = {"issprinting"},
   description = "Whether or not the target entity is sprinting. Only works on players."
)
public class SprintingCondition extends SkillCondition implements IEntityCondition {
   public SprintingCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      if (e.isPlayer()) {
         Player p = (Player)e.getBukkitEntity();
         return p.isSprinting();
      } else {
         return false;
      }
   }
}
