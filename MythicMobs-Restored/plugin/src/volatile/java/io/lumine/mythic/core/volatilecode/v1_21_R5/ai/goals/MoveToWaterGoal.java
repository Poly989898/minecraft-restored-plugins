package io.lumine.mythic.core.volatilecode.v1_21_R5.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v1_21_R5.ai.PathfinderHolder;
import java.util.EnumSet;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalGotoTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoal.Type;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathMode;

@MythicAIGoal(
   name = "moveToWater",
   aliases = {"gotowater"},
   description = "Path to nearby water"
)
public class MoveToWaterGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private double speed;
   private int searchRange = 8;
   private int verticalSearchRange = 2;

   public MoveToWaterGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speed = mlc.getDouble(new String[]{"speed", "s"}, 0.9);
   }

   @Override
   public boolean isValid() {
      return this.entity.isLiving();
   }

   @Override
   public PathfinderGoal create() {
      return new MoveToWaterGoal.TurtleGoToWaterGoal(PathfinderHolder.getNMSEntity(this.entity));
   }

   private class TurtleGoToWaterGoal extends PathfinderGoalGotoTarget {
      private final EntityInsentient mob;

      public TurtleGoToWaterGoal(EntityInsentient mob) {
         super((EntityCreature)mob, MoveToWaterGoal.this.speed, MoveToWaterGoal.this.searchRange, MoveToWaterGoal.this.verticalSearchRange);
         this.mob = mob;
         this.a(EnumSet.of(Type.c, Type.a));
      }

      public BlockPosition k() {
         return this.e;
      }

      public boolean c() {
         return !this.mob.bm() && this.a(this.mob.ai(), this.e);
      }

      public boolean b() {
         return !this.mob.bm() && super.b();
      }

      public boolean l() {
         return this.d % 20 == 0;
      }

      protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
         return iworldreader.a_(blockposition).a(Blocks.J) && iworldreader.a_(blockposition.d()).a(PathMode.a);
      }
   }
}
