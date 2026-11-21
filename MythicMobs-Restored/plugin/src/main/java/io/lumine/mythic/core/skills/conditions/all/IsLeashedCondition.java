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
import org.bukkit.entity.LivingEntity;

@MythicCondition(
   author = "Phil",
   name = "IsLeashed",
   aliases = {},
   description = "If the target is leashed"
)
public class IsLeashedCondition extends SkillCondition implements IEntityCondition {
   public IsLeashedCondition(String line, MythicLineConfig config) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      if (!e.isLiving()) {
         return false;
      } else {
         LivingEntity le = (LivingEntity)BukkitAdapter.adapt(e);
         return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(le) ? Schedulers.of(le).supply(() -> le.isLeashed()).join() : le.isLeashed();
      }
   }
}
