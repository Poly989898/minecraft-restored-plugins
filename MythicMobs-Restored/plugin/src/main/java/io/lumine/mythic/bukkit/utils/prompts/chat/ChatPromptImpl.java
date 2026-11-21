package io.lumine.mythic.bukkit.utils.prompts.chat;

import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.composite.CompositeTerminable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatPromptImpl implements ChatPrompt, Terminable {
   private final CompositeTerminable registry = CompositeTerminable.create();
   private final Player player;
   private final ChatPrompt.ResponseHandler handler;
   private final Promise<ChatResponse> promise = Promise.empty();

   public ChatPromptImpl(Player player, ChatPrompt.ResponseHandler handler) {
      this.player = player;
      this.handler = handler;
      Events.subscribe(AsyncPlayerChatEvent.class).biHandler((sub, event) -> {
         if (event.getPlayer().getUniqueId().equals(this.player.getUniqueId())) {
            event.setCancelled(true);
            if (handler != null) {
               ChatPrompt.Response res = handler.handleResponse(event.getMessage());
               if (res == ChatPrompt.Response.TRY_AGAIN) {
                  return;
               }
            }

            this.promise.supply(new ChatResponse(event.getPlayer(), event.getMessage()));
            this.terminate();
         }
      }).bindWith(this.registry);
      Events.subscribe(PlayerQuitEvent.class).handler(event -> {
         if (event.getPlayer().getUniqueId().equals(this.player.getUniqueId())) {
            this.promise.supply(null);
            this.terminate();
         }
      }).bindWith(this.registry);
   }

   @Override
   public void close() throws Exception {
      this.registry.closeAndReportException();
   }

   @Override
   public Promise<ChatResponse> get() {
      return this.promise;
   }
}
