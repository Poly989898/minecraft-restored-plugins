package io.lumine.mythic.core.volatilecode.v26_1.ai.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_1.ai.PathfinderHolder;
import io.lumine.mythic.core.volatilecode.v26_1.ai.pathfinding.AsyncNearestAttackableTargetGoal;
import java.util.Optional;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;

@MythicAIGoal(
   name = "nearestOtherFactionMonsters",
   aliases = {"otherFactionMonsters"},
   description = "Target a nearby monster that is in a different faction"
)
public class OtherFactionMonstersGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public OtherFactionMonstersGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public Goal create() {
      return new AsyncNearestAttackableTargetGoal(PathfinderHolder.getNMSEntity(this.entity), Monster.class, 0, true, false, () -> {
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
               if (mob == null || !mob.hasFaction()) {
                  return true;
               } else if (target.isPlayer()) {
                  return !getPlugin().getPlayerManager().getFactionProvider().isInFaction(target.asPlayer(), mob.getFaction());
               } else {
                  Optional<ActiveMob> maybeTargetAM = getPlugin().getMobManager().getActiveMob(target.getUniqueId());
                  if (!maybeTargetAM.isPresent()) {
                     return true;
                  } else {
                     ActiveMob targetAM = maybeTargetAM.get();
                     return targetAM.hasFaction() ? !mob.getFaction().equals(targetAM.getFaction()) : true;
                  }
               }
            } catch (Exception var6) {
               var6.printStackTrace();
               return false;
            }
         };
      });
   }
}
