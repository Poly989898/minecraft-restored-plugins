package io.lumine.mythic.core.volatilecode.v26_2.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_2.ai.PathfinderHolder;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathComputationType;

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
   public Goal create() {
      return new MoveToWaterGoal.TurtleGoToWaterGoal(PathfinderHolder.getNMSEntity(this.entity));
   }

   private class TurtleGoToWaterGoal extends net.minecraft.world.entity.ai.goal.MoveToBlockGoal {
      private final Mob mob;

      public TurtleGoToWaterGoal(Mob mob) {
         super((PathfinderMob)mob, MoveToWaterGoal.this.speed, MoveToWaterGoal.this.searchRange, MoveToWaterGoal.this.verticalSearchRange);
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
      }

      public BlockPos getMoveToTarget() {
         return this.blockPos;
      }

      public boolean canContinueToUse() {
         return !this.mob.isInWater() && this.isValidTarget(this.mob.level(), this.blockPos);
      }

      public boolean canUse() {
         return !this.mob.isInWater() && super.canUse();
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 20 == 0;
      }

      protected boolean isValidTarget(LevelReader iworldreader, BlockPos blockposition) {
         return iworldreader.getBlockState(blockposition).is(Blocks.WATER)
            && iworldreader.getBlockState(blockposition.above()).isPathfindable(PathComputationType.LAND);
      }
   }
}
