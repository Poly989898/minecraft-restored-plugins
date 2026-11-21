package io.lumine.mythic.core.volatilecode.v1_21_R7.ai.pathfinding;

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
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.goal.PathfinderGoalWrapped;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.entity.ai.navigation.NavigationSpider;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathfinderAbstract;

public final class AsyncPathfinding {
   public static final LongAdder SUBMITTED = new LongAdder();
   public static final LongAdder COMPLETED = new LongAdder();
   public static final LongAdder FAILED = new LongAdder();
   public static final LongAdder SKIPPED_STALE = new LongAdder();
   public static final LongAdder SYNC_FALLBACK = new LongAdder();
   private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();
   private static final Reflector<EntityInsentient> REF_MOB = new Reflector<>(EntityInsentient.class, "cr", "cs");
   private static final Reflector<NavigationAbstract> REF_NAV = new Reflector<>(NavigationAbstract.class, "x", "u");
   private static final Map<Class<?>, Field[]> GOAL_NAVIGATION_FIELDS = new ConcurrentHashMap<>();
   private static final Map<Class<?>, BiFunction<EntityInsentient, World, NavigationAbstract>> FACTORIES = Map.of(
      Navigation.class,
      (mob, level) -> new AsyncGroundPathNavigation(mob, level, false),
      NavigationSpider.class,
      (mob, level) -> new AsyncWallClimberNavigation(mob, level, false),
      NavigationFlying.class,
      (mob, level) -> new AsyncFlyingPathNavigation(mob, level, false),
      NavigationGuardian.class,
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

   public static void install(EntityInsentient mob) {
      configureFromConfig();
      if (enabled) {
         NavigationAbstract current = (NavigationAbstract)REF_MOB.get(mob, "cr");
         if (current != null && !(current instanceof AsyncNavigation)) {
            BiFunction<EntityInsentient, World, NavigationAbstract> factory = FACTORIES.get(current.getClass());
            if (factory == null) {
               factory = megFactory(current.getClass());
            }

            if (factory != null && !(mob instanceof Sniffer)) {
               NavigationAbstract next = factory.apply(mob, mob.ao());
               PathfinderAbstract from = current.o();
               PathfinderAbstract to = next.o();
               to.a(from.d());
               to.b(from.e());
               to.c(from.f());
               to.d(from.g());
               next.a((Float)REF_NAV.get(current, "x"));
               next.b((Float)REF_NAV.get(current, "u"));
               REF_MOB.set(mob, "cr", next);
               retarget((PathfinderGoalSelector)REF_MOB.get(mob, "cs"), current, next);
               retarget(mob.ct, current, next);
            }
         }
      }
   }

   private static BiFunction<EntityInsentient, World, NavigationAbstract> megFactory(Class<?> type) {
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

   private static void retarget(PathfinderGoalSelector selector, NavigationAbstract from, NavigationAbstract to) {
      if (selector != null) {
         for (PathfinderGoalWrapped wrapped : selector.b()) {
            PathfinderGoal goal = wrapped.k();

            for (Field field : GOAL_NAVIGATION_FIELDS.computeIfAbsent(goal.getClass(), AsyncPathfinding::navigationFields)) {
               try {
                  if (field.get(goal) == from) {
                     field.set(goal, to);
                  }
               } catch (IllegalAccessException var11) {
                  MythicLogger.handleMinorError(var11);
               }
            }
         }
      }
   }

   private static Field[] navigationFields(Class<?> goalClass) {
      List<Field> fields = new ArrayList<>();

      for (Class<?> type = goalClass; type != null && type != Object.class; type = type.getSuperclass()) {
         for (Field field : type.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) && NavigationAbstract.class.isAssignableFrom(field.getType())) {
               field.setAccessible(true);
               fields.add(field);
            }
         }
      }

      return fields.toArray(new Field[0]);
   }
}
