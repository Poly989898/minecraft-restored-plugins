package io.lumine.mythic.core.volatilecode.v26_2.ai.pathfinding;

import java.util.function.Supplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.targeting.TargetingConditions.Selector;

public final class AsyncNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
   private static final int MAX_AGE = 40;
   private final Supplier<Selector> prepare;
   private final TargetingConditions narrow = TargetingConditions.forCombat();
   private AsyncEntityScan.Job job;

   public AsyncNearestAttackableTargetGoal(Mob mob, Class<T> targetType, int randomInterval, boolean mustSee, boolean mustReach, Supplier<Selector> prepare) {
      super(mob, targetType, randomInterval, mustSee, mustReach, (target, level) -> prepare.get().test(target, level));
      this.prepare = prepare;
      AsyncPathfinding.configureFromConfig();
   }

   public boolean canUse() {
      if (!AsyncEntityScan.available()) {
         this.job = null;
         return super.canUse();
      } else if (this.job == null) {
         return this.gateAndScan();
      } else if (!this.job.done) {
         if (this.mob.tickCount - this.job.tick > 40) {
            this.job = null;
         }

         return false;
      } else {
         AsyncEntityScan.Job finished = this.job;
         this.job = null;
         if (this.mob.tickCount - finished.tick <= 40 && this.mob.level() == finished.level) {
            this.target = AsyncEntityScan.nearest(
               finished,
               this.getTargetSearchArea(this.getFollowDistance()),
               this.narrow.range(this.getFollowDistance()),
               this.mob,
               this.mob.getX(),
               this.mob.getEyeY(),
               this.mob.getZ()
            );
            return this.target != null || this.gateAndScan();
         } else {
            return this.gateAndScan();
         }
      }
   }

   private boolean gateAndScan() {
      if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
         return false;
      } else {
         Selector selector = this.prepare.get();
         ServerLevel level = (ServerLevel)this.mob.level();
         this.job = AsyncEntityScan.submit(
            this.mob, this.getTargetSearchArea(this.getFollowDistance()), entity -> this.targetType.isInstance(entity) && selector.test(entity, level)
         );
         return this.job == null && super.canUse();
      }
   }

   public void stop() {
      super.stop();
      this.job = null;
   }
}
