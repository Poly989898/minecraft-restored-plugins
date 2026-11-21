package com.ticxo.modelengine.core.listener;

import com.google.common.collect.Maps;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {
   private static final Map<UUID, Predicate<String>> FETCH_MAP = Maps.newConcurrentMap();

   @EventHandler
   public void onAsyncChat(AsyncChatEvent event) {
      UUID uuid = event.getPlayer().getUniqueId();
      Predicate<String> fetch = FETCH_MAP.get(uuid);
      if (fetch != null) {
         event.setCancelled(true);
         if (fetch.test(PlainTextComponentSerializer.plainText().serialize(event.message()))) {
            FETCH_MAP.remove(uuid);
         }
      }
   }

   public static void fetch(Player player, Predicate<String> consumer) {
      FETCH_MAP.put(player.getUniqueId(), consumer);
   }
}
