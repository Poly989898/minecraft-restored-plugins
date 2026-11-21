package io.lumine.mythic.core.volatilecode.v1_21_R1;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatilePacketHandler;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.reflection.Reflector;
import io.lumine.mythic.core.volatilecode.v1_21_R1.packets.VolatileChannelHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcher.c;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.level.PlayerChunkMap.EntityTracker;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VolatilePacketHandlerImpl implements VolatilePacketHandler {
   private Reflector<ServerCommonPacketListenerImpl> refConnection = new Reflector<>(ServerCommonPacketListenerImpl.class, "e");
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

      PlayerConnection listener = ply.c;
      NetworkManager connection = (NetworkManager)this.refConnection.get(listener, "e");
      ChannelPipeline pipeline = connection.n.pipeline();
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
      NetworkManager connection = (NetworkManager)this.refConnection.get(listener, "e");
      Channel channel = connection.n;
      channel.eventLoop().submit(() -> {
         channel.pipeline().remove("mythic_packet_handler");
         return null;
      });
   }

   @Override
   public void setPlayerPositionSilent(Player viewer, double x, double y, double z) {
      try {
         EntityPlayer sp = ((CraftPlayer)viewer).getHandle();
         sp.a_(x, y, z);
         sp.c.l();
      } catch (Throwable var9) {
      }
   }

   @Override
   public int spawnFakeCamera(Player viewer, double x, double y, double z, float yaw, float pitch, int teleportDuration) {
      try {
         EntityPlayer sp = ((CraftPlayer)viewer).getHandle();
         WorldServer level = (WorldServer)sp.dO();
         Entity nmsEntity = EntityTypes.bb.a(level);
         if (nmsEntity == null) {
            return -1;
         } else {
            nmsEntity.a_(x, y, z);
            nmsEntity.t(yaw);
            nmsEntity.u(pitch);
            TextDisplay bukkit = (TextDisplay)nmsEntity.getBukkitEntity();
            bukkit.setTeleportDuration(teleportDuration);
            bukkit.setInterpolationDelay(0);
            bukkit.setInterpolationDuration(teleportDuration);
            bukkit.setBillboard(Billboard.FIXED);
            bukkit.text(Component.empty());
            bukkit.setViewRange(1024.0F);
            bukkit.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            sp.c.b(new PacketPlayOutSpawnEntity(nmsEntity.an(), nmsEntity.cz(), x, y, z, pitch, yaw, EntityTypes.bb, 0, Vec3D.b, 0.0));
            List<c<?>> dataValues = nmsEntity.ar().packAll();
            if (dataValues != null && !dataValues.isEmpty()) {
               sp.c.b(new PacketPlayOutEntityMetadata(nmsEntity.an(), dataValues));
            }

            sp.d(nmsEntity);
            return nmsEntity.an();
         }
      } catch (Throwable var16) {
         var16.printStackTrace();
         return -1;
      }
   }

   @Override
   public void moveFakeCamera(Player viewer, int entityId, double x, double y, double z, float yaw, float pitch) {
      try {
         EntityPlayer sp = ((CraftPlayer)viewer).getHandle();
         Entity cam = sp.L();
         if (cam != sp && cam.an() == entityId) {
            cam.a_(x, y, z);
            cam.t(yaw);
            cam.u(pitch);
         }

         PacketDataSerializer buf = new PacketDataSerializer(Unpooled.buffer());
         buf.c(entityId);
         buf.a(x);
         buf.a(y);
         buf.a(z);
         buf.k((byte)(yaw * 256.0F / 360.0F));
         buf.k((byte)(pitch * 256.0F / 360.0F));
         buf.a(false);
         sp.c.b((Packet)PacketPlayOutEntityTeleport.a.decode(buf));
      } catch (Throwable var14) {
      }
   }

   @Override
   public void removeFakeCamera(Player viewer, int entityId) {
      try {
         EntityPlayer sp = ((CraftPlayer)viewer).getHandle();
         sp.d(null);
         sp.c.b(new PacketPlayOutEntityDestroy(new int[]{entityId}));
      } catch (Throwable var4) {
      }
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
            connection.b(packet);
         }
      }
   }

   public void broadcast(Player player, Packet<?>... packets) {
      PlayerConnection connection = ((CraftPlayer)player).getHandle().c;

      for (Packet<?> packet : packets) {
         connection.b(packet);
      }
   }

   public void broadcastAroundAndSelf(Player wearer, Packet<?>... packets) {
      WorldServer level = ((CraftWorld)wearer.getWorld()).getHandle();
      EntityTracker trackedEntity = (EntityTracker)level.l().a.K.get(wearer.getEntityId());
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
      EntityTracker trackedEntity = (EntityTracker)level.l().a.K.get(wearer.getEntityId());
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
            connection.b(packet);
         }
      }
   }

   public static PacketDataSerializer createByteBuf() {
      return new PacketDataSerializer(Unpooled.buffer());
   }
}
