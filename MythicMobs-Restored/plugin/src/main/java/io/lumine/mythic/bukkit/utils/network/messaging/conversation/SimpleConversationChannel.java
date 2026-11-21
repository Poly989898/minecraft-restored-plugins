package io.lumine.mythic.bukkit.utils.network.messaging.conversation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.reflect.TypeToken;
import io.lumine.mythic.bukkit.utils.network.messaging.Channel;
import io.lumine.mythic.bukkit.utils.network.messaging.ChannelAgent;
import io.lumine.mythic.bukkit.utils.network.messaging.ChannelListener;
import io.lumine.mythic.bukkit.utils.network.messaging.Messenger;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public class SimpleConversationChannel<T extends ConversationMessage, R extends ConversationMessage> implements ConversationChannel<T, R> {
   private final String name;
   private final Channel<T> outgoingChannel;
   private final Channel<R> replyChannel;
   private final Set<SimpleConversationChannel.Agent<T, R>> agents = ConcurrentHashMap.newKeySet();
   private final ScheduledExecutorService replyTimeoutExecutor = Executors.newSingleThreadScheduledExecutor();
   private final ChannelAgent<R> replyAgent;
   private final SetMultimap<UUID, SimpleConversationChannel.ReplyListenerRegistration<R>> replyListeners = Multimaps.newSetMultimap(
      new ConcurrentHashMap(), ConcurrentHashMap::newKeySet
   );

   public SimpleConversationChannel(Messenger messenger, String name, TypeToken<T> outgoingType, TypeToken<R> replyType) {
      this.name = name;
      this.outgoingChannel = messenger.getChannel(name + "-o", outgoingType);
      this.replyChannel = messenger.getChannel(name + "-r", replyType);
      this.replyAgent = this.replyChannel.newAgent(new SimpleConversationChannel.ReplyListener());
   }

   @Nonnull
   @Override
   public String getName() {
      return this.name;
   }

   @Nonnull
   @Override
   public Channel<T> getOutgoingChannel() {
      return this.outgoingChannel;
   }

   @Nonnull
   @Override
   public Channel<R> getReplyChannel() {
      return this.replyChannel;
   }

   @Nonnull
   @Override
   public ConversationChannelAgent<T, R> newAgent() {
      SimpleConversationChannel.Agent<T, R> agent = new SimpleConversationChannel.Agent<>(this);
      this.agents.add(agent);
      return agent;
   }

   @Nonnull
   @Override
   public Promise<Void> sendMessage(@Nonnull T message, @Nonnull ConversationReplyListener<R> replyListener, long timeoutDuration, @Nonnull TimeUnit unit) {
      SimpleConversationChannel.ReplyListenerRegistration<R> listenerRegistration = new SimpleConversationChannel.ReplyListenerRegistration<>(replyListener);
      listenerRegistration.timeoutFuture = this.replyTimeoutExecutor.schedule(listenerRegistration::timeout, timeoutDuration, unit);
      this.replyListeners.put(message.getConversationId(), listenerRegistration);
      return this.outgoingChannel.sendMessage(message);
   }

   @Nonnull
   @Override
   public Promise<Void> sendMessage(
      @Nonnull String server, @Nonnull T message, @Nonnull ConversationReplyListener<R> replyListener, long timeoutDuration, @Nonnull TimeUnit unit
   ) {
      SimpleConversationChannel.ReplyListenerRegistration<R> listenerRegistration = new SimpleConversationChannel.ReplyListenerRegistration<>(replyListener);
      listenerRegistration.timeoutFuture = this.replyTimeoutExecutor.schedule(listenerRegistration::timeout, timeoutDuration, unit);
      this.replyListeners.put(message.getConversationId(), listenerRegistration);
      return this.outgoingChannel.sendMessage(server, message);
   }

   @Override
   public void close() {
      this.replyAgent.close();
      this.replyTimeoutExecutor.shutdown();
      this.agents.forEach(Terminable::terminate);
   }

   private static final class Agent<T extends ConversationMessage, R extends ConversationMessage> implements ConversationChannelAgent<T, R> {
      private final SimpleConversationChannel<T, R> channel;
      private final ChannelAgent<T> delegateAgent;

      private Agent(@Nonnull SimpleConversationChannel<T, R> channel) {
         this.channel = channel;
         this.delegateAgent = this.channel.getOutgoingChannel().newAgent();
      }

      @Nonnull
      @Override
      public ConversationChannel<T, R> getChannel() {
         this.delegateAgent.getChannel();
         return this.channel;
      }

      @Nonnull
      @Override
      public Set<ConversationChannelListener<T, R>> getListeners() {
         Set<ChannelListener<T>> listeners = this.delegateAgent.getListeners();
         Builder<ConversationChannelListener<T, R>> ret = ImmutableSet.builder();

         for (ChannelListener<T> listener : listeners) {
            ret.add(((SimpleConversationChannel.Agent.WrappedListener)listener).delegate);
         }

         return ret.build();
      }

      @Override
      public boolean hasListeners() {
         return this.delegateAgent.hasListeners();
      }

      @Override
      public boolean addListener(@Nonnull ConversationChannelListener<T, R> listener) {
         return this.delegateAgent.addListener(new SimpleConversationChannel.Agent.WrappedListener(listener));
      }

      @Override
      public boolean removeListener(@Nonnull ConversationChannelListener<T, R> listener) {
         for (ChannelListener<T> other : this.delegateAgent.getListeners()) {
            SimpleConversationChannel.Agent<T, R>.WrappedListener wrapped = (SimpleConversationChannel.Agent.WrappedListener)other;
            if (wrapped.delegate == listener) {
               return this.delegateAgent.removeListener(other);
            }
         }

         return false;
      }

      @Override
      public void close() {
         this.delegateAgent.close();
      }

      private final class WrappedListener implements ChannelListener<T> {
         private final ConversationChannelListener<T, R> delegate;

         private WrappedListener(ConversationChannelListener<T, R> delegate) {
            this.delegate = delegate;
         }

         public void onMessage(ChannelAgent agent, String sender, T message) {
            ConversationReply<R> reply = this.delegate.onMessage(Agent.this, sender, message);
            if (reply.hasReply()) {
               reply.getReply().thenAcceptAsync(m -> {
                  if (m != null) {
                     Agent.this.channel.replyChannel.sendMessage(sender, (R)m);
                  }
               });
            }
         }
      }
   }

   private final class ReplyListener implements ChannelListener<R> {
      public void onMessage(@Nonnull ChannelAgent agent, String sender, R message) {
         SimpleConversationChannel.this.replyListeners.get(message.getConversationId()).removeIf(l -> l.onReply(message));
      }
   }

   private static final class ReplyListenerRegistration<R extends ConversationMessage> {
      private final ConversationReplyListener<R> listener;
      private final List<R> replies = new ArrayList<>();
      private ScheduledFuture<?> timeoutFuture;
      private boolean active = true;

      private ReplyListenerRegistration(ConversationReplyListener<R> listener) {
         this.listener = listener;
      }

      public boolean onReply(R message) {
         synchronized (this) {
            if (!this.active) {
               return true;
            } else {
               this.replies.add(message);
               ConversationReplyListener.RegistrationAction action = this.listener.onReply(message);
               if (action == ConversationReplyListener.RegistrationAction.STOP_LISTENING) {
                  this.active = false;
                  this.timeoutFuture.cancel(false);
                  return true;
               } else {
                  return false;
               }
            }
         }
      }

      public void timeout() {
         synchronized (this) {
            if (this.active) {
               this.listener.onTimeout(this.replies);
               this.active = false;
            }
         }
      }
   }
}
