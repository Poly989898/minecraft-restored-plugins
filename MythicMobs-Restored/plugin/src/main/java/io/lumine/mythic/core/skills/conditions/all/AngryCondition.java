package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Wolf;

@MythicCondition(
   name = "angry",
   aliases = {"isAngry"},
   description = "Checks whether the target entity is angry"
)
public class AngryCondition extends SkillCondition implements IEntityCondition {
   public AngryCondition(String line, MythicLineConfig config) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      Entity bukkitEntity = entity.getBukkitEntity();
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(bukkitEntity)) {
         Boolean result = Schedulers.of(bukkitEntity).supply(() -> this.checkAngry(bukkitEntity)).join();
         return result != null ? result : false;
      } else {
         return this.checkAngry(bukkitEntity);
      }
   }

   private boolean checkAngry(Entity bukkitEntity) {
      if (bukkitEntity instanceof Bee bee) {
         return bee.getAnger() > 0;
      } else if (bukkitEntity instanceof Wolf wolf) {
         return wolf.isAngry();
      } else {
         return bukkitEntity instanceof Vex vex ? vex.isCharging() : false;
      }
   }
}
