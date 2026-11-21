package io.lumine.mythic.core.volatilecode.v26_1.virtual;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.virtual.PacketEntity;
import io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer;
import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Pos;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.PosRot;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Rot;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3dc;

public abstract class VirtualEntity<T extends PacketEntity<?>> extends PacketEntityRenderer<T> {
   public VirtualEntity(T wrapper) {
      super(wrapper);
   }

   @Override
   public Collection<Object> getSpawnPackets(AbstractPlayer target) {
      ClientboundAddEntityPacket packetSpawn = this.createSpawnPacket();
      ClientboundSetEntityDataPacket packetData = this.createDataPacket(target, true);
      return Lists.newArrayList(new Object[]{packetSpawn, packetData});
   }

   @Override
   public void sendPacket(AbstractPlayer player, @NotNull Object object) {
      if (object instanceof Packet<?> packet) {
         ((CraftPlayer)player.getBukkitEntity()).getHandle().connection.send(packet);
      }
   }

   protected ClientboundBundlePacket bundlePackets(Object packets) {
      return new ClientboundBundlePacket((Collection)packets);
   }

   protected abstract ClientboundAddEntityPacket createSpawnPacket();

   protected ClientboundAddEntityPacket createSpawnPacket(EntityType entityType) {
      Vector3dc location = this.getWrapper().getLocation().get();
      return new ClientboundAddEntityPacket(
         this.getEntityId(), this.getUniqueId(), location.x(), location.y(), location.z(), 0.0F, 0.0F, entityType, 0, Vec3.ZERO, 0.0
      );
   }

   protected abstract ClientboundSetEntityDataPacket createDataPacket(boolean var1);

   protected ClientboundSetEntityDataPacket createDataPacket(AbstractPlayer player, boolean force) {
      return this.createDataPacket(force);
   }

   protected ClientboundRemoveEntitiesPacket createRemovePacket() {
      return new ClientboundRemoveEntitiesPacket(new int[]{this.entityId});
   }

   protected ClientboundSetEntityMotionPacket createVelocityPacket(double x, double y, double z) {
      return new ClientboundSetEntityMotionPacket(this.entityId, new Vec3(x, y, z));
   }

   protected ClientboundTeleportEntityPacket createTeleportPacket(double x, double y, double z, float yaw, float pitch) {
      PositionMoveRotation positionMoveRotation = new PositionMoveRotation(new Vec3(x, y, z), Vec3.ZERO, yaw, pitch);
      return new ClientboundTeleportEntityPacket(this.entityId, positionMoveRotation, Set.of(), false);
   }

   protected ClientboundMoveEntityPacket createRelativePosPacket(double deltaX, double deltaY, double deltaZ) {
      return new Pos(this.entityId, this.encodePos(deltaX), this.encodePos(deltaY), this.encodePos(deltaZ), false);
   }

   protected ClientboundMoveEntityPacket createRelativeRotPacket(float yaw, float pitch) {
      return new Rot(this.entityId, this.encodeRot(yaw), (byte)0, false);
   }

   protected ClientboundMoveEntityPacket createRelativePosRotPacket(double deltaX, double deltaY, double deltaZ, float yaw, float pitch) {
      return new PosRot(this.entityId, this.encodePos(deltaX), this.encodePos(deltaY), this.encodePos(deltaZ), this.encodeRot(yaw), (byte)0, false);
   }

   protected ClientboundSetPassengersPacket createMountPacket(int vehicleId) {
      FriendlyByteBuf mount = new FriendlyByteBuf(Unpooled.buffer());
      mount.writeVarInt(vehicleId);
      mount.writeVarInt(1);
      mount.writeVarInt(this.entityId);
      return (ClientboundSetPassengersPacket)ClientboundSetPassengersPacket.STREAM_CODEC.decode(mount);
   }

   protected ClientboundSetPassengersPacket createPassengerPacket(int passengerId) {
      FriendlyByteBuf mount = new FriendlyByteBuf(Unpooled.buffer());
      mount.writeVarInt(this.entityId);
      mount.writeVarInt(1);
      mount.writeVarInt(passengerId);
      return (ClientboundSetPassengersPacket)ClientboundSetPassengersPacket.STREAM_CODEC.decode(mount);
   }

   protected ClientboundSetPassengersPacket createPassengerPacket(Set<Entity> passengers) {
      FriendlyByteBuf mount = new FriendlyByteBuf(Unpooled.buffer());
      mount.writeVarInt(this.entityId);
      mount.writeVarInt(passengers.size());

      for (Entity ent : passengers) {
         mount.writeVarInt(ent.getId());
      }

      return (ClientboundSetPassengersPacket)ClientboundSetPassengersPacket.STREAM_CODEC.decode(mount);
   }

   public static <T> void writeData(List<DataValue<?>> packetData, int id, EntityDataSerializer<T> serializer, T val) {
      DataValue<T> data = new DataValue(id, serializer, val);
      packetData.add(data);
   }
}
