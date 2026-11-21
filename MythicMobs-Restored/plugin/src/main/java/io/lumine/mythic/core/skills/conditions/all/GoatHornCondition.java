package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Goat;

@MythicCondition(
   name = "goatHorn",
   aliases = {"goathorn", "goatHasHorn", "hasGoatHorn"},
   description = "Checks whether a goat has at least one horn"
)
public class GoatHornCondition extends SkillCondition implements IEntityCondition {
   public GoatHornCondition(String line, MythicLineConfig config) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (entity.getBukkitEntity() instanceof Goat goat) {
         return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(goat)
            ? Schedulers.of(goat).supply(() -> goat.hasLeftHorn() || goat.hasRightHorn()).join()
            : goat.hasLeftHorn() || goat.hasRightHorn();
      } else {
         return false;
      }
   }
}
