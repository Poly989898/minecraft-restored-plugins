package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "haspassenger",
   aliases = {},
   description = "If the target entity has a passenger"
)
public class HasPassengerCondition extends SkillCondition implements IEntityCondition {
   public HasPassengerCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(e.getBukkitEntity())
         ? Schedulers.of(e.getBukkitEntity()).supply(() -> !e.getBukkitEntity().getPassengers().isEmpty()).join()
         : !e.getBukkitEntity().getPassengers().isEmpty();
   }
}
