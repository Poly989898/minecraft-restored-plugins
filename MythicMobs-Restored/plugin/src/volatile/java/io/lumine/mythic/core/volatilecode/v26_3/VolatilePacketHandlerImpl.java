package io.lumine.mythic.core.volatilecode.v26_3;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatilePacketHandler;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.reflection.Reflector;
import io.lumine.mythic.core.volatilecode.v26_3.packets.VolatileChannelHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ChunkMap.TrackedEntity;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VolatilePacketHandlerImpl implements VolatilePacketHandler {
   private Reflector<ServerCommonPacketListenerImpl> refConnection = new Reflector<>(ServerCommonPacketListenerImpl.class, "connection");
   private final Map<UUID, ChannelPipeline> pipelines = Maps.newConcurrentMap();
   private boolean doPlayerActionEvent;

   public VolatilePacketHandlerImpl(VolatileCodeHandler handler) {
      CraftServer craftServer = (CraftServer)Bukkit.getServer();
      Events.subscribe(PlayerJoinEvent.class).handler(event -> this.injectPlayer(event.getPlayer()));
      Events.subscribe(PlayerQuitEvent.class).handler(event -> this.ejectPlayer(event.getPlayer()));
   }

   @Override
   public void injectPlayer(Player player) {
      ServerPlayer ply = ((CraftPlayer)player).getHandle();
      VolatileChannelHandler cdh = new VolatileChannelHandler(this, player);
      if (this.doPlayerActionEvent) {
         cdh.enablePlayerActionEvent();
      }

      ServerGamePacketListenerImpl listener = ply.connection;
      Connection connection = (Connection)this.refConnection.get(listener, "connection");
      ChannelPipeline pipeline = connection.channel.pipeline();
      this.pipelines.put(player.getUniqueId(), pipeline);

      for (String name : pipeline.toMap().keySet()) {
         if (pipeline.get(name) instanceof Connection) {
            pipeline.addBefore(name, "mythic_packet_handler", cdh);
            break;
         }
      }
   }

   @Override
   public void ejectPlayer(Player player) {
      ServerGamePacketListenerImpl listener = ((CraftPlayer)player).getHandle().connection;
      Connection connection = (Connection)this.refConnection.get(listener, "connection");
      Channel channel = connection.channel;
      channel.eventLoop().submit(() -> {
         channel.pipeline().remove("mythic_packet_handler");
         return null;
      });
   }

   @Override
   public void setPlayerPositionSilent(Player viewer, double x, double y, double z) {
      try {
         ServerPlayer sp = ((CraftPlayer)viewer).getHandle();
         sp.setPos(x, y, z);
         sp.connection.resetPosition();
      } catch (Throwable var9) {
      }
   }

   @Override
   public int spawnFakeCamera(Player viewer, double x, double y, double z, float yaw, float pitch, int teleportDuration) {
      try {
         ServerPlayer sp = ((CraftPlayer)viewer).getHandle();
         ServerLevel level = sp.level();
         Entity nmsEntity = EntityTypes.TEXT_DISPLAY.create(level, EntitySpawnReason.COMMAND);
         if (nmsEntity == null) {
            return -1;
         } else {
            nmsEntity.setPos(x, y, z);
            nmsEntity.setYRot(yaw);
            nmsEntity.setXRot(pitch);
            TextDisplay bukkit = (TextDisplay)nmsEntity.getBukkitEntity();
            bukkit.setTeleportDuration(teleportDuration);
            bukkit.setInterpolationDelay(0);
            bukkit.setInterpolationDuration(teleportDuration);
            bukkit.setBillboard(Billboard.FIXED);
            bukkit.text(Component.empty());
            bukkit.setViewRange(1024.0F);
            bukkit.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            sp.connection
               .send(new ClientboundAddEntityPacket(nmsEntity.getId(), nmsEntity.getUUID(), x, y, z, pitch, yaw, EntityTypes.TEXT_DISPLAY, 0, Vec3.ZERO, 0.0));
            List<DataValue<?>> dataValues = nmsEntity.getEntityData().packAll();
            if (dataValues != null && !dataValues.isEmpty()) {
               sp.connection.send(new ClientboundSetEntityDataPacket(nmsEntity.getId(), dataValues));
            }

            sp.setCamera(nmsEntity);
            return nmsEntity.getId();
         }
      } catch (Throwable var16) {
         var16.printStackTrace();
         return -1;
      }
   }

   @Override
   public void moveFakeCamera(Player viewer, int entityId, double x, double y, double z, float yaw, float pitch) {
      try {
         ServerPlayer sp = ((CraftPlayer)viewer).getHandle();
         Entity cam = sp.getCamera();
         if (cam != sp && cam.getId() == entityId) {
            cam.setPos(x, y, z);
            cam.setYRot(yaw);
            cam.setXRot(pitch);
         }

         PositionMoveRotation pmr = new PositionMoveRotation(new Vec3(x, y, z), Vec3.ZERO, yaw, pitch);
         Set<Relative> empty = new HashSet<>();
         sp.connection.send(new ClientboundTeleportEntityPacket(entityId, pmr, empty, false));
      } catch (Throwable var15) {
      }
   }

   @Override
   public void removeFakeCamera(Player viewer, int entityId) {
      try {
         ServerPlayer sp = ((CraftPlayer)viewer).getHandle();
         sp.setCamera(null);
         sp.connection.send(new ClientboundRemoveEntitiesPacket(new int[]{entityId}));
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
         ServerGamePacketListenerImpl connection = ((CraftPlayer)player).getHandle().connection;

         for (Packet<?> packet : packets) {
            connection.send(packet);
         }
      }
   }

   public void broadcast(Player player, Packet<?>... packets) {
      ServerGamePacketListenerImpl connection = ((CraftPlayer)player).getHandle().connection;

      for (Packet<?> packet : packets) {
         connection.send(packet);
      }
   }

   public void broadcastAroundAndSelf(Player wearer, Packet<?>... packets) {
      ServerLevel level = ((CraftWorld)wearer.getWorld()).getHandle();
      TrackedEntity trackedEntity = (TrackedEntity)level.getChunkSource().chunkMap.entityMap.get(wearer.getEntityId());
      if (trackedEntity == null) {
         this.broadcast(wearer.getWorld(), packets);
      }
   }

   public void broadcastAround(Player wearer, Packet<?>... packets) {
      ServerLevel level = ((CraftWorld)wearer.getWorld()).getHandle();
      TrackedEntity trackedEntity = (TrackedEntity)level.getChunkSource().chunkMap.entityMap.get(wearer.getEntityId());
      if (trackedEntity == null) {
         this.broadcast(wearer.getWorld(), packets);
      }
   }

   public void broadcast(World world, Packet<?>... packets) {
      for (Player player : world.getPlayers()) {
         ServerGamePacketListenerImpl connection = ((CraftPlayer)player).getHandle().connection;

         for (Packet<?> packet : packets) {
            connection.send(packet);
         }
      }
   }

   public static FriendlyByteBuf createByteBuf() {
      return new FriendlyByteBuf(Unpooled.buffer());
   }
}
