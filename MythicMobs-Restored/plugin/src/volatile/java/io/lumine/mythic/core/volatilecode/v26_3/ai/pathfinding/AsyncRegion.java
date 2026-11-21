package io.lumine.mythic.core.volatilecode.v26_3.ai.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.entity.BlockEntity;

final class AsyncRegion extends PathNavigationRegion {
   AsyncRegion(Level level, BlockPos start, BlockPos end) {
      super(level, start, end);
   }

   public BlockEntity getBlockEntity(BlockPos pos) {
      return null;
   }
}
