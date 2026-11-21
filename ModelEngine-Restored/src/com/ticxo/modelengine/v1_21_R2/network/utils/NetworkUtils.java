package com.ticxo.modelengine.v1_21_R2.network.utils;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.network.ProtectedPacket;
import com.ticxo.modelengine.api.utils.data.PositionCodec;
import com.ticxo.modelengine.v1_21_R2.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R2.network.NetworkHandlerImpl;
import io.netty.buffer.Unpooled;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Pos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class NetworkUtils {
   public static final ProtocolInfo<ClientGamePacketListener> CLIENTBOUND_CODEC = GameProtocols.CLIENTBOUND_TEMPLATE
      .bind(RegistryFriendlyByteBuf.decorator(MinecraftServer.getServer().registryAccess()));
   public static final ProtocolInfo<ServerGamePacketListener> SERVERBOUND_CODEC = GameProtocols.SERVERBOUND_TEMPLATE
      .bind(RegistryFriendlyByteBuf.decorator(MinecraftServer.getServer().registryAccess()));
   private static final Map<Class<?>, Constructor<?>> PACKET_CONSTRUCTOR = new HashMap<>();

   public static FriendlyByteBuf createByteBuf() {
      return new FriendlyByteBuf(Unpooled.buffer());
   }

   public static FriendlyByteBuf createByteBuf(Consumer<FriendlyByteBuf> consumer) {
      FriendlyByteBuf buf = createByteBuf();
      consumer.accept(buf);
      return buf;
   }

   public static <T extends Packet<ClientGamePacketListener>> T create(Class<T> clazz, FriendlyByteBuf buf) {
      Constructor<?> constructor = PACKET_CONSTRUCTOR.computeIfAbsent(clazz, aClass -> {
         try {
            Constructor<?> c = aClass.getDeclaredConstructor(RegistryFriendlyByteBuf.class);
            c.setAccessible(true);
            return c;
         } catch (NoSuchMethodException ignored) {
            try {
               Constructor<?> c = aClass.getDeclaredConstructor(FriendlyByteBuf.class);
               c.setAccessible(true);
               return c;
            } catch (NoSuchMethodException e) {
               throw new RuntimeException(e);
            }
         }
      });

      try {
         return (T)constructor.newInstance(buf);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
         throw new RuntimeException(e);
      }
   }

   public static FriendlyByteBuf readServerbound(Packet<? super ServerGamePacketListener> packet) {
      FriendlyByteBuf buf = createByteBuf();
      SERVERBOUND_CODEC.codec().encode(buf, packet);
      buf.readVarInt();
      return buf;
   }

   public static FriendlyByteBuf readClientbound(Packet<? super ClientGamePacketListener> packet) {
      FriendlyByteBuf buf = createByteBuf();
      CLIENTBOUND_CODEC.codec().encode(buf, packet);
      buf.readVarInt();
      return buf;
   }

   public static Packets.PacketSupplier createPivotSpawn(int id, UUID uuid, Vector3f pos) {
      return playerUUID -> ModelEngineAPI.getPlayerProtocolVersion(playerUUID) >= 764
         ? new ClientboundAddEntityPacket(id, uuid, pos.x, pos.y - 0.5, pos.z, 0.0F, 0.0F, EntityType.AREA_EFFECT_CLOUD, 0, Vec3.ZERO, 0.0)
         : new ClientboundAddEntityPacket(id, uuid, pos.x, pos.y - 0.375, pos.z, 0.0F, 0.0F, EntityType.AREA_EFFECT_CLOUD, 0, Vec3.ZERO, 0.0);
   }

   public static Packets.PacketSupplier createCameraPivotSpawn(int id, UUID uuid, Vector3f pos, float height) {
      ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(
         id, uuid, pos.x, pos.y - height, pos.z, 0.0F, 0.0F, EntityType.INTERACTION, 0, Vec3.ZERO, 0.0
      );
      return playerUUID -> packet;
   }

   public static Packets.PacketSupplier createCameraPivotTeleport(int id, Vector3f pos, float height) {
      EntityContainer container = EntityContainer.of(id);
      container.setPosRaw(pos.x, pos.y - height, pos.z);
      ClientboundEntityPositionSyncPacket packet = new ClientboundEntityPositionSyncPacket(
         container.getId(), PositionMoveRotation.of(container), container.onGround()
      );
      return playerUUID -> packet;
   }

   public static Packets.PacketSupplier createPivotTeleport(int id, Vector3f pos) {
      EntityContainer container = EntityContainer.of(id);
      container.setPosRaw(pos.x, pos.y - 0.375, pos.z);
      ClientboundEntityPositionSyncPacket tpHigher = new ClientboundEntityPositionSyncPacket(
         container.getId(), PositionMoveRotation.of(container), container.onGround()
      );
      container = EntityContainer.of(id);
      container.setId(id);
      container.setPosRaw(pos.x, pos.y - 0.5, pos.z);
      ClientboundEntityPositionSyncPacket tpLower = new ClientboundEntityPositionSyncPacket(
         container.getId(), PositionMoveRotation.of(container), container.onGround()
      );
      return playerUUID -> ModelEngineAPI.getPlayerProtocolVersion(playerUUID) >= 764 ? tpLower : tpHigher;
   }

   public static Object createPivotMove(int id, @Nullable PositionCodec codec, Vector3f pos) {
      if (codec == null) {
         return createPivotTeleport(id, pos);
      }

      return switch (codec.update(pos.x, pos.y, pos.z)) {
         case UNCHANGED -> null;
         case MOVE -> new Pos(id, codec.getDeltaX(), codec.getDeltaY(), codec.getDeltaZ(), false);
         case ABSOLUTE -> createPivotTeleport(id, pos);
      };
   }

   public static Object createCameraPivotMove(int id, @Nullable PositionCodec codec, Vector3f pos, float height) {
      if (codec == null) {
         return createCameraPivotTeleport(id, pos, height);
      }

      return switch (codec.update(pos.x, pos.y - height, pos.z)) {
         case UNCHANGED -> null;
         case MOVE -> new Pos(id, codec.getDeltaX(), codec.getDeltaY(), codec.getDeltaZ(), false);
         case ABSOLUTE -> createCameraPivotTeleport(id, pos, height);
      };
   }

   public static Packets.PacketSupplier lodWrapper(
      ModeledEntity modeledEntity, BiFunction<UUID, AnimationLODHandler.LODTracker, Packet<ClientGamePacketListener>> packetFunction
   ) {
      return uuid -> {
         AnimationLODHandler.LODTracker tracker = modeledEntity.getAnimationLodHandler().tick(uuid);
         return tracker.isCanSkip() ? null : packetFunction.apply(uuid, tracker);
      };
   }

   public static void send(UUID target, @Nullable Packet<? super ClientGamePacketListener> packet) {
      if (packet != null) {
         NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
         if (handler.isBatching()) {
            handler.appendPacket(target, packet);
         } else {
            ProtectedPacket wrapped = new ProtectedPacket(packet);
            ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlush(wrapped));
         }
      }
   }

   public static void send(Set<UUID> targets, @Nullable Packet<? super ClientGamePacketListener> packet) {
      if (packet != null) {
         NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
         if (handler.isBatching()) {
            for (UUID player : targets) {
               handler.appendPacket(player, packet);
            }
         } else {
            ProtectedPacket wrapped = new ProtectedPacket(packet);

            for (UUID player : targets) {
               ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> pipeline.writeAndFlush(wrapped));
            }
         }
      }
   }

   public static void send(Set<UUID> targets, @Nullable Packet<? super ClientGamePacketListener> packet, Predicate<Player> predicate) {
      if (packet != null) {
         NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
         if (handler.isBatching()) {
            for (UUID player : targets) {
               Player p = Bukkit.getPlayer(player);
               if (p != null && predicate.test(p)) {
                  handler.appendPacket(player, packet);
               }
            }
         } else {
            ProtectedPacket wrapped = new ProtectedPacket(packet);

            for (UUID player : targets) {
               Player p = Bukkit.getPlayer(player);
               if (p != null && predicate.test(p)) {
                  ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> pipeline.writeAndFlush(wrapped));
               }
            }
         }
      }
   }

   public static void sendRaw(UUID target, @Nullable Packet<? super ClientGamePacketListener> packet) {
      if (packet != null) {
         ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
      }
   }

   public static void sendRaw(Set<UUID> targets, @Nullable Packet<? super ClientGamePacketListener> packet) {
      if (packet != null) {
         for (UUID player : targets) {
            ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
         }
      }
   }

   public static void sendRaw(Set<UUID> targets, @Nullable Packet<? super ClientGamePacketListener> packet, Predicate<Player> predicate) {
      if (packet != null) {
         for (UUID player : targets) {
            Player p = Bukkit.getPlayer(player);
            if (p != null && predicate.test(p)) {
               ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
            }
         }
      }
   }

   public static void sendNow(UUID target, @Nullable Packet<? super ClientGamePacketListener> packet) {
      if (packet != null) {
         ProtectedPacket wrapped = new ProtectedPacket(packet);
         ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlushNow(wrapped));
      }
   }

   private static Packet<? super ClientGamePacketListener> bundle(Collection<Packet<? super ClientGamePacketListener>> packets) {
      return (Packet<? super ClientGamePacketListener>)(packets.size() == 1 ? packets.iterator().next() : new ClientboundBundlePacket(packets));
   }

   public static void sendBundled(UUID target, Packets collection) {
      if (!collection.isEmpty()) {
         collection.compile(target, packets -> {
            NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
            if (handler.isBatching()) {
               handler.appendPackets(target, packets);
            } else {
               ProtectedPacket wrapped = new ProtectedPacket(bundle(packets));
               handler.getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlush(wrapped));
            }
         });
      }
   }

   public static void sendBundled(Set<UUID> targets, Packets collection) {
      if (!collection.isEmpty()) {
         NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
         if (handler.isBatching()) {
            for (UUID player : targets) {
               collection.compile(player, packets -> handler.appendPackets(player, packets));
            }
         } else {
            for (UUID player : targets) {
               ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> collection.compile(player, packets -> {
                  ProtectedPacket wrapped = new ProtectedPacket(bundle(packets));
                  pipeline.writeAndFlush(wrapped);
               }));
            }
         }
      }
   }

   public static void sendBundled(Set<UUID> targets, Packets collection, Predicate<Player> predicate) {
      if (!collection.isEmpty()) {
         NetworkHandlerImpl handler = NetworkHandlerImpl.instance;
         if (handler.isBatching()) {
            for (UUID player : targets) {
               Player p = Bukkit.getPlayer(player);
               if (p != null && predicate.test(p)) {
                  collection.compile(player, packets -> handler.appendPackets(player, packets));
               }
            }
         } else {
            for (UUID player : targets) {
               Player p = Bukkit.getPlayer(player);
               if (p != null && predicate.test(p)) {
                  ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> collection.compile(player, packets -> {
                     ProtectedPacket wrapped = new ProtectedPacket(bundle(packets));
                     pipeline.writeAndFlush(wrapped);
                  }));
               }
            }
         }
      }
   }

   public static void sendBundledRaw(UUID target, Packets collection) {
      if (!collection.isEmpty()) {
         collection.compile(target, packets -> {
            Packet<? super ClientGamePacketListener> packet = bundle(packets);
            ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
         });
      }
   }

   public static void sendBundledRaw(Set<UUID> targets, Packets collection) {
      if (!collection.isEmpty()) {
         for (UUID player : targets) {
            ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> collection.compile(player, packets -> {
               Packet<? super ClientGamePacketListener> packet = bundle(packets);
               pipeline.writeAndFlush(packet);
            }));
         }
      }
   }

   public static void sendBundledRaw(Set<UUID> targets, Packets collection, Predicate<Player> predicate) {
      if (!collection.isEmpty()) {
         for (UUID player : targets) {
            Player p = Bukkit.getPlayer(player);
            if (p != null && predicate.test(p)) {
               ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> collection.compile(player, packets -> {
                  Packet<? super ClientGamePacketListener> packet = bundle(packets);
                  pipeline.writeAndFlush(packet);
               }));
            }
         }
      }
   }
}
