package io.lumine.mythic.core.volatilecode.v26_1.ai.pathfinding;

import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.volatilecode.v26_1.VolatileWorldHandlerImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;

public final class AsyncEntityScan {
   public static final LongAdder SUBMITTED = new LongAdder();
   public static final LongAdder COMPLETED = new LongAdder();
   public static final LongAdder FAILED = new LongAdder();
   public static final LongAdder SYNC_FALLBACK = new LongAdder();
   private static final double MARGIN = 4.0;

   private AsyncEntityScan() {
   }

   public static boolean available() {
      return AsyncPathfinding.enabled() && VolatileWorldHandlerImpl.hasAsyncEntityGetter() && !ServerVersion.isFolia();
   }

   static AsyncEntityScan.Job submit(Mob mob, AABB box, Predicate<LivingEntity> filter) {
      if (AsyncPathfinding.saturated()) {
         SYNC_FALLBACK.increment();
         return null;
      } else {
         ServerLevel level = (ServerLevel)mob.level();
         LevelEntityGetter<Entity> getter = VolatileWorldHandlerImpl.getEntityGetter(level);
         if (getter == null) {
            SYNC_FALLBACK.increment();
            return null;
         } else {
            AABB scanBox = box.inflate(4.0);
            AsyncEntityScan.Job job = new AsyncEntityScan.Job(level, mob.tickCount);
            Runnable task = () -> {
               try {
                  List<LivingEntity> out = new ArrayList<>();
                  getter.get(scanBox, entity -> {
                     if (entity != mob && entity instanceof LivingEntity living && living.canBeSeenByAnyone() && filter.test(living)) {
                        out.add(living);
                     }
                  });
                  job.result = out;
                  COMPLETED.increment();
               } catch (Throwable var9x) {
                  FAILED.increment();
                  MythicLogger.handleMinorError(var9x);
               } finally {
                  job.done = true;
               }
            };

            try {
               AsyncPathfinding.submit(task);
            } catch (RejectedExecutionException e) {
               SYNC_FALLBACK.increment();
               return null;
            }

            SUBMITTED.increment();
            return job;
         }
      }
   }

   static LivingEntity nearest(AsyncEntityScan.Job job, AABB box, TargetingConditions narrow, Mob mob, double x, double y, double z) {
      List<LivingEntity> found = job.result;
      if (found == null) {
         return null;
      } else {
         LivingEntity best = null;
         double bestSq = 0.0;

         for (LivingEntity candidate : found) {
            double distSq = candidate.distanceToSqr(x, y, z);
            if ((best == null || !(distSq >= bestSq))
               && candidate.level() == job.level
               && box.intersects(candidate.getBoundingBox())
               && narrow.test(job.level, mob, candidate)) {
               best = candidate;
               bestSq = distSq;
            }
         }

         return best;
      }
   }

   public static final class Job {
      final ServerLevel level;
      final int tick;
      volatile List<LivingEntity> result;
      volatile boolean done;

      Job(ServerLevel level, int tick) {
         this.level = level;
         this.tick = tick;
      }
   }
}
