package io.lumine.mythic.bukkit.utils.network.messaging.conversation;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface ConversationChannelListener<T extends ConversationMessage, R extends ConversationMessage> {
   ConversationReply<R> onMessage(@Nonnull ConversationChannelAgent<T, R> conversationChannelAgent, @Nonnull String str, @Nonnull T t);
}
