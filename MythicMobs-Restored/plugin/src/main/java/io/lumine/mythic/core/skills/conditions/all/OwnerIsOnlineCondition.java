package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;

@MythicCondition(
   author = "Ashijin",
   name = "ownerIsOnline",
   aliases = {},
   description = "Checks if the owner of the target mob is online, if the owner is a player"
)
public class OwnerIsOnlineCondition extends SkillCondition implements IEntityCondition {
   public OwnerIsOnlineCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity target) {
      if (!(target.getBukkitEntity() instanceof Item item)) {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(target);
         if (am == null) {
            return false;
         } else {
            return am.getOwnerUUID().isPresent() ? Bukkit.getPlayer(am.getOwnerUUID().get()) != null : false;
         }
      } else {
         return item.getOwner() != null && Bukkit.getPlayer(item.getOwner()) != null;
      }
   }
}
