package com.ticxo.modelengine.v1_20_R2.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.SegmentRenderer;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.utils.data.UpdateScheme;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v1_20_R2.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R2.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R2.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.joml.Vector3f;

public class SegmentParser implements BehaviorRendererParser<SegmentRenderer> {
   private final List<Runnable> cleanupQueue = new ArrayList<>();
   private float viewRange = 4096.0F;

   public void sendToClients(SegmentRenderer renderer) {
      this.cleanupQueue.clear();
      this.viewRange = renderer.getActiveModel().getViewRange();
      IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
      this.update(data.getTracking().keySet(), renderer);
      this.spawn(data.getStartTracking(), renderer);
      this.remove(data.getStopTracking(), renderer);
      this.cleanupQueue.forEach(Runnable::run);
      this.cleanupQueue.clear();
   }

   public void destroy(SegmentRenderer renderer) {
      IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
      HashSet<UUID> inRange = new HashSet<>(data.getStartTracking());
      inRange.addAll(data.getTracking().keySet());
      inRange.addAll(data.getStopTracking());
      this.remove(inRange, renderer);
   }

   private void spawn(Packets set, Map<String, SegmentRenderer.Pivot> map) {
      for (SegmentRenderer.Pivot pivot : map.values()) {
         set.add(this.pivotSpawn(pivot));
         set.add(this.pivotData(pivot));

         for (SegmentRenderer.SegmentDisplayBone bone : pivot.getRendered().values()) {
            for (DisplayBone.BoneData boneData : bone.getModel().values()) {
               set.add(this.displaySpawn(pivot, boneData));
               set.add(this.displayData(boneData, true));
            }
         }

         for (SegmentRenderer.SegmentDisplayBone bone : pivot.getSpawnQueue().values()) {
            for (DisplayBone.BoneData boneData : bone.getModel().values()) {
               set.add(this.displaySpawn(pivot, boneData));
               set.add(this.displayData(boneData, true));
            }
         }

         set.add(this.pivotMount(pivot));
         this.cleanupQueue.add(pivot::clearDirty);
      }
   }

