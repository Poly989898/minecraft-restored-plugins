package io.lumine.mythic.core.volatilecode.v1_20_R3.virtual;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.virtual.PacketEntity;
import io.lumine.mythic.api.volatilecode.virtual.PacketEntityRenderer;
import io.netty.buffer.Unpooled;
import java.util.Collection;
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
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.network.syncher.DataWatcherSerializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
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
         ((CraftPlayer)player.getBukkitEntity()).getHandle().c.b(packet);
      }
   }

   protected ClientboundBundlePacket bundlePackets(Object packets) {
      return new ClientboundBundlePacket((Collection)packets);
   }

   protected abstract PacketPlayOutSpawnEntity createSpawnPacket();

   protected PacketPlayOutSpawnEntity createSpawnPacket(EntityTypes entityType) {
      Vector3dc location = this.getWrapper().getLocation().get();
      return new PacketPlayOutSpawnEntity(
         this.getEntityId(), this.getUniqueId(), location.x(), location.y(), location.z(), 0.0F, 0.0F, entityType, 0, Vec3D.b, 0.0
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
      PacketDataSerializer pos = new PacketDataSerializer(Unpooled.buffer());
      pos.c(this.entityId);
      pos.a(x);
      pos.a(y);
      pos.a(z);
      pos.k(this.encodeRot(yaw));
      pos.k(0);
      pos.a(false);
      return new PacketPlayOutEntityTeleport(pos);
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
      return new PacketPlayOutMount(mount);
   }

   protected PacketPlayOutMount createPassengerPacket(int passengerId) {
      PacketDataSerializer mount = new PacketDataSerializer(Unpooled.buffer());
      mount.c(this.entityId);
      mount.c(1);
      mount.c(passengerId);
      return new PacketPlayOutMount(mount);
   }

   protected PacketPlayOutMount createPassengerPacket(Set<Entity> passengers) {
      PacketDataSerializer mount = new PacketDataSerializer(Unpooled.buffer());
      mount.c(this.entityId);
      mount.c(passengers.size());

      for (Entity ent : passengers) {
         mount.c(ent.aj());
      }

      return new PacketPlayOutMount(mount);
   }

   public static <T> void writeData(PacketDataSerializer byteBuf, int id, DataWatcherSerializer<T> serializer, T val) {
      int serializedId = DataWatcherRegistry.b(serializer);
      byteBuf.k(id);
      byteBuf.c(serializedId);
      serializer.a(byteBuf, val);
   }
}
