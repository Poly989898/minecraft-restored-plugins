package io.lumine.mythic.core.volatilecode.v1_21_R5.ai.pathfinding;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.ChunkCache;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;

final class AsyncRegion extends ChunkCache {
   AsyncRegion(World level, BlockPosition start, BlockPosition end) {
      super(level, start, end);
   }

   public TileEntity c_(BlockPosition pos) {
      return null;
   }
}
