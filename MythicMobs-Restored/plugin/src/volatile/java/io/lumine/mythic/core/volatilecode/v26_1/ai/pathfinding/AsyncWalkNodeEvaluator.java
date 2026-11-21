package io.lumine.mythic.core.volatilecode.v26_1.ai.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

final class AsyncWalkNodeEvaluator extends WalkNodeEvaluator {
   public void prepare(PathNavigationRegion level, Mob mob) {
      super.prepare(level, mob);
      this.currentContext = new UncachedPathfindingContext(level, mob);
   }

   static PathType pathTypeFromState(BlockGetter level, BlockPos pos) {
      return getPathTypeFromState(level, pos);
   }
}
