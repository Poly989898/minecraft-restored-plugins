package io.lumine.mythic.core.volatilecode.v1_21_R5.ai.pathfinding;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.ChunkCache;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfinderNormal;

final class AsyncWalkNodeEvaluator extends PathfinderNormal {
   public void a(ChunkCache level, EntityInsentient mob) {
      super.a(level, mob);
      this.b = new UncachedPathfindingContext(level, mob);
   }

   static PathType pathTypeFromState(IBlockAccess level, BlockPosition pos) {
      return b(level, pos);
   }
}
