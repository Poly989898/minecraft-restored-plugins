package com.nexomc.nexo.utils.breaker;

import com.nexomc.libs.folialib.impl.PlatformScheduler;
import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureDamageEvent;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.blocksounds.BlockSounds;
import com.nexomc.nexo.utils.drops.Drop;
import com.nexomc.protectionlib.ProtectionLib;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001:\u0001\u001bB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011J\u000e\u0010\u0012\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ(\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0010\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0017\u001a\u00020\u0006H\u0002R\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/utils/breaker/FurnitureBreakerManager;", "", "<init>", "()V", "activeBreakerDataMap", "Ljava/util/concurrent/ConcurrentHashMap;", "Ljava/util/UUID;", "Lcom/nexomc/nexo/utils/breaker/FurnitureBreakerManager$ActiveBreakerData;", "startFurnitureBreak", "", "player", "Lorg/bukkit/entity/Player;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "block", "Lorg/bukkit/block/Block;", "stopFurnitureBreak", "createBreakScheduler", "Lcom/nexomc/libs/folialib/wrapper/task/WrappedTask;", "blockBreakTime", "", "breakerUUID", "createBreakSoundScheduler", "SOURCE_RANDOM", "Ljava/util/Random;", "ActiveBreakerData", "com.nexomc:core"}
)
public final class FurnitureBreakerManager {
   @NotNull
   public static final FurnitureBreakerManager INSTANCE = new FurnitureBreakerManager();
   @NotNull
   private static final ConcurrentHashMap<UUID, ActiveBreakerData> activeBreakerDataMap = new ConcurrentHashMap();
   @NotNull
   private static final Random SOURCE_RANDOM = new Random();

   private FurnitureBreakerManager() {
   }

   public final void startFurnitureBreak(@NotNull Player player, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Block block) {
      this.stopFurnitureBreak(player);
      if (EventUtils.INSTANCE.call(new NexoFurnitureDamageEvent(mechanic, baseEntity, player))) {
         int breakTime = mechanic.getBreakable().breakTime(player);
         Map map = (Map)activeBreakerDataMap;
         UUID uniqueId = player.getUniqueId();
         Location location = block.getLocation();
         UUID uuid = baseEntity.getUniqueId();
         double value = (double)breakTime;
         UUID uuid2 = player.getUniqueId();
         WrappedTask wrappedTask = this.createBreakScheduler(value, uuid2, baseEntity, mechanic);
         uuid2 = player.getUniqueId();
         ActiveBreakerData activeBreakerData = new ActiveBreakerData(player, location, uuid, mechanic, breakTime, 0, wrappedTask, this.createBreakSoundScheduler(uuid2));
         map.put(uniqueId, activeBreakerData);
      }
   }

   public final void stopFurnitureBreak(@NotNull Player player) {
      ActiveBreakerData activeBreakerData2 = (ActiveBreakerData)activeBreakerDataMap.get(player.getUniqueId());
      if (activeBreakerData2 != null) {
         ActiveBreakerData activeBreakerData = activeBreakerData2;
         activeBreakerData.cancelTasks();
         activeBreakerDataMap.remove(player.getUniqueId());
         if (player.isOnline()) {
            NMSHandlers.handler().playerUtils().removeMiningEffect(player);
            activeBreakerData.resetProgress();
            activeBreakerData.sendBreakProgress();
         }
      }
   }

   private final WrappedTask createBreakScheduler(double blockBreakTime, UUID breakerUUID, ItemDisplay baseEntity, FurnitureMechanic mechanic) {
      PlatformScheduler foliaScheduler = SchedulerUtils.INSTANCE.getFoliaScheduler();
      ActiveBreakerData activeBreakerData = (ActiveBreakerData)activeBreakerDataMap.get(breakerUUID);
      WrappedTask wrappedTask = foliaScheduler.runAtLocationTimer(activeBreakerData != null ? activeBreakerData.getLocation() : null, (() -> FurnitureBreakerManager.createBreakScheduler$lambda$0(breakerUUID, mechanic, blockBreakTime, baseEntity)), 1L, 1L);
      return wrappedTask;
   }

   private final WrappedTask createBreakSoundScheduler(UUID breakerUUID) {
      PlatformScheduler foliaScheduler = SchedulerUtils.INSTANCE.getFoliaScheduler();
      ActiveBreakerData activeBreakerData = (ActiveBreakerData)activeBreakerDataMap.get(breakerUUID);
      WrappedTask wrappedTask = foliaScheduler.runAtLocationTimer(activeBreakerData != null ? activeBreakerData.getLocation() : null, (() -> FurnitureBreakerManager.createBreakSoundScheduler$lambda$0(breakerUUID)), 0L, 4L);
      return wrappedTask;
   }

   private static final boolean createBreakScheduler$lambda$0$0(UUID breakerUUID, ActiveBreakerData activeBreakerData, ActiveBreakerData it) {
      return !Intrinsics.areEqual(it.getBreaker().getUniqueId(), breakerUUID) && Intrinsics.areEqual(it.getLocation(), activeBreakerData.getLocation());
   }

