package com.nexomc.nexo.utils;
import com.nexomc.nexo.utils.VersionUtil;
import java.util.Iterator;
import java.util.function.Consumer;

import com.nexomc.libs.folialib.impl.PlatformScheduler;
import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.NexoPlugin;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J/\u0010\b\u001a\u00020\t2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\t0\u000bH\u0007b\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\b(\r¢\u0006\u0002\b\rJ.\u0010\b\u001a\u00020\t\"\n\b\u0000\u0010\u0010\u0018\u0001*\u00020\f2\u0014\b\b\u0010\n\u001a\u000e\u0012\u0004\u0012\u0002H\u0010\u0012\u0004\u0012\u00020\t0\u000bH\u0086\bø\u0001\u0000J(\u0010\u0011\u001a\u00020\t2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\t0\u000bJ*\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\u00192\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\t0\u000bJ*\u0010\u001a\u001a\u00020\t2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0018\u001a\u00020\u00192\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u001c\u0012\u0004\u0012\u00020\t0\u000bJ\u001c\u0010\u001d\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\u00192\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u001eJ$\u0010\u001f\u001a\u00020 2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010!\u001a\u00020\u00192\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u001eJ\u001e\u0010\"\u001a\u00020\t2\b\b\u0002\u0010\u0018\u001a\u00020\u00192\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u001eJ\u0018\u0010#\u001a\u0006\u0012\u0002\b\u00030$2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u001eJ\u0014\u0010%\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u001eJ\u001c\u0010&\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\u00192\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u001eJ$\u0010'\u001a\u00020(2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010!\u001a\u00020\u00192\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u001eJ\u0014\u0010)\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u001eR\u0011\u0010\u0004\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006*"},
   d2 = {"Lcom/nexomc/nexo/utils/SchedulerUtils;", "", "<init>", "()V", "foliaScheduler", "Lcom/nexomc/libs/folialib/impl/PlatformScheduler;", "getFoliaScheduler", "()Lcom/nexomc/libs/folialib/impl/PlatformScheduler;", "runAtWorldEntities", "", "task", "Lkotlin/Function1;", "Lorg/bukkit/entity/Entity;", "_runAtWorldEntities", "Lkotlin/jvm/JvmName;", "name", "T", "runAtWorldTileStates", "predicate", "Ljava/util/function/Predicate;", "Lorg/bukkit/block/Block;", "Lorg/bukkit/block/BlockState;", "runAtEntityLater", "entity", "delay", "", "runAtLocationLater", "location", "Lorg/bukkit/Location;", "runTaskLater", "Lkotlin/Function0;", "runTaskTimer", "Lorg/bukkit/scheduler/BukkitTask;", "period", "syncDelayedTask", "callSyncMethod", "Ljava/util/concurrent/Future;", "runTaskAsync", "runTaskAsyncLater", "runTaskAsyncTimer", "Lcom/nexomc/libs/folialib/wrapper/task/WrappedTask;", "runTask", "com.nexomc:core"}
)
public final class SchedulerUtils {
   @NotNull
   public static final SchedulerUtils INSTANCE = new SchedulerUtils();

   private SchedulerUtils() {
   }

   @NotNull
   public final PlatformScheduler getFoliaScheduler() {
      PlatformScheduler scheduler = NexoPlugin.Companion.instance().getFoliaLib().getScheduler();
      return scheduler;
   }

   @JvmName(
      name = "_runAtWorldEntities"
   )
   public final void _runAtWorldEntities(@NotNull Function1<? super Entity, Unit> task) {
      Function1 function1 = task;
      List worlds = Bukkit.getWorlds();
      Iterable iterable = (Iterable)worlds;

      for(Object element : iterable) {
         World world = (World)element;
         int value = 0;
         if (VersionUtil.INSTANCE.isFoliaServer()) {
            Chunk[] loadedChunks = world.getLoadedChunks();
            Object[] objs = loadedChunks;
            int value2 = 0;

            for(int i = objs.length; value2 < i; ++value2) {
               Object obj = objs[value2];
               Chunk chunk = (Chunk)obj;
               int value3 = 0;
               INSTANCE.getFoliaScheduler().runAtLocation(new Location(world, (double)chunk.getX() * (double)16.0F, (double)100.0F, (double)chunk.getZ() * (double)16.0F), new SchedulerUtils$runAtWorldEntities$$inlined$runAtWorldEntities$1(chunk, function1));
            }
         } else {
            Collection entitiesByClass = world.getEntitiesByClass(Entity.class);
            Iterable iterable2 = (Iterable)entitiesByClass;

            for(Object obj2 : iterable2) {
               Entity p0 = (Entity)obj2;
               int value4 = 0;
               function1.invoke(p0);
            }
         }
      }

   }

   public final void runAtWorldTileStates(@NotNull Predicate<Block> predicate, @NotNull Function1<? super BlockState, Unit> task) {
      List worlds = Bukkit.getWorlds();
      Iterable iterable = (Iterable)worlds;

      for(Object element : iterable) {
         World world = (World)element;
         int value = 0;
         Chunk[] loadedChunks = world.getLoadedChunks();
         Object[] objs = loadedChunks;
         int value2 = 0;

         for(int i = objs.length; value2 < i; ++value2) {
            Object obj = objs[value2];
            Chunk chunk = (Chunk)obj;
            int value3 = 0;
            if (VersionUtil.INSTANCE.isFoliaServer()) {
               INSTANCE.getFoliaScheduler().runAtLocation(new Location(world, (double)chunk.getX() * (double)16.0F, (double)100.0F, (double)chunk.getZ() * (double)16.0F), (a0_ -> SchedulerUtils.runAtWorldTileStates$lambda$0$0$0(chunk, predicate, task, (WrappedTask)a0_)));
            } else {
               BlockState[] tileEntities = chunk.getTileEntities(false);
               Object[] objs2 = tileEntities;
               int value4 = 0;

               for(int j = objs2.length; value4 < j; ++value4) {
                  Object obj2 = objs2[value4];
                  BlockState p0 = (BlockState)obj2;
                  int value5 = 0;
                  task.invoke(p0);
               }
            }
         }
      }

   }

