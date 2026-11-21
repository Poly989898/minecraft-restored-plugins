package io.lumine.mythic.core.volatilecode.v1_21_R1.ai.pathfinding;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;

public final class AsyncNearestAttackableTargetGoal<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget<T> {
   private static final int MAX_AGE = 40;
   private final Supplier<Predicate<EntityLiving>> prepare;
   private final PathfinderTargetCondition narrow;
   private AsyncEntityScan.Job job;

   public AsyncNearestAttackableTargetGoal(
      EntityInsentient mob, Class<T> targetType, int randomInterval, boolean mustSee, boolean mustReach, Supplier<Predicate<EntityLiving>> prepare
   ) {
      super(mob, targetType, randomInterval, mustSee, mustReach, target -> prepare.get().test(target));
      this.prepare = prepare;
      this.narrow = PathfinderTargetCondition.a().a(this.l());
      if (mob.dO().paperConfig().entities.entitiesTargetWithFollowRange) {
         this.narrow.useFollowRange();
      }

      AsyncPathfinding.configureFromConfig();
   }

   public boolean b() {
      if (!AsyncEntityScan.available()) {
         this.job = null;
         return super.b();
      } else if (this.job == null) {
         return this.gateAndScan();
      } else if (!this.job.done) {
         if (this.e.ai - this.job.tick > 40) {
            this.job = null;
         }

         return false;
      } else {
         AsyncEntityScan.Job finished = this.job;
         this.job = null;
         if (this.e.ai - finished.tick <= 40 && this.e.dO() == finished.level) {
            this.c = AsyncEntityScan.nearest(finished, this.a(this.l()), this.narrow, this.e, this.e.dt(), this.e.dx(), this.e.dz());
            return this.c != null || this.gateAndScan();
         } else {
            return this.gateAndScan();
         }
      }
   }

   private boolean gateAndScan() {
      if (this.b > 0 && this.e.dR().a(this.b) != 0) {
         return false;
      } else {
         Predicate<EntityLiving> selector = this.prepare.get();
         this.job = AsyncEntityScan.submit(this.e, this.a(this.l()), entity -> this.a.isInstance(entity) && selector.test(entity));
         return this.job == null && super.b();
      }
   }

   public void e() {
      super.e();
      this.job = null;
   }
}
