package io.lumine.mythic.bukkit.utils.network.messaging;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import io.lumine.mythic.bukkit.utils.functions.TriConsumer;
import io.lumine.mythic.bukkit.utils.network.messaging.codec.ByteArrayCodec;
import io.lumine.mythic.bukkit.utils.network.messaging.codec.Codec;
import io.lumine.mythic.bukkit.utils.network.messaging.codec.GsonCodec;
import io.lumine.mythic.bukkit.utils.network.messaging.codec.Message;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import java.util.Base64;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@NonnullByDefault
public class AbstractMessenger implements Messenger {
   private final LoadingCache<Entry<String, TypeToken<?>>, AbstractMessenger.AbstractChannel<?>> channels = CacheBuilder.newBuilder()
      .build(new AbstractMessenger.ChannelLoader());
   private final String serverName;
   private final TriConsumer<String, String, byte[]> outgoingToMessages;
   private final BiConsumer<String, byte[]> outgoingMessages;
   private final Consumer<String> notifySub;
   private final Consumer<String> notifyUnsub;

   public AbstractMessenger(
      String serverName,
      BiConsumer<String, byte[]> outgoingMessages,
      TriConsumer<String, String, byte[]> outgoingToMessages,
      Consumer<String> notifySub,
      Consumer<String> notifyUnsub
   ) {
      this.serverName = Objects.requireNonNull(serverName, "serverName");
      this.outgoingMessages = Objects.requireNonNull(outgoingMessages, "outgoingMessages");
      this.outgoingToMessages = Objects.requireNonNull(outgoingToMessages, "outgoingMessages");
      this.notifySub = Objects.requireNonNull(notifySub, "notifySub");
      this.notifyUnsub = Objects.requireNonNull(notifyUnsub, "notifyUnsub");
   }

   public void registerIncomingMessage(String channel, String sender, byte[] message) {
      for (Entry<Entry<String, TypeToken<?>>, AbstractMessenger.AbstractChannel<?>> c : this.channels.asMap().entrySet()) {
         if (c.getKey().getKey().equals(channel)) {
            c.getValue().onIncomingMessage(message);
         }
      }
   }

   @Nonnull
   @Override
   public <T> Channel<T> getChannel(@Nonnull String name, @Nonnull TypeToken<T> type) {
      Objects.requireNonNull(name, "name");
      Preconditions.checkArgument(!name.trim().isEmpty(), "name cannot be empty");
      Objects.requireNonNull(type, "type");
      return (Channel<T>)this.channels.getUnchecked(Maps.immutableEntry(name, type));
   }

   private static <T> Codec<T> getCodec(TypeToken<T> type) {
      Class<? super T> rawType = type.getRawType();

      do {
         Message message = rawType.getAnnotation(Message.class);
         if (message != null) {
            Class<? extends Codec<?>> codec = message.codec();

            try {
               return (Codec<T>)codec.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
               e.printStackTrace();
            }
         }
      } while ((rawType = rawType.getSuperclass()) != null);

      return new GsonCodec<>(type);
   }

   public String getServerName() {
      return this.serverName;
   }

   private static class AbstractChannel<T extends MessagePacket> implements Channel<T> {
      private final AbstractMessenger messenger;
      private final String name;
      private final TypeToken<T> type;
      private final Codec<T> codec;
      private final Set<AbstractMessenger.AbstractChannelAgent<T>> agents = ConcurrentHashMap.newKeySet();
      private boolean subscribed = false;

      private AbstractChannel(AbstractMessenger messenger, String name, TypeToken<T> type) {
         this.messenger = messenger;
         this.name = name;
         this.type = type;
         this.codec = new ByteArrayCodec<>(AbstractMessenger.getCodec(type));
      }

      private void onIncomingMessage(byte[] message) {
         try {
            T decoded = this.codec.decode(message);
            Objects.requireNonNull(decoded, "decoded");

            for (AbstractMessenger.AbstractChannelAgent<T> agent : this.agents) {
               try {
                  agent.onIncomingMessage(agent, decoded.getOriginServer(), decoded);
               } catch (Exception e) {
                  new RuntimeException("Unable to pass decoded message to agent: " + decoded, e).printStackTrace();
               }
            }
         } catch (Exception ex) {
            new RuntimeException("Unable to decode message: " + Base64.getEncoder().encodeToString(message), ex).printStackTrace();
         }
      }

