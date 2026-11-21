package com.ticxo.modelengine.v1_20_R3.parser.behavior;

import com.google.common.collect.Lists;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.MountRenderer;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v1_20_R3.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.Packets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateAttributes;
import net.minecraft.network.protocol.game.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateAttributes.AttributeSnapshot;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.phys.Vec3D;
import org.joml.Vector3f;

public class MountParser implements BehaviorRendererParser<MountRenderer> {
   public void sendToClients(MountRenderer renderer) {
      IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
      this.update(data.getTracking().keySet(), renderer);
      this.spawn(data.getStartTracking(), renderer);
      this.remove(data.getStopTracking(), renderer);
   }

   public void destroy(MountRenderer renderer) {
      IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
      HashSet<UUID> inRange = new HashSet<>(data.getStartTracking());
      inRange.addAll(data.getTracking().keySet());
      inRange.addAll(data.getStopTracking());
      this.remove(inRange, renderer);
   }

   private void spawn(Set<UUID> targets, MountRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();

         for (MountRenderer.Mount mount : renderer.getRendered().values()) {
            set.add(this.pivotSpawn(mount));
            set.add(this.pivotData(mount));
            set.add(this.mountSpawn(mount));
            set.add(this.mountData(mount));
            set.add(this.pivotMount(mount));
            set.add(this.mount(mount));
            set.add(this.mountHealth(mount));
            set.add(this.mountMaxHealth(mount));
         }

         for (MountRenderer.Mount mount : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(mount));
            set.add(this.pivotData(mount));
            set.add(this.mountSpawn(mount));
            set.add(this.mountData(mount));
            set.add(this.pivotMount(mount));
            set.add(this.mount(mount));
            set.add(this.mountHealth(mount));
            set.add(this.mountMaxHealth(mount));
         }

         NetworkUtils.sendBundled(targets, set);
      }
   }

   private void update(Set<UUID> targets, MountRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();

         for (MountRenderer.Mount mount : renderer.getRendered().values()) {
            if (mount.getPosition().isDirty()) {
               set.add(this.pivotMove(mount));
               mount.getPosition().clearDirty();
            }

            if (mount.getYaw().isDirty()) {
               set.add(this.mountRotate(mount));
               mount.getYaw().clearDirty();
            }

            if (mount.getHealth().isDirty()) {
               set.add(this.mountHealth(mount));
               mount.getHealth().clearDirty();
            }

            if (mount.getMaxHealth().isDirty()) {
               set.add(this.mountMaxHealth(mount));
               mount.getMaxHealth().clearDirty();
            }

            if (mount.getPassengers().isDirty()) {
               set.add(this.mount(mount));
               mount.getPassengers().clearDirty();
            }
         }

         for (MountRenderer.Mount mount : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(mount));
            set.add(this.pivotData(mount));
            set.add(this.mountSpawn(mount));
            set.add(this.mountData(mount));
            set.add(this.pivotMount(mount));
            set.add(this.mount(mount));
         }

         Map<String, MountRenderer.Mount> destroy = renderer.getDestroyQueue();
         if (!destroy.isEmpty()) {
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.c(destroy.size() * 2);
            destroy.forEach((s, mount) -> {
               buf.c(mount.getPivotId());
               buf.c(mount.getMountId());
            });
            set.add(new PacketPlayOutEntityDestroy(buf));
         }

         NetworkUtils.sendBundled(targets, set);
      }
   }

   private void remove(Set<UUID> targets, MountRenderer renderer) {
      if (!targets.isEmpty()) {
         PacketDataSerializer buf = NetworkUtils.createByteBuf();
         Collection<MountRenderer.Mount> mounts = renderer.getRendered().values();
         buf.c(mounts.size() * 2);
         mounts.forEach(mount -> {
            buf.c(mount.getPivotId());
            buf.c(mount.getMountId());
         });
         NetworkUtils.send(targets, new PacketPlayOutEntityDestroy(buf));
      }
   }

   private Packets.PacketSupplier pivotSpawn(MountRenderer.Mount renderer) {
      return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUuid(), renderer.getPosition().get());
   }

   private PacketPlayOutEntityMetadata pivotData(MountRenderer.Mount renderer) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(renderer.getPivotId());
      EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
      EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
      EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, 0.0F);
      buf.k(255);
      return new PacketPlayOutEntityMetadata(buf);
   }

   private PacketPlayOutSpawnEntity mountSpawn(MountRenderer.Mount renderer) {
      Vector3f pos = renderer.getPosition().get();
      return new PacketPlayOutSpawnEntity(
         renderer.getMountId(), renderer.getMountUuid(), pos.x, pos.y, pos.z, 0.0F, TMath.byteToRot(renderer.getYaw().get()), EntityTypes.d, 0, Vec3D.b, 0.0
      );
   }

   private PacketPlayOutEntityMetadata mountData(MountRenderer.Mount renderer) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(renderer.getMountId());
      EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
      EntityUtils.writeData(buf, 1, DataWatcherRegistry.b, Integer.MAX_VALUE);
      EntityUtils.writeData(buf, 15, DataWatcherRegistry.a, (byte)16);
      buf.k(255);
      return new PacketPlayOutEntityMetadata(buf);
   }

   private PacketPlayOutMount pivotMount(MountRenderer.Mount renderer) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(renderer.getPivotId());
      buf.c(1);
      buf.c(renderer.getMountId());
      return new PacketPlayOutMount(buf);
   }

   private PacketPlayOutMount mount(MountRenderer.Mount renderer) {
      CollectionDataTracker<Integer> ids = renderer.getPassengers();
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(renderer.getMountId());
      buf.c(ids.size());
      ids.forEach(buf::c);
      return new PacketPlayOutMount(buf);
   }

   private Packets.PacketSupplier pivotMove(MountRenderer.Mount renderer) {
      return NetworkUtils.createPivotTeleport(renderer.getPivotId(), renderer.getPosition().get());
   }

   private PacketPlayOutEntityLook mountRotate(MountRenderer.Mount renderer) {
      PacketDataSerializer rot = NetworkUtils.createByteBuf();
      rot.c(renderer.getMountId());
      rot.k(renderer.getYaw().get());
      rot.k(0);
      rot.a(false);
      return PacketPlayOutEntityLook.b(rot);
   }

   private PacketPlayOutEntityMetadata mountHealth(MountRenderer.Mount renderer) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(renderer.getMountId());
      EntityUtils.writeData(buf, 9, DataWatcherRegistry.d, renderer.getHealth().get());
      buf.k(255);
      return new PacketPlayOutEntityMetadata(buf);
   }

   private PacketPlayOutUpdateAttributes mountMaxHealth(MountRenderer.Mount renderer) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      Collection<AttributeSnapshot> attributes = Lists.newArrayList();
      attributes.add(new AttributeSnapshot(GenericAttributes.l, renderer.getMaxHealth().get().floatValue(), Lists.newArrayList()));
      buf.c(renderer.getMountId());
      buf.<AttributeSnapshot>a(attributes, (buf2, attribute) -> {
         buf2.a(BuiltInRegistries.u.b(attribute.a()));
         buf2.a(attribute.b());
         buf2.<net.minecraft.world.entity.ai.attributes.AttributeModifier>a(attribute.c(), (buf3, modifier) -> {
            buf3.a(modifier.a());
            buf3.a(modifier.c());
            buf3.k(modifier.b().a());
         });
      });
      return new PacketPlayOutUpdateAttributes(buf);
   }
}
