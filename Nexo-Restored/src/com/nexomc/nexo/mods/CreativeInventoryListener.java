package com.nexomc.nexo.mods;

import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import com.nexomc.nexo.nms.NMSHandlers;
import kotlin.Metadata;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0002\b\u0007J\u0010\u0010\b\u001a\u00020\u0005*\u00020\tH\u0007b\u0002\b\u0007¨\u0006\n"},
   d2 = {"Lcom/nexomc/nexo/mods/CreativeInventoryListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onLoadItems", "", "Lcom/nexomc/nexo/api/events/NexoItemsLoadedEvent;", "Lorg/bukkit/event/EventHandler;", "onJoin", "Lorg/bukkit/event/player/PlayerJoinEvent;", "com.nexomc:core"}
)
public final class CreativeInventoryListener implements Listener {
   @EventHandler
   public final void onLoadItems(@NotNull NexoItemsLoadedEvent nexoItemsLoadedEvent) {
      NMSHandlers.handler().creativeInventory().reload();
   }

   @EventHandler
   public final void onJoin(@NotNull PlayerJoinEvent playerJoinEvent) {
   }
}
