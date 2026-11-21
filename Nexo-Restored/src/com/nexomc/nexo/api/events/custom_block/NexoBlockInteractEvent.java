package com.nexomc.nexo.api.events.custom_block;

import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0016\u0018\u0000 (2\u00020\u00012\u00020\u0002:\u0001(BA\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0010¢\u0006\u0004\b\u0011\u0010\u0012J\b\u0010!\u001a\u00020\"H\u0016J\u0010\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020\"H\u0016J\b\u0010&\u001a\u00020'H\u0016R\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u000f\u001a\u00020\u0010¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u000e\u0010!\u001a\u00020\"X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006)"},
   d2 = {"Lcom/nexomc/nexo/api/events/custom_block/NexoBlockInteractEvent;", "Lorg/bukkit/event/Event;", "Lorg/bukkit/event/Cancellable;", "mechanic", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "player", "Lorg/bukkit/entity/Player;", "itemInHand", "Lorg/bukkit/inventory/ItemStack;", "hand", "Lorg/bukkit/inventory/EquipmentSlot;", "block", "Lorg/bukkit/block/Block;", "blockFace", "Lorg/bukkit/block/BlockFace;", "action", "Lorg/bukkit/event/block/Action;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;Lorg/bukkit/entity/Player;Lorg/bukkit/inventory/ItemStack;Lorg/bukkit/inventory/EquipmentSlot;Lorg/bukkit/block/Block;Lorg/bukkit/block/BlockFace;Lorg/bukkit/event/block/Action;)V", "getMechanic", "()Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "getPlayer", "()Lorg/bukkit/entity/Player;", "getItemInHand", "()Lorg/bukkit/inventory/ItemStack;", "getHand", "()Lorg/bukkit/inventory/EquipmentSlot;", "getBlock", "()Lorg/bukkit/block/Block;", "getBlockFace", "()Lorg/bukkit/block/BlockFace;", "getAction", "()Lorg/bukkit/event/block/Action;", "isCancelled", "", "setCancelled", "", "cancel", "getHandlers", "Lorg/bukkit/event/HandlerList;", "Companion", "com.nexomc:core"}
)
public class NexoBlockInteractEvent extends Event implements Cancellable {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final CustomBlockMechanic mechanic;
   @NotNull
   private final Player player;
   @Nullable
   private final ItemStack itemInHand;
   @NotNull
   private final EquipmentSlot hand;
   @NotNull
   private final Block block;
   @NotNull
   private final BlockFace blockFace;
   @NotNull
   private final Action action;
   private boolean isCancelled;
   @NotNull
   private static final HandlerList HANDLERS = new HandlerList();

   public NexoBlockInteractEvent(@NotNull CustomBlockMechanic mechanic, @NotNull Player player, @Nullable ItemStack itemInHand, @NotNull EquipmentSlot hand, @NotNull Block block, @NotNull BlockFace blockFace, @NotNull Action action) {
      super();
      this.mechanic = mechanic;
      this.player = player;
      this.itemInHand = itemInHand;
      this.hand = hand;
      this.block = block;
      this.blockFace = blockFace;
      this.action = action;
   }

   @NotNull
   public CustomBlockMechanic getMechanic() {
      return this.mechanic;
   }

   @NotNull
   public final Player getPlayer() {
      return this.player;
   }

   @Nullable
   public final ItemStack getItemInHand() {
      return this.itemInHand;
   }

   @NotNull
   public final EquipmentSlot getHand() {
      return this.hand;
   }

   @NotNull
   public final Block getBlock() {
      return this.block;
   }

   @NotNull
   public final BlockFace getBlockFace() {
      return this.blockFace;
   }

   @NotNull
   public final Action getAction() {
      return this.action;
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
      d2 = {"Lcom/nexomc/nexo/api/events/custom_block/NexoBlockInteractEvent$Companion;", "", "<init>", "()V", "HANDLERS", "Lorg/bukkit/event/HandlerList;", "getHandlerList", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final HandlerList getHandlerList() {
         return NexoBlockInteractEvent.HANDLERS;
      }
   }
}
