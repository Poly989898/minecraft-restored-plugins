package io.lumine.mythic.bukkit.utils;

import com.google.common.reflect.TypeToken;
import io.lumine.mythic.bukkit.utils.network.messaging.Channel;
import io.lumine.mythic.bukkit.utils.network.messaging.ChannelAgent;
import io.lumine.mythic.bukkit.utils.network.messaging.ChannelListener;
import io.lumine.mythic.bukkit.utils.network.messaging.Messenger;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;

public final class Messages {
   public static Messenger getMessenger() {
      return (Messenger)Bukkit.getServer().getServicesManager().load(Messenger.class);
   }

   public static <T> Channel<T> getChannel(@Nonnull Class<T> packet) {
      try {
         return getMessenger().getChannel((String)packet.getMethod("getChannel").invoke(null), packet);
      } catch (Error | Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public static <T> Channel<T> getChannel(@Nonnull String name, @Nonnull Class<T> clazz) {
      return getMessenger().getChannel(name, clazz);
   }

   public static <T> Channel<T> getChannel(@Nonnull String name, @Nonnull TypeToken<T> type) {
      return getMessenger().getChannel(name, type);
   }

   public static <T> ChannelAgent<T> subscribe(@Nonnull Class<T> clazz, ChannelListener<T> listener) {
      ChannelAgent agent = getChannel(clazz).newAgent();
      agent.addListener(listener);
      return agent;
   }

   public static <T> ChannelAgent<T> subscribe(@Nonnull String name, @Nonnull Class<T> clazz, ChannelListener<T> listener) {
      ChannelAgent agent = getMessenger().getChannel(name, clazz).newAgent();
      agent.addListener(listener);
      return agent;
   }

   public static <T> ChannelAgent<T> subscribe(@Nonnull String name, @Nonnull TypeToken<T> type, ChannelListener<T> listener) {
      ChannelAgent agent = getMessenger().getChannel(name, type).newAgent();
      agent.addListener(listener);
      return agent;
   }

   private Messages() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
