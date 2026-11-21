package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Player;

@MythicCondition(
   author = "Ashijin",
   name = "itemRecharging",
   aliases = {},
   description = "Checks if the target's weapon is recharging"
)
public class ItemRechargingCondition extends SkillCondition implements IEntityCondition {
   public ItemRechargingCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      return e.isPlayer() ? MythicBukkit.inst().getVolatileCodeHandler().getItemRecharge((Player)e.asPlayer().getBukkitEntity()) != 1.0F : false;
   }
}
