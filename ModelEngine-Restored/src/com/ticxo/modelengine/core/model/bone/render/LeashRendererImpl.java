package com.ticxo.modelengine.core.model.bone.render;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.interaction.InteractionTracker;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.LeashRenderer;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.utils.data.PositionCodec;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

public class LeashRendererImpl extends AbstractBehaviorRenderer implements LeashRenderer {
   @Getter
   private final Map<String, LeashRenderer.Leash> spawnQueue = new HashMap<>();
   @Getter
   private final Map<String, LeashRenderer.Leash> rendered = new HashMap<>();
   @Getter
   private final Map<String, LeashRenderer.Leash> destroyQueue = new HashMap<>();
   private boolean initialized;
   private BehaviorRendererParser<LeashRenderer> cachedParser;
   private RenderParsers cachedParserOwner;

   public LeashRendererImpl(ActiveModel activeModel) {
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
      Optional maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.LEASH);
      if (!maybeData.isEmpty()) {
         BoneBehavior leashData = (BoneBehavior)maybeData.get();
         LeashRendererImpl.LeashImpl leash = new LeashRendererImpl.LeashImpl(
            this.nmsHandler.getEntityHandler().getNextEntityId(), ((com.ticxo.modelengine.api.model.bone.type.Leash)leashData).getId()
         );
         leash.position.set(((com.ticxo.modelengine.api.model.bone.type.Leash)leashData).getLocation());
         leash.connected.set(this.getConnectedId((com.ticxo.modelengine.api.model.bone.type.Leash)leashData));
         this.spawnQueue.put(boneId, leash);
         this.destroyQueue.remove(boneId);
         ModelEngineAPI.getInteractionTracker().setModelRelay(leash.getLeashId(), this.activeModel);
      }
   }

   @Override
   public void readBoneData() {
      if (this.initialized) {
         this.destroyQueue.putAll(this.rendered);

         for (Entry<String, ModelBone> boneEntry : this.activeModel.getBones().entrySet()) {
            String boneId = boneEntry.getKey();
            ModelBone modelBone = boneEntry.getValue();
            LeashRenderer.Leash renderer = this.getQueued(boneId);
            if (renderer != null) {
               this.read(boneId, renderer, modelBone);
            } else {
               this.create(boneId, modelBone);
            }
         }
      }
   }

   private void read(String boneId, LeashRenderer.Leash leash, ModelBone modelBone) {
      Optional maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.LEASH);
      maybeData.ifPresent(leashData -> {
         leash.getPosition().set(((com.ticxo.modelengine.api.model.bone.type.Leash)leashData).getLocation());
         leash.getConnected().set(this.getConnectedId((com.ticxo.modelengine.api.model.bone.type.Leash)leashData));
         this.destroyQueue.remove(boneId);
      });
   }

   @Override
   public void sendToClient(RenderParsers parsers) {
      if (this.initialized) {
         this.destroyQueue.keySet().forEach(this.rendered::remove);
         this.parser(parsers).sendToClients(this);
         this.destroyQueue.values().forEach(leash -> ModelEngineAPI.getInteractionTracker().removeModelRelay(leash.getLeashId()));
         this.rendered.putAll(this.spawnQueue);
         this.spawnQueue.clear();
         this.destroyQueue.clear();
      }
   }

   @Override
   public void destroy(RenderParsers parsers) {
      if (this.initialized) {
         InteractionTracker tracker = ModelEngineAPI.getInteractionTracker();
         this.rendered.values().forEach(leash -> tracker.removeModelRelay(leash.getLeashId()));
         this.spawnQueue.values().forEach(leash -> tracker.removeModelRelay(leash.getLeashId()));
         parsers.getBehaviorParser(this).destroy(this);
      }
   }

   private int getConnectedId(com.ticxo.modelengine.api.model.bone.type.Leash leash) {
      Entity entity = leash.getConnectedEntity();
      if (entity != null) {
         return entity.getEntityId();
      }

      var other = leash.getConnectedLeash();
      return other != null ? ((com.ticxo.modelengine.api.model.bone.type.Leash)other).getId() : -1;
   }

   private BehaviorRendererParser<LeashRenderer> parser(RenderParsers parsers) {
      if (this.cachedParser == null || this.cachedParserOwner != parsers) {
         this.cachedParserOwner = parsers;
         this.cachedParser = parsers.getBehaviorParser(this);
      }

      return this.cachedParser;
   }

   @Getter
   public static class LeashImpl implements LeashRenderer.Leash {
      private final int pivotId;
      private final UUID pivotUUID = UUID.randomUUID();
      private final int leashId;
      private final UUID leastUUID = UUID.randomUUID();
      private final DataTracker<Vector3f> position = new UpdateDataTracker<>(new Vector3f(), Vector3f::set);
      private final DataTracker<Integer> connected = new DataTracker<>();
      private final PositionCodec positionCodec = new PositionCodec();

      @Generated
      public LeashImpl(int pivotId, int leashId) {
         this.pivotId = pivotId;
         this.leashId = leashId;
      }
   }
}
