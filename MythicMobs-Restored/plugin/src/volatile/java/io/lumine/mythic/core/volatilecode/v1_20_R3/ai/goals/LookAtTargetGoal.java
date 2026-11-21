package io.lumine.mythic.core.volatilecode.v1_20_R3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_20_R3.ai.PathfinderHolder;
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

      public boolean a() {
         EntityLiving target = this.mob.q();
         if (target == null) {
            return false;
         } else if (target.f(this.mob) > LookAtTargetGoal.this.maxRange) {
            return false;
         } else {
            this.lookAt = target;
            return true;
         }
      }

      public void c() {
      }

      public boolean b() {
         if (this.mob.fU()) {
            return false;
         } else {
            return this.lookAt != null && this.lookAt.bx() ? !(this.mob.f(this.lookAt) > LookAtTargetGoal.this.maxRange) : false;
         }
      }

      public void d() {
         this.lookAt = null;
      }

      public void e() {
         if (this.lookAt.bx()) {
            double var0 = LookAtTargetGoal.this.onlyHorizontal ? this.mob.dv() : this.lookAt.dv();
            this.mob.I().a(this.lookAt.dr(), var0, this.lookAt.dx());
         }
      }
   }
}
