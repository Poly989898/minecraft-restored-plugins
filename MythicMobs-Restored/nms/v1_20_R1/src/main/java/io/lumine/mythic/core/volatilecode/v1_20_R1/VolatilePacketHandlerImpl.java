package io.lumine.mythic.core.volatilecode.v1_20_R1;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatilePacketHandler;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.reflection.Reflector;
import io.lumine.mythic.core.volatilecode.v1_20_R1.packets.VolatileChannelHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.level.PlayerChunkMap.EntityTracker;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VolatilePacketHandlerImpl implements VolatilePacketHandler {
   private Reflector<PlayerConnection> refConnection = new Reflector<>(PlayerConnection.class, "h");
   private final Map<UUID, ChannelPipeline> pipelines = Maps.newConcurrentMap();
   private boolean doPlayerActionEvent;

   public VolatilePacketHandlerImpl(VolatileCodeHandler handler) {
      CraftServer craftServer = (CraftServer)Bukkit.getServer();
      Events.subscribe(PlayerJoinEvent.class).handler(event -> this.injectPlayer(event.getPlayer()));
      Events.subscribe(PlayerQuitEvent.class).handler(event -> this.ejectPlayer(event.getPlayer()));
   }

   @Override
   public void injectPlayer(Player player) {
      EntityPlayer ply = ((CraftPlayer)player).getHandle();
      VolatileChannelHandler cdh = new VolatileChannelHandler(this, player);
      if (this.doPlayerActionEvent) {
         cdh.enablePlayerActionEvent();
      }

      PlayerConnection listener = ((CraftPlayer)player).getHandle().c;
      NetworkManager connection = (NetworkManager)this.refConnection.get(listener, "h");
      ChannelPipeline pipeline = connection.m.pipeline();
      this.pipelines.put(player.getUniqueId(), pipeline);

      for (String name : pipeline.toMap().keySet()) {
         if (pipeline.get(name) instanceof NetworkManager) {
            pipeline.addBefore(name, "mythic_packet_handler", cdh);
            break;
         }
      }
   }

   @Override
   public void ejectPlayer(Player player) {
      PlayerConnection listener = ((CraftPlayer)player).getHandle().c;
      NetworkManager connection = (NetworkManager)this.refConnection.get(listener, "h");
      Channel channel = connection.m;
      channel.eventLoop().submit(() -> {
         channel.pipeline().remove("mythic_packet_handler");
         return null;
      });
      this.pipelines.remove(player.getUniqueId());
   }

   @Override
   public void enablePlayerActionEvent() {
      this.doPlayerActionEvent = true;
      this.pipelines.values().forEach(pipeline -> ((VolatileChannelHandler)pipeline.get("mythic_packet_handler")).enablePlayerActionEvent());
   }

   public void broadcast(Packet<?>... packets) {
      for (Player player : Bukkit.getOnlinePlayers()) {
         PlayerConnection connection = ((CraftPlayer)player).getHandle().c;

         for (Packet<?> packet : packets) {
            connection.a(packet);
         }
      }
   }

   public void broadcast(Player player, Packet<?>... packets) {
      PlayerConnection connection = ((CraftPlayer)player).getHandle().c;

      for (Packet<?> packet : packets) {
         connection.a(packet);
      }
   }

   public void broadcastAroundAndSelf(Player wearer, Packet<?>... packets) {
      WorldServer level = ((CraftWorld)wearer.getWorld()).getHandle();
      EntityTracker trackedEntity = (EntityTracker)level.k().a.K.get(wearer.getEntityId());
      if (trackedEntity == null) {
         this.broadcast(wearer.getWorld(), packets);
      } else {
         for (Packet<?> packet : packets) {
            trackedEntity.b(packet);
         }
      }
   }

   public void broadcastAround(Player wearer, Packet<?>... packets) {
      WorldServer level = ((CraftWorld)wearer.getWorld()).getHandle();
      EntityTracker trackedEntity = (EntityTracker)level.k().a.K.get(wearer.getEntityId());
      if (trackedEntity == null) {
         this.broadcast(wearer.getWorld(), packets);
      } else {
         for (Packet<?> packet : packets) {
            trackedEntity.a(packet);
         }
      }
   }

   public void broadcast(World world, Packet<?>... packets) {
      for (Player player : world.getPlayers()) {
         PlayerConnection connection = ((CraftPlayer)player).getHandle().c;

         for (Packet<?> packet : packets) {
            connection.a(packet);
         }
      }
   }

   public static PacketDataSerializer createByteBuf() {
      return new PacketDataSerializer(Unpooled.buffer());
   }

   public static PacketDataSerializer readData(Packet<?> packet) {
      PacketDataSerializer buf = createByteBuf();
      packet.a(buf);
      return buf;
   }
}
