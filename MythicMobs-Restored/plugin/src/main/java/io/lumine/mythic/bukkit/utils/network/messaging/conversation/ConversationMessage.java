package io.lumine.mythic.bukkit.utils.network.messaging.conversation;

import java.util.UUID;
import javax.annotation.Nonnull;

public interface ConversationMessage {
   @Nonnull
   UUID getConversationId();
}
