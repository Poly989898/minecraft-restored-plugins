package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Entity;

@MythicCondition(
   author = "Joshinn",
   name = "burning",
   aliases = {"isburning", "isonfire"},
   description = "Whether or not the target entity is on fire."
)
public class BurningCondition extends SkillCondition implements IEntityCondition {
   public BurningCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      Entity bukkitEntity = e.getBukkitEntity();
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(bukkitEntity)) {
         Boolean result = Schedulers.of(bukkitEntity).supply(() -> this.checkBurning(bukkitEntity)).join();
         return result != null ? result : false;
      } else {
         return this.checkBurning(bukkitEntity);
      }
   }

   private boolean checkBurning(Entity bukkitEntity) {
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Entity's current fire tick is {0}", bukkitEntity.getFireTicks());
      return bukkitEntity.getFireTicks() > 0;
   }
}
