package io.lumine.mythic.core.volatilecode.v26_1.ai.goals;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.mobs.ai.WrappedPathfindingGoal;
import io.lumine.mythic.core.utils.annotations.MythicAIGoal;
import io.lumine.mythic.core.volatilecode.v26_1.ai.PathfinderHolder;
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
   name = "moveToLava",
   aliases = {"gotolava"},
   description = "Path to nearby lava"
)
public class MoveToLavaGoal extends WrappedPathfindingGoal implements PathfinderHolder {
   private double speed;
   private int searchRange = 8;
   private int verticalSearchRange = 2;

   public MoveToLavaGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
      super(entity, line, mlc);
      this.speed = mlc.getDouble(new String[]{"speed", "s"}, 0.9);
   }

   @Override
   public boolean isValid() {
      return this.entity.isCreature();
   }

   @Override
   public Goal create() {
      return new MoveToLavaGoal.StriderGoToLavaGoal(PathfinderHolder.getNMSEntity(this.entity));
   }

   private class StriderGoToLavaGoal extends net.minecraft.world.entity.ai.goal.MoveToBlockGoal {
      private final Mob mob;

      public StriderGoToLavaGoal(Mob mob) {
         super((PathfinderMob)mob, MoveToLavaGoal.this.speed, MoveToLavaGoal.this.searchRange, MoveToLavaGoal.this.verticalSearchRange);
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
      }

      public BlockPos getMoveToTarget() {
         return this.blockPos;
      }

      public boolean canContinueToUse() {
         return !this.mob.isInLava() && this.isValidTarget(this.mob.level(), this.blockPos);
      }

      public boolean canUse() {
         return !this.mob.isInLava() && super.canUse();
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 20 == 0;
      }

      protected boolean isValidTarget(LevelReader iworldreader, BlockPos blockposition) {
         return iworldreader.getBlockState(blockposition).is(Blocks.LAVA)
            && iworldreader.getBlockState(blockposition.above()).isPathfindable(PathComputationType.LAND);
      }
   }
}
