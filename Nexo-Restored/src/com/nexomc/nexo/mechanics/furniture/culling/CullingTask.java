package com.nexomc.nexo.mechanics.furniture.culling;

import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00112\u00020\u0001:\u0001\u0011B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\b\u001a\u00020\tH\u0016J$\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\f2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u000eH\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0012"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/culling/CullingTask;", "Ljava/lang/Runnable;", "manager", "Lcom/nexomc/nexo/mechanics/furniture/culling/FurnitureCullingManager;", "<init>", "(Lcom/nexomc/nexo/mechanics/furniture/culling/FurnitureCullingManager;)V", "getManager", "()Lcom/nexomc/nexo/mechanics/furniture/culling/FurnitureCullingManager;", "run", "", "processPlayer", "player", "Lorg/bukkit/entity/Player;", "furnitureMap", "Ljava/util/concurrent/ConcurrentHashMap;", "Ljava/util/UUID;", "Lcom/nexomc/nexo/mechanics/furniture/culling/FurnitureCullingManager$TrackedFurniture;", "Companion", "com.nexomc:core"}
)
public final class CullingTask implements Runnable {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final FurnitureCullingManager manager;
   private static volatile boolean requiresTickThread;

   public CullingTask(@NotNull FurnitureCullingManager manager) {
      super();
      this.manager = manager;
   }

   @NotNull
   public final FurnitureCullingManager getManager() {
      return this.manager;
   }

   public void run() {
      if (this.manager.getEnabled()) {
         long start = System.nanoTime();
         Map trackedEntities = (Map)this.manager.getTrackedEntities();

         for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)trackedEntities.entrySet()) {
            int value = 0;
            UUID playerUUID = (UUID)element.getKey();
            ConcurrentHashMap furnitureMap = (ConcurrentHashMap)element.getValue();
            Player player2 = Bukkit.getPlayer(playerUUID);
            if (player2 != null) {
               Player player = player2;
               if (!VersionUtil.INSTANCE.isFoliaServer() && !requiresTickThread) {
                  try {
                     this.processPlayer(player, furnitureMap);
                  } catch (Exception e) {
                     if (!Companion.isThreadCheckFailure(e)) {
                        throw e;
                     }

                     requiresTickThread = true;
                     Logs.logWarn("Server does not allow reading the world async, furniture-culling will run on the tick-thread instead", false);
                  }
               } else {
                  SchedulerUtils.INSTANCE.getFoliaScheduler().runAtEntity((Entity)player, (a0_ -> CullingTask.run$lambda$0$0(this, player, furnitureMap, (WrappedTask)a0_)));
               }
            }
         }

         this.manager.setLastCullTime$com_nexomc_core(System.nanoTime() - start);
      }
   }

   private final void processPlayer(Player player, ConcurrentHashMap<UUID, FurnitureCullingManager.TrackedFurniture> furnitureMap) {
      Location eyeLocation = player.getEyeLocation();
      Location playerEye = eyeLocation;
      Collection collection = furnitureMap.values();
      Iterable iterable = (Iterable)collection;

      for(Object element : iterable) {
         FurnitureCullingManager.TrackedFurniture tracked = (FurnitureCullingManager.TrackedFurniture)element;
         int value = 0;
         if (tracked.getEntity().isValid() && Intrinsics.areEqual(player.getWorld().getUID(), tracked.getEntity().getWorld().getUID())) {
            if (!tracked.getMechanic().getProperties().getCullable().toBoolean(this.manager.getForceCulling())) {
               FurnitureCullingManager furnitureCullingManager = this.manager;
               furnitureCullingManager.ensureVisible(player, tracked);
            } else {
               FurnitureCullingManager furnitureCullingManager2 = this.manager;
               Pair pair = furnitureCullingManager2.effectiveDimensions(tracked);
               double checkWidth = ((Number)pair.component1()).doubleValue();
               double checkHeight = ((Number)pair.component2()).doubleValue();
               if (checkWidth <= (double)0.0F && checkHeight <= (double)0.0F) {
                  this.manager.ensureVisible(player, tracked);
               } else if (this.manager.getIgnoreWidth() > 0.0F && checkWidth < (double)this.manager.getIgnoreWidth()) {
                  this.manager.ensureVisible(player, tracked);
               } else if (this.manager.getIgnoreHeight() > 0.0F && checkHeight < (double)this.manager.getIgnoreHeight()) {
                  this.manager.ensureVisible(player, tracked);
               } else {
                  double distSquared = playerEye.distanceSquared(tracked.getEntity().getLocation());
                  if (((Number)this.manager.getCullingDistanceSquared().getStart()).floatValue() > 0.0F && distSquared < (double)((Number)this.manager.getCullingDistanceSquared().getEndInclusive()).floatValue()) {
                     this.manager.ensureVisible(player, tracked);
                  } else if (distSquared > (double)((Number)this.manager.getCullingDistanceSquared().getEndInclusive()).floatValue()) {
                     this.manager.ensureVisible(player, tracked);
                  } else {
                     furnitureCullingManager2 = this.manager;
                     Location location = tracked.getEntity().getLocation();
                     boolean isOccluded = furnitureCullingManager2.isOccluded(playerEye, location, checkWidth, checkHeight);
                     if (isOccluded) {
                        this.manager.ensureHidden(player, tracked);
                     } else {
                        this.manager.ensureVisible(player, tracked);
                     }
                  }
               }
            }
         }
      }

   }

   private static final void run$lambda$0$0(CullingTask cullingTask, Player player, ConcurrentHashMap furnitureMap, WrappedTask it) {
      cullingTask.processPlayer(player, furnitureMap);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0006\u001a\u00020\u0005*\u00060\u0007j\u0002`\bH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\t"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/culling/CullingTask$Companion;", "", "<init>", "()V", "requiresTickThread", "", "isThreadCheckFailure", "Ljava/lang/Exception;", "Lkotlin/Exception;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      private final boolean isThreadCheckFailure(Exception exception) {
         boolean flag;
         String message = exception.getMessage();
         boolean flag2;
         if (!(message != null ? StringsKt.contains((CharSequence)message, (CharSequence)"asynchronously", false) : false)) {
            StackTraceElement[] stackTrace = exception.getStackTrace();
            Object[] objs = stackTrace;
            int value = 0;
            int value2 = objs.length;

            while(true) {
               if (value >= value2) {
                  flag = false;
                  break;
               }

               Object element = objs[value];
               StackTraceElement it = (StackTraceElement)element;
               int value3 = 0;
               String className = it.getClassName();
               if (Intrinsics.areEqual(StringsKt.substringAfterLast(className, '.', className), "TickThread")) {
                  flag = true;
                  break;
               }

               ++value;
            }

            if (!flag) {
               flag2 = false;
               return flag2;
            }
         }

         flag2 = true;
         return flag2;
      }
   }
}
