package com.ticxo.modelengine.v1_21_R1.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.HeldItemRenderer;
import com.ticxo.modelengine.api.utils.data.PositionCodec;
import com.ticxo.modelengine.v1_21_R1.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R1.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R1.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R1.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Pos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

public class HeldItemParser implements BehaviorRendererParser<HeldItemRenderer> {
   private float viewRange = 4096.0F;

   public void sendToClients(HeldItemRenderer renderer) {
      this.viewRange = renderer.getActiveModel().getViewRange();
      IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
      this.update(data.getTracking().keySet(), renderer);
      this.spawn(data.getStartTracking(), renderer);
      this.remove(data.getStopTracking(), renderer);
   }

   public void destroy(HeldItemRenderer renderer) {
      IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
      HashSet<UUID> inRange = new HashSet<>(data.getStartTracking());
      inRange.addAll(data.getTracking().keySet());
      inRange.addAll(data.getStopTracking());
      this.remove(inRange, renderer);
   }

   private void spawn(Set<UUID> targets, HeldItemRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();
         Location location = renderer.getActiveModel().getModeledEntity().getBase().getLocation();
         set.add(this.pivotSpawn(location, renderer));
         set.add(this.pivotData(renderer));

         for (HeldItemRenderer.Item item : renderer.getRendered().values()) {
            set.add(this.itemSpawn(location, item));
            set.add(this.itemData(item, true));
         }

         for (HeldItemRenderer.Item item : renderer.getSpawnQueue().values()) {
            set.add(this.itemSpawn(location, item));
            set.add(this.itemData(item, true));
         }

         set.add(this.mount(renderer));
         NetworkUtils.sendBundled(targets, set);
      }
   }

   private void update(Set<UUID> targets, HeldItemRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();
         set.add(this.teleport(renderer));

         for (HeldItemRenderer.Item item : renderer.getRendered().values()) {
            set.add(this.itemData(item, false));
         }

         Location location = renderer.getActiveModel().getModeledEntity().getBase().getLocation();

         for (HeldItemRenderer.Item item : renderer.getSpawnQueue().values()) {
            set.add(this.itemSpawn(location, item));
            set.add(this.itemData(item, true));
         }

         Map<String, HeldItemRenderer.Item> destroy = renderer.getDestroyQueue();
         if (!destroy.isEmpty()) {
            set.add(new ClientboundRemoveEntitiesPacket(IntArrayList.toList(destroy.values().stream().mapToInt(HeldItemRenderer.Item::getId))));
         }

         if (renderer.getPassengers().isDirty()) {
            set.add(this.mount(renderer));
            renderer.getPassengers().clearDirty();
         }

         NetworkUtils.sendBundled(targets, set);
      }
   }

   private void remove(Set<UUID> targets, HeldItemRenderer renderer) {
      if (!targets.isEmpty()) {
         Map<String, HeldItemRenderer.Item> items = renderer.getRendered();
         IntArrayList entityIds = IntArrayList.toList(items.values().stream().mapToInt(HeldItemRenderer.Item::getId));
         entityIds.add(renderer.getId());
         NetworkUtils.send(targets, new ClientboundRemoveEntitiesPacket(entityIds));
      }
   }

   private ClientboundAddEntityPacket pivotSpawn(Location location, HeldItemRenderer renderer) {
      return new ClientboundAddEntityPacket(
         renderer.getId(), renderer.getUuid(), location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F, EntityType.ARMOR_STAND, 0, Vec3.ZERO, 0.0
      );
   }

   private ClientboundSetEntityDataPacket pivotData(HeldItemRenderer renderer) {
      return new ClientboundSetEntityDataPacket(renderer.getId(), EntityUtils.DEFAULT_ARMOR_STAND_DATA);
   }

   private ClientboundAddEntityPacket itemSpawn(Location location, HeldItemRenderer.Item renderer) {
      return new ClientboundAddEntityPacket(
         renderer.getId(), renderer.getUuid(), location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0
      );
   }

   private ClientboundSetEntityDataPacket itemData(HeldItemRenderer.Item renderer, boolean spawn) {
      if (!spawn && !renderer.isDirty()) {
         return null;
      }

      ArrayList<DataValue<?>> entityData = new ArrayList<>(11);
      if (spawn) {
         entityData.add(new DataValue(8, EntityDataSerializers.INT, 0));
         entityData.add(new DataValue(9, EntityDataSerializers.INT, 1));
         entityData.add(new DataValue(17, EntityDataSerializers.FLOAT, this.viewRange));
      } else if (renderer.isTransformDirty()) {
         entityData.add(new DataValue(8, EntityDataSerializers.INT, 0));
      }

      renderer.getGlowing().ifDirty(flag -> entityData.add(new DataValue(0, EntityDataSerializers.BYTE, (byte)(flag ? 96 : 32))), spawn);
      renderer.getGlowColor().ifDirty(color -> entityData.add(new DataValue(22, EntityDataSerializers.INT, color)), spawn);
      renderer.getPosition().ifDirty(vector3f -> entityData.add(new DataValue(11, EntityDataSerializers.VECTOR3, vector3f)), spawn);
      renderer.getScale().ifDirty(vector3f -> entityData.add(new DataValue(12, EntityDataSerializers.VECTOR3, vector3f)), spawn);
      renderer.getRotation().ifDirty(quaternionf -> entityData.add(new DataValue(13, EntityDataSerializers.QUATERNION, quaternionf)), spawn);
      renderer.getModel().ifDirty(itemStack -> entityData.add(new DataValue(23, EntityDataSerializers.ITEM_STACK, CraftItemStack.asNMSCopy(itemStack))), spawn);
      renderer.getDisplay()
         .ifDirty(display -> entityData.add(new DataValue(24, EntityDataSerializers.BYTE, display == null ? 0 : (byte)display.ordinal())), spawn);
      renderer.clearDirty();
      return entityData.isEmpty() ? null : new ClientboundSetEntityDataPacket(renderer.getId(), entityData);
   }

   private Object teleport(HeldItemRenderer renderer) {
      Location loc = renderer.getActiveModel().getModeledEntity().getBase().getLocation();
      PositionCodec codec = renderer.getPositionCodec();
      if (codec != null) {
         switch (codec.update(loc.getX(), loc.getY(), loc.getZ())) {
            case UNCHANGED:
               return null;
            case MOVE:
               return new Pos(renderer.getId(), codec.getDeltaX(), codec.getDeltaY(), codec.getDeltaZ(), false);
            case ABSOLUTE:
         }
      }

      EntityContainer container = EntityContainer.of(renderer.getId());
      container.setPosRaw(loc.getX(), loc.getY(), loc.getZ());
      return new ClientboundTeleportEntityPacket(container);
   }

   private ClientboundSetPassengersPacket mount(HeldItemRenderer renderer) {
      return new ClientboundSetPassengersPacket(EntityContainer.of(renderer.getId(), renderer.getPassengers()));
   }
}
