package io.lumine.mythic.core.volatilecode.v26_2.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_2.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.entity.Zombie;

@MythicAIGoal(
   name = "zombieAttack",
   aliases = {},
   description = "Zombie melee attack"
)
public class ZombieAttackGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public ZombieAttackGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.getBukkitEntity() instanceof Zombie;
   }

   @Override
   public Goal create() {
      return new ZombieAttackGoal.ZombieMeleeAttackGoal((net.minecraft.world.entity.monster.zombie.Zombie)PathfinderHolder.getNMSEntity(this.entity));
   }

   protected static class ZombieMeleeAttackGoal extends net.minecraft.world.entity.ai.goal.ZombieAttackGoal {
      public ZombieMeleeAttackGoal(net.minecraft.world.entity.monster.zombie.Zombie entity) {
         super(entity, 1.0, false);
      }
   }
}