   private static final void createBreakScheduler$lambda$0(UUID breakerUUID, FurnitureMechanic mechanic, double blockBreakTime, ItemDisplay baseEntity) {
      ActiveBreakerData activeBreakerData2 = (ActiveBreakerData)activeBreakerDataMap.get(breakerUUID);
      if (activeBreakerData2 != null) {
         ActiveBreakerData activeBreakerData = activeBreakerData2;
         Player player = activeBreakerData.getBreaker();
         Block block2 = activeBreakerData.getLocation().getBlock();
         Block block = block2;
         if (!player.isOnline()) {
            INSTANCE.stopFurnitureBreak(player);
         } else if (!Intrinsics.areEqual(activeBreakerData.getMechanic(), mechanic)) {
            INSTANCE.stopFurnitureBreak(player);
         } else if (!Intrinsics.areEqual(mechanic.getItemID(), activeBreakerData.getMechanic().getItemID())) {
            INSTANCE.stopFurnitureBreak(player);
         } else if (!activeBreakerData.isBroken()) {
            activeBreakerData.addBreakTimeProgress(blockBreakTime / (double)mechanic.getBreakable().breakTime(player));
            activeBreakerData.sendBreakProgress();
         } else {
            if (EventUtils.INSTANCE.call(new NexoFurnitureBreakEvent(mechanic, baseEntity, player))) {
               Location location = block.getLocation();
               if (ProtectionLib.canBreak(player, location)) {
                  activeBreakerData.resetProgress();
                  activeBreakerData.sendBreakProgress();
                  Collection collection = activeBreakerDataMap.values();
                  Sequence sequence = SequencesKt.filter(CollectionsKt.asSequence((Iterable)collection), (a0_ -> FurnitureBreakerManager.createBreakScheduler$lambda$0$0(breakerUUID, activeBreakerData, (ActiveBreakerData)a0_)));

                  for(Object element : kotlin.sequences.SequencesKt.asIterable(sequence)) {
                     ActiveBreakerData it = (ActiveBreakerData)element;
                     int value = 0;
                     INSTANCE.stopFurnitureBreak(it.getBreaker());
                  }

                  NexoFurniture.remove((Entity)((Entity)baseEntity), player, (Drop)null);
                  activeBreakerData.cancelTasks();
                  activeBreakerDataMap.remove(breakerUUID);
                  return;
               }
            }

            INSTANCE.stopFurnitureBreak(player);
         }

      }
   }

