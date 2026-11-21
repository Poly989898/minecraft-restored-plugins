package io.lumine.mythic.core.volatilecode.v26_3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_3.ai.PathfinderHolder;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

@MythicAIGoal(
   name = "moveWithinDistanceOfTarget",
   aliases = {"moveWithin"},
   description = "Moves towards the target to be within a certain range"
)
public class MoveWithinGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private double speedModifier;
   private double minRangeSq;
   private double maxRangeSq;

   public MoveWithinGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speedModifier = mlc.getDouble(new String[]{"speed", "s"}, 1.0);
      double minRange = mlc.getDouble(new String[]{"minrange", "minrange", "range", "r", "distance", "d"}, 10.0);
      double maxRange = mlc.getDouble(new String[]{"maxrange", "maxrange", "maxr"}, 32.0);
      this.minRangeSq = minRange * minRange;
      this.maxRangeSq = maxRange * maxRange;
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public Goal create() {
      return new MoveWithinGoal.MoveWithinDistanceOfTargetGoal(PathfinderHolder.getNMSEntity(this.entity));
   }

   public class MoveWithinDistanceOfTargetGoal extends Goal {
      private final PathfinderMob mob;
      @Nullable
      private LivingEntity target;
      private int repathDelay;

      public MoveWithinDistanceOfTargetGoal(PathfinderMob mob) {
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         this.target = this.mob.getTarget();
         return this.inRange();
      }

      public boolean canContinueToUse() {
         return this.target == this.mob.getTarget() && this.inRange();
      }

      public void stop() {
         this.target = null;
         this.mob.getNavigation().stop();
      }

      public void start() {
         this.repathDelay = 10;
         this.mob.getNavigation().moveTo(this.target, MoveWithinGoal.this.speedModifier);
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         if (--this.repathDelay <= 0) {
            this.repathDelay = 10;
            this.mob.getNavigation().moveTo(this.target, MoveWithinGoal.this.speedModifier);
         }
      }

      private boolean inRange() {
         if (this.target != null && this.target.isAlive()) {
            double distance = this.target.distanceToSqr(this.mob);
            return distance >= MoveWithinGoal.this.minRangeSq && distance <= MoveWithinGoal.this.maxRangeSq;
         } else {
            return false;
         }
      }
   }
}
