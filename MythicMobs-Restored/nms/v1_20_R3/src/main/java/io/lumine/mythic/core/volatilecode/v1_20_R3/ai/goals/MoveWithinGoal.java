package io.lumine.mythic.core.volatilecode.v1_20_R3.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_20_R3.ai.PathfinderHolder;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoal.Type;

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
   public PathfinderGoal create() {
      return new MoveWithinGoal.MoveWithinDistanceOfTargetGoal(PathfinderHolder.getNMSEntity(this.entity));
   }

   public class MoveWithinDistanceOfTargetGoal extends PathfinderGoal {
      private final EntityCreature mob;
      @Nullable
      private EntityLiving target;
      private int repathDelay;

      public MoveWithinDistanceOfTargetGoal(EntityCreature mob) {
         this.mob = mob;
         this.a(EnumSet.of(Type.a));
      }

      public boolean a() {
         this.target = this.mob.q();
         return this.inRange();
      }

      public boolean b() {
         return this.target == this.mob.q() && this.inRange();
      }

      public void d() {
         this.target = null;
         this.mob.N().n();
      }

      public void c() {
         this.repathDelay = 10;
         this.mob.N().a(this.target, MoveWithinGoal.this.speedModifier);
      }

      public boolean T_() {
         return true;
      }

      public void e() {
         if (--this.repathDelay <= 0) {
            this.repathDelay = 10;
            this.mob.N().a(this.target, MoveWithinGoal.this.speedModifier);
         }
      }

      private boolean inRange() {
         if (this.target != null && this.target.bx()) {
            double distance = this.target.f(this.mob);
            return distance >= MoveWithinGoal.this.minRangeSq && distance <= MoveWithinGoal.this.maxRangeSq;
         } else {
            return false;
         }
      }
   }
}
