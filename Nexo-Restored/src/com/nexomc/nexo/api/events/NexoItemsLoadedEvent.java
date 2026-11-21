package com.nexomc.nexo.api.events;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00062\u00020\u0001:\u0001\u0006B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\u0007"},
   d2 = {"Lcom/nexomc/nexo/api/events/NexoItemsLoadedEvent;", "Lorg/bukkit/event/Event;", "<init>", "()V", "getHandlers", "Lorg/bukkit/event/HandlerList;", "Companion", "com.nexomc:core"}
)
public final class NexoItemsLoadedEvent extends Event {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private static final HandlerList handlerList = new HandlerList();

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
      d2 = {"Lcom/nexomc/nexo/api/events/NexoItemsLoadedEvent$Companion;", "", "<init>", "()V", "handlerList", "Lorg/bukkit/event/HandlerList;", "getHandlerList$annotations", "getHandlerList", "()Lorg/bukkit/event/HandlerList;", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final HandlerList getHandlerList() {
         return NexoItemsLoadedEvent.handlerList;
      }
   }
}
