package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.serialize.Optl;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "isParentAlive",
   aliases = {"parentAlive"},
   version = "5.6",
   description = "Tests if the target mob's parent is alive"
)
public class IsParentAliveCondition extends SkillCondition implements IEntityCondition {
   public IsParentAliveCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity target) {
      ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(target);
      if (am != null && am.getParentUUID().isPresent()) {
         Optl<AbstractEntity> maybeParent = am.getParent();
         if (!maybeParent.isPresent()) {
            return false;
         } else {
            AbstractEntity parent = maybeParent.get();
            return !parent.isDead() && parent.isValid();
         }
      } else {
         return false;
      }
   }
}
