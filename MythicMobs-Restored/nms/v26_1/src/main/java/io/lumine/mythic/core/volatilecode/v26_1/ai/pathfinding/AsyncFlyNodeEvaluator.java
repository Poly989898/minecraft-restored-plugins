package io.lumine.mythic.core.volatilecode.v26_1.ai.pathfinding;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;

final class AsyncFlyNodeEvaluator extends FlyNodeEvaluator {
   public void prepare(PathNavigationRegion level, Mob mob) {
      super.prepare(level, mob);
      this.currentContext = new UncachedPathfindingContext(level, mob);
   }
}
