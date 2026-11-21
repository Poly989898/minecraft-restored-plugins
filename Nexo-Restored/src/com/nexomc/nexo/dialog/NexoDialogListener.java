package com.nexomc.nexo.dialog;

import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import kotlin.Metadata;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/dialog/NexoDialogListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onLoadItems", "", "Lcom/nexomc/nexo/api/events/NexoItemsLoadedEvent;", "Lorg/bukkit/event/EventHandler;", "com.nexomc:core"}
)
public final class NexoDialogListener implements Listener {
   @EventHandler
   public final void onLoadItems(@NotNull NexoItemsLoadedEvent nexoItemsLoadedEvent) {
      NexoDialogs.INSTANCE.reloadDialogsIfNeeded();
   }
}
