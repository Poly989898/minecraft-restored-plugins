package io.lumine.mythic.core.volatilecode.v1_21_R4.ai.targeters;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R4.ai.PathfinderHolder;
import io.lumine.mythic.core.volatilecode.v1_21_R4.ai.pathfinding.AsyncNearestAttackableTargetGoal;
import java.util.Optional;
import java.util.Set;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.monster.EntityMonster;

@MythicAIGoal(
   name = "nearestSpecificFactionMonsters",
   aliases = {"specificFactionMonsters"},
   description = "Target a nearby monster that is in a specific faction"
)
public class SpecificFactionMonstersGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private Set<String> faction = Sets.newHashSet();

   public SpecificFactionMonstersGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
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
      return new AsyncNearestAttackableTargetGoal(PathfinderHolder.getNMSEntity(this.entity), EntityMonster.class, 0, true, false, () -> {
         ActiveMob mob;
         try {
            mob = getPlugin().getMobManager().getMythicMobInstance(this.getEntity());
         } catch (Exception var3) {
            var3.printStackTrace();
            return (targetEntity, serverLevel) -> false;
         }

         return (targetEntity, serverLevel) -> {
            try {
               AbstractEntity target = BukkitAdapter.adapt(targetEntity.getBukkitEntity());
               if (mob == null) {
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
            } catch (Exception var8) {
               var8.printStackTrace();
            }

            return false;
         };
      });
   }
}
