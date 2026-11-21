package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Item;

@MythicCondition(
   author = "Ashijin",
   name = "hasOwner",
   aliases = {},
   description = "Tests if the target mob has an owner"
)
public class HasOwnerCondition extends SkillCondition implements IEntityCondition {
   public HasOwnerCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity target) {
      if (target.getBukkitEntity() instanceof Item item) {
         return item.getOwner() != null;
      } else {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(target);
         return am == null ? false : am.getOwnerUUID().isPresent();
      }
   }
}
