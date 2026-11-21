package io.lumine.mythic.bukkit.utils.network.messaging.conversation;

import io.lumine.mythic.bukkit.utils.network.messaging.Channel;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public interface ConversationChannel<T extends ConversationMessage, R extends ConversationMessage> extends Terminable {
   @Nonnull
   String getName();

   @Nonnull
   Channel<T> getOutgoingChannel();

   @Nonnull
   Channel<R> getReplyChannel();

   @Nonnull
   ConversationChannelAgent<T, R> newAgent();

   @Nonnull
   default ConversationChannelAgent<T, R> newAgent(ConversationChannelListener<T, R> listener) {
      ConversationChannelAgent<T, R> agent = this.newAgent();
      agent.addListener(listener);
      return agent;
   }

   @Nonnull
   Promise<Void> sendMessage(@Nonnull T t, @Nonnull ConversationReplyListener<R> conversationReplyListener, long value, @Nonnull TimeUnit timeUnit);

   @Nonnull
   Promise<Void> sendMessage(@Nonnull String str, @Nonnull T t, @Nonnull ConversationReplyListener<R> conversationReplyListener, long value, @Nonnull TimeUnit timeUnit);

   @Override
   void close();
}
