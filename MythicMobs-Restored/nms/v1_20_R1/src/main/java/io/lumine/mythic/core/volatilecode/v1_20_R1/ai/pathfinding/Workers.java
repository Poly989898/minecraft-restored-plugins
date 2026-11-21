package io.lumine.mythic.core.volatilecode.v1_20_R1.ai.pathfinding;

import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderAbstract;
import net.minecraft.world.level.pathfinder.PathfinderFlying;
import net.minecraft.world.level.pathfinder.PathfinderNormal;
import net.minecraft.world.level.pathfinder.PathfinderWater;

final class Workers {
   private static final ThreadLocal<PathfinderAbstract[]> LOCAL = ThreadLocal.withInitial(() -> new PathfinderAbstract[Workers.Kind.values().length]);

   static Pathfinder get(Workers.Kind kind, int maxVisitedNodes) {
      PathfinderAbstract[] slots = LOCAL.get();
      PathfinderAbstract evaluator = slots[kind.ordinal()];
      if (evaluator == null) {
         evaluator = create(kind);
         slots[kind.ordinal()] = evaluator;
      }

      return new Pathfinder(evaluator, maxVisitedNodes);
   }

   static void drop(Workers.Kind kind) {
      LOCAL.get()[kind.ordinal()] = null;
   }

   private static PathfinderAbstract create(Workers.Kind kind) {
      return (PathfinderAbstract)(switch (kind) {
         case WALK -> new PathfinderNormal();
         case FLY -> new PathfinderFlying();
         case SWIM -> new PathfinderWater(false);
         case SWIM_BREACHING -> new PathfinderWater(true);
      });
   }

   static enum Kind {
      WALK,
      FLY,
      SWIM,
      SWIM_BREACHING;
   }
}
