package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Raider;

@MythicCondition(
   author = "Ashijin",
   name = "isRaiderPatrolLeader",
   aliases = {"isPatrolLeader"},
   description = "Checks if the target entity is the captain of a pillager group"
)
public class IsRaiderPatrolLeaderCondition extends SkillCondition implements IEntityCondition {
   public IsRaiderPatrolLeaderCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity target) {
      Entity bukkitEntity = target.getBukkitEntity();
      if (bukkitEntity instanceof Raider raider) {
         return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(bukkitEntity)
            ? Schedulers.of(bukkitEntity).supply(() -> raider.isPatrolLeader()).join()
            : raider.isPatrolLeader();
      } else {
         return false;
      }
   }
}
