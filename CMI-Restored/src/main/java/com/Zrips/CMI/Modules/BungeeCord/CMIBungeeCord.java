package com.Zrips.CMI.Modules.BungeeCord;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class CMIBungeeCord {
  private static WeakHashMap<Plugin, CMIBungeeCord> registeredInstances = new WeakHashMap<>();
  private final PluginMessageListener messageListener;
  private final Plugin plugin;
  private final Map<String, Queue<CompletableFuture<?>>> callbackMap;
  private Map<String, CMIBungeeCord.ForwardConsumer> forwardListeners;
  private CMIBungeeCord.ForwardConsumer globalForwardListener;

  public static synchronized CMIBungeeCord of(Plugin owner) {
    return registeredInstances.compute(
        owner,
        (registeredOwner, cmiBungeeCord) -> {
          if (cmiBungeeCord == null) {
            cmiBungeeCord = new CMIBungeeCord(owner);
          }

          return (CMIBungeeCord) cmiBungeeCord;
        });
  }

  public CMIBungeeCord(Plugin owner) {
    this.plugin = Objects.requireNonNull(owner, "plugin cannot be null");
    this.callbackMap = new HashMap<>();
    synchronized (registeredInstances) {
      registeredInstances.compute(
          owner,
          (registeredOwner, cmiBungeeCord) -> {
            if (cmiBungeeCord != null) {
              cmiBungeeCord.unregister();
            }

            return this;
          });
    }

    this.messageListener = this::onPluginMessageReceived;
    Messenger messenger = Bukkit.getServer().getMessenger();
    messenger.registerOutgoingPluginChannel(owner, "BungeeCord");
    messenger.registerIncomingPluginChannel(owner, "cmib:fromproxy", this.messageListener);
    messenger.registerIncomingPluginChannel(owner, "BungeeCord", this.messageListener);
  }

  public void registerForwardListener(CMIBungeeCord.ForwardConsumer forwardConsumer) {
    this.globalForwardListener = forwardConsumer;
  }

  public void registerForwardListener(
      String string, CMIBungeeCord.ForwardConsumer forwardConsumer) {
    if (this.forwardListeners == null) {
      this.forwardListeners = new HashMap<>();
    }

    synchronized (this.forwardListeners) {
      this.forwardListeners.put(string, forwardConsumer);
    }
  }

  public CompletableFuture<Integer> getPlayerCount(String string) {
    Player player = this.getFirstPlayer();
    CompletableFuture completableFuture = new CompletableFuture();
    if (player == null) {
      return completableFuture;
    }

    synchronized (this.callbackMap) {
      this.callbackMap.compute("PlayerCount-" + string, this.computeQueueValue(completableFuture));
    }

    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
    byteArrayDataOutput.writeUTF("PlayerCount");
    byteArrayDataOutput.writeUTF(string);
    player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    return completableFuture;
  }

  public CompletableFuture<String> getServerPlayerCount(String string) {
    Player player = this.getFirstPlayer();
    CompletableFuture completableFuture = new CompletableFuture();
    if (player == null) {
      return completableFuture;
    }

    synchronized (this.callbackMap) {
      this.callbackMap.compute(
          "CMIServerPlayerCounts-" + string, this.computeQueueValue(completableFuture));
    }

    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
    byteArrayDataOutput.writeUTF("CMIServerPlayerCounts");
    byteArrayDataOutput.writeUTF(string);
    player.sendPluginMessage(this.plugin, "cmib:fromserver", byteArrayDataOutput.toByteArray());
    return completableFuture;
  }

  public CompletableFuture<List<String>> getServersPlayerCount() {
    Player player = this.getFirstPlayer();
    CompletableFuture completableFuture = new CompletableFuture();
    if (player == null) {
      return completableFuture;
    }

    synchronized (this.callbackMap) {
      this.callbackMap.compute("CMIServersPlayerCounts", this.computeQueueValue(completableFuture));
    }

    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
    byteArrayDataOutput.writeUTF("CMIServersPlayerCounts");
    player.sendPluginMessage(this.plugin, "cmib:fromserver", byteArrayDataOutput.toByteArray());
    return completableFuture;
  }

  public CompletableFuture<List<String>> getPlayerList(String string) {
    Player player = this.getFirstPlayer();
    CompletableFuture completableFuture = new CompletableFuture();
    if (player == null) {
      return completableFuture;
    }

    synchronized (this.callbackMap) {
      this.callbackMap.compute("PlayerList-" + string, this.computeQueueValue(completableFuture));
    }

    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
    byteArrayDataOutput.writeUTF("PlayerList");
    byteArrayDataOutput.writeUTF(string);
    player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    return completableFuture;
  }

  public CompletableFuture<List<String>> getServers() {
    Player player = this.getFirstPlayer();
    CompletableFuture completableFuture = new CompletableFuture();
    if (player == null) {
      return completableFuture;
    }

    synchronized (this.callbackMap) {
      this.callbackMap.compute("GetServers", this.computeQueueValue(completableFuture));
    }

    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
    byteArrayDataOutput.writeUTF("GetServers");
    player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    return completableFuture;
  }

  public void connect(Player player, String string) {
    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
    byteArrayDataOutput.writeUTF("Connect");
    byteArrayDataOutput.writeUTF(string);
    player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
  }

  public void connectOther(String string, String server) {
    Player player = this.getFirstPlayer();
    if (player != null) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("ConnectOther");
      byteArrayDataOutput.writeUTF(string);
      byteArrayDataOutput.writeUTF(server);
      player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    }
  }

  public CompletableFuture<InetSocketAddress> getIp(Player player) {
    CompletableFuture completableFuture = new CompletableFuture();
    synchronized (this.callbackMap) {
      this.callbackMap.compute("IP", this.computeQueueValue(completableFuture));
    }

    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
    byteArrayDataOutput.writeUTF("IP");
    player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    return completableFuture;
  }

  public void sendMessage(String string, String message) {
    Player player = this.getFirstPlayer();
    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
    if (player != null) {
      byteArrayDataOutput.writeUTF("Message");
      byteArrayDataOutput.writeUTF(string);
      byteArrayDataOutput.writeUTF(message);
      player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    }
  }

  public CompletableFuture<String> getServer() {
    Player player = this.getFirstPlayer();
    CompletableFuture completableFuture = new CompletableFuture();
    if (player == null) {
      return completableFuture;
    }

    synchronized (this.callbackMap) {
      this.callbackMap.compute("GetServer", this.computeQueueValue(completableFuture));
    }

    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
    byteArrayDataOutput.writeUTF("GetServer");
    player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    return completableFuture;
  }

  public CompletableFuture<String> getUUID(Player player) {
    CompletableFuture completableFuture = new CompletableFuture();
    synchronized (this.callbackMap) {
      this.callbackMap.compute("UUID", this.computeQueueValue(completableFuture));
    }

    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
    byteArrayDataOutput.writeUTF("UUID");
    player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    return completableFuture;
  }

  public CompletableFuture<String> getUUID(String string) {
    Player player = this.getFirstPlayer();
    CompletableFuture completableFuture = new CompletableFuture();
    if (player == null) {
      return completableFuture;
    }

    synchronized (this.callbackMap) {
      this.callbackMap.compute("UUIDOther-" + string, this.computeQueueValue(completableFuture));
    }

    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
    byteArrayDataOutput.writeUTF("UUIDOther");
    byteArrayDataOutput.writeUTF(string);
    player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    return completableFuture;
  }

  public CompletableFuture<InetSocketAddress> getServerIp(String string) {
    Player player = this.getFirstPlayer();
    CompletableFuture completableFuture = new CompletableFuture();
    if (player == null) {
      return completableFuture;
    }

    synchronized (this.callbackMap) {
      this.callbackMap.compute("ServerIP-" + string, this.computeQueueValue(completableFuture));
    }

    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
    byteArrayDataOutput.writeUTF("ServerIP");
    byteArrayDataOutput.writeUTF(string);
    player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    return completableFuture;
  }

  public void kickPlayer(String string, String reason) {
    Player player = this.getFirstPlayer();
    if (player != null) {
      CompletableFuture completableFuture = new CompletableFuture();
      synchronized (this.callbackMap) {
        this.callbackMap.compute("KickPlayer", this.computeQueueValue(completableFuture));
      }

      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("KickPlayer");
      byteArrayDataOutput.writeUTF(string);
      byteArrayDataOutput.writeUTF(reason);
      player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    }
  }

  public void forward(String string, String channel, byte[] bs) {
    Player player = this.getFirstPlayer();
    if (player != null) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("Forward");
      byteArrayDataOutput.writeUTF(string);
      byteArrayDataOutput.writeUTF(channel);
      byteArrayDataOutput.writeShort(bs.length);
      byteArrayDataOutput.write(bs);
      player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    }
  }

  public void forwardToPlayer(String string, String channel, byte[] bs) {
    Player player = this.getFirstPlayer();
    if (player != null) {
      ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
      byteArrayDataOutput.writeUTF("ForwardToPlayer");
      byteArrayDataOutput.writeUTF(string);
      byteArrayDataOutput.writeUTF(channel);
      byteArrayDataOutput.writeShort(bs.length);
      byteArrayDataOutput.write(bs);
      player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayDataOutput.toByteArray());
    }
  }

  private void onPluginMessageReceived(String string, Player player, byte[] bs) {
    if ((string.equalsIgnoreCase("BungeeCord") || string.equalsIgnoreCase("cmib:fromproxy"))
        && bs != null) {
      ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(bs);
      String server = null;

      try {
        server = byteArrayDataInput.readUTF();
      } catch (Throwable throwable) {
        return;
      }

      synchronized (this.callbackMap) {
        try {
          if (server.equals("PlayerCount")
              || server.equals("PlayerList")
              || server.equals("UUIDOther")
              || server.equals("ServerIP")
              || server.equals("CMIServerPlayerCounts")) {
            String subchannel = byteArrayDataInput.readUTF();
            var queue = this.callbackMap.get(server + "-" + subchannel);
            if (queue == null || queue.isEmpty()) {
              return;
            }

            CompletableFuture completableFuture = (CompletableFuture) queue.poll();

            try {
              String serverName = server;
              switch (server.hashCode()) {
                case -2095967602:
                  if (serverName.equals("PlayerCount")) {
                    completableFuture.complete(byteArrayDataInput.readInt());
                  }

                  return;
                case -205896897:
                  if (serverName.equals("PlayerList")) {
                    completableFuture.complete(
                        Arrays.asList(byteArrayDataInput.readUTF().split(", ")));
                  }

                  return;
                case 1002306023:
                  if (serverName.equals("CMIServerPlayerCounts")) {
                    completableFuture.complete(byteArrayDataInput.readUTF());
                  }

                  return;
                case 1186775061:
                  if (serverName.equals("UUIDOther")) {
                    String formattedUuid =
                        byteArrayDataInput
                            .readUTF()
                            .replaceFirst(
                                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                                "$1-$2-$3-$4-$5");
                    completableFuture.complete(formattedUuid);
                  }

                  return;
                case 1443747786:
                  if (serverName.equals("ServerIP")) {
                    String host = byteArrayDataInput.readUTF();
                    int value = byteArrayDataInput.readUnsignedShort();
                    completableFuture.complete(new InetSocketAddress(host, value));
                    return;
                  }
              }
            } catch (Exception exception) {
              completableFuture.completeExceptionally(exception);
            }

            return;
          }
        } catch (Throwable throwable2) {
          return;
        }

        var pendingCallbacks = this.callbackMap.get(server);
        if (pendingCallbacks == null) {
          try {
            short shortValue = byteArrayDataInput.readShort();
            byte[] payload = new byte[shortValue];
            byteArrayDataInput.readFully(payload);
            if (this.globalForwardListener != null) {
              this.globalForwardListener.accept(server, player, payload);
            }

            if (this.forwardListeners != null) {
              synchronized (this.forwardListeners) {
                CMIBungeeCord.ForwardConsumer forwardConsumer = this.forwardListeners.get(server);
                if (forwardConsumer != null) {
                  forwardConsumer.accept(server, player, payload);
                }
              }
            }
          } catch (Throwable throwable3) {
          }
        } else if (!pendingCallbacks.isEmpty()) {
          CompletableFuture pendingFuture = (CompletableFuture) pendingCallbacks.poll();

          try {
            String channel = server;
            switch (server.hashCode()) {
              case -1500810727:
                if (!channel.equals("GetServer")) {
                  return;
                }
                break;
              case 2343:
                if (channel.equals("IP")) {
                  String host = byteArrayDataInput.readUTF();
                  int port = byteArrayDataInput.readInt();
                  pendingFuture.complete(new InetSocketAddress(host, port));
                }

                return;
              case 2616251:
                if (!channel.equals("UUID")) {
                  return;
                }
                break;
              case 719507834:
                if (channel.equals("GetServers")) {
                  pendingFuture.complete(Arrays.asList(byteArrayDataInput.readUTF().split(", ")));
                }

                return;
              case 959822230:
                if (channel.equals("CMIServersPlayerCounts")) {
                  pendingFuture.complete(Arrays.asList(byteArrayDataInput.readUTF().split(";;")));
                }

                return;
              default:
                return;
            }

            pendingFuture.complete(byteArrayDataInput.readUTF());
          } catch (Exception exception2) {
            pendingFuture.completeExceptionally(exception2);
          }
        }
      }
    }
  }

  public void unregister() {
    Messenger messenger = Bukkit.getServer().getMessenger();
    messenger.unregisterIncomingPluginChannel(this.plugin, "BungeeCord", this.messageListener);
    messenger.unregisterOutgoingPluginChannel(this.plugin);
    this.callbackMap.clear();
  }

  private BiFunction<String, Queue<CompletableFuture<?>>, Queue<CompletableFuture<?>>>
      computeQueueValue(CompletableFuture<?> completableFuture) {
    return (string, queue) -> {
      if (queue == null) {
        queue = new ArrayDeque<>();
      }

      queue.add(completableFuture);
      return queue;
    };
  }

  private Player getFirstPlayer() {
    return this.getFirstPlayer(Bukkit.getOnlinePlayers());
  }

  private Player getFirstPlayer(Collection<? extends Player> collection) {
    return (Player) Iterables.getFirst(collection, null);
  }

  @FunctionalInterface
  public interface ForwardConsumer {
    void accept(String string, Player player, byte[] bs);
  }
}
