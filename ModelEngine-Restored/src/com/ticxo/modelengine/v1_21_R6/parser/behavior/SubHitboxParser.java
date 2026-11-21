package com.ticxo.modelengine.v1_21_R6.parser.behavior;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.SubHitboxRenderer;
import com.ticxo.modelengine.v1_21_R6.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R6.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R6.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R6.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
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
import org.joml.Vector3f;

public class SubHitboxParser implements BehaviorRendererParser<SubHitboxRenderer> {
   public void sendToClients(SubHitboxRenderer renderer) {
      IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
      this.update(data.getTracking().keySet(), renderer);
      this.spawn(data.getStartTracking(), renderer);
      this.remove(data.getStopTracking(), renderer);
   }

   public void destroy(SubHitboxRenderer renderer) {
      IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
      HashSet<UUID> inRange = new HashSet<>(data.getStartTracking());
      inRange.addAll(data.getTracking().keySet());
      inRange.addAll(data.getStopTracking());
      this.remove(inRange, renderer);
   }

   private void spawn(Set<UUID> targets, SubHitboxRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();

         for (SubHitboxRenderer.SubHitbox subHitbox : renderer.getRendered().values()) {
            set.add(this.pivotSpawn(subHitbox));
            set.add(this.pivotData(subHitbox));
            set.add(this.hitboxSpawn(subHitbox));
            set.add(this.hitboxData(subHitbox, true));
            set.add(this.pivotMount(subHitbox));
         }

         for (SubHitboxRenderer.SubHitbox subHitbox : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(subHitbox));
            set.add(this.pivotData(subHitbox));
            set.add(this.hitboxSpawn(subHitbox));
            set.add(this.hitboxData(subHitbox, true));
            set.add(this.pivotMount(subHitbox));
         }

         BaseEntity<?> base = renderer.getActiveModel().getModeledEntity().getBase();
         NetworkUtils.sendBundled(targets, set, player -> player != base.getOriginal());
      }
   }

   private void update(Set<UUID> targets, SubHitboxRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();

         for (SubHitboxRenderer.SubHitbox subHitbox : renderer.getRendered().values()) {
            set.add(this.hitboxData(subHitbox, false));
            if (subHitbox.getPosition().isDirty()) {
               set.add(this.pivotMove(subHitbox));
               subHitbox.getPosition().clearDirty();
            }
         }

         for (SubHitboxRenderer.SubHitbox subHitbox : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(subHitbox));
            set.add(this.pivotData(subHitbox));
            set.add(this.hitboxSpawn(subHitbox));
            set.add(this.hitboxData(subHitbox, true));
            set.add(this.pivotMount(subHitbox));
         }

         Map<String, SubHitboxRenderer.SubHitbox> destroy = renderer.getDestroyQueue();
         if (!destroy.isEmpty()) {
            IntArrayList entityIds = new IntArrayList(destroy.size() * 2);
            entityIds.addAll(IntArrayList.toList(destroy.values().stream().mapToInt(SubHitboxRenderer.SubHitbox::getPivotId)));
            entityIds.addAll(IntArrayList.toList(destroy.values().stream().mapToInt(SubHitboxRenderer.SubHitbox::getHitboxId)));
            set.add(new ClientboundRemoveEntitiesPacket(entityIds));
         }

         BaseEntity<?> base = renderer.getActiveModel().getModeledEntity().getBase();
         NetworkUtils.sendBundled(targets, set, player -> player != base.getOriginal());
      }
   }

   private void remove(Set<UUID> targets, SubHitboxRenderer renderer) {
      if (!targets.isEmpty()) {
         Collection<SubHitboxRenderer.SubHitbox> subHitboxes = renderer.getRendered().values();
         IntArrayList entityIds = new IntArrayList(subHitboxes.size() * 2);
         entityIds.addAll(IntArrayList.toList(subHitboxes.stream().mapToInt(SubHitboxRenderer.SubHitbox::getPivotId)));
         entityIds.addAll(IntArrayList.toList(subHitboxes.stream().mapToInt(SubHitboxRenderer.SubHitbox::getHitboxId)));
         NetworkUtils.send(targets, new ClientboundRemoveEntitiesPacket(entityIds));
      }
   }

   private Packets.PacketSupplier pivotSpawn(SubHitboxRenderer.SubHitbox renderer) {
      return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUuid(), renderer.getPosition().get());
   }

   private ClientboundSetEntityDataPacket pivotData(SubHitboxRenderer.SubHitbox renderer) {
      return new ClientboundSetEntityDataPacket(renderer.getPivotId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
   }

   private Object pivotMove(SubHitboxRenderer.SubHitbox renderer) {
      return NetworkUtils.createPivotMove(renderer.getPivotId(), renderer.getPositionCodec(), renderer.getPosition().get());
   }

   private ClientboundSetPassengersPacket pivotMount(SubHitboxRenderer.SubHitbox renderer) {
      return new ClientboundSetPassengersPacket(EntityContainer.of(renderer.getPivotId(), renderer.getHitboxId()));
   }

   private ClientboundAddEntityPacket hitboxSpawn(SubHitboxRenderer.SubHitbox renderer) {
      Vector3f pos = renderer.getPosition().get();
      return new ClientboundAddEntityPacket(
         renderer.getHitboxId(), renderer.getHitboxUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityType.INTERACTION, 0, Vec3.ZERO, 0.0
      );
   }

   private ClientboundSetEntityDataPacket hitboxData(SubHitboxRenderer.SubHitbox renderer, boolean spawn) {
      if (!spawn && !renderer.isDirty()) {
         return null;
      }

      ArrayList<DataValue<?>> entityData = new ArrayList<>(4);
      renderer.getWidth().ifDirty(width -> entityData.add(new DataValue(8, EntityDataSerializers.FLOAT, width)), spawn);
      renderer.getHeight().ifDirty(height -> entityData.add(new DataValue(9, EntityDataSerializers.FLOAT, height)), spawn);
      renderer.clearDirty();
      return entityData.isEmpty() ? null : new ClientboundSetEntityDataPacket(renderer.getHitboxId(), entityData);
   }
}
