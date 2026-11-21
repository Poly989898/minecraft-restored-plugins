package com.nexomc.nexo.mechanics.furniture.states;

import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.furniture.FurnitureBaseEntity;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.UtilsKt;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b&\u0018\u0000 (2\u00020\u0001:\u0001(B?\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\t¢\u0006\u0004\b\u000b\u0010\fB\u0011\b\u0016\u0012\u0006\u0010\r\u001a\u00020\u000e¢\u0006\u0004\b\u000b\u0010\u000fJ\u000e\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cJ\u0010\u0010\u001e\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0014J\u0010\u0010\u001f\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0014J\u0010\u0010 \u001a\u00020\u001a2\u0006\u0010!\u001a\u00020\"H\u0016J\u000e\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&J\u0010\u0010'\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0013\u0010\n\u001a\u0004\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017¨\u0006)"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureState;", "", "id", "", "type", "Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureStateType;", "conditions", "", "resetAfter", "Lkotlin/time/Duration;", "nextAfter", "<init>", "(Ljava/lang/String;Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureStateType;Ljava/util/List;Lkotlin/time/Duration;Lkotlin/time/Duration;Lkotlin/jvm/internal/DefaultConstructorMarker;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getId", "()Ljava/lang/String;", "getType", "()Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureStateType;", "getConditions", "()Ljava/util/List;", "getResetAfter-FghU774", "()Lkotlin/time/Duration;", "getNextAfter-FghU774", "setState", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "unsetState", "doSetState", "doUnsetState", "stateItem", "builder", "Lcom/nexomc/nexo/items/ItemBuilder;", "canRun", "", "player", "Lorg/bukkit/entity/Player;", "scheduleTransition", "Companion", "com.nexomc:core"}
)
public abstract class FurnitureState {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final String id;
   @NotNull
   private final FurnitureStateType type;
   @NotNull
   private final List<String> conditions;
   @Nullable
   private final Duration resetAfter;
   @Nullable
   private final Duration nextAfter;
   @NotNull
   private static final AtomicLong STAMP_COUNTER = new AtomicLong();
   @NotNull
   private static final ConcurrentHashMap<UUID, Long> transitionStamps = new ConcurrentHashMap();

   protected FurnitureState(String id, FurnitureStateType type, List<String> conditions, Duration resetAfter, Duration nextAfter) {
      super();
      this.id = id;
      this.type = type;
      this.conditions = conditions;
      this.resetAfter = resetAfter;
      this.nextAfter = nextAfter;
   }

   @NotNull
   public String getId() {
      return this.id;
   }

   @NotNull
   public FurnitureStateType getType() {
      return this.type;
   }

   @NotNull
   public final List<String> getConditions() {
      return this.conditions;
   }

   @Nullable
   public final Duration getResetAfter_FghU774/* $FF was: getResetAfter-FghU774*/() {
      return this.resetAfter;
   }

   @Nullable
   public final Duration getNextAfter_FghU774/* $FF was: getNextAfter-FghU774*/() {
      return this.nextAfter;
   }

   public FurnitureState(@NotNull ConfigurationSection section) {
      String name = section.getName();
      String key = "type";
      Enum enumValue2 = EnumUtils.getEnum(FurnitureStateType.class, NexoYamlKt.stringOrNull(section, key));
      this(name, (FurnitureStateType)enumValue2, NexoYamlKt.stringListOrSingle(section, "conditions", (String)null), NexoYamlKt.durationOrNull(section, "reset_after", (DurationUnit)null), NexoYamlKt.durationOrNull(section, "next_after", (DurationUnit)null));
   }

   public final void setState(@NotNull ItemDisplay baseEntity) {
      this.doSetState(baseEntity);
      this.scheduleTransition(baseEntity);
   }

   public final void unsetState(@NotNull ItemDisplay baseEntity) {
      this.doUnsetState(baseEntity);
   }

   protected void doSetState(@NotNull ItemDisplay baseEntity) {
      FurnitureBaseEntity furnitureBaseEntity = IFurniturePacketManager.Companion.getFurnitureBaseMap().get(baseEntity.getUniqueId());
      if (furnitureBaseEntity != null) {
         furnitureBaseEntity.refreshItem(baseEntity);
      }

   }

   protected void doUnsetState(@NotNull ItemDisplay baseEntity) {
   }

   public void stateItem(@NotNull ItemBuilder builder) {
   }

   public final boolean canRun(@NotNull Player player) {
      return this.conditions.isEmpty() || FurnitureStates.Companion.evaluateConditions$com_nexomc_core(this.conditions, player);
   }

   private final void scheduleTransition(ItemDisplay baseEntity) {
      long value;
      block: {
         Duration duration = this.resetAfter;
         if (duration != null) {
            duration = UtilsKt.above(duration, Duration.box_impl(Duration.Companion.getZERO_UwyO8pc()));
            if (duration != null) {
               value = duration.unbox_impl();
               break block;
            }
         }

         duration = this.nextAfter;
         duration = duration != null ? UtilsKt.above(duration, Duration.box_impl(Duration.Companion.getZERO_UwyO8pc())) : null;
         if (duration == null) {
            return;
         }

         value = duration.unbox_impl();
      }

      long delay = value;
      boolean reset = this.resetAfter != null;
      UUID uniqueId = baseEntity.getUniqueId();
      UUID entityId = uniqueId;
      long stamp = STAMP_COUNTER.incrementAndGet();
      ((Map)transitionStamps).put(entityId, stamp);
      SchedulerUtils.INSTANCE.runAtEntityLater((Entity)baseEntity, RangesKt.coerceAtLeast(DurationUtilsKt.getInWholeTicks_LRDsOJo(delay), 1L), (a0_ -> FurnitureState.scheduleTransition$lambda$0(entityId, stamp, baseEntity, this, reset, a0_)));
   }

   private static final Unit scheduleTransition$lambda$0(UUID entityId, long stamp, ItemDisplay baseEntity, FurnitureState furnitureState, boolean reset, Entity it) {
      Long number = transitionStamps.get(entityId);
      if (number != null) {
         if (number == stamp) {
            if (!baseEntity.isValid()) {
               return Unit.INSTANCE;
            }

            String currentStateId = baseEntity.getPersistentDataContainer().get(FurnitureStates.Companion.getSTATE_KEY(), PersistentDataType.STRING);
            if (!Intrinsics.areEqual(currentStateId, furnitureState.getId())) {
               return Unit.INSTANCE;
            }

            FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
            if (furnitureMechanic == null) {
               return Unit.INSTANCE;
            }

            FurnitureMechanic mechanic = furnitureMechanic;
            if (reset) {
               FurnitureStates.Companion.resetToEmpty$com_nexomc_core(baseEntity, mechanic);
            } else {
               FurnitureStates.Companion.cycleNext$com_nexomc_core(baseEntity, mechanic);
            }

            return Unit.INSTANCE;
         }
      }

      return Unit.INSTANCE;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureState$Companion;", "", "<init>", "()V", "STAMP_COUNTER", "Ljava/util/concurrent/atomic/AtomicLong;", "transitionStamps", "Ljava/util/concurrent/ConcurrentHashMap;", "Ljava/util/UUID;", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}
