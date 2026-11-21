package io.lumine.mythic.core.volatilecode.v26_1.ai.pathfinding;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.reflection.Reflector;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.config.ConfigExecutor;
import io.lumine.mythic.core.logging.MythicLogger;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiFunction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.NodeEvaluator;

public final class AsyncPathfinding {
   public static final LongAdder SUBMITTED = new LongAdder();
   public static final LongAdder COMPLETED = new LongAdder();
   public static final LongAdder FAILED = new LongAdder();
   public static final LongAdder SKIPPED_STALE = new LongAdder();
   public static final LongAdder SYNC_FALLBACK = new LongAdder();
   private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();
   private static final Reflector<Mob> REF_MOB = new Reflector<>(Mob.class, "navigation", "goalSelector");
   private static final Reflector<PathNavigation> REF_NAV = new Reflector<>(PathNavigation.class, "requiredPathLength", "maxVisitedNodesMultiplier");
   private static final Map<Class<?>, Boolean> MOB_CLASS_SUPPORTED = new ConcurrentHashMap<>();
   private static final Map<Class<?>, Field[]> GOAL_NAVIGATION_FIELDS = new ConcurrentHashMap<>();
   private static final Map<Class<?>, BiFunction<Mob, Level, PathNavigation>> FACTORIES = Map.of(
      GroundPathNavigation.class,
      (mob, level) -> new AsyncGroundPathNavigation(mob, level, false),
      WallClimberNavigation.class,
      (mob, level) -> new AsyncWallClimberNavigation(mob, level, false),
      FlyingPathNavigation.class,
      (mob, level) -> new AsyncFlyingPathNavigation(mob, level, false),
      WaterBoundPathNavigation.class,
      (mob, level) -> new AsyncWaterBoundPathNavigation(mob, level, false)
   );
   private static volatile boolean enabled;
   private static volatile ThreadPoolExecutor pool;
   private static int configuredThreads = -1;
   private static boolean premiumNotified;

   private AsyncPathfinding() {
   }

   public static boolean enabled() {
      return enabled;
   }

   static void submit(Runnable task) {
      ThreadPoolExecutor executor = pool;
      if (executor == null) {
         throw new RejectedExecutionException("async pathfinding pool is not running");
      } else {
         executor.execute(task);
      }
   }

   static boolean saturated() {
      ThreadPoolExecutor executor = pool;
      return executor == null || executor.getQueue().size() > 128 * executor.getCorePoolSize();
   }

   public static void configureFromConfig() {
      if (ServerVersion.isFolia()) {
         configure(false, 0);
      } else {
         ConfigExecutor config = MythicBukkit.inst().getConfiguration();
         if (!MythicBukkit.isVolatile()) {
            if (config.isAsyncPathfindingEnabled() && !premiumNotified) {
               premiumNotified = true;
               MythicLogger.log("Async pathfinding and async goal scans require Mythic Premium");
            }

            configure(false, 0);
         } else {
            configure(config.isAsyncPathfindingEnabled(), config.getAsyncPathfindingThreads());
         }
      }
   }

