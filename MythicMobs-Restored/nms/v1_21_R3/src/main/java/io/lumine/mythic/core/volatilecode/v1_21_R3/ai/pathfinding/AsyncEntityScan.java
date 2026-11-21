package io.lumine.mythic.core.volatilecode.v1_21_R3.ai.pathfinding;

import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.volatilecode.v1_21_R3.VolatileWorldHandlerImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Predicate;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AxisAlignedBB;

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

   static AsyncEntityScan.Job submit(EntityInsentient mob, AxisAlignedBB box, Predicate<EntityLiving> filter) {
      if (AsyncPathfinding.saturated()) {
         SYNC_FALLBACK.increment();
         return null;
      } else {
         WorldServer level = (WorldServer)mob.dV();
         LevelEntityGetter<Entity> getter = VolatileWorldHandlerImpl.getEntityGetter(level);
         if (getter == null) {
            SYNC_FALLBACK.increment();
            return null;
         } else {
            AxisAlignedBB scanBox = box.g(4.0);
            AsyncEntityScan.Job job = new AsyncEntityScan.Job(level, mob.af);
            Runnable task = () -> {
               try {
                  List<EntityLiving> out = new ArrayList<>();
                  getter.a(scanBox, entity -> {
                     if (entity != mob && entity instanceof EntityLiving living && living.ex() && filter.test(living)) {
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
            } catch (RejectedExecutionException var9) {
               SYNC_FALLBACK.increment();
               return null;
            }

            SUBMITTED.increment();
            return job;
         }
      }
   }

   static EntityLiving nearest(AsyncEntityScan.Job job, AxisAlignedBB box, PathfinderTargetCondition narrow, EntityInsentient mob, double x, double y, double z) {
      List<EntityLiving> found = job.result;
      if (found == null) {
         return null;
      } else {
         EntityLiving best = null;
         double bestSq = 0.0;

         for (EntityLiving candidate : found) {
            double distSq = candidate.i(x, y, z);
            if ((best == null || !(distSq >= bestSq)) && candidate.dV() == job.level && box.c(candidate.cR()) && narrow.a(job.level, mob, candidate)) {
               best = candidate;
               bestSq = distSq;
            }
         }

         return best;
      }
   }

   public static final class Job {
      final WorldServer level;
      final int tick;
      volatile List<EntityLiving> result;
      volatile boolean done;

      Job(WorldServer level, int tick) {
         this.level = level;
         this.tick = tick;
      }
   }
}
