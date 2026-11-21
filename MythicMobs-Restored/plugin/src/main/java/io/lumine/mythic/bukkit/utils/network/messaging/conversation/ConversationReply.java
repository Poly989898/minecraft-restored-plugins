package io.lumine.mythic.bukkit.utils.network.messaging.conversation;

import io.lumine.mythic.bukkit.utils.promise.Promise;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public final class ConversationReply<R extends ConversationMessage> {
   private static final ConversationReply<?> NO_REPLY = new ConversationReply(null);
   private final Promise<R> reply;

   public static <R extends ConversationMessage> ConversationReply<R> noReply() {
      return (ConversationReply<R>)NO_REPLY;
   }

   public static <R extends ConversationMessage> ConversationReply<R> of(R reply) {
      Objects.requireNonNull(reply, "reply");
      return new ConversationReply<>(Promise.completed(reply));
   }

   public static <R extends ConversationMessage> ConversationReply<R> ofCompletableFuture(CompletableFuture<R> futureReply) {
      Objects.requireNonNull(futureReply, "futureReply");
      return new ConversationReply<>(Promise.wrapFuture(futureReply));
   }

   public static <R extends ConversationMessage> ConversationReply<R> ofPromise(Promise<R> promiseReply) {
      Objects.requireNonNull(promiseReply, "promiseReply");
      return new ConversationReply<>(promiseReply);
   }

   private ConversationReply(Promise<R> reply) {
      this.reply = reply;
   }

   public boolean hasReply() {
      return this.reply != null;
   }

   @Nonnull
   public Promise<R> getReply() {
      if (this.reply == null) {
         throw new IllegalStateException("No reply present");
      } else {
         return this.reply;
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ConversationReply<?> other) ? false : Objects.equals(this.reply, other.reply);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.reply);
   }

   @Override
   public String toString() {
      return this.reply != null ? String.format("ConversationReply[%s]", this.reply) : "ConversationReply.noReply";
   }
}
