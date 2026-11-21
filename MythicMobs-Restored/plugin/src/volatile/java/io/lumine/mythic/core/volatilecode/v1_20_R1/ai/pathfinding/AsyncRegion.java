package io.lumine.mythic.core.volatilecode.v1_20_R1.ai.pathfinding;

import net.minecraft.core.BlockPosition;
import net.minecraft.util.profiling.GameProfilerDisabled;
import net.minecraft.util.profiling.GameProfilerFiller;
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

   public GameProfilerFiller a() {
      return GameProfilerDisabled.a;
   }
}
