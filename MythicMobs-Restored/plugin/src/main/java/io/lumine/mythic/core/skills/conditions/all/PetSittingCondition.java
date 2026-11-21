package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Sittable;

@MythicCondition(
   author = "Mythic",
   name = "petsitting",
   aliases = {"petsit"},
   description = "Checks if a sittable entity (wolf, cat, parrot, fox, camel, panda) is currently sitting"
)
public class PetSittingCondition extends SkillCondition implements IEntityCondition {
   public PetSittingCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity target) {
      Object entity = target.getBukkitEntity();
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(target.getBukkitEntity())) {
         Boolean result = Schedulers.of(target.getBukkitEntity()).supply(() -> this.checkSitting(entity)).join();
         return result != null ? result : false;
      } else {
         return this.checkSitting(entity);
      }
   }

   private boolean checkSitting(Object entity) {
      return entity instanceof Sittable sittable ? sittable.isSitting() : false;
   }
}
