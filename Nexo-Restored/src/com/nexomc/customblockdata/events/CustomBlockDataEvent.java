package com.nexomc.customblockdata.events;

import com.nexomc.customblockdata.CustomBlockData;
import java.util.Arrays;
import java.util.List;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0016\u0018\u0000  2\u00020\u00012\u00020\u0002:\u0002\u001f B#\b\u0004\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0001\u0010\u0007\u001a\u00020\u0001¢\u0006\u0004\b\b\u0010\tJ\b\u0010\u0014\u001a\u00020\u0015H\u0016J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0015H\u0016J\b\u0010\u001d\u001a\u00020\u001eH\u0016R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0007\u001a\u00020\u00018\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0010\u001a\u00020\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u0019\u001a\u00020\u001a8F¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u001c¨\u0006!"},
   d2 = {"Lcom/nexomc/customblockdata/events/CustomBlockDataEvent;", "Lorg/bukkit/event/Event;", "Lorg/bukkit/event/Cancellable;", "plugin", "Lorg/bukkit/plugin/Plugin;", "block", "Lorg/bukkit/block/Block;", "bukkitEvent", "<init>", "(Lorg/bukkit/plugin/Plugin;Lorg/bukkit/block/Block;Lorg/bukkit/event/Event;)V", "getPlugin", "()Lorg/bukkit/plugin/Plugin;", "getBlock", "()Lorg/bukkit/block/Block;", "getBukkitEvent", "()Lorg/bukkit/event/Event;", "customBlockData", "Lcom/nexomc/customblockdata/CustomBlockData;", "getCustomBlockData", "()Lcom/nexomc/customblockdata/CustomBlockData;", "isCancelled", "", "setCancelled", "", "cancel", "reason", "Lcom/nexomc/customblockdata/events/CustomBlockDataEvent$Reason;", "getReason", "()Lcom/nexomc/customblockdata/events/CustomBlockDataEvent$Reason;", "getHandlers", "Lorg/bukkit/event/HandlerList;", "Reason", "Companion", "custom-block-data"}
)
public class CustomBlockDataEvent extends Event implements Cancellable {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Plugin plugin;
   @NotNull
   private final Block block;
   @NotNull
   private final Event bukkitEvent;
   @NotNull
   private final CustomBlockData customBlockData;
   private boolean isCancelled;
   @NotNull
   private static final HandlerList handlerList = new HandlerList();

   protected CustomBlockDataEvent(@NotNull Plugin plugin, @NotNull Block block, @NotNull Event bukkitEvent) {
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      Intrinsics.checkNotNullParameter(block, "block");
      Intrinsics.checkNotNullParameter(bukkitEvent, "bukkitEvent");
      super();
      this.plugin = plugin;
      this.block = block;
      this.bukkitEvent = bukkitEvent;
      this.customBlockData = new CustomBlockData(this.block, this.plugin);
   }

   @NotNull
   public final Plugin getPlugin() {
      return this.plugin;
   }

   @NotNull
   public final Block getBlock() {
      return this.block;
   }

   @NotNull
   public final Event getBukkitEvent() {
      return this.bukkitEvent;
   }

   @NotNull
   public final CustomBlockData getCustomBlockData() {
      return this.customBlockData;
   }

   public boolean isCancelled() {
      return this.isCancelled;
   }

   public void setCancelled(boolean cancel) {
      this.isCancelled = cancel;
   }

   @NotNull
   public final Reason getReason() {
      for(Reason reason : CustomBlockDataEvent.Reason.getEntries()) {
         if (reason != CustomBlockDataEvent.Reason.UNKNOWN && reason.getApplicableEvents().stream().anyMatch(CustomBlockDataEvent::_get_reason_$lambda$1)) {
            return reason;
         }
      }

      return CustomBlockDataEvent.Reason.UNKNOWN;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlerList;
   }

   private static final boolean _get_reason_$lambda$0(CustomBlockDataEvent this$0, Class clazz) {
      return Intrinsics.areEqual(clazz, this$0.bukkitEvent.getClass());
   }

   private static final boolean _get_reason_$lambda$1(Function1 $tmp0, Object p0) {
      return (Boolean)$tmp0.invoke(p0);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010!\n\u0002\b\u0003\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B5\b\u0003\u0012*\u0010\u0002\u001a\u0016\u0012\u0012\b\u0001\u0012\u000e\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u0005\u0018\u00010\u00040\u0003\"\u000e\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u0005\u0018\u00010\u0004¢\u0006\u0004\b\u0006\u0010\u0007R#\u0010\u0013\u001a\u0014\u0012\u0010\u0012\u000e\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u0005\u0018\u00010\u00040\u0014¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012¨\u0006\u0017"},
      d2 = {"Lcom/nexomc/customblockdata/events/CustomBlockDataEvent$Reason;", "", "eventClasses", "", "Ljava/lang/Class;", "Lorg/bukkit/event/Event;", "<init>", "(Ljava/lang/String;I[Ljava/lang/Class;)V", "BLOCK_BREAK", "BLOCK_PLACE", "EXPLOSION", "PISTON", "BURN", "ENTITY_CHANGE_BLOCK", "FADE", "STRUCTURE_GROW", "FERTILIZE", "LEAVES_DECAY", "UNKNOWN", "applicableEvents", "", "getApplicableEvents", "()Ljava/util/List;", "custom-block-data"}
   )
   public static enum Reason {
      BLOCK_BREAK(BlockBreakEvent.class),
      BLOCK_PLACE(BlockPlaceEvent.class, BlockMultiPlaceEvent.class),
      EXPLOSION(EntityExplodeEvent.class, BlockExplodeEvent.class),
      PISTON(BlockPistonExtendEvent.class, BlockPistonRetractEvent.class),
      BURN(BlockBurnEvent.class),
      ENTITY_CHANGE_BLOCK(EntityChangeBlockEvent.class),
      FADE(BlockFadeEvent.class),
      STRUCTURE_GROW(StructureGrowEvent.class),
      FERTILIZE(BlockFertilizeEvent.class),
      /** @deprecated */
      @Deprecated
      LEAVES_DECAY(LeavesDecayEvent.class),
      UNKNOWN((Class<? extends Event>)null);

      private final List<Class<? extends Event>> applicableEvents;

      @SafeVarargs
      private Reason(Class<? extends Event>... eventClasses) {
         this.applicableEvents = Arrays.asList(eventClasses);
      }

      @NotNull
      public final List<Class<? extends Event>> getApplicableEvents() {
         return this.applicableEvents;
      }

      @NotNull
      public static EnumEntries<Reason> getEntries() {
         return EnumEntriesKt.enumEntries(values());
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/customblockdata/events/CustomBlockDataEvent$Companion;", "", "<init>", "()V", "handlerList", "Lorg/bukkit/event/HandlerList;", "getHandlerList", "()Lorg/bukkit/event/HandlerList;", "custom-block-data"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final HandlerList getHandlerList() {
         return CustomBlockDataEvent.handlerList;
      }
   }
}
