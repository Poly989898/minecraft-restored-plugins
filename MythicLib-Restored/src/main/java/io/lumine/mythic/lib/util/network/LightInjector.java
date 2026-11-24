package io.lumine.mythic.lib.util.network;

import com.mojang.authlib.GameProfile;
import io.lumine.mythic.lib.MythicLib;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public abstract class LightInjector {
   private static final String COMPLETE_VERSION = MythicLib.plugin.getVersion().getCraftBukkitVersion();
   private static final int VERSION = MythicLib.plugin.getVersion().getBukkitVersion()[1];
   private static final Class<?> SERVER_CLASS = getNMSClass("MinecraftServer", "server");
   private static final Class<?> SERVER_CONNECTION_CLASS = getNMSClass("ServerConnection", "server.network");
   private static final Class<?> NETWORK_MANAGER_CLASS = getNMSClass("NetworkManager", "network");
   private static final Class<?> ENTITY_PLAYER_CLASS = getNMSClass("EntityPlayer", "server.level");
   private static final Class<?> PLAYER_CONNECTION_CLASS = getNMSClass("PlayerConnection", "server.network");
   private static final Class<?> PACKET_LOGIN_OUT_SUCCESS_CLASS = getNMSClass("PacketLoginOutSuccess", "network.protocol.login");
   private static final Field NMS_SERVER = getField(getCBClass("CraftServer"), SERVER_CLASS, 1);
   private static final Field NMS_SERVER_CONNECTION = getField(SERVER_CLASS, SERVER_CONNECTION_CLASS, 1);
   private static final Field NMS_NETWORK_MANAGERS_LIST = getField(SERVER_CONNECTION_CLASS, List.class, 2);
   @Nullable
   private static final Field NMS_PENDING_NETWORK_MANAGERS = getPendingNetworkManagersFieldOrNull(SERVER_CONNECTION_CLASS);
   private static final Field NMS_CHANNEL_FROM_NM = getField(NETWORK_MANAGER_CLASS, Channel.class, 1);
   private static final Field GAME_PROFILE_FROM_PACKET = getField(PACKET_LOGIN_OUT_SUCCESS_CLASS, GameProfile.class, 1);
   private static final Field GET_PLAYER_CONNECTION = getField(ENTITY_PLAYER_CLASS, PLAYER_CONNECTION_CLASS, 1);
   private static final Field GET_NETWORK_MANAGER = getField(PLAYER_CONNECTION_CLASS, NETWORK_MANAGER_CLASS, 1, 1);
   private static final Method GET_PLAYER_HANDLE = getMethod(getCBClass("entity.CraftPlayer"), "getHandle");
   private static int ID = 0;
   private final Plugin plugin;
   private final String identifier;
   private final List<?> networkManagers;
   @Nullable
   private final Iterable<?> pendingNetworkManagers;
   private final LightInjector.EventListener listener = new LightInjector.EventListener();
   private final AtomicBoolean closed = new AtomicBoolean(false);
   private final Map<UUID, Player> playerCache = Collections.synchronizedMap(new HashMap<>());
   private final Set<Channel> injectedChannels = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

   public LightInjector(@NotNull Plugin plugin) {
      if (!Bukkit.isPrimaryThread()) {
         throw new IllegalStateException("LightInjector must be constructed on the main thread.");
      }

      if (!Objects.requireNonNull(plugin, "Plugin is null.").isEnabled()) {
         throw new IllegalArgumentException("Plugin " + plugin.getName() + " is not enabled");
      }

      this.plugin = plugin;
      this.identifier = Objects.requireNonNull(this.getIdentifier(), "getIdentifier() returned a null value.") + '-' + ID++;

      try {
         Object conn = NMS_SERVER_CONNECTION.get(NMS_SERVER.get(Bukkit.getServer()));
         if (conn == null) {
            throw new RuntimeException("[LightInjector] ServerConnection is null.");
         }

         this.networkManagers = (List<?>)NMS_NETWORK_MANAGERS_LIST.get(conn);
         if (NMS_PENDING_NETWORK_MANAGERS != null) {
            this.pendingNetworkManagers = (Iterable<?>)NMS_PENDING_NETWORK_MANAGERS.get(conn);
         } else {
            this.pendingNetworkManagers = null;
         }
      } catch (ReflectiveOperationException exception) {
         throw new RuntimeException("[LightInjector] An error occurred while injecting.", exception);
      }

      Bukkit.getPluginManager().registerEvents(this.listener, plugin);

      for (Player p : Bukkit.getOnlinePlayers()) {
         try {
            this.injectPlayer(p);
         } catch (Exception exception) {
            plugin.getLogger().log(Level.SEVERE, "[LightInjector] An error occurred while injecting a player:", exception);
         }
      }
   }

   @Nullable
   protected abstract Object onPacketReceiveAsync(@Nullable Player player, @NotNull Channel channel, @NotNull Object obj);

   @Nullable
   protected abstract Object onPacketSendAsync(@Nullable Player player, @NotNull Channel channel, @NotNull Object obj);

   public final void sendPacket(@NotNull Player receiver, @NotNull Object packet) {
      Objects.requireNonNull(receiver, "Player is null.");
      Objects.requireNonNull(packet, "Packet is null.");
      this.sendPacket(this.getChannel(receiver), packet);
   }

   public final void sendPacket(@NotNull Channel channel, @NotNull Object packet) {
      Objects.requireNonNull(channel, "Channel is null.");
      Objects.requireNonNull(packet, "Packet is null.");
      channel.pipeline().writeAndFlush(packet);
   }

   public final void receivePacket(@NotNull Player sender, @NotNull Object packet) {
      Objects.requireNonNull(sender, "Player is null.");
      Objects.requireNonNull(packet, "Packet is null.");
      this.receivePacket(this.getChannel(sender), packet);
   }

   public final void receivePacket(@NotNull Channel channel, @NotNull Object packet) {
      Objects.requireNonNull(channel, "Channel is null.");
      Objects.requireNonNull(packet, "Packet is null.");
      ChannelHandlerContext encoder = channel.pipeline().context("encoder");
      Objects.requireNonNull(encoder, "Channel is not a player channel").fireChannelRead(packet);
   }

   @NotNull
   protected String getIdentifier() {
      return "light-injector-" + this.plugin.getName();
   }

   public final void close() {
      if (!this.closed.getAndSet(true)) {
         this.listener.unregister();
         synchronized (this.networkManagers) {
            for (Object manager : this.networkManagers) {
               try {
                  Channel channel = this.getChannel(manager);
                  channel.eventLoop().submit(() -> channel.pipeline().remove(this.identifier));
               } catch (Exception exception) {
                  this.plugin.getLogger().log(Level.SEVERE, "[LightInjector] An error occurred while uninjecting a player:", exception);
               }
            }
         }

         this.playerCache.clear();
         this.injectedChannels.clear();
      }
   }

   public final boolean isClosed() {
      return this.closed.get();
   }

   @NotNull
   public final Plugin getPlugin() {
      return this.plugin;
   }

   private void injectPlayer(Player player) {
      this.injectChannel(this.getChannel(player)).player = player;
   }

   private LightInjector.PacketHandler injectChannel(Channel channel) {
      LightInjector.PacketHandler handler = new LightInjector.PacketHandler();
      channel.eventLoop()
         .submit(
            () -> {
               if (!this.isClosed()) {
                  if (this.injectedChannels.add(channel)) {
                     try {
                        channel.pipeline().addBefore("packet_handler", this.identifier, handler);
                     } catch (IllegalArgumentException ignored) {
                        this.plugin
                           .getLogger()
                           .severe("[LightInjector] Couldn't inject a player, an handler with identifier '" + this.identifier + "' is already present");
                     }
                  }
               }
            }
         );
      return handler;
   }

   private void injectNetworkManager(Object networkManager) {
      Channel channel = this.getChannel(networkManager);
      if (!this.injectedChannels.contains(channel)) {
         this.injectChannel(channel);
      }
   }

   private Object getNetworkManager(Player player) {
      try {
         return GET_NETWORK_MANAGER.get(GET_PLAYER_CONNECTION.get(GET_PLAYER_HANDLE.invoke(player)));
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("[LightInjector] Couldn't get player's network manager.", e);
      }
   }

   private Channel getChannel(Player player) {
      return this.getChannel(this.getNetworkManager(player));
   }

   private Channel getChannel(Object networkManager) {
      try {
         return (Channel)NMS_CHANNEL_FROM_NM.get(networkManager);
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException("[LightInjector] Couldn't get network manager's channel.", e);
      }
   }

   private static Class<?> getNMSClass(String name, String mcPackage) {
      String path = "net.minecraft." + (VERSION >= 17 ? mcPackage : "server." + COMPLETE_VERSION) + '.' + name;

      try {
         return Class.forName(path);
      } catch (ClassNotFoundException exception) {
         throw new RuntimeException("[LightInjector] Cannot find NMS Class! (" + path + ')', exception);
      }
   }

   private static Class<?> getCBClass(String name) {
      String clazz = "org.bukkit.craftbukkit." + COMPLETE_VERSION + "." + name;

      try {
         return Class.forName(clazz);
      } catch (ClassNotFoundException exception) {
         throw new RuntimeException("[LightInjector] Cannot find CB Class! (" + clazz + ')', exception);
      }
   }

   private static Field getField(Class<?> clazz, String name) {
      try {
         Field f = clazz.getDeclaredField(name);
         f.setAccessible(true);
         return f;
      } catch (ReflectiveOperationException exception) {
         throw new RuntimeException("[LightInjector] Cannot find field! (" + clazz.getName() + '.' + name + ')', exception);
      }
   }

   private static Field getField(Class<?> clazz, Class<?> type, @Range(from = 1L, to = 2147483647L) int index) {
      return getField(clazz, type, index, 0);
   }

   private static Field getField(
      Class<?> clazz, Class<?> type, @Range(from = 1L, to = 2147483647L) int index, @Range(from = 0L, to = 2147483647L) int superClassesToTry
   ) {
      Class<?> savedClazz = clazz;
      int savedIndex = index;

      for (int i = 0; i <= superClassesToTry; i++) {
         Field[] fields = clazz.getDeclaredFields();

         for (Field f : fields) {
            if (type.equals(f.getType())) {
               if (--index <= 0) {
                  f.setAccessible(true);
                  return f;
               }
            }
         }

         index = savedIndex;

         for (Field f : fields) {
            if (type.isAssignableFrom(f.getType())) {
               if (--index <= 0) {
                  f.setAccessible(true);
                  return f;
               }
            }
         }

         clazz = clazz.getSuperclass();
         if (clazz == null || clazz == Object.class) {
            break;
         }

         index = savedIndex;
      }

      String errorMsg = "[LightInjector] Cannot find field! (" + savedIndex + getOrdinal(savedIndex) + type.getName() + " in " + savedClazz.getName();
      if (superClassesToTry > 0) {
         errorMsg = errorMsg + " and in its " + superClassesToTry + (superClassesToTry == 1 ? " super class" : " super classes");
      }

      errorMsg = errorMsg + ')';
      throw new RuntimeException(errorMsg);
   }

   @Nullable
   private static Field getPendingNetworkManagersFieldOrNull(Class<?> serverConnectionClass) {
      try {
         Field pending = getField(serverConnectionClass, "pending");
         if (pending.getType() == Queue.class || pending.getType() == List.class) {
            return pending;
         }
      } catch (Exception exception) {
      }

      try {
         return getField(serverConnectionClass, Queue.class, 1);
      } catch (Exception exception) {
         try {
            return getField(serverConnectionClass, List.class, 3);
         } catch (Exception fallbackException) {
            return null;
         }
      }
   }

   private static Method getMethod(Class<?> clazz, String name, Class<?>... parameters) {
      try {
         Method m = clazz.getDeclaredMethod(name, parameters);
         m.setAccessible(true);
         return m;
      } catch (ReflectiveOperationException exception) {
         StringJoiner params = new StringJoiner(", ");

         for (Class<?> p : parameters) {
            params.add(p.getName());
         }

         throw new RuntimeException("[LightInjector] Cannot find method! (" + clazz.getName() + '.' + name + '(' + params.toString() + ')', exception);
      }
   }

   private static Method getMethod(Class<?> clazz, Class<?> returnType, @Range(from = 1L, to = 2147483647L) int index) {
      int savedIndex = index;
      Method[] methods = clazz.getDeclaredMethods();

      for (Method m : methods) {
         if (returnType.equals(m.getReturnType())) {
            if (--index <= 0) {
               m.setAccessible(true);
               return m;
            }
         }
      }

      index = savedIndex;

      for (Method m : methods) {
         if (returnType.isAssignableFrom(m.getReturnType())) {
            if (--index <= 0) {
               m.setAccessible(true);
               return m;
            }
         }
      }

      throw new RuntimeException(
         "[LightInjector] Cannot find method! (" + savedIndex + getOrdinal(savedIndex) + " returning " + returnType.getName() + " in " + clazz.getName() + ')'
      );
   }

   private static String getOrdinal(int i) {
      switch (i) {
         case 1:
            return "st ";
         case 2:
            return "nd ";
         case 3:
            return "rd ";
         default:
            return "th ";
      }
   }

   private final class EventListener implements Listener {
      private EventListener() {
      }

      @EventHandler(priority = EventPriority.LOWEST)
      private void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
         if (!LightInjector.this.isClosed()) {
            synchronized (LightInjector.this.networkManagers) {
               if (LightInjector.this.networkManagers instanceof RandomAccess) {
                  for (int i = LightInjector.this.networkManagers.size() - 1; i >= 0; i--) {
                     Object networkManager = LightInjector.this.networkManagers.get(i);
                     LightInjector.this.injectNetworkManager(networkManager);
                  }
               } else {
                  for (Object networkManager : LightInjector.this.networkManagers) {
                     LightInjector.this.injectNetworkManager(networkManager);
                  }
               }

               if (LightInjector.this.pendingNetworkManagers != null) {
                  synchronized (LightInjector.this.pendingNetworkManagers) {
                     for (Object networkManager : LightInjector.this.pendingNetworkManagers) {
                        LightInjector.this.injectNetworkManager(networkManager);
                     }
                  }
               }
            }
         }
      }

      @EventHandler(priority = EventPriority.LOWEST)
      private void onPlayerLoginEvent(PlayerLoginEvent event) {
         if (!LightInjector.this.isClosed()) {
            LightInjector.this.playerCache.put(event.getPlayer().getUniqueId(), event.getPlayer());
         }
      }

      @EventHandler(priority = EventPriority.LOWEST)
      private void onPlayerJoinEvent(PlayerJoinEvent event) {
         if (!LightInjector.this.isClosed()) {
            Player player = event.getPlayer();
            Object networkManager = LightInjector.this.getNetworkManager(player);
            Channel channel = LightInjector.this.getChannel(networkManager);
            ChannelHandler channelHandler = channel.pipeline().get(LightInjector.this.identifier);
            if (channelHandler != null) {
               if (channelHandler instanceof LightInjector.PacketHandler) {
                  ((LightInjector.PacketHandler)channelHandler).player = player;
                  LightInjector.this.playerCache.remove(player.getUniqueId());
               }
            } else {
               LightInjector.this.plugin.getLogger().info("[LightInjector] Late injection for player " + player.getName());
               LightInjector.this.injectChannel(channel).player = player;
            }
         }
      }

      @EventHandler(priority = EventPriority.MONITOR)
      private void onPluginDisableEvent(PluginDisableEvent event) {
         if (LightInjector.this.plugin.equals(event.getPlugin())) {
            LightInjector.this.close();
         }
      }

      private void unregister() {
         AsyncPlayerPreLoginEvent.getHandlerList().unregister(this);
         PlayerLoginEvent.getHandlerList().unregister(this);
         PlayerJoinEvent.getHandlerList().unregister(this);
         PluginDisableEvent.getHandlerList().unregister(this);
      }
   }

   private final class PacketHandler extends ChannelDuplexHandler {
      private volatile Player player;

      private PacketHandler() {
      }

      public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
         LightInjector.this.injectedChannels.remove(ctx.channel());
         super.channelUnregistered(ctx);
      }

      public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
         if (this.player == null && LightInjector.PACKET_LOGIN_OUT_SUCCESS_CLASS.isInstance(packet)) {
            try {
               Player player = LightInjector.this.playerCache.remove(((GameProfile)LightInjector.GAME_PROFILE_FROM_PACKET.get(packet)).getId());
               if (player != null) {
                  this.player = player;
               }
            } catch (ReflectiveOperationException exception) {
               LightInjector.this.plugin.getLogger().log(Level.SEVERE, "[LightInjector] An error occurred while handling PacketLoginOutSuccess:", exception);
            }
         }

         Object newPacket;
         try {
            newPacket = LightInjector.this.onPacketSendAsync(this.player, ctx.channel(), packet);
         } catch (OutOfMemoryError error) {
            throw error;
         } catch (Throwable throwable) {
            LightInjector.this.plugin.getLogger().log(Level.SEVERE, "[LightInjector] An error occurred while calling onPacketSendAsync:", throwable);
            super.write(ctx, packet, promise);
            return;
         }

         if (newPacket != null) {
            super.write(ctx, newPacket, promise);
         }
      }

      public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
         Object newPacket;
         try {
            newPacket = LightInjector.this.onPacketReceiveAsync(this.player, ctx.channel(), packet);
         } catch (OutOfMemoryError error) {
            throw error;
         } catch (Throwable throwable) {
            LightInjector.this.plugin.getLogger().log(Level.SEVERE, "[LightInjector] An error occurred while calling onPacketReceiveAsync:", throwable);
            super.channelRead(ctx, packet);
            return;
         }

         if (newPacket != null) {
            super.channelRead(ctx, newPacket);
         }
      }
   }
}
