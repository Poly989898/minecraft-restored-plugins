package io.lumine.mythic.core.volatilecode.v1_21_R4.ai.pathfinding;

import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderAbstract;
import net.minecraft.world.level.pathfinder.PathfinderWater;

final class Workers {
   private static final ThreadLocal<Pathfinder[]> LOCAL = ThreadLocal.withInitial(() -> new Pathfinder[Workers.Kind.values().length]);

   static Pathfinder get(Workers.Kind kind) {
      Pathfinder[] slots = LOCAL.get();
      Pathfinder finder = slots[kind.ordinal()];
      if (finder == null) {
         finder = new Pathfinder(create(kind), 0);
         slots[kind.ordinal()] = finder;
      }

      return finder;
   }

   static void drop(Workers.Kind kind) {
      LOCAL.get()[kind.ordinal()] = null;
   }

   private static PathfinderAbstract create(Workers.Kind kind) {
      return (PathfinderAbstract)(switch (kind) {
         case WALK -> new AsyncWalkNodeEvaluator();
         case FLY -> new AsyncFlyNodeEvaluator();
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
