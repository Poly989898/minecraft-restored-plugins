package io.lumine.mythic.core.volatilecode.v26_1.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_1.ai.PathfinderHolder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
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
   public Goal create() {
      return new SpiderAttackGoal.SpiderMeleeAttackGoal(PathfinderHolder.getNMSEntity(this.entity));
   }

   protected static class SpiderMeleeAttackGoal extends net.minecraft.world.entity.ai.goal.MeleeAttackGoal {
      public SpiderMeleeAttackGoal(PathfinderMob entity) {
         super(entity, 1.0, true);
      }

      public boolean canUse() {
         return super.canUse();
      }

      protected double getAttackReachSqr(Mob entityliving) {
         return 4.0F + entityliving.getBbWidth();
      }
   }
}
