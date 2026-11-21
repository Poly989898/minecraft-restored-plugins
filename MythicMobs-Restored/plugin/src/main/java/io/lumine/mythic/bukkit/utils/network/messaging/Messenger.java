package io.lumine.mythic.bukkit.utils.network.messaging;

import com.google.common.reflect.TypeToken;
import io.lumine.mythic.bukkit.utils.network.messaging.conversation.ConversationChannel;
import io.lumine.mythic.bukkit.utils.network.messaging.conversation.ConversationMessage;
import io.lumine.mythic.bukkit.utils.network.messaging.conversation.SimpleConversationChannel;
import java.util.Objects;
import javax.annotation.Nonnull;

public interface Messenger {
   @Nonnull
   <T> Channel<T> getChannel(@Nonnull String str, @Nonnull TypeToken<T> typeToken);

   @Nonnull
   default <T extends ConversationMessage, R extends ConversationMessage> ConversationChannel<T, R> getConversationChannel(
      @Nonnull String name, @Nonnull TypeToken<T> type, @Nonnull TypeToken<R> replyType
   ) {
      return new SimpleConversationChannel<>(this, name, type, replyType);
   }

   @Nonnull
   default <T> Channel<T> getChannel(@Nonnull String name, @Nonnull Class<T> clazz) {
      return this.getChannel(name, TypeToken.of(Objects.requireNonNull(clazz)));
   }

   @Nonnull
   default <T extends ConversationMessage, R extends ConversationMessage> ConversationChannel<T, R> getConversationChannel(
      @Nonnull String name, @Nonnull Class<T> clazz, @Nonnull Class<R> replyClazz
   ) {
      return this.getConversationChannel(name, TypeToken.of(Objects.requireNonNull(clazz)), TypeToken.of(Objects.requireNonNull(replyClazz)));
   }
}
