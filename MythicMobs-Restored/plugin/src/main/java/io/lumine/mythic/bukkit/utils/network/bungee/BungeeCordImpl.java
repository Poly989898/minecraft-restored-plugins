package io.lumine.mythic.bukkit.utils.network.bungee;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.lumine.mythic.bukkit.utils.Players;
import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

@NonnullByDefault
public final class BungeeCordImpl implements BungeeCord, PluginMessageListener {
   private static final String CHANNEL = "BungeeCord";
   private final LuminePlugin plugin;
   private final AtomicBoolean setup = new AtomicBoolean(false);
   private final List<BungeeCordImpl.MessageCallback> listeners = new LinkedList<>();
   private final ReentrantLock lock = new ReentrantLock();
   private final Set<BungeeCordImpl.MessageAgent> queuedMessages = ConcurrentHashMap.newKeySet();

   public BungeeCordImpl(LuminePlugin plugin) {
      this.plugin = plugin;
   }

   private void ensureSetup() {
      if (this.setup.compareAndSet(false, true)) {
         this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, "BungeeCord");
         this.plugin.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, "BungeeCord", this);
      }
   }

   public void onPluginMessageReceived(String channel, Player player, byte[] data) {
      if (channel.equals("BungeeCord")) {
         ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
         ByteArrayDataInput in = ByteStreams.newDataInput(byteIn);
         String subChannel = in.readUTF();
         byteIn.mark(0);
         this.lock.lock();

         try {
            Iterator<BungeeCordImpl.MessageCallback> it = this.listeners.iterator();

            while (it.hasNext()) {
               BungeeCordImpl.MessageCallback e = it.next();
               if (e.getSubChannel().equals(subChannel)) {
                  byteIn.reset();
                  boolean accepted = e.testResponse(player, in);
                  if (accepted) {
                     byteIn.reset();
                     boolean shouldRemove = e.acceptResponse(player, in);
                     if (shouldRemove) {
                        it.remove();
                     }
                  }
               }
            }
         } finally {
            this.lock.unlock();
         }
      }
   }

   private void sendMessage(BungeeCordImpl.MessageAgent agent) {
      Player player = agent.getHandle();
      if (player != null) {
         if (!player.isOnline()) {
            throw new IllegalStateException("Player not online");
         } else {
            this.sendToChannel(agent, player);
         }
      } else {
         player = (Player)Iterables.getFirst(Players.all(), null);
         if (player != null) {
            this.sendToChannel(agent, player);
         } else {
            this.queuedMessages.add(agent);
            this.ensureSetup();
         }
      }
   }

   private void flushQueuedMessages() {
      if (!this.queuedMessages.isEmpty()) {
         Player p = (Player)Iterables.getFirst(Players.all(), null);
         if (p != null) {
            this.queuedMessages.removeIf(ma -> {
               this.sendToChannel(ma, p);
               return true;
            });
         }
      }
   }

   private void sendToChannel(BungeeCordImpl.MessageAgent agent, Player player) {
      this.ensureSetup();
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF(agent.getSubChannel());
      agent.appendPayload(out);
      byte[] buf = out.toByteArray();
      player.sendPluginMessage(this.plugin, "BungeeCord", buf);
      if (agent instanceof BungeeCordImpl.MessageCallback callback) {
         this.registerCallback(callback);
      }
   }

   private void registerCallback(BungeeCordImpl.MessageCallback callback) {
      this.ensureSetup();
      this.lock.lock();

      try {
         this.listeners.add(callback);
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public void connect(Player player, String serverName) {
      this.sendMessage(new BungeeCordImpl.ConnectAgent(player, serverName));
   }

   @Override
   public void connectOther(String playerName, String serverName) {
      this.sendMessage(new BungeeCordImpl.ConnectOtherAgent(playerName, serverName));
   }

   @Override
   public Promise<Entry<String, Integer>> ip(Player player) {
      Promise<Entry<String, Integer>> fut = Promise.empty();
      this.sendMessage(new BungeeCordImpl.IPAgent(player, fut));
      return fut;
   }

   @Override
   public Promise<Integer> playerCount(String serverName) {
      Promise<Integer> fut = Promise.empty();
      this.sendMessage(new BungeeCordImpl.PlayerCountAgent(serverName, fut));
      return fut;
   }

   @Override
   public Promise<List<String>> playerList(String serverName) {
      Promise<List<String>> fut = Promise.empty();
      this.sendMessage(new BungeeCordImpl.PlayerListAgent(serverName, fut));
      return fut;
   }

   @Override
   public Promise<List<String>> getServers() {
      Promise<List<String>> fut = Promise.empty();
      this.sendMessage(new BungeeCordImpl.GetServersAgent(fut));
      return fut;
   }

   @Override
   public void message(String playerName, String message) {
      this.sendMessage(new BungeeCordImpl.PlayerMessageAgent(playerName, message));
   }

   @Override
   public Promise<String> getServer() {
      Promise<String> fut = Promise.empty();
      this.sendMessage(new BungeeCordImpl.GetServerAgent(fut));
      return fut;
   }

   @Override
   public Promise<UUID> uuid(Player player) {
      Promise<UUID> fut = Promise.empty();
      this.sendMessage(new BungeeCordImpl.UUIDAgent(player, fut));
      return fut;
   }

   @Override
   public Promise<UUID> uuidOther(String playerName) {
      Promise<UUID> fut = Promise.empty();
      this.sendMessage(new BungeeCordImpl.UUIDOtherAgent(playerName, fut));
      return fut;
   }

   @Override
   public Promise<Entry<String, Integer>> serverIp(String serverName) {
      Promise<Entry<String, Integer>> fut = Promise.empty();
      this.sendMessage(new BungeeCordImpl.ServerIPAgent(serverName, fut));
      return fut;
   }

   @Override
   public void kickPlayer(String playerName, String reason) {
      this.sendMessage(new BungeeCordImpl.KickPlayerAgent(playerName, reason));
   }

   @Override
   public void forward(String serverName, String channelName, byte[] data) {
      this.sendMessage(new BungeeCordImpl.ForwardAgent(serverName, channelName, data));
   }

   @Override
   public void forward(String serverName, String channelName, ByteArrayDataOutput data) {
      this.sendMessage(new BungeeCordImpl.ForwardAgent(serverName, channelName, data));
   }

   @Override
   public void forwardToPlayer(String playerName, String channelName, byte[] data) {
      this.sendMessage(new BungeeCordImpl.ForwardToPlayerAgent(playerName, channelName, data));
   }

   @Override
   public void forwardToPlayer(String playerName, String channelName, ByteArrayDataOutput data) {
      this.sendMessage(new BungeeCordImpl.ForwardToPlayerAgent(playerName, channelName, data));
   }

   @Override
   public void registerForwardCallbackRaw(String channelName, Predicate<byte[]> callback) {
      BungeeCordImpl.ForwardCustomCallback customCallback = new BungeeCordImpl.ForwardCustomCallback(channelName, callback);
      this.registerCallback(customCallback);
   }

   @Override
   public void registerForwardCallback(String channelName, Predicate<ByteArrayDataInput> callback) {
      Predicate<ByteArrayDataInput> cb = Objects.requireNonNull(callback, "callback");
      BungeeCordImpl.ForwardCustomCallback customCallback = new BungeeCordImpl.ForwardCustomCallback(
         channelName, bytes -> cb.test(ByteStreams.newDataInput(bytes))
      );
      this.registerCallback(customCallback);
   }

   private static final class ConnectAgent implements BungeeCordImpl.MessageAgent {
      private static final String CHANNEL = "Connect";
      private final Player player;
      private final String serverName;

      private ConnectAgent(Player player, String serverName) {
         this.player = Objects.requireNonNull(player, "player");
         this.serverName = Objects.requireNonNull(serverName, "serverName");
      }

      @Override
      public String getSubChannel() {
         return "Connect";
      }

      @Override
      public Player getHandle() {
         return this.player;
      }

      @Override
      public void appendPayload(ByteArrayDataOutput out) {
         out.writeUTF(this.serverName);
      }
   }

   private static final class ConnectOtherAgent implements BungeeCordImpl.MessageAgent {
      private static final String CHANNEL = "ConnectOther";
      private final String playerName;
      private final String serverName;

      private ConnectOtherAgent(String playerName, String serverName) {
         this.playerName = Objects.requireNonNull(playerName, "playerName");
         this.serverName = Objects.requireNonNull(serverName, "serverName");
      }

      @Override
      public String getSubChannel() {
         return "ConnectOther";
      }

      @Override
      public void appendPayload(ByteArrayDataOutput out) {
         out.writeUTF(this.playerName);
         out.writeUTF(this.serverName);
      }
   }

   private static final class ForwardAgent implements BungeeCordImpl.MessageAgent {
      private static final String CHANNEL = "Forward";
      private final String serverName;
      private final String channelName;
      private final byte[] data;

      private ForwardAgent(String serverName, String channelName, byte[] data) {
         this.serverName = Objects.requireNonNull(serverName, "serverName");
         this.channelName = Objects.requireNonNull(channelName, "channelName");
         this.data = data;
      }

      private ForwardAgent(String serverName, String channelName, ByteArrayDataOutput data) {
         this.serverName = Objects.requireNonNull(serverName, "serverName");
         this.channelName = Objects.requireNonNull(channelName, "channelName");
         this.data = Objects.requireNonNull(data, "data").toByteArray();
      }

      @Override
      public String getSubChannel() {
         return "Forward";
      }

      @Override
      public void appendPayload(ByteArrayDataOutput out) {
         out.writeUTF(this.serverName);
         out.writeUTF(this.channelName);
         out.writeShort(this.data.length);
         out.write(this.data);
      }
   }

   private static final class ForwardCustomCallback implements BungeeCordImpl.MessageCallback {
      private final String subChannel;
      private final Predicate<byte[]> callback;

      private ForwardCustomCallback(String subChannel, Predicate<byte[]> callback) {
         this.subChannel = Objects.requireNonNull(subChannel, "subChannel");
         this.callback = Objects.requireNonNull(callback, "callback");
      }

      @Override
      public String getSubChannel() {
         return this.subChannel;
      }

      @Override
      public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
         short len = in.readShort();
         byte[] data = new byte[len];
         in.readFully(data);
         return this.callback.test(data);
      }
   }

   private static final class ForwardToPlayerAgent implements BungeeCordImpl.MessageAgent {
      private static final String CHANNEL = "ForwardToPlayer";
      private final String playerName;
      private final String channelName;
      private final byte[] data;

      private ForwardToPlayerAgent(String playerName, String channelName, byte[] data) {
         this.playerName = Objects.requireNonNull(playerName, "playerName");
         this.channelName = Objects.requireNonNull(channelName, "channelName");
         this.data = data;
      }

      private ForwardToPlayerAgent(String playerName, String channelName, ByteArrayDataOutput data) {
         this.playerName = Objects.requireNonNull(playerName, "playerName");
         this.channelName = Objects.requireNonNull(channelName, "channelName");
         this.data = Objects.requireNonNull(data, "data").toByteArray();
      }

      @Override
      public String getSubChannel() {
         return "ForwardToPlayer";
      }

      @Override
      public void appendPayload(ByteArrayDataOutput out) {
         out.writeUTF(this.playerName);
         out.writeUTF(this.channelName);
         out.writeShort(this.data.length);
         out.write(this.data);
      }
   }

   private static final class GetServerAgent implements BungeeCordImpl.MessageAgent, BungeeCordImpl.MessageCallback {
      private static final String CHANNEL = "GetServer";
      private final Promise<String> callback;

      private GetServerAgent(Promise<String> callback) {
         this.callback = Objects.requireNonNull(callback, "callback");
      }

      @Override
      public String getSubChannel() {
         return "GetServer";
      }

      @Override
      public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
         this.callback.supply(in.readUTF());
         return true;
      }
   }

   private static final class GetServersAgent implements BungeeCordImpl.MessageAgent, BungeeCordImpl.MessageCallback {
      private static final String CHANNEL = "GetServers";
      private final Promise<List<String>> callback;

      private GetServersAgent(Promise<List<String>> callback) {
         this.callback = Objects.requireNonNull(callback, "callback");
      }

      @Override
      public String getSubChannel() {
         return "GetServers";
      }

      @Override
      public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
         String csv = in.readUTF();
         if (csv.isEmpty()) {
            this.callback.supply(ImmutableList.of());
            return true;
         } else {
            this.callback.supply(ImmutableList.copyOf(Splitter.on(", ").splitToList(csv)));
            return true;
         }
      }
   }

   private static final class IPAgent implements BungeeCordImpl.MessageAgent, BungeeCordImpl.MessageCallback {
      private static final String CHANNEL = "IP";
      private final Player player;
      private final Promise<Entry<String, Integer>> callback;

      private IPAgent(Player player, Promise<Entry<String, Integer>> callback) {
         this.player = Objects.requireNonNull(player, "player");
         this.callback = Objects.requireNonNull(callback, "callback");
      }

      @Override
      public String getSubChannel() {
         return "IP";
      }

      @Override
      public Player getHandle() {
         return this.player;
      }

      @Override
      public boolean testResponse(Player receiver, ByteArrayDataInput in) {
         return receiver.getUniqueId().equals(this.player.getUniqueId());
      }

      @Override
      public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
         String ip = in.readUTF();
         int port = in.readInt();
         this.callback.supply(Maps.immutableEntry(ip, port));
         return true;
      }
   }

   private static final class KickPlayerAgent implements BungeeCordImpl.MessageAgent {
      private static final String CHANNEL = "KickPlayer";
      private final String playerName;
      private final String reason;

      private KickPlayerAgent(String playerName, String reason) {
         this.playerName = Objects.requireNonNull(playerName, "playerName");
         this.reason = Objects.requireNonNull(reason, "reason");
      }

      @Override
      public String getSubChannel() {
         return "KickPlayer";
      }

      @Override
      public void appendPayload(ByteArrayDataOutput out) {
         out.writeUTF(this.playerName);
         out.writeUTF(this.reason);
      }
   }

   private interface MessageAgent {
      String getSubChannel();

      @Nullable
      default Player getHandle() {
         return null;
      }

      default void appendPayload(ByteArrayDataOutput out) {
      }
   }

   private interface MessageCallback {
      String getSubChannel();

      default boolean testResponse(Player receiver, ByteArrayDataInput in) {
         return true;
      }

      boolean acceptResponse(Player player, ByteArrayDataInput byteArrayDataInput);
   }

   private static final class PlayerCountAgent implements BungeeCordImpl.MessageAgent, BungeeCordImpl.MessageCallback {
      private static final String CHANNEL = "PlayerCount";
      private final String serverName;
      private final Promise<Integer> callback;

      private PlayerCountAgent(String serverName, Promise<Integer> callback) {
         this.serverName = Objects.requireNonNull(serverName, "serverName");
         this.callback = Objects.requireNonNull(callback, "callback");
      }

      @Override
      public String getSubChannel() {
         return "PlayerCount";
      }

      @Override
      public void appendPayload(ByteArrayDataOutput out) {
         out.writeUTF(this.serverName);
      }

      @Override
      public boolean testResponse(Player receiver, ByteArrayDataInput in) {
         return in.readUTF().equalsIgnoreCase(this.serverName);
      }

      @Override
      public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
         in.readUTF();
         int count = in.readInt();
         this.callback.supply(count);
         return true;
      }
   }

   private static final class PlayerListAgent implements BungeeCordImpl.MessageAgent, BungeeCordImpl.MessageCallback {
      private static final String CHANNEL = "PlayerList";
      private final String serverName;
      private final Promise<List<String>> callback;

      private PlayerListAgent(String serverName, Promise<List<String>> callback) {
         this.serverName = Objects.requireNonNull(serverName, "serverName");
         this.callback = Objects.requireNonNull(callback, "callback");
      }

      @Override
      public String getSubChannel() {
         return "PlayerList";
      }

      @Override
      public void appendPayload(ByteArrayDataOutput out) {
         out.writeUTF(this.serverName);
      }

      @Override
      public boolean testResponse(Player receiver, ByteArrayDataInput in) {
         return in.readUTF().equalsIgnoreCase(this.serverName);
      }

      @Override
      public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
         in.readUTF();
         String csv = in.readUTF();
         if (csv.isEmpty()) {
            this.callback.supply(ImmutableList.of());
            return true;
         } else {
            this.callback.supply(ImmutableList.copyOf(Splitter.on(", ").splitToList(csv)));
            return true;
         }
      }
   }

   private static final class PlayerMessageAgent implements BungeeCordImpl.MessageAgent {
      private static final String CHANNEL = "Message";
      private final String playerName;
      private final String message;

      private PlayerMessageAgent(String playerName, String message) {
         this.playerName = Objects.requireNonNull(playerName, "playerName");
         this.message = Objects.requireNonNull(message, "message");
      }

      @Override
      public String getSubChannel() {
         return "Message";
      }

      @Override
      public void appendPayload(ByteArrayDataOutput out) {
         out.writeUTF(this.playerName);
         out.writeUTF(this.message);
      }
   }

   private static final class ServerIPAgent implements BungeeCordImpl.MessageAgent, BungeeCordImpl.MessageCallback {
      private static final String CHANNEL = "ServerIP";
      private final String serverName;
      private final Promise<Entry<String, Integer>> callback;

      private ServerIPAgent(String serverName, Promise<Entry<String, Integer>> callback) {
         this.serverName = Objects.requireNonNull(serverName, "serverName");
         this.callback = Objects.requireNonNull(callback, "callback");
      }

      @Override
      public String getSubChannel() {
         return "ServerIP";
      }

      @Override
      public void appendPayload(ByteArrayDataOutput out) {
         out.writeUTF(this.serverName);
      }

      @Override
      public boolean testResponse(Player receiver, ByteArrayDataInput in) {
         return in.readUTF().equalsIgnoreCase(this.serverName);
      }

      @Override
      public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
         in.readUTF();
         String ip = in.readUTF();
         int port = in.readInt();
         this.callback.supply(Maps.immutableEntry(ip, port));
         return true;
      }
   }

   private static final class UUIDAgent implements BungeeCordImpl.MessageAgent, BungeeCordImpl.MessageCallback {
      private static final String CHANNEL = "UUID";
      private final Player player;
      private final Promise<UUID> callback;

      private UUIDAgent(Player player, Promise<UUID> callback) {
         this.player = Objects.requireNonNull(player, "player");
         this.callback = Objects.requireNonNull(callback, "callback");
      }

      @Override
      public String getSubChannel() {
         return "UUID";
      }

      @Override
      public Player getHandle() {
         return this.player;
      }

      @Override
      public boolean testResponse(Player receiver, ByteArrayDataInput in) {
         return receiver.getUniqueId().equals(this.player.getUniqueId());
      }

      @Override
      public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
         String uuid = in.readUTF();
         this.callback.supply(UUID.fromString(uuid));
         return true;
      }
   }

   private static final class UUIDOtherAgent implements BungeeCordImpl.MessageAgent, BungeeCordImpl.MessageCallback {
      private static final String CHANNEL = "UUIDOther";
      private final String playerName;
      private final Promise<UUID> callback;

      private UUIDOtherAgent(String playerName, Promise<UUID> callback) {
         this.playerName = Objects.requireNonNull(playerName, "playerName");
         this.callback = Objects.requireNonNull(callback, "callback");
      }

      @Override
      public String getSubChannel() {
         return "UUIDOther";
      }

      @Override
      public void appendPayload(ByteArrayDataOutput out) {
         out.writeUTF(this.playerName);
      }

      @Override
      public boolean testResponse(Player receiver, ByteArrayDataInput in) {
         return in.readUTF().equalsIgnoreCase(this.playerName);
      }

      @Override
      public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
         in.readUTF();
         String uuid = in.readUTF();
         this.callback.supply(UUID.fromString(uuid));
         return true;
      }
   }
}
