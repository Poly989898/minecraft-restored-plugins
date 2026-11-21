package io.lumine.mythic.core.volatilecode.v1_21_R6.ai.pathfinding;

import net.minecraft.core.BlockPosition.MutableBlockPosition;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.ICollisionAccess;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;

final class UncachedPathfindingContext extends PathfindingContext {
   private final MutableBlockPosition pos = new MutableBlockPosition();

   UncachedPathfindingContext(ICollisionAccess level, EntityInsentient mob) {
      super(level, mob);
   }

   public PathType a(int x, int y, int z) {
      return AsyncWalkNodeEvaluator.pathTypeFromState(this.a(), this.pos.d(x, y, z));
   }
}
