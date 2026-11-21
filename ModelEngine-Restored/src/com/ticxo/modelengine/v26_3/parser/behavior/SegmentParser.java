package com.ticxo.modelengine.v26_3.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.SegmentRenderer;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.utils.data.UpdateScheme;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.v26_3.entity.EntityContainer;
import com.ticxo.modelengine.v26_3.entity.EntityUtils;
import com.ticxo.modelengine.v26_3.network.utils.NetworkUtils;
import com.ticxo.modelengine.v26_3.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.joml.Vector3f;

public class SegmentParser implements BehaviorRendererParser<SegmentRenderer> {
   private final List<Runnable> cleanupQueue = new ArrayList<>();
   private float viewRange = 4096.0F;

   public void sendToClients(SegmentRenderer renderer) {
      this.cleanupQueue.clear();
      IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
      this.viewRange = renderer.getActiveModel().getViewRange();
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
         IntArrayList destroy = new IntArrayList();

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
            set.add(new ClientboundRemoveEntitiesPacket(destroy));
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
         NetworkUtils.send(targets, new ClientboundRemoveEntitiesPacket(ids));
      }
   }

   private Packets.PacketSupplier pivotSpawn(SegmentRenderer.Pivot pivot) {
      return NetworkUtils.createPivotSpawn(pivot.getId(), pivot.getUuid(), pivot.getPosition().get());
   }

   private ClientboundSetEntityDataPacket pivotData(SegmentRenderer.Pivot pivot) {
      return new ClientboundSetEntityDataPacket(pivot.getId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
   }

   private ClientboundSetPassengersPacket pivotMount(SegmentRenderer.Pivot pivot) {
      return new ClientboundSetPassengersPacket(EntityContainer.of(pivot.getId(), pivot.getPassengers()));
   }

   private Object pivotTeleport(SegmentRenderer.Pivot pivot) {
      if (!pivot.getPosition().isDirty()) {
         return null;
      }

      this.cleanupQueue.add(() -> pivot.getPosition().clearDirty());
      return NetworkUtils.createPivotMove(pivot.getId(), pivot.getPositionCodec(), pivot.getPosition().get());
   }

   private ClientboundAddEntityPacket displaySpawn(SegmentRenderer.Pivot pivot, DisplayBone.BoneData boneData) {
      Vector3f pos = pivot.getPosition().get();
      return new ClientboundAddEntityPacket(boneData.getId(), boneData.getUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityTypes.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
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

   private ClientboundSetEntityDataPacket displayData(UUID uuid, DisplayBone.BoneData boneData, boolean force) {
      DisplayBone bone = boneData.getBone();
      ArrayList<DataValue<?>> entityData = new ArrayList<>(13);
      if (force) {
         entityData.add(new DataValue(8, EntityDataSerializers.INT, 0));
      } else if (bone.isTransformDirty()) {
         entityData.add(new DataValue(8, EntityDataSerializers.INT, 0));
      }

      byte update = bone.getSnapshotHandler().getUpdate(uuid);
      bone.getStep().ifDirty(flag -> entityData.add(new DataValue(9, EntityDataSerializers.INT, flag ? 0 : 1)), force);
      bone.getGlowing().ifDirty(flag -> entityData.add(new DataValue(0, EntityDataSerializers.BYTE, (byte)(flag ? 96 : 32))), force);
      bone.getGlowColor().ifDirty(color -> entityData.add(new DataValue(22, EntityDataSerializers.INT, color)), force);
      bone.getBrightness().ifDirty(val -> entityData.add(new DataValue(16, EntityDataSerializers.INT, val)), force);
      bone.getPosition().ifDirty(vector3f -> entityData.add(new DataValue(11, EntityDataSerializers.VECTOR3, vector3f)), force || TMath.getBit(update, 0));
      bone.getScale().ifDirty(vector3f -> entityData.add(new DataValue(12, EntityDataSerializers.VECTOR3, vector3f)), force || TMath.getBit(update, 2));
      bone.getLeftRotation()
         .ifDirty(quaternionf -> entityData.add(new DataValue(13, EntityDataSerializers.QUATERNION, quaternionf)), force || TMath.getBit(update, 1));
      bone.getRightRotation()
         .ifDirty(quaternionf -> entityData.add(new DataValue(14, EntityDataSerializers.QUATERNION, quaternionf)), force || TMath.getBit(update, 3));
      bone.getBillboard().ifDirty(billboard -> entityData.add(new DataValue(15, EntityDataSerializers.BYTE, (byte)billboard.ordinal())));
      bone.getVisibility().ifDirty(flag -> entityData.add(new DataValue(17, EntityDataSerializers.FLOAT, flag ? this.viewRange : 0.0F)), force);
      boneData.getModel().ifDirty(itemStack -> entityData.add(new DataValue(23, EntityDataSerializers.ITEM_STACK, CraftItemStack.asNMSCopy(itemStack))), force);
      bone.getDisplay().ifDirty(display -> entityData.add(new DataValue(24, EntityDataSerializers.BYTE, display == null ? 0 : (byte)display.ordinal())), force);
      return entityData.isEmpty() ? null : new ClientboundSetEntityDataPacket(boneData.getId(), entityData);
   }
}
