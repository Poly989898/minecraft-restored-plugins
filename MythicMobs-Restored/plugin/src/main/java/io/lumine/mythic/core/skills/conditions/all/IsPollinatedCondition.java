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

@MythicCondition(
   name = "pollinated",
   aliases = {"isPollinated", "hasNectar"},
   description = "Checks whether the target bee has nectar"
)
public class IsPollinatedCondition extends SkillCondition implements IEntityCondition {
   public IsPollinatedCondition(String line, MythicLineConfig config) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (entity.getBukkitEntity() instanceof Bee bee) {
         return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(bee) ? Schedulers.of(bee).supply(() -> bee.hasNectar()).join() : bee.hasNectar();
      } else {
         return false;
      }
   }
}
