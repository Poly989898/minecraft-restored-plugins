package io.lumine.mythic.core.volatilecode.v26_3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_3.ai.PathfinderHolder;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

@MythicAIGoal(
   name = "lookAtTarget",
   aliases = {},
   description = "Look at the current target"
)
public class LookAtTargetGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private double speed;
   private double maxRange;
   private boolean onlyHorizontal;

   public LookAtTargetGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speed = mlc.getDouble(new String[]{"speed", "s"}, 0.9);
      this.maxRange = mlc.getFloat(new String[]{"maxrange", "range", "r"}, 32.0F);
      this.onlyHorizontal = mlc.getBoolean(new String[]{"onlyhorizontal", "horizontal", "h"}, false);
      this.maxRange = Math.pow(this.maxRange, 2.0);
   }

   @Override
   public boolean isValid() {
      return this.entity.isMob();
   }

   @Override
   public Goal create() {
      return new LookAtTargetGoal.LookAtTarget(PathfinderHolder.getNMSEntity(this.entity));
   }

   public class LookAtTarget extends Goal {
      private final Mob mob;
      @Nullable
      protected Entity lookAt;

      public LookAtTarget(Mob mob2) {
         this.mob = mob2;
         this.setFlags(EnumSet.of(Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity target = this.mob.getTarget();
         if (target == null) {
            return false;
         } else if (target.distanceToSqr(this.mob) > LookAtTargetGoal.this.maxRange) {
            return false;
         } else {
            this.lookAt = target;
            return true;
         }
      }

      public void start() {
      }

      public boolean canContinueToUse() {
         if (this.mob.isNoAi()) {
            return false;
         } else {
            return this.lookAt != null && this.lookAt.isAlive() ? !(this.mob.distanceToSqr(this.lookAt) > LookAtTargetGoal.this.maxRange) : false;
         }
      }

      public void stop() {
         this.lookAt = null;
      }

      public void tick() {
         if (this.lookAt.isAlive()) {
            double value = LookAtTargetGoal.this.onlyHorizontal ? this.mob.getEyeY() : this.lookAt.getEyeY();
            this.mob.getLookControl().setLookAt(this.lookAt.getX(), value, this.lookAt.getZ());
         }
      }
   }
}
