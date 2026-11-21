package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Entity;

@MythicCondition(
   author = "Phil",
   name = "isFrozen",
   aliases = {"freezing", "isfreezing"},
   description = "Tests if the target is frozen"
)
public class IsFrozenCondition extends SkillCondition implements IEntityCondition {
   public IsFrozenCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      Entity bukkitEntity = BukkitAdapter.adapt(e);
      return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(bukkitEntity)
         ? Schedulers.of(bukkitEntity).supply(() -> bukkitEntity.isFrozen()).join()
         : bukkitEntity.isFrozen();
   }
}
