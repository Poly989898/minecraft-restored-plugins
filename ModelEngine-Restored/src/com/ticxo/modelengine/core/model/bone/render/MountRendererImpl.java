package com.ticxo.modelengine.core.model.bone.render;

import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.MountRenderer;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.model.bone.behavior.MountImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

public class MountRendererImpl extends AbstractBehaviorRenderer implements MountRenderer {
   @Getter
   private final Map<String, MountRenderer.Mount> spawnQueue = new HashMap<>();
   @Getter
   private final Map<String, MountRenderer.Mount> rendered = new HashMap<>();
   @Getter
   private final Map<String, MountRenderer.Mount> destroyQueue = new HashMap<>();
   private boolean initialized;
   private BehaviorRendererParser<MountRenderer> cachedParser;
   private RenderParsers cachedParserOwner;

   public MountRendererImpl(ActiveModel activeModel) {
      super(activeModel);
   }

   @Override
   public void initialize() {
      for (Entry<String, ModelBone> boneEntry : this.activeModel.getBones().entrySet()) {
         String boneId = boneEntry.getKey();
         ModelBone modelBone = boneEntry.getValue();
         this.create(boneId, modelBone);
      }

      this.initialized = true;
   }

   private void create(String boneId, ModelBone modelBone) {
      Optional maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.MOUNT);
      if (!maybeData.isEmpty()) {
         BoneBehavior mountData = (BoneBehavior)maybeData.get();
         MountRendererImpl.MountRenderData mount = new MountRendererImpl.MountRenderData(
            this.nmsHandler.getEntityHandler().getNextEntityId(), UUID.randomUUID(), this.nmsHandler.getEntityHandler().getNextEntityId(), UUID.randomUUID()
         );
         mount.position.set(((com.ticxo.modelengine.api.model.bone.type.Mount)mountData).getGlobalLocation());
         mount.yaw.set(TMath.rotToByte(modelBone.getYaw()));
         mount.health.set(modelBone.getActiveModel().getModeledEntity().getBase().getHealth());
         mount.maxHealth.set(modelBone.getActiveModel().getModeledEntity().getBase().getMaxHealth());
         if (ServerInfo.IS_FOLIA && mountData instanceof MountImpl mountImpl) {
            mount.passengers.get().addAll(mountImpl.getPassengerEntityIds());
         } else {
            for (Entity entity : ((com.ticxo.modelengine.api.model.bone.type.Mount)mountData).getPassengers()) {
               mount.passengers.get().add(entity.getEntityId());
            }
         }

         this.spawnQueue.put(boneId, mount);
         this.destroyQueue.remove(boneId);
      }
   }

   @Override
   public void readBoneData() {
      if (this.initialized) {
         this.destroyQueue.putAll(this.rendered);

         for (Entry<String, ModelBone> boneEntry : this.activeModel.getBones().entrySet()) {
            String boneId = boneEntry.getKey();
            ModelBone modelBone = boneEntry.getValue();
            MountRenderer.Mount renderer = this.getQueued(boneId);
            if (renderer != null) {
               this.read(boneId, renderer, modelBone);
            } else {
               this.create(boneId, modelBone);
            }
         }
      }
   }

   private void read(String boneId, MountRenderer.Mount mount, ModelBone modelBone) {
      mount.getYaw().set(TMath.rotToByte(modelBone.getYaw()));
      mount.getHealth().set(modelBone.getActiveModel().getModeledEntity().getBase().getHealth());
      mount.getMaxHealth().set(modelBone.getActiveModel().getModeledEntity().getBase().getMaxHealth());
      Optional maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.MOUNT);
      maybeData.ifPresent(mountData -> {
         mount.getPosition().set(((com.ticxo.modelengine.api.model.bone.type.Mount)mountData).getGlobalLocation());
         HashSet<Integer> newIds = new HashSet<>();
         if (ServerInfo.IS_FOLIA && mountData instanceof MountImpl mountImpl) {
            newIds.addAll(mountImpl.getPassengerEntityIds());
         } else {
            for (Entity entity : ((com.ticxo.modelengine.api.model.bone.type.Mount)mountData).getPassengers()) {
               newIds.add(entity.getEntityId());
            }
         }

         mount.getPassengers().retainAll(newIds);
         mount.getPassengers().addAll(newIds);
         this.destroyQueue.remove(boneId);
      });
   }

   @Override
   public void sendToClient(RenderParsers parsers) {
      if (this.initialized) {
         this.destroyQueue.keySet().forEach(this.rendered::remove);
         this.parser(parsers).sendToClients(this);
         this.rendered.putAll(this.spawnQueue);
         this.spawnQueue.clear();
         this.destroyQueue.clear();
      }
   }

   @Override
   public void destroy(RenderParsers parsers) {
      if (this.initialized) {
         parsers.getBehaviorParser(this).destroy(this);
      }
   }

   private BehaviorRendererParser<MountRenderer> parser(RenderParsers parsers) {
      if (this.cachedParser == null || this.cachedParserOwner != parsers) {
         this.cachedParserOwner = parsers;
         this.cachedParser = parsers.getBehaviorParser(this);
      }

      return this.cachedParser;
   }

   @Getter
   public static class MountRenderData implements MountRenderer.Mount {
      private final int pivotId;
      private final UUID pivotUuid;
      private final int mountId;
      private final UUID mountUuid;
      private final DataTracker<Vector3f> position = new UpdateDataTracker<>(new Vector3f(), Vector3f::set);
      private final DataTracker<Float> health = new DataTracker<>(20.0F);
      private final DataTracker<Float> maxHealth = new DataTracker<>(20.0F);
      private final DataTracker<Byte> yaw = new DataTracker<>((byte)0);
      private final CollectionDataTracker<Integer> passengers = new CollectionDataTracker<>(new HashSet<>());

      @Generated
      public MountRenderData(int pivotId, UUID pivotUuid, int mountId, UUID mountUuid) {
         this.pivotId = pivotId;
         this.pivotUuid = pivotUuid;
         this.mountId = mountId;
         this.mountUuid = mountUuid;
      }
   }
}
