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
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;

@MythicCondition(
   author = "Joshinn",
   name = "charged",
   aliases = {"isCharged", "creeperCharged"},
   description = "Whether or not the creeper is charged."
)
public class CreeperChargeCondition extends SkillCondition implements IEntityCondition {
   public CreeperChargeCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity e) {
      Entity entity = BukkitAdapter.adapt(e);
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity)) {
         Boolean result = Schedulers.of(entity).supply(() -> this.checkCharged(entity)).join();
         return result != null ? result : false;
      } else {
         return this.checkCharged(entity);
      }
   }

   private boolean checkCharged(Entity entity) {
      return !(entity instanceof Creeper) ? false : ((Creeper)entity).isPowered();
   }
}
