package com.ticxo.modelengine.v1_21_R7.parser.behavior;

import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.NameTagRenderer;
import com.ticxo.modelengine.v1_21_R7.entity.EntityContainer;
import com.ticxo.modelengine.v1_21_R7.entity.EntityUtils;
import com.ticxo.modelengine.v1_21_R7.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_21_R7.network.utils.Packets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.joml.Vector3f;

public class NameTagParser implements BehaviorRendererParser<NameTagRenderer> {
   private float viewRange = 4096.0F;

   public void sendToClients(NameTagRenderer renderer) {
      this.viewRange = renderer.getActiveModel().getViewRange();
      IEntityData data = renderer.getModelRenderer().getActiveModel().getModeledEntity().getBase().getData();
      this.update(data.getTracking().keySet(), renderer);
      this.spawn(data.getStartTracking(), renderer);
      this.remove(data.getStopTracking(), renderer);
   }

   public void destroy(NameTagRenderer renderer) {
      IEntityData data = renderer.getActiveModel().getModeledEntity().getBase().getData();
      HashSet<UUID> inRange = new HashSet<>(data.getStartTracking());
      inRange.addAll(data.getTracking().keySet());
      inRange.addAll(data.getStopTracking());
      this.remove(inRange, renderer);
   }

   private void spawn(Set<UUID> targets, NameTagRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();

         for (NameTagRenderer.NameTag nameTag : renderer.getRendered().values()) {
            set.add(this.pivotSpawn(nameTag));
            set.add(this.pivotData(nameTag, true));
            set.add(this.tagSpawn(nameTag));
            set.add(this.tagData(nameTag, true));
            set.add(this.pivotMount(nameTag));
         }

         for (NameTagRenderer.NameTag nameTag : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(nameTag));
            set.add(this.pivotData(nameTag, true));
            set.add(this.tagSpawn(nameTag));
            set.add(this.tagData(nameTag, true));
            set.add(this.pivotMount(nameTag));
         }

