package io.lumine.mythic.core.volatilecode.v1_21_R4.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R4.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalZombieAttack;
import net.minecraft.world.entity.monster.EntityZombie;
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
   public PathfinderGoal create() {
      return new ZombieAttackGoal.ZombieMeleeAttackGoal((EntityZombie)PathfinderHolder.getNMSEntity(this.entity));
   }

   protected static class ZombieMeleeAttackGoal extends PathfinderGoalZombieAttack {
      public ZombieMeleeAttackGoal(EntityZombie entity) {
         super(entity, 1.0, false);
      }
   }
}
