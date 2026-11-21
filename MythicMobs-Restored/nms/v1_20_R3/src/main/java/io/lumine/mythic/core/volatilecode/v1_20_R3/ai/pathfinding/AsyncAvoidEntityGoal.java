package io.lumine.mythic.core.volatilecode.v1_20_R3.ai.pathfinding;

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

   public boolean a() {
      if (!AsyncEntityScan.available()) {
         this.job = null;
         return super.a();
      } else if (this.job == null) {
         return this.scan();
      } else if (!this.job.done) {
         if (this.a.ah - this.job.tick > 40) {
            this.job = null;
         }

         return false;
      } else {
         AsyncEntityScan.Job finished = this.job;
         this.job = null;
         if (this.a.ah - finished.tick <= 40 && this.a.dM() == finished.level) {
            this.b = AsyncEntityScan.nearest(finished, this.a.cH().c(this.c, 3.0, this.c), this.narrow, this.a, this.a.dr(), this.a.dt(), this.a.dx());
            if (this.b == null) {
               return this.scan();
            } else {
               Vec3D pos = DefaultRandomPos.a(this.a, 16, 7, this.b.dk());
               if (pos == null) {
                  return false;
               } else if (this.b.i(pos.c, pos.d, pos.e) < this.b.f(this.a)) {
                  return false;
               } else {
                  this.d = this.e.a(pos.c, pos.d, pos.e, 0);
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
         this.a, this.a.cH().c(this.c, 3.0, this.c), entity -> this.f.isInstance(entity) && filter.test(entity) && this.g.test(entity)
      );
      return this.job == null && super.a();
   }

   public void d() {
      super.d();
      this.job = null;
   }
}
