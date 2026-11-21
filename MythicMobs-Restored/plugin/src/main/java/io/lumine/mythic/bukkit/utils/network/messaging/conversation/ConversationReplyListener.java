package io.lumine.mythic.bukkit.utils.network.messaging.conversation;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;

public interface ConversationReplyListener<R extends ConversationMessage> {
   static <R extends ConversationMessage> ConversationReplyListener<R> of(final Function<? super R, ConversationReplyListener.RegistrationAction> onReply) {
      return new ConversationReplyListener<R>() {
         @Nonnull
         @Override
         public ConversationReplyListener.RegistrationAction onReply(@Nonnull R reply) {
            return onReply.apply(reply);
         }

         @Override
         public void onTimeout(@Nonnull List<R> replies) {
         }
      };
   }

   @Nonnull
   ConversationReplyListener.RegistrationAction onReply(@Nonnull R r);

   void onTimeout(@Nonnull List<R> rs);

   public static enum RegistrationAction {
      CONTINUE_LISTENING,
      STOP_LISTENING;
   }
}
