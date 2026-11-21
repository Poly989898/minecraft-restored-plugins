package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Item;

@MythicCondition(
   author = "Ashijin",
   name = "owner",
   aliases = {"isOwner"},
   description = "Checks if the target entity is the owner of the caster"
)
public class OwnerCondition extends SkillCondition implements IEntityComparisonCondition {
   public OwnerCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity, AbstractEntity target) {
      if (entity.getBukkitEntity() instanceof Item item) {
         return target.getUniqueId().equals(item.getOwner());
      } else {
         if (MythicBukkit.inst().getMobManager().isActiveMob(entity)) {
            ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
            if (am.getOwnerUUID().isPresent()) {
               return am.getOwnerUUID().get().equals(target.getUniqueId());
            }
         }

         return false;
      }
   }
}
