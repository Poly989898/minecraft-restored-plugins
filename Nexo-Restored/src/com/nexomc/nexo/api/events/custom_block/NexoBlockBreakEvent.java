package com.nexomc.nexo.api.events.custom_block;

import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import com.nexomc.nexo.utils.drops.Drop;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0016\u0018\u0000  2\u00020\u00012\u00020\u0002:\u0001 B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nB)\b\u0016\u0012\u0006\u0010\u0003\u001a\u00020\u000b\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\f\u001a\u00020\r¢\u0006\u0004\b\t\u0010\u000eJ\b\u0010\u0019\u001a\u00020\u001aH\u0016J\u0010\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001aH\u0016J\b\u0010\u001e\u001a\u00020\u001fH\u0016R\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u001a\u0010\f\u001a\u00020\rX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006!"},
   d2 = {"Lcom/nexomc/nexo/api/events/custom_block/NexoBlockBreakEvent;", "Lorg/bukkit/event/Event;", "Lorg/bukkit/event/Cancellable;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "block", "Lorg/bukkit/block/Block;", "player", "Lorg/bukkit/entity/Player;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;Lorg/bukkit/block/Block;Lorg/bukkit/entity/Player;)V", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "drop", "Lcom/nexomc/nexo/utils/drops/Drop;", "(Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;Lorg/bukkit/block/Block;Lorg/bukkit/entity/Player;Lcom/nexomc/nexo/utils/drops/Drop;)V", "getMechanic", "()Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "getBlock", "()Lorg/bukkit/block/Block;", "getPlayer", "()Lorg/bukkit/entity/Player;", "getDrop", "()Lcom/nexomc/nexo/utils/drops/Drop;", "setDrop", "(Lcom/nexomc/nexo/utils/drops/Drop;)V", "isCancelled", "", "setCancelled", "", "cancel", "getHandlers", "Lorg/bukkit/event/HandlerList;", "Companion", "com.nexomc:core"}
)
public class NexoBlockBreakEvent extends Event implements Cancellable {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final CustomBlockMechanic mechanic;
   @NotNull
   private final Block block;
   @NotNull
   private final Player player;
   public Drop drop;
   private boolean isCancelled;
   @NotNull
   private static final HandlerList HANDLERS = new HandlerList();

   public NexoBlockBreakEvent(@NotNull CustomBlockMechanic mechanic, @NotNull Block block, @NotNull Player player) {
      super();
      this.mechanic = mechanic;
      this.block = block;
      this.player = player;
   }

   @NotNull
   public CustomBlockMechanic getMechanic() {
      return this.mechanic;
   }

   @NotNull
   public final Block getBlock() {
      return this.block;
   }

   @NotNull
   public final Player getPlayer() {
      return this.player;
   }

   public NexoBlockBreakEvent(@NotNull NoteBlockMechanic mechanic, @NotNull Block block, @NotNull Player player, @NotNull Drop drop) {
      this(mechanic, block, player);
      this.setDrop(drop);
   }

   @NotNull
   public final Drop getDrop() {
      Drop drop2 = this.drop;
      if (drop2 != null) {
         return drop2;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("drop");
         return null;
      }
   }

   public final void setDrop(@NotNull Drop drop2) {
      this.drop = drop2;
   }

   public boolean isCancelled() {
      return this.isCancelled;
   }

   public void setCancelled(boolean cancel) {
      this.isCancelled = cancel;
   }

   @NotNull
   public HandlerList getHandlers() {
      return HANDLERS;
   }

   @JvmStatic
   @NotNull
   public static final HandlerList getHandlerList() {
      return Companion.getHandlerList();
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\u0006\u001a\u00020\u0005H\u0007b\u0002\b\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/api/events/custom_block/NexoBlockBreakEvent$Companion;", "", "<init>", "()V", "HANDLERS", "Lorg/bukkit/event/HandlerList;", "getHandlerList", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final HandlerList getHandlerList() {
         return NexoBlockBreakEvent.HANDLERS;
      }
   }
}
