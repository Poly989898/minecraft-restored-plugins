package io.lumine.mythic.bukkit.utils.network.messaging.redis;

import com.google.common.reflect.TypeToken;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.network.messaging.AbstractMessenger;
import io.lumine.mythic.bukkit.utils.network.messaging.Channel;
import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisExhaustedPoolException;
import io.lumine.mythic.bukkit.utils.terminable.composite.CompositeTerminable;
import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

public class RedisImpl implements Redis {
   private final String serverName;
   private final JedisPool pool;
   private final AbstractMessenger messenger;
   private final RedisCredentials credentials;
   private boolean closed = false;
   private RedisImpl.PubSubListener listener = null;
   private Set<String> channels = ConcurrentHashMap.newKeySet();
   private CompositeTerminable registry = CompositeTerminable.create();

   public RedisImpl(@Nonnull String serverName, @Nonnull RedisCredentials credentials) {
      this.serverName = serverName;
      this.credentials = credentials;
      this.listener = new RedisImpl.PubSubListener();
      JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
      jedisPoolConfig.setMaxTotal(50);
      jedisPoolConfig.setMaxIdle(50);
      jedisPoolConfig.setMinIdle(10);
      jedisPoolConfig.setMaxWaitMillis(2000L);
      jedisPoolConfig.setTestOnReturn(true);
      if (credentials.getPassword() != null && !credentials.getPassword().isEmpty()) {
         Log.info("Redis Authenticating...");
         this.pool = new JedisPool(jedisPoolConfig, credentials.getAddress(), credentials.getPort(), 2000, credentials.getPassword());
      } else {
         this.pool = new JedisPool(jedisPoolConfig, credentials.getAddress(), credentials.getPort());
      }

      Log.info("Redis Pool Initialized....");
      this.openMessagingChannel("NETWORK.*");
      this.openMessagingChannel(serverName + ".*");
      Schedulers.async().runRepeating(() -> {
         int active = this.pool.getNumActive();
         int idle = this.pool.getNumIdle();
         int total = active + idle;
      }, 1200L, 1200L);
      LoaderUtils.getPlugin().provideService(Redis.class, this);
      this.messenger = new AbstractMessenger(serverName, (channel, message) -> {
         try (Jedis j = this.pool.getResource()) {
            channel = "NETWORK." + channel;
            j.publish(channel.getBytes(StandardCharsets.UTF_8), message);
         } catch (JedisExhaustedPoolException e) {
            Log.severe("ERROR: Jedis Pool is Exhausted!");
            crunchifyGenerateThreadDump();
         }
      }, (server, channel, message) -> {
         try (Jedis j = this.pool.getResource()) {
            channel = server + "." + channel;
            j.publish(channel.getBytes(StandardCharsets.UTF_8), message);
         } catch (JedisExhaustedPoolException ex) {
            Log.severe("ERROR: Jedis Pool is Exhausted!");
            crunchifyGenerateThreadDump();
         }
      }, channel -> {
         String lChannel = serverName + "." + channel;
         Log.info("[lumine-redis] Would have subscribed to channel: " + lChannel);
         this.channels.add(lChannel);
         String gChannel = "NETWORK." + channel;
         Log.info("[lumine-redis] Would have subscribed to: " + gChannel);
         this.channels.add(gChannel);
      }, channel -> {
         String lChannel = serverName + "." + channel;
         Log.info("[lumine-redis] Unsubscribing from channel: " + lChannel);
         this.channels.remove(lChannel);
         String gChannel = "NETWORK." + channel;
         Log.info("[lumine-redis] Unsubscribing from channel: " + gChannel);
         this.channels.remove(gChannel);
      });
   }

   private void openMessagingChannel(String channel) {
      Schedulers.async().run(() -> {
         while (!this.closed) {
            try (Jedis j = this.pool.getResource()) {
               Log.info("Opening messaging channel for " + channel + " class communications.");
               j.psubscribe(this.listener, new byte[][]{channel.getBytes(StandardCharsets.UTF_8)});
            }
         }
      }).bindWith(this.registry);
   }

   public static String crunchifyGenerateThreadDump() {
      StringBuilder dump = new StringBuilder();
      ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
      ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 1000);

      for (ThreadInfo threadInfo : threadInfos) {
         dump.append('"');
         dump.append(threadInfo.getThreadName());
         dump.append("\" ");
         State state = threadInfo.getThreadState();
         dump.append("\n   java.lang.Thread.State: ");
         dump.append(state);
         StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();

         for (StackTraceElement stackTraceElement : stackTraceElements) {
            dump.append("\n        at ");
            dump.append(stackTraceElement);
         }

         dump.append("\n\n");
      }

      return dump.toString();
   }

   @Override
   public void close() throws Exception {
      this.closed = true;
      if (this.listener != null) {
         this.listener.unsubscribe();
         this.listener = null;
      }

      if (this.pool != null) {
         this.pool.close();
      }

      this.registry.close();
   }

   @Nonnull
   @Override
   public <T> Channel<T> getChannel(@Nonnull String name, @Nonnull TypeToken<T> type) {
      return this.messenger.getChannel(name, type);
   }

   public String getServerName() {
      return this.serverName;
   }

   @Override
   public JedisPool getPool() {
      return this.pool;
   }

   public RedisCredentials getCredentials() {
      return this.credentials;
   }

   private final class PubSubListener extends BinaryJedisPubSub {
      @Override
      public void onPSubscribe(byte[] channel, int subscribedChannels) {
         Log.info("[lumine-redis] <MEMER> Subscribed to channel: " + new String(channel, StandardCharsets.UTF_8));
      }

      @Override
      public void onPUnsubscribe(byte[] channel, int subscribedChannels) {
         String channelName = new String(channel, StandardCharsets.UTF_8);
         Log.info("[lumine-redis] <MEMER> Unsubscribed from channel: " + channelName);
      }

      @Override
      public void onPMessage(byte[] pattern, byte[] channel, byte[] message) {
         String channelName = new String(channel, StandardCharsets.UTF_8);
         String[] spl = channelName.split("\\.");
         String server = spl[0];
         String subchannel = spl[1];

         try {
            RedisImpl.this.messenger.registerIncomingMessage(subchannel, server, message);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
}