   public static synchronized void configure(boolean on, int threads) {
      if (!on) {
         enabled = false;
         stopPool(false);
      } else {
         int size = threads > 0 ? threads : Math.max(1, Runtime.getRuntime().availableProcessors() / 4);
         if (pool == null || size != configuredThreads) {
            stopPool(false);
            ThreadPoolExecutor executor = new ThreadPoolExecutor(size, size, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), runnable -> {
               Thread thread = new Thread(runnable, "MythicMobs-Pathfind-Goals-" + THREAD_COUNTER.incrementAndGet());
               thread.setDaemon(true);
               thread.setPriority(4);
               return thread;
            });
            executor.allowCoreThreadTimeOut(true);
            pool = executor;
            configuredThreads = size;
         }

         enabled = true;
      }
   }

   public static synchronized void shutdown() {
      enabled = false;
      stopPool(true);
   }

   private static void stopPool(boolean now) {
      ThreadPoolExecutor executor = pool;
      pool = null;
      configuredThreads = -1;
      if (executor != null) {
         if (now) {
            executor.shutdownNow();
         } else {
            executor.shutdown();
         }
      }
   }

   public static void install(Mob mob) {
      configureFromConfig();
      if (enabled) {
         PathNavigation current = (PathNavigation)REF_MOB.get(mob, "navigation");
         if (current != null && !(current instanceof AsyncNavigation)) {
            BiFunction<Mob, Level, PathNavigation> factory = FACTORIES.get(current.getClass());
            if (factory == null) {
               factory = megFactory(current.getClass());
            }

            if (factory != null && MOB_CLASS_SUPPORTED.computeIfAbsent(mob.getClass(), AsyncPathfinding::mobClassSupported)) {
               PathNavigation next = factory.apply(mob, mob.level());
               NodeEvaluator from = current.getNodeEvaluator();
               NodeEvaluator to = next.getNodeEvaluator();
               to.setCanPassDoors(from.canPassDoors());
               to.setCanOpenDoors(from.canOpenDoors());
               to.setCanFloat(from.canFloat());
               to.setCanWalkOverFences(from.canWalkOverFences());
               next.setRequiredPathLength((Float)REF_NAV.get(current, "requiredPathLength"));
               next.setMaxVisitedNodesMultiplier((Float)REF_NAV.get(current, "maxVisitedNodesMultiplier"));
               REF_MOB.set(mob, "navigation", next);
               retarget((GoalSelector)REF_MOB.get(mob, "goalSelector"), current, next);
               retarget(mob.targetSelector, current, next);
            }
         }
      }
   }

   private static BiFunction<Mob, Level, PathNavigation> megFactory(Class<?> type) {
      if (MegSupport.isWrapper(type, "GroundNavigationWrapper")) {
         return (mob, level) -> new AsyncGroundPathNavigation(mob, level, true);
      } else if (MegSupport.isWrapper(type, "WallClimberNavigationWrapper")) {
         return (mob, level) -> new AsyncWallClimberNavigation(mob, level, true);
      } else if (MegSupport.isWrapper(type, "FlyingNavigationWrapper")) {
         return (mob, level) -> new AsyncFlyingPathNavigation(mob, level, true);
      } else {
         return MegSupport.isWrapper(type, "WaterBoundNavigationWrapper") ? (mob, level) -> new AsyncWaterBoundPathNavigation(mob, level, true) : null;
      }
   }

   private static boolean mobClassSupported(Class<?> mobClass) {
      try {
         return mobClass.getMethod("onPathfindingStart").getDeclaringClass() == Mob.class
            && mobClass.getMethod("onPathfindingDone").getDeclaringClass() == Mob.class;
      } catch (NoSuchMethodException e) {
         return false;
      }
   }

   private static void retarget(GoalSelector selector, PathNavigation from, PathNavigation to) {
      if (selector != null) {
         for (WrappedGoal wrapped : selector.getAvailableGoals()) {
            Goal goal = wrapped.getGoal();

            for (Field field : GOAL_NAVIGATION_FIELDS.computeIfAbsent(goal.getClass(), AsyncPathfinding::navigationFields)) {
               try {
                  if (field.get(goal) == from) {
                     field.set(goal, to);
                  }
               } catch (IllegalAccessException e) {
                  MythicLogger.handleMinorError(e);
               }
            }
         }
      }
   }

   private static Field[] navigationFields(Class<?> goalClass) {
      List<Field> fields = new ArrayList<>();

      for (Class<?> type = goalClass; type != null && type != Object.class; type = type.getSuperclass()) {
         for (Field field : type.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) && PathNavigation.class.isAssignableFrom(field.getType())) {
               field.setAccessible(true);
               fields.add(field);
            }
         }
      }

      return fields.toArray(new Field[0]);
   }
}
