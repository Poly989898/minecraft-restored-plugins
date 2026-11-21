package io.lumine.mythic.bukkit.utils.prompts.chat;

import io.lumine.mythic.bukkit.utils.promise.Promise;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public interface ChatPrompt {
   static Promise<ChatResponse> open(Player player) {
      return new ChatPromptImpl(player, null).get();
   }

   static Promise<ChatResponse> listen(Player player, ChatPrompt.ResponseHandler handler) {
      return new ChatPromptImpl(player, handler).get();
   }

   Promise<ChatResponse> get();

   public static enum Response {
      ACCEPTED,
      TRY_AGAIN;
   }

   @FunctionalInterface
   public interface ResponseHandler {
      @Nonnull
      ChatPrompt.Response handleResponse(@Nonnull String str);
   }
}
