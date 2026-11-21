package com.ticxo.modelengine.v1_20_R1.network;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.nms.network.ClientDesyncMonitor;
import com.ticxo.modelengine.api.nms.network.NetworkHandler;
import com.ticxo.modelengine.api.nms.network.PipelineWrapper;
import com.ticxo.modelengine.api.nms.network.ProtectedPacketUnpacker;
import com.ticxo.modelengine.api.utils.ReflectionUtils;
import com.ticxo.modelengine.v1_20_R1.NMSFields;
import com.ticxo.modelengine.v1_20_R1.network.utils.Bundler;
import com.ticxo.modelengine.v1_20_R1.network.utils.NetworkUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import lombok.Getter;
import net.minecraft.SharedConstants;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPingPacket;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NetworkHandlerImpl implements NetworkHandler {
   public static NetworkHandlerImpl instance;
   private final Map<UUID, PipelineWrapper> pipelines = Maps.newConcurrentMap();
   private final Map<UUID, Bundler> bundles = Maps.newConcurrentMap();
   @Getter
   private volatile boolean isBatching;

   public NetworkHandlerImpl() {
      if (instance != null) {
         throw new IllegalStateException("Network handler already initialized");
      }

      instance = this;
   }

   @Override
   public int getProtocolVersion() {
      return SharedConstants.c();
   }

   @Override
   public Optional<PipelineWrapper> getPipeline(UUID uuid) {
      return Optional.ofNullable(this.pipelines.get(uuid));
   }

   @Override
   public void removePipeline(UUID uuid) {
      this.pipelines.remove(uuid);
   }

   @Override
   public void injectChannel(Player player) {
      PlayerConnection listener = ((CraftPlayer)player).getHandle().c;
      NetworkManager connection = ReflectionUtils.get(listener, NMSFields.SERVER_GAME_PACKET_LISTENER_IMPL_connection);
      ChannelPipeline pipeline = connection.m.pipeline();
      PipelineWrapper wrapper = new PipelineWrapper(player, connection.m);
      ModelEngineChannelHandler handler = new ModelEngineChannelHandler(player, wrapper);
      this.pipelines.put(player.getUniqueId(), wrapper);
      this.bundles.put(player.getUniqueId(), new Bundler());

      for (String name : pipeline.toMap().keySet()) {
         if (pipeline.get(name) instanceof NetworkManager) {
            pipeline.addBefore(name, "protected_packet_unpacker", new ProtectedPacketUnpacker());
            pipeline.addBefore(name, "model_engine_packet_handler", handler);
            break;
         }
      }
   }

   @Override
   public void ejectChannel(Player player) {
      PlayerConnection listener = ((CraftPlayer)player).getHandle().c;
      NetworkManager connection = ReflectionUtils.get(listener, NMSFields.SERVER_GAME_PACKET_LISTENER_IMPL_connection);
      Channel channel = connection.m;
      channel.eventLoop().submit(() -> {
         channel.pipeline().remove("protected_packet_unpacker");
         channel.pipeline().remove("model_engine_packet_handler");
         return null;
      });
      this.removePipeline(player.getUniqueId());
      this.bundles.remove(player.getUniqueId());
   }

   @Override
   public void ping(UUID uuid) {
      this.getPipeline(uuid).ifPresent(wrapper -> {
         ClientDesyncMonitor monitor = wrapper.getDesyncMonitor();
         if (monitor.shouldPing()) {
            monitor.markPingSent();
            int id = monitor.getPingCounter().getAndIncrement();
            NetworkUtils.sendNow(uuid, new ClientboundPingPacket(id));
         }
      });
   }

   @Override
   public void startBatch() {
      this.isBatching = true;
   }

   @Override
   public void endBatch() {
      this.isBatching = false;

      for (Entry<UUID, Bundler> entry : this.bundles.entrySet()) {
         PipelineWrapper pipeline = this.pipelines.get(entry.getKey());
         if (pipeline != null) {
            Bundler bundler = entry.getValue();
            synchronized (bundler) {
               bundler.bundle(pipeline::writeAndFlush);
               bundler.clear();
            }
         }
      }
   }

   public void appendPacket(UUID uuid, Packet packet) {
      Bundler bundler = this.bundles.get(uuid);
      if (bundler != null) {
         bundler.appendPacket(packet);
      }
   }

   public void appendPackets(UUID uuid, Collection<? extends Packet> collection) {
      Bundler bundler = this.bundles.get(uuid);
      if (bundler != null) {
         bundler.appendPacket(collection);
      }
   }
}
