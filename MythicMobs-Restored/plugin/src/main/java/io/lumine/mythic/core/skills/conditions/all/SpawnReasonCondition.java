package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

@MythicCondition(
   author = "Ashijin",
   name = "spawnReason",
   description = "Checks the target's spawn reason."
)
public class SpawnReasonCondition extends SkillCondition implements IEntityCondition {
   private SpawnReason reason;

   public SpawnReasonCondition(String line, MythicLineConfig mlc) {
      super(line);
      String strReason = mlc.getString(new String[]{"reason", "r"}, this.conditionVar);

      try {
         this.reason = SpawnReason.valueOf(strReason);
      } catch (Throwable e) {
         MythicLogger.errorConditionConfig(this, mlc, "Invalid SpawnReason supplied for condition");
         this.reason = null;
      }
   }

   @Override
   public boolean check(AbstractEntity target) {
      if (target.isPlayer()) {
         return false;
      } else {
         Entity bukkitEntity = target.getBukkitEntity();
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(bukkitEntity)) {
            Boolean result = Schedulers.of(bukkitEntity).supply(() -> this.checkSpawnReason(bukkitEntity)).join();
            return result != null ? result : false;
         } else {
            return this.checkSpawnReason(bukkitEntity);
         }
      }
   }

   private boolean checkSpawnReason(Entity bukkitEntity) {
      SpawnReason spawnReason = bukkitEntity.getEntitySpawnReason();
      return spawnReason == null ? false : spawnReason == this.reason;
   }
}