   public final void runAtEntityLater(@NotNull Entity entity, long delay, @NotNull Function1<? super Entity, Unit> task) {
      this.getFoliaScheduler().runAtEntityLater(entity, (() -> SchedulerUtils.runAtEntityLater$lambda$0(task, entity)), delay);
   }

   public final void runAtLocationLater(@NotNull Location location, long delay, @NotNull Function1<? super Location, Unit> task) {
      this.getFoliaScheduler().runAtLocationLater(location, (() -> SchedulerUtils.runAtLocationLater$lambda$0(task, location)), delay);
   }

   public final void runTaskLater(long delay, @NotNull Function0<Unit> task) {
        Intrinsics.checkNotNullParameter(task, (String)"task");
        if (!NexoPlugin.Companion.instance().isEnabled()) {
            task.invoke();
            return;
        }
        this.getFoliaScheduler().runLater(() -> SchedulerUtils.runTaskLater$lambda$0(task), delay);
    }

   @NotNull
   public final BukkitTask runTaskTimer(long delay, long period, @NotNull Function0<Unit> task) {
        Intrinsics.checkNotNullParameter(task, (String)"task");
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer((Plugin)NexoPlugin.Companion.instance(), () -> SchedulerUtils.runTaskTimer$lambda$0(task), delay, period);
        Intrinsics.checkNotNullExpressionValue((Object)bukkitTask, (String)"runTaskTimer(...)");
        return bukkitTask;
    }

   public final void syncDelayedTask(long delay, @NotNull Function0<Unit> task) {
        Intrinsics.checkNotNullParameter(task, (String)"task");
        if (!NexoPlugin.Companion.instance().isEnabled()) {
            task.invoke();
            return;
        }
        this.getFoliaScheduler().runLater(() -> SchedulerUtils.syncDelayedTask$lambda$0(task), delay);
    }

   @NotNull
   public final Future<?> callSyncMethod(@NotNull Function0<Unit> task) {
      CompletableFuture completableFuture = this.getFoliaScheduler().runNextTick((a0_ -> SchedulerUtils.callSyncMethod$lambda$0(task, (WrappedTask)a0_)));
      return (Future)completableFuture;
   }

   public final void runTaskAsync(@NotNull Function0<Unit> task) {
        Intrinsics.checkNotNullParameter(task, (String)"task");
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)NexoPlugin.Companion.instance(), () -> SchedulerUtils.runTaskAsync$lambda$0(task));
    }

   public final void runTaskAsyncLater(long delay, @NotNull Function0<Unit> task) {
        Intrinsics.checkNotNullParameter(task, (String)"task");
        Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)NexoPlugin.Companion.instance(), () -> SchedulerUtils.runTaskAsyncLater$lambda$0(task), delay);
    }

   @NotNull
   public final WrappedTask runTaskAsyncTimer(long delay, long period, @NotNull Function0<Unit> task) {
      WrappedTask wrappedTask = this.getFoliaScheduler().runTimerAsync((() -> SchedulerUtils.runTaskAsyncTimer$lambda$0(task)), delay, period);
      return wrappedTask;
   }

   public final void runTask(@NotNull Function0<Unit> task) {
        Intrinsics.checkNotNullParameter(task, (String)"task");
        Bukkit.getScheduler().runTask((Plugin)NexoPlugin.Companion.instance(), () -> SchedulerUtils.runTask$lambda$0(task));
    }

   private static final void runAtWorldTileStates$lambda$0$0$0(Chunk chunk, Predicate predicate, Function1 task, WrappedTask it) {
      Collection tileEntities = chunk.getTileEntities(predicate, false);
      Iterable iterable = (Iterable)tileEntities;

      for(Object element : iterable) {
         BlockState p0 = (BlockState)element;
         int value = 0;
         task.invoke(p0);
      }

   }

   private static final void runAtEntityLater$lambda$0(Function1 task, Entity entity) {
      task.invoke(entity);
   }

   private static final void runAtLocationLater$lambda$0(Function1 task, Location location) {
      task.invoke(location);
   }

   private static final void runTaskLater$lambda$0(Function0 tmp0) {
      tmp0.invoke();
   }

   private static final void runTaskTimer$lambda$0(Function0 tmp0) {
      tmp0.invoke();
   }

   private static final void syncDelayedTask$lambda$0(Function0 tmp0) {
      tmp0.invoke();
   }

   private static final void callSyncMethod$lambda$0(Function0 task, WrappedTask it) {
      task.invoke();
   }

   private static final void runTaskAsync$lambda$0(Function0 tmp0) {
      tmp0.invoke();
   }

   private static final void runTaskAsyncLater$lambda$0(Function0 tmp0) {
      tmp0.invoke();
   }

   private static final void runTaskAsyncTimer$lambda$0(Function0 task) {
      task.invoke();
   }

   private static final void runTask$lambda$0(Function0 tmp0) {
      tmp0.invoke();
   }
}
