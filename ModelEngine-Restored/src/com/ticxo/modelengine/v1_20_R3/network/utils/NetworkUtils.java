package com.ticxo.modelengine.v1_20_R3.network.utils;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.network.ProtectedPacket;
import com.ticxo.modelengine.api.utils.data.PositionCodec;
import com.ticxo.modelengine.v1_20_R3.network.NetworkHandlerImpl;
import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutRelEntityMove;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class NetworkUtils {
   public static PacketDataSerializer createByteBuf() {
      return new PacketDataSerializer(Unpooled.buffer());
   }

   public static PacketDataSerializer createByteBuf(Consumer<PacketDataSerializer> consumer) {
      PacketDataSerializer buf = createByteBuf();
      consumer.accept(buf);
      return buf;
   }

   public static PacketDataSerializer readData(Packet<?> packet) {
      PacketDataSerializer buf = createByteBuf();
      packet.a(buf);
      return buf;
   }

   public static Packets.PacketSupplier createPivotSpawn(int id, UUID uuid, Vector3f pos) {
      return playerUUID -> ModelEngineAPI.getPlayerProtocolVersion(playerUUID) >= 764
         ? new PacketPlayOutSpawnEntity(id, uuid, pos.x, pos.y - 0.5, pos.z, 0.0F, 0.0F, EntityTypes.c, 0, Vec3D.b, 0.0)
         : new PacketPlayOutSpawnEntity(id, uuid, pos.x, pos.y - 0.375, pos.z, 0.0F, 0.0F, EntityTypes.c, 0, Vec3D.b, 0.0);
   }

   public static Packets.PacketSupplier createPivotTeleport(int id, Vector3f pos) {
      PacketDataSerializer buf = createByteBuf();
      buf.c(id);
      buf.a(pos.x);
      buf.a(pos.y - 0.375);
      buf.a(pos.z);
      buf.k(0);
      buf.k(0);
      buf.a(false);
      PacketPlayOutEntityTeleport tpHigher = new PacketPlayOutEntityTeleport(buf);
      buf = createByteBuf();
      buf.c(id);
      buf.a(pos.x);
      buf.a(pos.y - 0.5);
      buf.a(pos.z);
      buf.k(0);
      buf.k(0);
      buf.a(false);
      PacketPlayOutEntityTeleport tpLower = new PacketPlayOutEntityTeleport(buf);
      return playerUUID -> ModelEngineAPI.getPlayerProtocolVersion(playerUUID) >= 764 ? tpLower : tpHigher;
   }

   public static Object createPivotMove(int id, @Nullable PositionCodec codec, Vector3f pos) {
      if (codec == null) {
         return createPivotTeleport(id, pos);
      }

      return switch (codec.update(pos.x, pos.y, pos.z)) {
         case UNCHANGED -> null;
         case MOVE -> new PacketPlayOutRelEntityMove(id, codec.getDeltaX(), codec.getDeltaY(), codec.getDeltaZ(), false);
         case ABSOLUTE -> createPivotTeleport(id, pos);
      };
   }

   public static Packets.PacketSupplier lodWrapper(
      ModeledEntity modeledEntity, BiFunction<UUID, AnimationLODHandler.LODTracker, Packet<PacketListenerPlayOut>> packetFunction
   ) {
      return uuid -> {
         AnimationLODHandler.LODTracker tracker = modeledEntity.getAnimationLodHandler().tick(uuid);
         return tracker.isCanSkip() ? null : packetFunction.apply(uuid, tracker);
      };
   }

   public static void send(UUID target, @Nullable Packet<? super PacketListenerPlayOut> packet) {
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

   public static void send(Set<UUID> targets, @Nullable Packet<? super PacketListenerPlayOut> packet) {
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

   public static void send(Set<UUID> targets, @Nullable Packet<? super PacketListenerPlayOut> packet, Predicate<Player> predicate) {
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

   public static void sendRaw(UUID target, @Nullable Packet<? super PacketListenerPlayOut> packet) {
      if (packet != null) {
         ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
      }
   }

   public static void sendRaw(Set<UUID> targets, @Nullable Packet<? super PacketListenerPlayOut> packet) {
      if (packet != null) {
         for (UUID player : targets) {
            ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
         }
      }
   }

   public static void sendRaw(Set<UUID> targets, @Nullable Packet<? super PacketListenerPlayOut> packet, Predicate<Player> predicate) {
      if (packet != null) {
         for (UUID player : targets) {
            Player p = Bukkit.getPlayer(player);
            if (p != null && predicate.test(p)) {
               ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
            }
         }
      }
   }

   public static void sendNow(UUID target, @Nullable Packet<? super PacketListenerPlayOut> packet) {
      if (packet != null) {
         ProtectedPacket wrapped = new ProtectedPacket(packet);
         ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlushNow(wrapped));
      }
   }

   private static Packet<PacketListenerPlayOut> bundle(Collection<Packet<PacketListenerPlayOut>> packets) {
      return (packets.size() == 1 ? packets.iterator().next() : new ClientboundBundlePacket(packets));
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
            Packet<PacketListenerPlayOut> packet = bundle(packets);
            ModelEngineAPI.getNetworkHandler().getPipeline(target).ifPresent(pipeline -> pipeline.writeAndFlush(packet));
         });
      }
   }

   public static void sendBundledRaw(Set<UUID> targets, Packets collection) {
      if (!collection.isEmpty()) {
         for (UUID player : targets) {
            ModelEngineAPI.getNetworkHandler().getPipeline(player).ifPresent(pipeline -> collection.compile(player, packets -> {
               Packet<PacketListenerPlayOut> packet = bundle(packets);
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
                  Packet<PacketListenerPlayOut> packet = bundle(packets);
                  pipeline.writeAndFlush(packet);
               }));
            }
         }
      }
   }
}