   private void spawn(Set<UUID> targets, SegmentRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();
         this.spawn(set, renderer.getRendered());
         this.spawn(set, renderer.getSpawnQueue());
         NetworkUtils.sendBundled(targets, set);
      }
   }

   private void update(Set<UUID> targets, SegmentRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();
         HashSet<Integer> destroy = new HashSet<>();

         for (SegmentRenderer.Pivot pivot : renderer.getRendered().values()) {
            set.add(this.pivotTeleport(pivot));
            pivot.getRendered().values().forEach(bone -> {
               UpdateScheme<DisplayBone.BoneData> scheme = bone.getModelUpdateScheme();

               for (DisplayBone.BoneData boneData : bone.getModel().values()) {
                  switch (scheme.getUpdateMode(boneData)) {
                     case NONE:
                     case UPDATE:
                        set.add(this.displayData(boneData, false));
                  }
               }

               scheme.getAdded().forEach(boneData2 -> {
                  set.add(this.displaySpawn(pivot, boneData2));
                  set.add(this.displayData(boneData2, true));
               });
               scheme.getRemoved().forEach(boneData3 -> destroy.add(boneData3.getId()));
            });
            this.cleanupQueue.add(pivot::clearDirty);
         }

         this.spawn(set, renderer.getSpawnQueue());
         renderer.getDestroyQueue().forEach((s, pivot2) -> {
            destroy.add(pivot2.getId());
            destroy.addAll(pivot2.getPassengers());
         });
         if (!destroy.isEmpty()) {
            PacketDataSerializer buf = NetworkUtils.createByteBuf();
            buf.<Integer>a(destroy, PacketDataSerializer::c);
            set.add(new PacketPlayOutEntityDestroy(buf));
         }

         NetworkUtils.sendBundled(targets, set);
      }
   }

   private void remove(Set<UUID> targets, SegmentRenderer renderer) {
      if (!targets.isEmpty()) {
         Collection<SegmentRenderer.Pivot> pivots = renderer.getRendered().values();
         IntArrayList ids = new IntArrayList();
         pivots.forEach(pivot -> {
            ids.add(pivot.getId());
            ids.addAll(pivot.getPassengers());
         });
         NetworkUtils.send(targets, new PacketPlayOutEntityDestroy(ids));
      }
   }

   private Packets.PacketSupplier pivotSpawn(SegmentRenderer.Pivot pivot) {
      return NetworkUtils.createPivotSpawn(pivot.getId(), pivot.getUuid(), pivot.getPosition().get());
   }

   private PacketPlayOutEntityMetadata pivotData(SegmentRenderer.Pivot pivot) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(pivot.getId());
      EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)32);
      EntityUtils.writeData(buf, 8, DataWatcherRegistry.d, 0.0F);
      buf.k(255);
      return new PacketPlayOutEntityMetadata(buf);
   }

   private PacketPlayOutMount pivotMount(SegmentRenderer.Pivot pivot) {
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(pivot.getId());
      buf.<Integer>a(pivot.getPassengers(), PacketDataSerializer::c);
      return new PacketPlayOutMount(buf);
   }

   private Object pivotTeleport(SegmentRenderer.Pivot pivot) {
      if (!pivot.getPosition().isDirty()) {
         return null;
      }

      this.cleanupQueue.add(() -> pivot.getPosition().clearDirty());
      return NetworkUtils.createPivotMove(pivot.getId(), pivot.getPositionCodec(), pivot.getPosition().get());
   }

   private PacketPlayOutSpawnEntity displaySpawn(SegmentRenderer.Pivot pivot, DisplayBone.BoneData boneData) {
      Vector3f pos = pivot.getPosition().get();
      return new PacketPlayOutSpawnEntity(boneData.getId(), boneData.getUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityTypes.ae, 0, Vec3D.b, 0.0);
   }

   private Packets.PacketSupplier displayData(DisplayBone.BoneData boneData, boolean force) {
      DisplayBone bone = boneData.getBone();
      if (!force && !bone.isDirty() && !boneData.getModel().isDirty()) {
         return null;
      }

      this.cleanupQueue.add(bone::clearDirty);
      this.cleanupQueue.add(() -> boneData.getModel().clearDirty());
      return uuid -> this.displayData(uuid, boneData, force);
   }

   private PacketPlayOutEntityMetadata displayData(UUID uuid, DisplayBone.BoneData boneData, boolean force) {
      DisplayBone bone = boneData.getBone();
      PacketDataSerializer buf = NetworkUtils.createByteBuf();
      buf.c(boneData.getId());
      if (force) {
         EntityUtils.writeData(buf, 8, DataWatcherRegistry.b, 0);
      } else if (bone.isTransformDirty()) {
         EntityUtils.writeData(buf, 8, DataWatcherRegistry.b, 0);
      }

      byte update = bone.getSnapshotHandler().getUpdate(uuid);
      bone.getStep().ifDirty(flag -> EntityUtils.writeData(buf, 9, DataWatcherRegistry.b, flag ? 0 : 1), force);
      bone.getGlowing().ifDirty(flag -> EntityUtils.writeData(buf, 0, DataWatcherRegistry.a, (byte)(flag ? 96 : 32)), force);
      bone.getGlowColor().ifDirty(color -> EntityUtils.writeData(buf, 22, DataWatcherRegistry.b, color), force);
      bone.getBrightness().ifDirty(val -> EntityUtils.writeData(buf, 16, DataWatcherRegistry.b, val), force);
      bone.getPosition().ifDirty(vector3f -> EntityUtils.writeData(buf, 11, DataWatcherRegistry.A, vector3f), force || TMath.getBit(update, 0));
      bone.getScale().ifDirty(vector3f -> EntityUtils.writeData(buf, 12, DataWatcherRegistry.A, vector3f), force || TMath.getBit(update, 2));
      bone.getLeftRotation().ifDirty(quaternionf -> EntityUtils.writeData(buf, 13, DataWatcherRegistry.B, quaternionf), force || TMath.getBit(update, 1));
      bone.getRightRotation().ifDirty(quaternionf -> EntityUtils.writeData(buf, 14, DataWatcherRegistry.B, quaternionf), force || TMath.getBit(update, 3));
      bone.getBillboard().ifDirty(billboard -> EntityUtils.writeData(buf, 15, DataWatcherRegistry.a, (byte)billboard.ordinal()));
      bone.getVisibility().ifDirty(flag -> EntityUtils.writeData(buf, 17, DataWatcherRegistry.d, flag ? this.viewRange : 0.0F), force);
      boneData.getModel().ifDirty(itemStack -> EntityUtils.writeData(buf, 23, DataWatcherRegistry.h, CraftItemStack.asNMSCopy(itemStack)), force);
      bone.getDisplay().ifDirty(display -> EntityUtils.writeData(buf, 24, DataWatcherRegistry.a, display == null ? 0 : (byte)display.ordinal()), force);
      buf.k(255);
      this.cleanupQueue.add(bone::clearDirty);
      return new PacketPlayOutEntityMetadata(buf);
   }
}
