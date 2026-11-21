package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;

@MythicCondition(
   author = "Ashijin",
   name = "isCreeperPrimed",
   aliases = {},
   description = "If the target creeper is primed to explode"
)
public class IsCreeperPrimedCondition extends SkillCondition implements IEntityCondition {
   public IsCreeperPrimedCondition(String line, MythicLineConfig config) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (!entity.isLiving()) {
         return false;
      } else {
         Entity e = entity.getBukkitEntity();
         if (!(e instanceof Creeper)) {
            return false;
         } else {
            return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(e)
               ? Schedulers.of(e).supply(() -> ((Creeper)e).isIgnited()).join()
               : ((Creeper)e).isIgnited();
         }
      }
   }
}
