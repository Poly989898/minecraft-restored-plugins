package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Bogged;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Snowman;

@MythicCondition(
   name = "isSheared",
   aliases = {"sheared"},
   description = "Checks whether the target entity has been sheared"
)
public class IsShearedCondition extends SkillCondition implements IEntityCondition {
   public IsShearedCondition(String line, MythicLineConfig config) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      Entity bukkitEntity = entity.getBukkitEntity();
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(bukkitEntity)) {
         Boolean result = Schedulers.of(bukkitEntity).supply(() -> this.checkSheared(bukkitEntity)).join();
         return result != null ? result : false;
      } else {
         return this.checkSheared(bukkitEntity);
      }
   }

   private boolean checkSheared(Entity bukkitEntity) {
      if (bukkitEntity instanceof Sheep sheep) {
         return sheep.isSheared();
      } else if (bukkitEntity instanceof Bogged bogged) {
         return bogged.isSheared();
      } else {
         return bukkitEntity instanceof Snowman snowman ? snowman.isDerp() : false;
      }
   }
}