   private static final void createBreakSoundScheduler$lambda$0(UUID breakerUUID) {
      ActiveBreakerData activeBreakerData2 = (ActiveBreakerData)activeBreakerDataMap.get(breakerUUID);
      if (activeBreakerData2 != null) {
         ActiveBreakerData activeBreakerData = activeBreakerData2;
         Player player = activeBreakerData.getBreaker();
         Entity entity = Bukkit.getEntity(activeBreakerData.getBaseUUID());
         if (entity == null) {
            INSTANCE.stopFurnitureBreak(player);
         } else {
            Entity baseEntity = entity;
            FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic(baseEntity);
            if (furnitureMechanic == null) {
               INSTANCE.stopFurnitureBreak(player);
            } else {
               FurnitureMechanic mechanic = furnitureMechanic;
               BlockSounds hitSound = mechanic.getBlockSounds();
               if (player.isOnline() && Intrinsics.areEqual(mechanic.getItemID(), activeBreakerData.getMechanic().getItemID())) {
                  if ((hitSound != null ? hitSound.getHitSound() : null) == null) {
                     WrappedTask breakerSoundTask = activeBreakerData.getBreakerSoundTask();
                     breakerSoundTask.cancel();
                  } else {
                     Location location = baseEntity.getLocation();
                     BlockHelpers.playCustomBlockSound(location, hitSound.getHitSound(), hitSound.getHitVolume(), hitSound.getHitPitch());
                  }
               } else {
                  WrappedTask wrappedTask = activeBreakerData.getBreakerSoundTask();
                  wrappedTask.cancel();
               }

            }
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u0006\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\u0018\u0000 (2\u00020\u0001:\u0001(BK\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\u000b\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u000e\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\u000e¢\u0006\u0004\b\u0010\u0010\u0011J\u0006\u0010\n\u001a\u00020\u000bJ\u0006\u0010\f\u001a\u00020\u001dJ\u000e\u0010\f\u001a\u00020\u001e2\u0006\u0010\f\u001a\u00020\u001dJ\u000e\u0010\u001f\u001a\u00020\u001e2\u0006\u0010\f\u001a\u00020\u001dJ\u0006\u0010 \u001a\u00020\u001eJ\u0006\u0010!\u001a\u00020\"J\u0006\u0010&\u001a\u00020\u001eJ\u0006\u0010'\u001a\u00020\u001eR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u000e\u0010\u001c\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u001dX\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010#\u001a\u00020$8F¢\u0006\u0006\u001a\u0004\b#\u0010%¨\u0006)"},
      d2 = {"Lcom/nexomc/nexo/utils/breaker/FurnitureBreakerManager$ActiveBreakerData;", "", "breaker", "Lorg/bukkit/entity/Player;", "location", "Lorg/bukkit/Location;", "baseUUID", "Ljava/util/UUID;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "totalBreakTime", "", "breakTimeProgress", "breakerTask", "Lcom/nexomc/libs/folialib/wrapper/task/WrappedTask;", "breakerSoundTask", "<init>", "(Lorg/bukkit/entity/Player;Lorg/bukkit/Location;Ljava/util/UUID;Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;IILcom/nexomc/libs/folialib/wrapper/task/WrappedTask;Lcom/nexomc/libs/folialib/wrapper/task/WrappedTask;)V", "getBreaker", "()Lorg/bukkit/entity/Player;", "getLocation", "()Lorg/bukkit/Location;", "getBaseUUID", "()Ljava/util/UUID;", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "getBreakerSoundTask", "()Lcom/nexomc/libs/folialib/wrapper/task/WrappedTask;", "sourceId", "", "", "addBreakTimeProgress", "sendBreakProgress", "calculateDamage", "", "isBroken", "", "()Z", "resetProgress", "cancelTasks", "Companion", "com.nexomc:core"}
   )
   public static final class ActiveBreakerData {
      @NotNull
      public static final Companion Companion = new Companion();
      @NotNull
      private final Player breaker;
      @NotNull
      private final Location location;
      @NotNull
      private final UUID baseUUID;
      @NotNull
      private final FurnitureMechanic mechanic;
      private final int totalBreakTime;
      @Nullable
      private final WrappedTask breakerTask;
      @Nullable
      private final WrappedTask breakerSoundTask;
      private final int sourceId;
      private double breakTimeProgress;
      public static final float MAX_DAMAGE = 1.0F;
      public static final float MIN_DAMAGE = 0.0F;

      public ActiveBreakerData(@NotNull Player breaker, @NotNull Location location, @NotNull UUID baseUUID, @NotNull FurnitureMechanic mechanic, int totalBreakTime, int breakTimeProgress, @Nullable WrappedTask breakerTask, @Nullable WrappedTask breakerSoundTask) {
         super();
         this.breaker = breaker;
         this.location = location;
         this.baseUUID = baseUUID;
         this.mechanic = mechanic;
         this.totalBreakTime = totalBreakTime;
         this.breakerTask = breakerTask;
         this.breakerSoundTask = breakerSoundTask;
         this.sourceId = FurnitureBreakerManager.SOURCE_RANDOM.nextInt();
         this.breakTimeProgress = (double)breakTimeProgress;
      }

      @NotNull
      public final Player getBreaker() {
         return this.breaker;
      }

      @NotNull
      public final Location getLocation() {
         return this.location;
      }

      @NotNull
      public final UUID getBaseUUID() {
         return this.baseUUID;
      }

      @NotNull
      public final FurnitureMechanic getMechanic() {
         return this.mechanic;
      }

      @Nullable
      public final WrappedTask getBreakerSoundTask() {
         return this.breakerSoundTask;
      }

      public final int totalBreakTime() {
         return this.totalBreakTime;
      }

      public final double breakTimeProgress() {
         return this.breakTimeProgress;
      }

      public final void breakTimeProgress(double breakTimeProgress) {
         this.breakTimeProgress = breakTimeProgress;
      }

      public final void addBreakTimeProgress(double breakTimeProgress) {
         this.breakTimeProgress = Math.min(this.breakTimeProgress + breakTimeProgress, (double)this.totalBreakTime);
      }

      public final void sendBreakProgress() {
         this.breaker.sendBlockDamage(this.location, this.calculateDamage(), this.sourceId);
      }

      public final float calculateDamage() {
         double percentage = this.breakTimeProgress / (double)this.totalBreakTime;
         return RangesKt.coerceIn((float)((double)1.0F * percentage), 0.0F, 1.0F);
      }

      public final boolean isBroken() {
         return this.breakTimeProgress >= (double)this.totalBreakTime;
      }

      public final void resetProgress() {
         this.breakTimeProgress = (double)0.0F;
      }

      public final void cancelTasks() {
         WrappedTask wrappedTask = this.breakerTask;
         if (wrappedTask != null) {
            wrappedTask.cancel();
         }

         wrappedTask = this.breakerSoundTask;
         if (wrappedTask != null) {
            wrappedTask.cancel();
         }

      }

      @Metadata(
         mv = {2, 4, 0},
         k = 1,
         xi = 48,
         d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0007"},
         d2 = {"Lcom/nexomc/nexo/utils/breaker/FurnitureBreakerManager$ActiveBreakerData$Companion;", "", "<init>", "()V", "MAX_DAMAGE", "", "MIN_DAMAGE", "com.nexomc:core"}
      )
      public static final class Companion {
         private Companion() {
         }
      }
   }
}
