package io.lumine.mythic.core.volatilecode.v1_21_R1.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import org.bukkit.entity.Spider;

@MythicAIGoal(
   name = "spiderAttack",
   aliases = {},
   description = "Spider melee attack"
)
public class SpiderAttackGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   public SpiderAttackGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
   }

   @Override
   public boolean isValid() {
      return this.entity.getBukkitEntity() instanceof Spider;
   }

   @Override
   public PathfinderGoal create() {
      return new SpiderAttackGoal.SpiderMeleeAttackGoal(PathfinderHolder.getNMSEntity(this.entity));
   }

   protected static class SpiderMeleeAttackGoal extends PathfinderGoalMeleeAttack {
      public SpiderMeleeAttackGoal(EntityCreature entity) {
         super(entity, 1.0, true);
      }

      public boolean b() {
         return super.b();
      }

      protected double getAttackReachSqr(EntityInsentient entityliving) {
         return 4.0F + entityliving.dj();
      }
   }
}
