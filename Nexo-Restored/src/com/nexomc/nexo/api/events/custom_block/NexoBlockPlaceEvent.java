package com.nexomc.nexo.api.events.custom_block;

import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0016\u0018\u0000  2\u00020\u00012\u00020\u0002:\u0001 B/\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\b\u0010\u0019\u001a\u00020\u001aH\u0016J\u0010\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001aH\u0016J\b\u0010\u001e\u001a\u00020\u001fH\u0016R\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006!"},
   d2 = {"Lcom/nexomc/nexo/api/events/custom_block/NexoBlockPlaceEvent;", "Lorg/bukkit/event/Event;", "Lorg/bukkit/event/Cancellable;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "block", "Lorg/bukkit/block/Block;", "player", "Lorg/bukkit/entity/Player;", "itemInHand", "Lorg/bukkit/inventory/ItemStack;", "hand", "Lorg/bukkit/inventory/EquipmentSlot;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;Lorg/bukkit/block/Block;Lorg/bukkit/entity/Player;Lorg/bukkit/inventory/ItemStack;Lorg/bukkit/inventory/EquipmentSlot;)V", "getMechanic", "()Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "getBlock", "()Lorg/bukkit/block/Block;", "getPlayer", "()Lorg/bukkit/entity/Player;", "getItemInHand", "()Lorg/bukkit/inventory/ItemStack;", "getHand", "()Lorg/bukkit/inventory/EquipmentSlot;", "isCancelled", "", "setCancelled", "", "cancel", "getHandlers", "Lorg/bukkit/event/HandlerList;", "Companion", "com.nexomc:core"}
)
public class NexoBlockPlaceEvent extends Event implements Cancellable {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final CustomBlockMechanic mechanic;
   @NotNull
   private final Block block;
   @NotNull
   private final Player player;
   @NotNull
   private final ItemStack itemInHand;
   @NotNull
   private final EquipmentSlot hand;
   private boolean isCancelled;
   @NotNull
   private static final HandlerList HANDLERS = new HandlerList();

   public NexoBlockPlaceEvent(@NotNull CustomBlockMechanic mechanic, @NotNull Block block, @NotNull Player player, @NotNull ItemStack itemInHand, @NotNull EquipmentSlot hand) {
      super();
      this.mechanic = mechanic;
      this.block = block;
      this.player = player;
      this.itemInHand = itemInHand;
      this.hand = hand;
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
   public final ItemStack getItemInHand() {
      return this.itemInHand;
   }

   @NotNull
   public final EquipmentSlot getHand() {
      return this.hand;
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
      d2 = {"Lcom/nexomc/nexo/api/events/custom_block/NexoBlockPlaceEvent$Companion;", "", "<init>", "()V", "HANDLERS", "Lorg/bukkit/event/HandlerList;", "getHandlerList", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final HandlerList getHandlerList() {
         return NexoBlockPlaceEvent.HANDLERS;
      }
   }
}
