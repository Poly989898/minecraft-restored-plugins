package io.lumine.mythic.core.volatilecode.v26_3.ai.pathfinding;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;

final class UncachedPathfindingContext extends PathfindingContext {
   private final MutableBlockPos pos = new MutableBlockPos();

   UncachedPathfindingContext(CollisionGetter level, Mob mob) {
      super(level, mob);
   }

   public PathType getPathTypeFromState(int x, int y, int z) {
      return AsyncWalkNodeEvaluator.pathTypeFromState(this.level(), this.pos.set(x, y, z));
   }
}
