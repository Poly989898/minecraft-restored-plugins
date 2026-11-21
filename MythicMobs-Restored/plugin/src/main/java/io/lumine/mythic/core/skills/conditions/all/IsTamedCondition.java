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
import org.bukkit.entity.Tameable;

@MythicCondition(
   name = "isTamed",
   description = "Checks whether the target is tamed"
)
public class IsTamedCondition extends SkillCondition implements IEntityCondition {
   public IsTamedCondition(String line, MythicLineConfig config) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      Entity bukkitEntity = e.getBukkitEntity();
      if (bukkitEntity instanceof Tameable tameable) {
         return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(bukkitEntity)
            ? Schedulers.of(bukkitEntity).supply(() -> tameable.isTamed()).join()
            : tameable.isTamed();
      } else {
         return false;
      }
   }
}
