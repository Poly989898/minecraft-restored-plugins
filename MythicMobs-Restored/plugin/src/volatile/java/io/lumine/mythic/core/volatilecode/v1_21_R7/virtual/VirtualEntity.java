package io.lumine.mythic.core.volatilecode.v1_21_R7.virtual;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.virtual.PacketEntity;
import io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer;
import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutRelEntityMove;
import net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook;
import net.minecraft.network.syncher.DataWatcherSerializer;
import net.minecraft.network.syncher.DataWatcher.c;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3dc;

public abstract class VirtualEntity<T extends PacketEntity<?>> extends PacketEntityRenderer<T> {
   public VirtualEntity(T wrapper) {
      super(wrapper);
   }

   @Override
   public Collection<Object> getSpawnPackets(AbstractPlayer target) {
      PacketPlayOutSpawnEntity packetSpawn = this.createSpawnPacket();
      PacketPlayOutEntityMetadata packetData = this.createDataPacket(target, true);
      return Lists.newArrayList(new Object[]{packetSpawn, packetData});
   }

   @Override
   public void sendPacket(AbstractPlayer player, @NotNull Object object) {
      if (object instanceof Packet<?> packet) {
         ((CraftPlayer)player.getBukkitEntity()).getHandle().g.b(packet);
      }
   }

   protected ClientboundBundlePacket bundlePackets(Object packets) {
      return new ClientboundBundlePacket((Collection)packets);
   }

   protected abstract PacketPlayOutSpawnEntity createSpawnPacket();

   protected PacketPlayOutSpawnEntity createSpawnPacket(EntityTypes entityType) {
      Vector3dc location = this.getWrapper().getLocation().get();
      return new PacketPlayOutSpawnEntity(
         this.getEntityId(), this.getUniqueId(), location.x(), location.y(), location.z(), 0.0F, 0.0F, entityType, 0, Vec3D.c, 0.0
      );
   }

   protected abstract PacketPlayOutEntityMetadata createDataPacket(boolean var1);

   protected PacketPlayOutEntityMetadata createDataPacket(AbstractPlayer player, boolean force) {
      return this.createDataPacket(force);
   }

   protected PacketPlayOutEntityDestroy createRemovePacket() {
      return new PacketPlayOutEntityDestroy(new int[]{this.entityId});
   }

   protected PacketPlayOutEntityVelocity createVelocityPacket(double x, double y, double z) {
      return new PacketPlayOutEntityVelocity(this.entityId, new Vec3D(x, y, z));
   }

   protected PacketPlayOutEntityTeleport createTeleportPacket(double x, double y, double z, float yaw, float pitch) {
      PositionMoveRotation positionMoveRotation = new PositionMoveRotation(new Vec3D(x, y, z), Vec3D.c, yaw, pitch);
      return new PacketPlayOutEntityTeleport(this.entityId, positionMoveRotation, Set.of(), false);
   }

   protected PacketPlayOutEntity createRelativePosPacket(double deltaX, double deltaY, double deltaZ) {
      return new PacketPlayOutRelEntityMove(this.entityId, this.encodePos(deltaX), this.encodePos(deltaY), this.encodePos(deltaZ), false);
   }

   protected PacketPlayOutEntity createRelativeRotPacket(float yaw, float pitch) {
      return new PacketPlayOutEntityLook(this.entityId, this.encodeRot(yaw), (byte)0, false);
   }

   protected PacketPlayOutEntity createRelativePosRotPacket(double deltaX, double deltaY, double deltaZ, float yaw, float pitch) {
      return new PacketPlayOutRelEntityMoveLook(
         this.entityId, this.encodePos(deltaX), this.encodePos(deltaY), this.encodePos(deltaZ), this.encodeRot(yaw), (byte)0, false
      );
   }

   protected PacketPlayOutMount createMountPacket(int vehicleId) {
      PacketDataSerializer mount = new PacketDataSerializer(Unpooled.buffer());
      mount.c(vehicleId);
      mount.c(1);
      mount.c(this.entityId);
      return (PacketPlayOutMount)PacketPlayOutMount.a.decode(mount);
   }

   protected PacketPlayOutMount createPassengerPacket(int passengerId) {
      PacketDataSerializer mount = new PacketDataSerializer(Unpooled.buffer());
      mount.c(this.entityId);
      mount.c(1);
      mount.c(passengerId);
      return (PacketPlayOutMount)PacketPlayOutMount.a.decode(mount);
   }

   protected PacketPlayOutMount createPassengerPacket(Set<Entity> passengers) {
      PacketDataSerializer mount = new PacketDataSerializer(Unpooled.buffer());
      mount.c(this.entityId);
      mount.c(passengers.size());

      for (Entity ent : passengers) {
         mount.c(ent.aA());
      }

      return (PacketPlayOutMount)PacketPlayOutMount.a.decode(mount);
   }

   public static <T> void writeData(List<c<?>> packetData, int id, DataWatcherSerializer<T> serializer, T val) {
      c<T> data = new c(id, serializer, val);
      packetData.add(data);
   }
}
