package com.nexomc.nexo.api.events.furniture;

import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
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
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 $2\u00020\u00012\u00020\u0002:\u0001$B7\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e¢\u0006\u0004\b\u000f\u0010\u0010J\b\u0010\u001d\u001a\u00020\u001eH\u0016J\u0010\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u001eH\u0016J\b\u0010\"\u001a\u00020#H\u0016R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u000e\u0010\u001d\u001a\u00020\u001eX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006%"},
   d2 = {"Lcom/nexomc/nexo/api/events/furniture/NexoFurniturePlaceEvent;", "Lorg/bukkit/event/Event;", "Lorg/bukkit/event/Cancellable;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "block", "Lorg/bukkit/block/Block;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "player", "Lorg/bukkit/entity/Player;", "itemInHand", "Lorg/bukkit/inventory/ItemStack;", "hand", "Lorg/bukkit/inventory/EquipmentSlot;", "<init>", "(Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;Lorg/bukkit/block/Block;Lorg/bukkit/entity/ItemDisplay;Lorg/bukkit/entity/Player;Lorg/bukkit/inventory/ItemStack;Lorg/bukkit/inventory/EquipmentSlot;)V", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "getBlock", "()Lorg/bukkit/block/Block;", "getBaseEntity", "()Lorg/bukkit/entity/ItemDisplay;", "getPlayer", "()Lorg/bukkit/entity/Player;", "getItemInHand", "()Lorg/bukkit/inventory/ItemStack;", "getHand", "()Lorg/bukkit/inventory/EquipmentSlot;", "isCancelled", "", "setCancelled", "", "cancelled", "getHandlers", "Lorg/bukkit/event/HandlerList;", "Companion", "com.nexomc:core"}
)
public final class NexoFurniturePlaceEvent extends Event implements Cancellable {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final FurnitureMechanic mechanic;
   @NotNull
   private final Block block;
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final Player player;
   @NotNull
   private final ItemStack itemInHand;
   @NotNull
   private final EquipmentSlot hand;
   private boolean isCancelled;
   @NotNull
   private static final HandlerList handlerList = new HandlerList();

   public NexoFurniturePlaceEvent(@NotNull FurnitureMechanic mechanic, @NotNull Block block, @NotNull ItemDisplay baseEntity, @NotNull Player player, @NotNull ItemStack itemInHand, @NotNull EquipmentSlot hand) {
      super();
      this.mechanic = mechanic;
      this.block = block;
      this.baseEntity = baseEntity;
      this.player = player;
      this.itemInHand = itemInHand;
      this.hand = hand;
   }

   @NotNull
   public final FurnitureMechanic getMechanic() {
      return this.mechanic;
   }

   @NotNull
   public final Block getBlock() {
      return this.block;
   }

   @NotNull
   public final ItemDisplay getBaseEntity() {
      return this.baseEntity;
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

   public void setCancelled(boolean cancelled) {
      this.isCancelled = cancelled;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlerList;
   }

   @NotNull
   public static final HandlerList getHandlerList() {
      return Companion.getHandlerList();
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R \u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004r\u0002\b\t¢\u0006\u000e\n\u0000\u0012\u0004\b\u0006\u0010\u0003\u001a\u0004\b\u0007\u0010\b¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/api/events/furniture/NexoFurniturePlaceEvent$Companion;", "", "<init>", "()V", "handlerList", "Lorg/bukkit/event/HandlerList;", "getHandlerList$annotations", "getHandlerList", "()Lorg/bukkit/event/HandlerList;", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final HandlerList getHandlerList() {
         return NexoFurniturePlaceEvent.handlerList;
      }
   }
}
