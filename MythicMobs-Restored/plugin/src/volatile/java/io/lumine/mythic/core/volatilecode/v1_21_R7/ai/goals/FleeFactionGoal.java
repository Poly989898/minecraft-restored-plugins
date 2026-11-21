package io.lumine.mythic.core.volatilecode.v1_21_R7.ai.goals;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R7.ai.PathfinderHolder;
import io.lumine.mythic.core.volatilecode.v1_21_R7.ai.pathfinding.AsyncAvoidEntityGoal;
import java.util.Optional;
import java.util.Set;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;

@MythicAIGoal(
   name = "fleeFaction",
   aliases = {"runFromFaction"},
   description = "Runs away from nearby entities that is in a specific faction"
)
public class FleeFactionGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   protected float distance;
   protected double speed;
   protected double safeSpeed;
   private Set<String> faction = Sets.newHashSet();

   public FleeFactionGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.distance = mlc.getFloat(new String[]{"distance", "d"}, 16.0F);
      this.speed = mlc.getDouble(new String[]{"speed", "s"}, 1.2F);
      this.safeSpeed = mlc.getDouble(new String[]{"safespeed", "ss"}, 1.0);
      String factions = mlc.getString(new String[]{"faction", "f"}, this.dataVar1);
      if (factions != null) {
         String[] split = factions.split(",");

         for (String s : split) {
            this.faction.add(s.toUpperCase());
         }
      }
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public PathfinderGoal create() {
      return new AsyncAvoidEntityGoal(PathfinderHolder.getNMSEntity(this.entity), this.distance, this.safeSpeed, this.speed, () -> {
         ActiveMob am;
         try {
            am = getPlugin().getMobManager().getMythicMobInstance(this.getEntity());
         } catch (Exception var3) {
            var3.printStackTrace();
            return targetEntity -> false;
         }

         return targetEntity -> {
            try {
               AbstractEntity target = BukkitAdapter.adapt(targetEntity.getBukkitEntity());
               if (am == null) {
                  return false;
               }

               if (target.isPlayer()) {
                  for (String faction : this.faction) {
                     if (getPlugin().getPlayerManager().getFactionProvider().isInFaction(target.asPlayer(), faction)) {
                        return true;
                     }
                  }
               } else {
                  Optional<ActiveMob> maybeTargetAM = getPlugin().getMobManager().getActiveMob(target.getUniqueId());
                  if (!maybeTargetAM.isPresent()) {
                     return false;
                  }

                  ActiveMob targetAM = maybeTargetAM.get();
                  if (targetAM.hasFaction()) {
                     String factionx = targetAM.getFaction().toUpperCase();
                     return this.faction.contains(factionx);
                  }
               }
            } catch (Exception var7) {
               var7.printStackTrace();
            }

            return false;
         };
      });
   }
}
