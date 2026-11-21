package com.ticxo.modelengine.core.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.SubHitboxRenderer;
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
import org.joml.Vector3f;

public class SubHitboxRendererImpl extends AbstractBehaviorRenderer implements SubHitboxRenderer {
   @Getter
   private final Map<String, SubHitboxRenderer.SubHitbox> spawnQueue = new HashMap<>();
   @Getter
   private final Map<String, SubHitboxRenderer.SubHitbox> rendered = new HashMap<>();
   @Getter
   private final Map<String, SubHitboxRenderer.SubHitbox> destroyQueue = new HashMap<>();
   private boolean initialized;
   private BehaviorRendererParser<SubHitboxRenderer> cachedParser;
   private RenderParsers cachedParserOwner;

   public SubHitboxRendererImpl(ActiveModel activeModel) {
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
      Optional maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.SUB_HITBOX);
      if (!maybeData.isEmpty()) {
         BoneBehavior subHitboxData = (BoneBehavior)maybeData.get();
         if (!((com.ticxo.modelengine.api.model.bone.type.SubHitbox)subHitboxData).isOBB()) {
            SubHitboxRendererImpl.SubHitboxImpl subHitbox = new SubHitboxRendererImpl.SubHitboxImpl(
               this.nmsHandler.getEntityHandler().getNextEntityId(),
               ((com.ticxo.modelengine.api.model.bone.type.SubHitbox)subHitboxData).getHitboxId(),
               UUID.randomUUID(),
               UUID.randomUUID()
            );
            subHitbox.position.set(((com.ticxo.modelengine.api.model.bone.type.SubHitbox)subHitboxData).getLocation());
            subHitbox.width.set(((com.ticxo.modelengine.api.model.bone.type.SubHitbox)subHitboxData).getDimension().x);
            subHitbox.height.set(((com.ticxo.modelengine.api.model.bone.type.SubHitbox)subHitboxData).getDimension().y);
            this.spawnQueue.put(boneId, subHitbox);
            this.destroyQueue.remove(boneId);
         }
      }
   }

   @Override
   public void readBoneData() {
      if (this.initialized) {
         this.destroyQueue.putAll(this.rendered);

         for (Entry<String, ModelBone> boneEntry : this.activeModel.getBones().entrySet()) {
            String boneId = boneEntry.getKey();
            ModelBone modelBone = boneEntry.getValue();
            SubHitboxRenderer.SubHitbox renderer = this.getQueued(boneId);
            if (renderer != null) {
               this.read(boneId, renderer, modelBone);
            } else {
               this.create(boneId, modelBone);
            }
         }
      }
   }

   private void read(String boneId, SubHitboxRenderer.SubHitbox subHitbox, ModelBone modelBone) {
      Optional maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.SUB_HITBOX);
      maybeData.ifPresent(subHitboxData -> {
         subHitbox.getPosition().set(((com.ticxo.modelengine.api.model.bone.type.SubHitbox)subHitboxData).getLocation());
         subHitbox.getWidth().set(((com.ticxo.modelengine.api.model.bone.type.SubHitbox)subHitboxData).getDimension().x);
         subHitbox.getHeight().set(((com.ticxo.modelengine.api.model.bone.type.SubHitbox)subHitboxData).getDimension().y);
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

   private BehaviorRendererParser<SubHitboxRenderer> parser(RenderParsers parsers) {
      if (this.cachedParser == null || this.cachedParserOwner != parsers) {
         this.cachedParserOwner = parsers;
         this.cachedParser = parsers.getBehaviorParser(this);
      }

      return this.cachedParser;
   }

   @Getter
   public static class SubHitboxImpl implements SubHitboxRenderer.SubHitbox {
      private final int pivotId;
      private final int hitboxId;
      private final UUID pivotUuid;
      private final UUID hitboxUuid;
      private final DataTracker<Vector3f> position = new UpdateDataTracker<>(new Vector3f(), Vector3f::set);
      private final DataTracker<Float> width = new DataTracker<>();
      private final DataTracker<Float> height = new DataTracker<>();
      private final PositionCodec positionCodec = new PositionCodec();

      @Generated
      public SubHitboxImpl(int pivotId, int hitboxId, UUID pivotUuid, UUID hitboxUuid) {
         this.pivotId = pivotId;
         this.hitboxId = hitboxId;
         this.pivotUuid = pivotUuid;
         this.hitboxUuid = hitboxUuid;
      }
   }
}
