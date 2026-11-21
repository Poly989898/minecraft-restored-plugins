package io.lumine.mythic.core.volatilecode.v1_21_R4;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatilePacketHandler;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.reflection.Reflector;
import io.lumine.mythic.core.volatilecode.v1_21_R4.packets.VolatileChannelHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R4.CraftServer;
import org.bukkit.craftbukkit.v1_21_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer;
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

      PlayerConnection listener = ply.f;
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
      PlayerConnection listener = ((CraftPlayer)player).getHandle().f;
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
         sp.f.l();
      } catch (Throwable var9) {
      }
   }

   @Override
   public int spawnFakeCamera(Player viewer, double x, double y, double z, float yaw, float pitch, int teleportDuration) {
      try {
         EntityPlayer sp = ((CraftPlayer)viewer).getHandle();
         WorldServer level = (WorldServer)sp.dV();
         Entity nmsEntity = EntityTypes.bw.a(level, EntitySpawnReason.n);
         if (nmsEntity == null) {
            return -1;
         } else {
            nmsEntity.a_(x, y, z);
            nmsEntity.w(yaw);
            nmsEntity.x(pitch);
            TextDisplay bukkit = (TextDisplay)nmsEntity.getBukkitEntity();
            bukkit.setTeleportDuration(teleportDuration);
            bukkit.setInterpolationDelay(0);
            bukkit.setInterpolationDuration(teleportDuration);
            bukkit.setBillboard(Billboard.FIXED);
            bukkit.text(Component.empty());
            bukkit.setViewRange(1024.0F);
            bukkit.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            sp.f.b(new PacketPlayOutSpawnEntity(nmsEntity.ao(), nmsEntity.cG(), x, y, z, pitch, yaw, EntityTypes.bw, 0, Vec3D.c, 0.0));
            List<c<?>> dataValues = nmsEntity.ar().packAll();
            if (dataValues != null && !dataValues.isEmpty()) {
               sp.f.b(new PacketPlayOutEntityMetadata(nmsEntity.ao(), dataValues));
            }

            sp.d(nmsEntity);
            return nmsEntity.ao();
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
         Entity cam = sp.K();
         if (cam != sp && cam.ao() == entityId) {
            cam.a_(x, y, z);
            cam.w(yaw);
            cam.x(pitch);
         }

         PositionMoveRotation pmr = new PositionMoveRotation(new Vec3D(x, y, z), Vec3D.c, yaw, pitch);
         Set<Relative> empty = new HashSet<>();
         sp.f.b(new PacketPlayOutEntityTeleport(entityId, pmr, empty, false));
      } catch (Throwable var15) {
      }
   }

   @Override
   public void removeFakeCamera(Player viewer, int entityId) {
      try {
         EntityPlayer sp = ((CraftPlayer)viewer).getHandle();
         sp.d(null);
         sp.f.b(new PacketPlayOutEntityDestroy(new int[]{entityId}));
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
         PlayerConnection connection = ((CraftPlayer)player).getHandle().f;

         for (Packet<?> packet : packets) {
            connection.b(packet);
         }
      }
   }

   public void broadcast(Player player, Packet<?>... packets) {
      PlayerConnection connection = ((CraftPlayer)player).getHandle().f;

      for (Packet<?> packet : packets) {
         connection.b(packet);
      }
   }

   public void broadcastAroundAndSelf(Player wearer, Packet<?>... packets) {
      WorldServer level = ((CraftWorld)wearer.getWorld()).getHandle();
      EntityTracker trackedEntity = (EntityTracker)level.m().a.L.get(wearer.getEntityId());
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
      EntityTracker trackedEntity = (EntityTracker)level.m().a.L.get(wearer.getEntityId());
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
         PlayerConnection connection = ((CraftPlayer)player).getHandle().f;

         for (Packet<?> packet : packets) {
            connection.b(packet);
         }
      }
   }

   public static PacketDataSerializer createByteBuf() {
      return new PacketDataSerializer(Unpooled.buffer());
   }
}
