package io.lumine.mythic.bukkit.utils.network.messaging.conversation;

import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import java.util.Set;
import javax.annotation.Nonnull;

public interface ConversationChannelAgent<T extends ConversationMessage, R extends ConversationMessage> extends Terminable {
   @Nonnull
   ConversationChannel<T, R> getChannel();

   @Nonnull
   Set<ConversationChannelListener<T, R>> getListeners();

   boolean hasListeners();

   boolean addListener(@Nonnull ConversationChannelListener<T, R> conversationChannelListener);

   boolean removeListener(@Nonnull ConversationChannelListener<T, R> conversationChannelListener);

   @Override
   void close();
}
