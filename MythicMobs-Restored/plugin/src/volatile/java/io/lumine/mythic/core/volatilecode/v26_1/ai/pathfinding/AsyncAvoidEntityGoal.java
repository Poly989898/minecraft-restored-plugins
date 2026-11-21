package io.lumine.mythic.core.volatilecode.v26_1.ai.pathfinding;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public final class AsyncAvoidEntityGoal extends AvoidEntityGoal<LivingEntity> {
   private static final int MAX_AGE = 40;
   private final Supplier<Predicate<LivingEntity>> prepare;
   private final TargetingConditions narrow;
   private AsyncEntityScan.Job job;

   public AsyncAvoidEntityGoal(PathfinderMob mob, float maxDist, double walkSpeed, double sprintSpeed, Supplier<Predicate<LivingEntity>> prepare) {
      super(mob, LivingEntity.class, maxDist, walkSpeed, sprintSpeed, entity -> prepare.get().test(entity));
      this.prepare = prepare;
      this.narrow = TargetingConditions.forCombat().range(maxDist);
      AsyncPathfinding.configureFromConfig();
   }

   public boolean canUse() {
      if (!AsyncEntityScan.available()) {
         this.job = null;
         return super.canUse();
      } else if (this.job == null) {
         return this.scan();
      } else if (!this.job.done) {
         if (this.mob.tickCount - this.job.tick > 40) {
            this.job = null;
         }

         return false;
      } else {
         AsyncEntityScan.Job finished = this.job;
         this.job = null;
         if (this.mob.tickCount - finished.tick <= 40 && this.mob.level() == finished.level) {
            this.toAvoid = AsyncEntityScan.nearest(
               finished,
               this.mob.getBoundingBox().inflate(this.maxDist, 3.0, this.maxDist),
               this.narrow,
               this.mob,
               this.mob.getX(),
               this.mob.getY(),
               this.mob.getZ()
            );
            if (this.toAvoid == null) {
               return this.scan();
            } else {
               Vec3 pos = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
               if (pos == null) {
                  return false;
               } else if (this.toAvoid.distanceToSqr(pos.x, pos.y, pos.z) < this.toAvoid.distanceToSqr(this.mob)) {
                  return false;
               } else {
                  this.path = this.pathNav.createPath(pos.x, pos.y, pos.z, 0);
                  return this.path != null;
               }
            }
         } else {
            return this.scan();
         }
      }
   }

   private boolean scan() {
      Predicate<LivingEntity> filter = this.prepare.get();
      this.job = AsyncEntityScan.submit(
         this.mob,
         this.mob.getBoundingBox().inflate(this.maxDist, 3.0, this.maxDist),
         entity -> this.avoidClass.isInstance(entity) && filter.test(entity) && this.avoidPredicate.test(entity)
      );
      return this.job == null && super.canUse();
   }

   public void stop() {
      super.stop();
      this.job = null;
   }
}
