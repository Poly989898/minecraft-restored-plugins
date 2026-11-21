package com.ticxo.modelengine.v1_21_R5.parser.vfx;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.vfx.render.VFXDisplayRenderer;
import com.ticxo.modelengine.api.vfx.render.VFXRendererParser;
import com.ticxo.modelengine.v1_21_R5.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R5.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R5.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R5.network.utils.Packets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.joml.Vector3f;

public class VFXDisplayParser implements VFXRendererParser<VFXDisplayRenderer> {
   public void sendToClients(VFXDisplayRenderer renderer) {
      IEntityData data = renderer.getVFX().getBase().getData();
      if (renderer.isRespawnRequired()) {
         this.teleport(renderer.getVFXModel());
         this.spawn(data.getTracking().keySet(), renderer.getVFXModel());
         renderer.setRespawnRequired(false);
      } else {
         this.update(data.getTracking().keySet(), renderer.getVFXModel());
         this.spawn(data.getStartTracking(), renderer.getVFXModel());
         this.remove(data.getStopTracking(), renderer.getVFXModel());
      }
   }

   public void destroy(VFXDisplayRenderer renderer) {
      IEntityData data = renderer.getVFX().getBase().getData();
      HashSet<UUID> inRange = new HashSet<>(data.getStartTracking());
      inRange.addAll(data.getTracking().keySet());
      inRange.addAll(data.getStopTracking());
      this.remove(inRange, renderer.getVFXModel());
   }

   public void spawn(Set<UUID> targets, VFXDisplayRenderer.VFXModel vfx) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();
         set.add(this.pivotSpawn(vfx));
         set.add(this.pivotData(vfx));
         set.add(this.vfxSpawn(vfx));
         set.add(this.vfxData(vfx, true));
         set.add(this.mount(vfx));
         NetworkUtils.sendBundled(targets, set);
      }
   }

   public void update(Set<UUID> targets, VFXDisplayRenderer.VFXModel vfx) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();
         set.add(this.teleport(vfx));
         set.add(this.vfxData(vfx, false));
         NetworkUtils.sendBundled(targets, set);
      }
   }

   public void remove(Set<UUID> targets, VFXDisplayRenderer.VFXModel vfx) {
      if (!targets.isEmpty()) {
         NetworkUtils.send(targets, new ClientboundRemoveEntitiesPacket(new int[]{vfx.getPivotId(), vfx.getModelId()}));
      }
   }

   private Packets.PacketSupplier pivotSpawn(VFXDisplayRenderer.VFXModel vfx) {
      return NetworkUtils.createPivotSpawn(vfx.getPivotId(), vfx.getPivotUuid(), vfx.getOrigin().get());
   }

   private ClientboundSetEntityDataPacket pivotData(VFXDisplayRenderer.VFXModel vfx) {
      return new ClientboundSetEntityDataPacket(vfx.getPivotId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
   }

   private ClientboundAddEntityPacket vfxSpawn(VFXDisplayRenderer.VFXModel vfx) {
      Vector3f location = vfx.getOrigin().get();
      return new ClientboundAddEntityPacket(
         vfx.getModelId(), vfx.getModelUuid(), location.x, location.y, location.z, 0.0F, 0.0F, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0
      );
   }

   private ClientboundSetEntityDataPacket vfxData(VFXDisplayRenderer.VFXModel vfx, boolean spawn) {
      if (!spawn && !vfx.isModelDirty()) {
         return null;
      }

      ArrayList<DataValue<?>> entityData = new ArrayList<>(9);
      if (spawn) {
         entityData.add(new DataValue(0, EntityDataSerializers.BYTE, (byte)32));
         entityData.add(new DataValue(8, EntityDataSerializers.INT, 0));
         entityData.add(new DataValue(9, EntityDataSerializers.INT, 1));
         entityData.add(new DataValue(17, EntityDataSerializers.FLOAT, 4096.0F));
         entityData.add(new DataValue(24, EntityDataSerializers.BYTE, (byte)ItemDisplayTransform.HEAD.ordinal()));
      } else if (vfx.isModelDirty()) {
         entityData.add(new DataValue(8, EntityDataSerializers.INT, 0));
      }

      vfx.getPosition().ifDirty(vector3f -> entityData.add(new DataValue(11, EntityDataSerializers.VECTOR3, vector3f)), spawn);
      vfx.getScale().ifDirty(vector3f -> entityData.add(new DataValue(12, EntityDataSerializers.VECTOR3, vector3f)), spawn);
      vfx.getLeftRotation().ifDirty(quaternionf -> entityData.add(new DataValue(13, EntityDataSerializers.QUATERNION, quaternionf)), spawn);
      vfx.getModel().ifDirty(itemStack -> entityData.add(new DataValue(23, EntityDataSerializers.ITEM_STACK, CraftItemStack.asNMSCopy(itemStack))), spawn);
      vfx.getBrightness().ifDirty(val -> entityData.add(new DataValue(16, EntityDataSerializers.INT, val)), spawn);
      vfx.clearModelDirty();
      return entityData.isEmpty() ? null : new ClientboundSetEntityDataPacket(vfx.getModelId(), entityData);
   }

   private Object teleport(VFXDisplayRenderer.VFXModel vfx) {
      return !vfx.getOrigin().isDirty() ? null : NetworkUtils.createPivotMove(vfx.getPivotId(), vfx.getPositionCodec(), vfx.getOrigin().get());
   }

   private ClientboundSetPassengersPacket mount(VFXDisplayRenderer.VFXModel vfx) {
      return new ClientboundSetPassengersPacket(EntityContainer.of(vfx.getPivotId(), vfx.getModelId()));
   }
}
