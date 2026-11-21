package com.nexomc.nexo.api.events.furniture;

import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.drops.Drop;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u001e2\u00020\u00012\u00020\u0002:\u0001\u001eB\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\b\u0010\u0011\u001a\u00020\u0012H\u0016J\u0010\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u0012H\u0016J\b\u0010\u001c\u001a\u00020\u001dH\u0016R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u0013\u001a\u00020\u0014X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018¨\u0006\u001f"},
   d2 = {"Lcom/nexomc/nexo/api/events/furniture/NexoFurnitureBreakEvent;", "Lorg/bukkit/event/Event;", "Lorg/bukkit/event/Cancellable;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "player", "Lorg/bukkit/entity/Player;", "<init>", "(Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;Lorg/bukkit/entity/ItemDisplay;Lorg/bukkit/entity/Player;)V", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "getBaseEntity", "()Lorg/bukkit/entity/ItemDisplay;", "getPlayer", "()Lorg/bukkit/entity/Player;", "isCancelled", "", "drop", "Lcom/nexomc/nexo/utils/drops/Drop;", "getDrop", "()Lcom/nexomc/nexo/utils/drops/Drop;", "setDrop", "(Lcom/nexomc/nexo/utils/drops/Drop;)V", "setCancelled", "", "cancel", "getHandlers", "Lorg/bukkit/event/HandlerList;", "Companion", "com.nexomc:core"}
)
public final class NexoFurnitureBreakEvent extends Event implements Cancellable {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final FurnitureMechanic mechanic;
   @NotNull
   private final ItemDisplay baseEntity;
   @NotNull
   private final Player player;
   private boolean isCancelled;
   @NotNull
   private Drop drop;
   @NotNull
   private static final HandlerList handlerList = new HandlerList();

   public NexoFurnitureBreakEvent(@NotNull FurnitureMechanic mechanic, @NotNull ItemDisplay baseEntity, @NotNull Player player) {
      super();
      this.mechanic = mechanic;
      this.baseEntity = baseEntity;
      this.player = player;
      this.drop = this.mechanic.getBreakable().getDrop();
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
   public final Drop getDrop() {
      return this.drop;
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
      d2 = {"Lcom/nexomc/nexo/api/events/furniture/NexoFurnitureBreakEvent$Companion;", "", "<init>", "()V", "handlerList", "Lorg/bukkit/event/HandlerList;", "getHandlerList$annotations", "getHandlerList", "()Lorg/bukkit/event/HandlerList;", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final HandlerList getHandlerList() {
         return NexoFurnitureBreakEvent.handlerList;
      }
   }
}
