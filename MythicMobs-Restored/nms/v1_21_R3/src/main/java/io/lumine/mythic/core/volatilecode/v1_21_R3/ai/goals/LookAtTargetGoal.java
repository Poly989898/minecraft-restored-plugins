package io.lumine.mythic.core.volatilecode.v1_21_R3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R3.ai.PathfinderHolder;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoal.Type;

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
   public PathfinderGoal create() {
      return new LookAtTargetGoal.LookAtTarget(PathfinderHolder.getNMSEntity(this.entity));
   }

   public class LookAtTarget extends PathfinderGoal {
      private final EntityInsentient mob;
      @Nullable
      protected Entity lookAt;

      public LookAtTarget(EntityInsentient var0) {
         this.mob = var0;
         this.a(EnumSet.of(Type.b));
      }

      public boolean b() {
         EntityLiving target = this.mob.O_();
         if (target == null) {
            return false;
         } else if (target.g(this.mob) > LookAtTargetGoal.this.maxRange) {
            return false;
         } else {
            this.lookAt = target;
            return true;
         }
      }

      public void d() {
      }

      public boolean c() {
         if (this.mob.gf()) {
            return false;
         } else {
            return this.lookAt != null && this.lookAt.bL() ? !(this.mob.g(this.lookAt) > LookAtTargetGoal.this.maxRange) : false;
         }
      }

      public void e() {
         this.lookAt = null;
      }

      public void a() {
         if (this.lookAt.bL()) {
            double var0 = LookAtTargetGoal.this.onlyHorizontal ? this.mob.dE() : this.lookAt.dE();
            this.mob.L().a(this.lookAt.dA(), var0, this.lookAt.dG());
         }
      }
   }
}
