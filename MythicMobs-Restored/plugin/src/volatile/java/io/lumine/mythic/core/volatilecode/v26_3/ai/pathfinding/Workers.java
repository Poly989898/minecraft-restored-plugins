package io.lumine.mythic.core.volatilecode.v26_3.ai.pathfinding;

import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;

final class Workers {
   private static final ThreadLocal<PathFinder[]> LOCAL = ThreadLocal.withInitial(() -> new PathFinder[Workers.Kind.values().length]);

   static PathFinder get(Workers.Kind kind) {
      PathFinder[] slots = LOCAL.get();
      PathFinder finder = slots[kind.ordinal()];
      if (finder == null) {
         finder = new PathFinder(create(kind), 0);
         slots[kind.ordinal()] = finder;
      }

      return finder;
   }

   static void drop(Workers.Kind kind) {
      LOCAL.get()[kind.ordinal()] = null;
   }

   private static NodeEvaluator create(Workers.Kind kind) {
      return (NodeEvaluator)(switch (kind) {
         case WALK -> new AsyncWalkNodeEvaluator();
         case FLY -> new AsyncFlyNodeEvaluator();
         case SWIM -> new SwimNodeEvaluator(false);
         case SWIM_BREACHING -> new SwimNodeEvaluator(true);
      });
   }

   static enum Kind {
      WALK,
      FLY,
      SWIM,
      SWIM_BREACHING;
   }
}
