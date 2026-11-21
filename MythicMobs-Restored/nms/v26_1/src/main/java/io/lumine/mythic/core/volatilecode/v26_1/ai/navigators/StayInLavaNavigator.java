package io.lumine.mythic.core.volatilecode.v26_1.ai.navigators;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;

public class StayInLavaNavigator extends WaterBoundPathNavigation {
   public StayInLavaNavigator(Mob entitystrider, Level world) {
      super(entitystrider, world);
   }

   protected boolean canUpdatePath() {
      return true;
   }

   protected PathFinder createPathFinder(int value) {
      this.nodeEvaluator = new SwimNodeEvaluator(true);
      return new PathFinder(this.nodeEvaluator, value);
   }

   public boolean isStableDestination(BlockPos blockposition) {
      return this.level.getBlockState(blockposition).is(Blocks.LAVA);
   }
}
