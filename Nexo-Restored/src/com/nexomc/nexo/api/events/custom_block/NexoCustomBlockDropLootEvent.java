package com.nexomc.nexo.api.events.custom_block;

import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.utils.drops.DroppedLoot;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0016\u0018\u0000 \u00172\u00020\u0001:\u0001\u0017B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\u0004\b\u000b\u0010\fJ\b\u0010\u0015\u001a\u00020\u0016H\u0016R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u0018"},
   d2 = {"Lcom/nexomc/nexo/api/events/custom_block/NexoCustomBlockDropLootEvent;", "Lorg/bukkit/event/Event;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "block", "Lorg/bukkit/block/Block;", "player", "Lorg/bukkit/entity/Player;", "loots", "", "Lcom/nexomc/nexo/utils/drops/DroppedLoot;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;Lorg/bukkit/block/Block;Lorg/bukkit/entity/Player;Ljava/util/List;)V", "getMechanic", "()Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "getBlock", "()Lorg/bukkit/block/Block;", "getPlayer", "()Lorg/bukkit/entity/Player;", "getLoots", "()Ljava/util/List;", "getHandlers", "Lorg/bukkit/event/HandlerList;", "Companion", "com.nexomc:core"}
)
public class NexoCustomBlockDropLootEvent extends Event {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final CustomBlockMechanic mechanic;
   @NotNull
   private final Block block;
   @NotNull
   private final Player player;
   @NotNull
   private final List<DroppedLoot> loots;
   @NotNull
   private static final HandlerList HANDLERS = new HandlerList();

   public NexoCustomBlockDropLootEvent(@NotNull CustomBlockMechanic mechanic, @NotNull Block block, @NotNull Player player, @NotNull List<DroppedLoot> loots) {
      super();
      this.mechanic = mechanic;
      this.block = block;
      this.player = player;
      this.loots = loots;
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

   @NotNull
   public final List<DroppedLoot> getLoots() {
      return this.loots;
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
      d2 = {"Lcom/nexomc/nexo/api/events/custom_block/NexoCustomBlockDropLootEvent$Companion;", "", "<init>", "()V", "HANDLERS", "Lorg/bukkit/event/HandlerList;", "getHandlerList", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final HandlerList getHandlerList() {
         return NexoCustomBlockDropLootEvent.HANDLERS;
      }
   }
}
