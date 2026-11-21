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
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EnderDragon.Phase;

@MythicCondition(
   author = "Ashijin",
   name = "enderdragonPhase",
   aliases = {"edragonPhase"},
   description = "Tests the phase of the target EnderDragon"
)
public class EnderDragonPhaseCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "type",
      aliases = {"types", "t"},
      description = "A list of phases to match"
   )
   protected EnumSet<Phase> phases;

   public EnderDragonPhaseCondition(String line, MythicLineConfig mlc) {
      super(line);
      String phaseStringList = mlc.getString(new String[]{"phase", "p"}, null);
      Collection<Phase> collection = new ArrayList<>();
      if (phaseStringList != null) {
         for (String entityTypeString : phaseStringList.split(",")) {
            try {
               collection.add(Phase.valueOf(entityTypeString.toUpperCase()));
            } catch (Exception e) {
               MythicLogger.errorConditionConfig(this, mlc, "Couldn't parse \"" + entityTypeString + "\" as an EnderDragon Phase!");
            }
         }
      }

      this.phases = !collection.isEmpty() ? EnumSet.copyOf(collection) : EnumSet.noneOf(Phase.class);
   }

   @Override
   public boolean check(AbstractEntity targetEntity) {
      Entity entity = targetEntity.getBukkitEntity();
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity)) {
         Boolean result = Schedulers.of(entity).supply(() -> this.checkPhase(entity)).join();
         return result != null ? result : false;
      } else {
         return this.checkPhase(entity);
      }
   }

   private boolean checkPhase(Entity entity) {
      return entity instanceof EnderDragon dragon ? this.phases.contains(dragon.getPhase()) : false;
   }
}
