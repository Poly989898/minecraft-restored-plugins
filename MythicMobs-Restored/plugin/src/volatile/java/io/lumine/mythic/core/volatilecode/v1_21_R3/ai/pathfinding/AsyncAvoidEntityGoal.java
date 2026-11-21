package io.lumine.mythic.core.volatilecode.v1_21_R3.ai.pathfinding;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3D;

public final class AsyncAvoidEntityGoal extends PathfinderGoalAvoidTarget<EntityLiving> {
   private static final int MAX_AGE = 40;
   private final Supplier<Predicate<EntityLiving>> prepare;
   private final PathfinderTargetCondition narrow;
   private AsyncEntityScan.Job job;

   public AsyncAvoidEntityGoal(EntityCreature mob, float maxDist, double walkSpeed, double sprintSpeed, Supplier<Predicate<EntityLiving>> prepare) {
      super(mob, EntityLiving.class, maxDist, walkSpeed, sprintSpeed, entity -> prepare.get().test(entity));
      this.prepare = prepare;
      this.narrow = PathfinderTargetCondition.a().a(maxDist);
      AsyncPathfinding.configureFromConfig();
   }

   public boolean b() {
      if (!AsyncEntityScan.available()) {
         this.job = null;
         return super.b();
      } else if (this.job == null) {
         return this.scan();
      } else if (!this.job.done) {
         if (this.a.af - this.job.tick > 40) {
            this.job = null;
         }

         return false;
      } else {
         AsyncEntityScan.Job finished = this.job;
         this.job = null;
         if (this.a.af - finished.tick <= 40 && this.a.dV() == finished.level) {
            this.b = AsyncEntityScan.nearest(finished, this.a.cR().c(this.c, 3.0, this.c), this.narrow, this.a, this.a.dA(), this.a.dC(), this.a.dG());
            if (this.b == null) {
               return this.scan();
            } else {
               Vec3D pos = DefaultRandomPos.a(this.a, 16, 7, this.b.dt());
               if (pos == null) {
                  return false;
               } else if (this.b.i(pos.d, pos.e, pos.f) < this.b.g(this.a)) {
                  return false;
               } else {
                  this.d = this.e.a(pos.d, pos.e, pos.f, 0);
                  return this.d != null;
               }
            }
         } else {
            return this.scan();
         }
      }
   }

   private boolean scan() {
      Predicate<EntityLiving> filter = this.prepare.get();
      this.job = AsyncEntityScan.submit(
         this.a, this.a.cR().c(this.c, 3.0, this.c), entity -> this.f.isInstance(entity) && filter.test(entity) && this.g.test(entity)
      );
      return this.job == null && super.b();
   }

   public void e() {
      super.e();
      this.job = null;
   }
}
