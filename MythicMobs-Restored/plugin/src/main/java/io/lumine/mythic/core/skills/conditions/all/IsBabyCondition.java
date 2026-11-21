package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

@MythicCondition(
   author = "Pine",
   name = "isBaby",
   aliases = {},
   description = "If the target is a baby"
)
public class IsBabyCondition extends SkillCondition implements IEntityCondition {
   public IsBabyCondition(String line, MythicLineConfig config) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (!entity.isLiving()) {
         return false;
      } else {
         Entity e = entity.getBukkitEntity();
         if (!(e instanceof Ageable age)) {
            return false;
         } else {
            return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(e) ? Schedulers.of(e).supply(() -> !age.isAdult()).join() : !age.isAdult();
         }
      }
   }
}
