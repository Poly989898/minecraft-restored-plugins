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

@MythicCondition(
   author = "evlad",
   name = "isInvulnerable",
   aliases = {"isInvincible"},
   description = "If the target is invulnerable"
)
public class IsInvulnerableCondition extends SkillCondition implements IEntityCondition {
   public IsInvulnerableCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      Entity bukkitEntity = entity.getBukkitEntity();
      return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(bukkitEntity)
         ? Schedulers.of(bukkitEntity).supply(() -> bukkitEntity.isInvulnerable()).join()
         : bukkitEntity.isInvulnerable();
   }
}
