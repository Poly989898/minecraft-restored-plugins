package io.lumine.mythic.core.volatilecode.v1_21_R3.ai.pathfinding;

import java.util.function.Supplier;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition.a;

public final class AsyncNearestAttackableTargetGoal<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget<T> {
   private static final int MAX_AGE = 40;
   private final Supplier<a> prepare;
   private final PathfinderTargetCondition narrow = PathfinderTargetCondition.a();
   private AsyncEntityScan.Job job;

   public AsyncNearestAttackableTargetGoal(
      EntityInsentient mob, Class<T> targetType, int randomInterval, boolean mustSee, boolean mustReach, Supplier<a> prepare
   ) {
      super(mob, targetType, randomInterval, mustSee, mustReach, (target, level) -> prepare.get().test(target, level));
      this.prepare = prepare;
      AsyncPathfinding.configureFromConfig();
   }

   public boolean b() {
      if (!AsyncEntityScan.available()) {
         this.job = null;
         return super.b();
      } else if (this.job == null) {
         return this.gateAndScan();
      } else if (!this.job.done) {
         if (this.e.af - this.job.tick > 40) {
            this.job = null;
         }

         return false;
      } else {
         AsyncEntityScan.Job finished = this.job;
         this.job = null;
         if (this.e.af - finished.tick <= 40 && this.e.dV() == finished.level) {
            this.c = AsyncEntityScan.nearest(finished, this.a(this.l()), this.narrow.a(this.l()), this.e, this.e.dA(), this.e.dE(), this.e.dG());
            return this.c != null || this.gateAndScan();
         } else {
            return this.gateAndScan();
         }
      }
   }

   private boolean gateAndScan() {
      if (this.b > 0 && this.e.dY().a(this.b) != 0) {
         return false;
      } else {
         a selector = this.prepare.get();
         WorldServer level = (WorldServer)this.e.dV();
         this.job = AsyncEntityScan.submit(this.e, this.a(this.l()), entity -> this.a.isInstance(entity) && selector.test(entity, level));
         return this.job == null && super.b();
      }
   }

   public void e() {
      super.e();
      this.job = null;
   }
}