      private void checkSubscription() {
         boolean shouldSubscribe = this.agents.stream().anyMatch(AbstractMessenger.AbstractChannelAgent::hasListeners);
         if (shouldSubscribe != this.subscribed) {
            this.subscribed = shouldSubscribe;
            Schedulers.async().run(() -> {
               try {
                  if (shouldSubscribe) {
                     this.messenger.notifySub.accept(this.name);
                  } else {
                     this.messenger.notifyUnsub.accept(this.name);
                  }
               } catch (Exception e) {
                  e.printStackTrace();
               }
            });
         }
      }

      @Override
      public String getName() {
         return this.name;
      }

      @Override
      public TypeToken<T> getType() {
         return this.type;
      }

      @Nonnull
      @Override
      public Codec<T> getCodec() {
         return this.codec;
      }

      @Override
      public ChannelAgent<T> newAgent() {
         AbstractMessenger.AbstractChannelAgent<T> agent = new AbstractMessenger.AbstractChannelAgent<>(this);
         this.agents.add(agent);
         return agent;
      }

      public Promise<Void> sendMessage(T message) {
         Objects.requireNonNull(message, "message");
         message.setOriginServer(this.messenger.getServerName());
         return Schedulers.async().call(() -> {
            byte[] buf = this.codec.encode(message);
            this.messenger.outgoingMessages.accept(this.name, buf);
            return null;
         });
      }

      public Promise<Void> sendMessage(String server, T message) {
         Objects.requireNonNull(server, "server");
         Objects.requireNonNull(message, "message");
         message.setOriginServer(this.messenger.getServerName());
         return Schedulers.async().call(() -> {
            byte[] buf = this.codec.encode(message);
            this.messenger.outgoingToMessages.accept(server, this.name, buf);
            return null;
         });
      }

      public AbstractMessenger getMessenger() {
         return this.messenger;
      }
   }

   private static class AbstractChannelAgent<T extends MessagePacket> implements ChannelAgent<T> {
      @Nullable
      private AbstractMessenger.AbstractChannel<T> channel;
      private final Set<ChannelListener<T>> listeners = ConcurrentHashMap.newKeySet();

      AbstractChannelAgent(AbstractMessenger.AbstractChannel<T> channel) {
         this.channel = channel;
      }

      private void onIncomingMessage(ChannelAgent agent, String sender, T data) {
         for (ChannelListener<T> listener : this.listeners) {
            Schedulers.async().run(() -> {
               try {
                  listener.onMessage(agent, sender, data);
               } catch (Exception var5x) {
                  new RuntimeException("Unable to pass decoded message to listener: " + listener, var5x).printStackTrace();
               }
            });
         }
      }

      @Override
      public Channel<T> getChannel() {
         Preconditions.checkState(this.channel != null, "agent not active");
         return this.channel;
      }

      @Override
      public Set<ChannelListener<T>> getListeners() {
         Preconditions.checkState(this.channel != null, "agent not active");
         return ImmutableSet.copyOf(this.listeners);
      }

      @Override
      public boolean hasListeners() {
         return !this.listeners.isEmpty();
      }

      @Override
      public boolean addListener(ChannelListener<T> listener) {
         Preconditions.checkState(this.channel != null, "agent not active");

         boolean flag;
         try {
            flag = this.listeners.add(listener);
         } finally {
            this.channel.checkSubscription();
         }

         return flag;
      }

      @Override
      public boolean removeListener(ChannelListener<T> listener) {
         Preconditions.checkState(this.channel != null, "agent not active");

         boolean flag;
         try {
            flag = this.listeners.remove(listener);
         } finally {
            this.channel.checkSubscription();
         }

         return flag;
      }

      @Override
      public void close() {
         if (this.channel != null) {
            this.listeners.clear();
            this.channel.agents.remove(this);
            this.channel.checkSubscription();
            this.channel = null;
         }
      }
   }

   private class ChannelLoader<T extends MessagePacket> extends CacheLoader<Entry<String, TypeToken<T>>, Channel<T>> {
      public Channel<T> load(Entry<String, TypeToken<T>> spec) throws Exception {
         return new AbstractMessenger.AbstractChannel<>(AbstractMessenger.this, spec.getKey(), spec.getValue());
      }
   }
}
