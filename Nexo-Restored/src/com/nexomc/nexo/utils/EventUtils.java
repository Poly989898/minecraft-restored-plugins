package com.nexomc.nexo.utils;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\n\u0010\u0004\u001a\u00020\u0005*\u00020\u0006J2\u0010\u0004\u001a\u00020\u0007\"\b\b\u0000\u0010\b*\u00020\u0006*\u0002H\b2\u0017\u0010\t\u001a\u0013\u0012\u0004\u0012\u0002H\b\u0012\u0004\u0012\u00020\u00070\n¢\u0006\u0002\b\u000b¢\u0006\u0002\u0010\fJ\n\u0010\r\u001a\u00020\u0007*\u00020\u000eJ\u0012\u0010\u000f\u001a\u00020\u0007*\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u0005¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/utils/EventUtils;", "", "<init>", "()V", "call", "", "Lorg/bukkit/event/Event;", "", "T", "block", "Lkotlin/Function1;", "Lkotlin/ExtensionFunctionType;", "(Lorg/bukkit/event/Event;Lkotlin/jvm/functions/Function1;)V", "returnCancelled", "Lorg/bukkit/event/Cancellable;", "cancelIf", "boolean", "com.nexomc:core"}
)
public final class EventUtils {
   @NotNull
   public static final EventUtils INSTANCE = new EventUtils();

   private EventUtils() {
   }

   public final boolean call(@NotNull Event event) {
      Bukkit.getPluginManager().callEvent(event);
      return event instanceof Cancellable ? !((Cancellable)event).isCancelled() : true;
   }

   public final <T extends Event> void call(@NotNull T t, @NotNull Function1<? super T, Unit> block) {
      Bukkit.getPluginManager().callEvent(t);
      if (t instanceof Cancellable && !((Cancellable)t).isCancelled()) {
         block.invoke(t);
      }

   }

   public final void returnCancelled(@NotNull Cancellable cancellable) {
      cancellable.setCancelled(true);
   }

   public final void cancelIf(@NotNull Cancellable cancellable, boolean flag) {
      if (flag) {
         cancellable.setCancelled(true);
      }

   }
}
