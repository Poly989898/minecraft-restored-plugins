package io.lumine.mythic.core.volatilecode.v1_21_R1.ai.pathfinding;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.ChunkCache;
import net.minecraft.world.level.pathfinder.PathfinderFlying;

final class AsyncFlyNodeEvaluator extends PathfinderFlying {
   public void a(ChunkCache level, EntityInsentient mob) {
      super.a(level, mob);
      this.a = new UncachedPathfindingContext(level, mob);
   }
}
