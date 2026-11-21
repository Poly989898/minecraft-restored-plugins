package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Player;

@MythicCondition(
   author = "Ashijin",
   name = "hasFreeInventorySlot",
   aliases = {},
   description = "If the target has a free inventory slot"
)
public class HasInventorySpaceCondition extends SkillCondition implements IEntityCondition {
   public HasInventorySpaceCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity target) {
      return !target.isPlayer() ? false : ((Player)target.getBukkitEntity()).getInventory().firstEmpty() >= 0;
   }
}
