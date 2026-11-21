package com.nexomc.nexo.api.events.furniture;

import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event.Result;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0016\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 G2\u00020\u00012\u00020\u0002:\u0001GBc\b\u0007\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0010\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0013\u001a\u0002\b\u0016¢\u0006\u0004\b\u0014\u0010\u0015J\b\u0010+\u001a\u00020,H\u0016J\u0010\u0010B\u001a\u00020C2\u0006\u0010D\u001a\u00020,H\u0016J\b\u0010E\u001a\u00020FH\u0016R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0013\u0010\r\u001a\u0004\u0018\u00010\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u001a\u0010\u000f\u001a\u00020\u0010X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$R\u001a\u0010\u0011\u001a\u00020\u0010X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b%\u0010\"\"\u0004\b&\u0010$R\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0013¢\u0006\b\n\u0000\u001a\u0004\b'\u0010(R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u000e\u0010+\u001a\u00020,X\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010-\u001a\u00020\u00108FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b.\u0010\"\"\u0004\b/\u0010$R\u001c\u00100\u001a\u00020\u00108FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b1\u0010\"\"\u0004\b2\u0010$R\u001c\u00103\u001a\u00020\u00108FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b4\u0010\"\"\u0004\b5\u0010$R\u001c\u00106\u001a\u00020\u00108FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b7\u0010\"\"\u0004\b8\u0010$R\u001c\u00109\u001a\u00020\u00108FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b:\u0010\"\"\u0004\b;\u0010$R\u001c\u0010<\u001a\u00020\u00108FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b=\u0010\"\"\u0004\b>\u0010$R\u001c\u0010?\u001a\u00020\u00108FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b@\u0010\"\"\u0004\bA\u0010$¨\u0006H"},
   d2 = {"Lcom/nexomc/nexo/api/events/furniture/NexoFurnitureInteractEvent;", "Lorg/bukkit/event/Event;", "Lorg/bukkit/event/Cancellable;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "player", "Lorg/bukkit/entity/Player;", "itemInHand", "Lorg/bukkit/inventory/ItemStack;", "hand", "Lorg/bukkit/inventory/EquipmentSlot;", "interactionPoint", "Lorg/bukkit/Location;", "useFurniture", "Lorg/bukkit/event/Event$Result;", "useItemInHand", "blockFace", "Lorg/bukkit/block/BlockFace;", "<init>", "(Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;Lorg/bukkit/entity/ItemDisplay;Lorg/bukkit/entity/Player;Lorg/bukkit/inventory/ItemStack;Lorg/bukkit/inventory/EquipmentSlot;Lorg/bukkit/Location;Lorg/bukkit/event/Event$Result;Lorg/bukkit/event/Event$Result;Lorg/bukkit/block/BlockFace;)V", "Lkotlin/jvm/JvmOverloads;", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "getBaseEntity", "()Lorg/bukkit/entity/ItemDisplay;", "getPlayer", "()Lorg/bukkit/entity/Player;", "getHand", "()Lorg/bukkit/inventory/EquipmentSlot;", "getInteractionPoint", "()Lorg/bukkit/Location;", "getUseFurniture", "()Lorg/bukkit/event/Event$Result;", "setUseFurniture", "(Lorg/bukkit/event/Event$Result;)V", "getUseItemInHand", "setUseItemInHand", "getBlockFace", "()Lorg/bukkit/block/BlockFace;", "getItemInHand", "()Lorg/bukkit/inventory/ItemStack;", "isCancelled", "", "canToggleLight", "getCanToggleLight", "setCanToggleLight", "canRotate", "getCanRotate", "setCanRotate", "canSit", "getCanSit", "setCanSit", "canSleep", "getCanSleep", "setCanSleep", "canOpenStorage", "getCanOpenStorage", "setCanOpenStorage", "canRunAction", "getCanRunAction", "setCanRunAction", "canCycleState", "getCanCycleState", "setCanCycleState", "setCancelled", "", "cancel", "getHandlers", "Lorg/bukkit/event/HandlerList;", "Companion", "com.nexomc:core"}
)
public final class NexoFurnitureInteractEvent extends Event implements Cancellable {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final FurnitureMechanic mechanic;
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final Player player;
   @NotNull
   private final EquipmentSlot hand;
   @Nullable
   private final Location interactionPoint;
   @NotNull
   private Event.Result useFurniture;
   @NotNull
   private Event.Result useItemInHand;
   @Nullable
   private final BlockFace blockFace;
   @NotNull
   private final ItemStack itemInHand;
   private boolean isCancelled;
   @NotNull
   private Event.Result canToggleLight;
   @NotNull
   private Event.Result canRotate;
   @NotNull
   private Event.Result canSit;
   @NotNull
   private Event.Result canSleep;
   @NotNull
   private Event.Result canOpenStorage;
   @NotNull
   private Event.Result canRunAction;
   @NotNull
   private Event.Result canCycleState;
   @NotNull
   private static final HandlerList handlerList = new HandlerList();