         NetworkUtils.sendBundled(targets, set);
      }
   }

   private void update(Set<UUID> targets, NameTagRenderer renderer) {
      if (!targets.isEmpty()) {
         Packets set = new Packets();

         for (NameTagRenderer.NameTag nameTag : renderer.getRendered().values()) {
            set.add(this.tagData(nameTag, false));
            if (nameTag.getPosition().isDirty()) {
               set.add(this.pivotMove(nameTag));
               nameTag.getPosition().clearDirty();
            }
         }

         for (NameTagRenderer.NameTag nameTag : renderer.getSpawnQueue().values()) {
            set.add(this.pivotSpawn(nameTag));
            set.add(this.pivotData(nameTag, true));
            set.add(this.tagSpawn(nameTag));
            set.add(this.tagData(nameTag, true));
            set.add(this.pivotMount(nameTag));
         }

         Map<String, NameTagRenderer.NameTag> destroy = renderer.getDestroyQueue();
         if (!destroy.isEmpty()) {
            IntArrayList entityIds = new IntArrayList(destroy.size() * 2);
            entityIds.addAll(IntArrayList.toList(destroy.values().stream().mapToInt(NameTagRenderer.NameTag::getPivotId)));
            entityIds.addAll(IntArrayList.toList(destroy.values().stream().mapToInt(NameTagRenderer.NameTag::getTagId)));
            set.add(new ClientboundRemoveEntitiesPacket(entityIds));
         }

         NetworkUtils.sendBundled(targets, set);
      }
   }

   private void remove(Set<UUID> targets, NameTagRenderer renderer) {
      if (!targets.isEmpty()) {
         Collection<NameTagRenderer.NameTag> nameTags = renderer.getRendered().values();
         IntArrayList entityIds = new IntArrayList(nameTags.size() * 2);
         entityIds.addAll(IntArrayList.toList(nameTags.stream().mapToInt(NameTagRenderer.NameTag::getPivotId)));
         entityIds.addAll(IntArrayList.toList(nameTags.stream().mapToInt(NameTagRenderer.NameTag::getTagId)));
         NetworkUtils.send(targets, new ClientboundRemoveEntitiesPacket(entityIds));
      }
   }

   private Packets.PacketSupplier pivotSpawn(NameTagRenderer.NameTag renderer) {
      return NetworkUtils.createPivotSpawn(renderer.getPivotId(), renderer.getPivotUuid(), renderer.getPosition().get());
   }

   private ClientboundSetEntityDataPacket pivotData(NameTagRenderer.NameTag renderer, boolean spawn) {
      if (!spawn && !renderer.isDirty()) {
         return null;
      } else {
         return !spawn ? null : new ClientboundSetEntityDataPacket(renderer.getPivotId(), EntityUtils.DEFAULT_AREA_EFFECT_CLOUD_DATA);
      }
   }

   private ClientboundAddEntityPacket tagSpawn(NameTagRenderer.NameTag renderer) {
      Vector3f pos = renderer.getPosition().get();
      return new ClientboundAddEntityPacket(
         renderer.getTagId(), renderer.getTagUuid(), pos.x, pos.y, pos.z, 0.0F, 0.0F, EntityType.TEXT_DISPLAY, 0, Vec3.ZERO, 0.0
      );
   }

   private ClientboundSetEntityDataPacket tagData(NameTagRenderer.NameTag renderer, boolean spawn) {
      if (!spawn && !renderer.isDirty()) {
         return null;
      }

      ArrayList<DataValue<?>> entityData = new ArrayList<>(7);
      renderer.getVisibility().ifDirty(flag -> entityData.add(new DataValue(17, EntityDataSerializers.FLOAT, flag ? this.viewRange : 0.0F)), spawn);
      renderer.getTranslate().ifDirty(flag -> entityData.add(new DataValue(11, EntityDataSerializers.VECTOR3, flag)), spawn);
      renderer.getScale().ifDirty(flag -> entityData.add(new DataValue(12, EntityDataSerializers.VECTOR3, flag)), spawn);
      renderer.getTextOpacity().ifDirty(flag -> entityData.add(new DataValue(26, EntityDataSerializers.BYTE, flag)), spawn);
      renderer.getBackgroundColor().ifDirty(flag -> entityData.add(new DataValue(25, EntityDataSerializers.INT, flag)), spawn);
      renderer.getLineWidth().ifDirty(flag -> entityData.add(new DataValue(24, EntityDataSerializers.INT, flag)), spawn);
      renderer.getStyle().ifDirty(flags -> entityData.add(new DataValue(27, EntityDataSerializers.BYTE, flags)), spawn);
      renderer.getBillboard().ifDirty(flag -> {
         byte value = switch (flag) {
            case FIXED -> 0;
            case VERTICAL -> 1;
            case HORIZONTAL -> 2;
            case CENTER -> 3;
            default -> throw new MatchException(null, null);
         };
         entityData.add(new DataValue(15, EntityDataSerializers.BYTE, value));
      }, spawn);
      renderer.getJsonString()
         .ifDirty(
            jsonString -> {
               if (jsonString == null) {
                  entityData.add(new DataValue(23, EntityDataSerializers.COMPONENT, Component.empty()));
               } else {
                  entityData.add(
                     new DataValue(23, EntityDataSerializers.COMPONENT, Optional.ofNullable(CraftChatMessage.fromJSON(jsonString)).orElse(Component.empty()))
                  );
               }
            },
            spawn
         );
      renderer.getBrightness().ifDirty(val -> entityData.add(new DataValue(16, EntityDataSerializers.INT, val)), spawn);
      renderer.clearDirty();
      return entityData.isEmpty() ? null : new ClientboundSetEntityDataPacket(renderer.getTagId(), entityData);
   }

   private Object pivotMove(NameTagRenderer.NameTag renderer) {
      return NetworkUtils.createPivotMove(renderer.getPivotId(), renderer.getPositionCodec(), renderer.getPosition().get());
   }

   private ClientboundSetPassengersPacket pivotMount(NameTagRenderer.NameTag renderer) {
      return new ClientboundSetPassengersPacket(EntityContainer.of(renderer.getPivotId(), renderer.getTagId()));
   }
}