   @JvmOverloads
   public NexoFurnitureInteractEvent(@NotNull FurnitureMechanic mechanic, @NotNull ItemDisplay baseEntity, @NotNull Player player, @Nullable ItemStack itemInHand, @NotNull EquipmentSlot hand, @Nullable Location interactionPoint, @NotNull Event.Result useFurniture, @NotNull Event.Result useItemInHand, @Nullable BlockFace blockFace) {
      super();
      this.mechanic = mechanic;
      this.baseEntity = baseEntity;
      this.player = player;
      this.hand = hand;
      this.interactionPoint = interactionPoint;
      this.useFurniture = useFurniture;
      this.useItemInHand = useItemInHand;
      this.blockFace = blockFace;
      ItemStack itemStack = itemInHand;
      if (itemInHand == null) {
         itemStack = ItemStack.of(Material.AIR);
      }

      this.itemInHand = itemStack;
      this.canToggleLight = Result.DEFAULT;
      this.canRotate = Result.DEFAULT;
      this.canSit = Result.DEFAULT;
      this.canSleep = Result.DEFAULT;
      this.canOpenStorage = Result.DEFAULT;
      this.canRunAction = Result.DEFAULT;
      this.canCycleState = Result.DEFAULT;
   }

   @NotNull
   public final FurnitureMechanic getMechanic() {
      return this.mechanic;
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
   public final EquipmentSlot getHand() {
      return this.hand;
   }

   @Nullable
   public final Location getInteractionPoint() {
      return this.interactionPoint;
   }

   @NotNull
   public final Event.Result getUseFurniture() {
      return this.useFurniture;
   }

   public final void setUseFurniture(@NotNull Event.Result result) {
      this.useFurniture = result;
   }

   @NotNull
   public final Event.Result getUseItemInHand() {
      return this.useItemInHand;
   }

   public final void setUseItemInHand(@NotNull Event.Result result) {
      this.useItemInHand = result;
   }

   @Nullable
   public final BlockFace getBlockFace() {
      return this.blockFace;
   }

   @NotNull
   public final ItemStack getItemInHand() {
      return this.itemInHand;
   }

   @NotNull
   public final Event.Result getCanToggleLight() {
      return this.canToggleLight == Result.DEFAULT ? (this.useFurniture != Result.DENY && this.mechanic.getLight().getToggleable() ? Result.ALLOW : Result.DENY) : this.canToggleLight;
   }

   public final void setCanToggleLight(@NotNull Event.Result result) {
      this.canToggleLight = result;
   }

   @NotNull
   public final Event.Result getCanRotate() {
      return this.canRotate == Result.DEFAULT ? (this.useFurniture != Result.DENY && this.mechanic.getRotatable().shouldRotate(this.player) ? Result.ALLOW : Result.DENY) : this.canRotate;
   }

   public final void setCanRotate(@NotNull Event.Result result) {
      this.canRotate = result;
   }

   @NotNull
   public final Event.Result getCanSit() {
      return this.canSit == Result.DEFAULT ? (this.useFurniture != Result.DENY && this.mechanic.getHasSeats() && !this.player.isSneaking() ? Result.ALLOW : Result.DENY) : this.canSit;
   }

   public final void setCanSit(@NotNull Event.Result result) {
      this.canSit = result;
   }

   @NotNull
   public final Event.Result getCanSleep() {
      return this.canSleep == Result.DEFAULT ? (this.useFurniture != Result.DENY && this.mechanic.getHasBeds() && !this.player.isSneaking() ? Result.ALLOW : Result.DENY) : this.canSleep;
   }

   public final void setCanSleep(@NotNull Event.Result result) {
      this.canSleep = result;
   }

   @NotNull
   public final Event.Result getCanOpenStorage() {
      return this.canOpenStorage == Result.DEFAULT ? (this.useFurniture != Result.DENY && this.mechanic.isStorage() && !this.player.isSneaking() ? Result.ALLOW : Result.DENY) : this.canOpenStorage;
   }

   public final void setCanOpenStorage(@NotNull Event.Result result) {
      this.canOpenStorage = result;
   }

   @NotNull
   public final Event.Result getCanRunAction() {
      return this.canRunAction == Result.DEFAULT ? (this.useFurniture != Result.DENY && !((Collection)this.mechanic.getClickActions()).isEmpty() ? Result.ALLOW : Result.DENY) : this.canRunAction;
   }

   public final void setCanRunAction(@NotNull Event.Result result) {
      this.canRunAction = result;
   }

   @NotNull
   public final Event.Result getCanCycleState() {
      return this.canCycleState == Result.DEFAULT ? (this.useFurniture != Result.DENY && this.mechanic.getStates() != null ? Result.ALLOW : Result.DENY) : this.canCycleState;
   }

   public final void setCanCycleState(@NotNull Event.Result result) {
      this.canCycleState = result;
   }

   public boolean isCancelled() {
      return this.isCancelled || this.useFurniture == Result.DENY && this.useItemInHand == Result.DENY;
   }

   public void setCancelled(boolean cancel) {
      this.useFurniture = cancel ? Result.DENY : (this.useFurniture == Result.DENY ? Result.DEFAULT : this.useFurniture);
      this.useItemInHand = cancel ? Result.DENY : (this.useItemInHand == Result.DENY ? Result.DEFAULT : this.useItemInHand);
      this.isCancelled = cancel;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlerList;
   }

   @JvmOverloads
   public NexoFurnitureInteractEvent(@NotNull FurnitureMechanic mechanic, @NotNull ItemDisplay baseEntity, @NotNull Player player, @Nullable ItemStack itemInHand, @NotNull EquipmentSlot hand, @Nullable Location interactionPoint, @NotNull Event.Result useFurniture, @NotNull Event.Result useItemInHand) {
      this(mechanic, baseEntity, player, itemInHand, hand, interactionPoint, useFurniture, useItemInHand, null);
   }

   @JvmOverloads
   public NexoFurnitureInteractEvent(@NotNull FurnitureMechanic mechanic, @NotNull ItemDisplay baseEntity, @NotNull Player player, @Nullable ItemStack itemInHand, @NotNull EquipmentSlot hand, @Nullable Location interactionPoint, @NotNull Event.Result useFurniture) {
      this(mechanic, baseEntity, player, itemInHand, hand, interactionPoint, useFurniture, org.bukkit.event.Event.Result.DEFAULT, null);
   }

   @JvmOverloads
   public NexoFurnitureInteractEvent(@NotNull FurnitureMechanic mechanic, @NotNull ItemDisplay baseEntity, @NotNull Player player, @Nullable ItemStack itemInHand, @NotNull EquipmentSlot hand, @Nullable Location interactionPoint) {
      this(mechanic, baseEntity, player, itemInHand, hand, interactionPoint, org.bukkit.event.Event.Result.DEFAULT, org.bukkit.event.Event.Result.DEFAULT, null);
   }

   @JvmOverloads
   public NexoFurnitureInteractEvent(@NotNull FurnitureMechanic mechanic, @NotNull ItemDisplay baseEntity, @NotNull Player player, @Nullable ItemStack itemInHand, @NotNull EquipmentSlot hand) {
      this(mechanic, baseEntity, player, itemInHand, hand, (baseEntity).getLocation(), org.bukkit.event.Event.Result.DEFAULT, org.bukkit.event.Event.Result.DEFAULT, null);
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
      d2 = {"Lcom/nexomc/nexo/api/events/furniture/NexoFurnitureInteractEvent$Companion;", "", "<init>", "()V", "handlerList", "Lorg/bukkit/event/HandlerList;", "getHandlerList$annotations", "getHandlerList", "()Lorg/bukkit/event/HandlerList;", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final HandlerList getHandlerList() {
         return NexoFurnitureInteractEvent.handlerList;
      }
   }

   /** Kotlin default-constructor bridge, kept for binary compatibility with classes that were not recompiled. */
   public NexoFurnitureInteractEvent(FurnitureMechanic mechanic, ItemDisplay baseEntity, Player player, ItemStack itemInHand, EquipmentSlot hand, Location interactionPoint, Event.Result useFurniture, Event.Result useItemInHand, BlockFace blockFace, int mask, kotlin.jvm.internal.DefaultConstructorMarker marker) {
      this(mechanic, baseEntity, player, itemInHand, hand,
         (mask & 32) != 0 ? baseEntity.getLocation() : interactionPoint,
         (mask & 64) != 0 ? Event.Result.DEFAULT : useFurniture,
         (mask & 128) != 0 ? Event.Result.DEFAULT : useItemInHand,
         (mask & 256) != 0 ? null : blockFace);
   }
}
