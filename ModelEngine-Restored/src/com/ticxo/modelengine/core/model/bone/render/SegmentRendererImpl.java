package com.ticxo.modelengine.core.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.BoneItems;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.SegmentRenderer;
import com.ticxo.modelengine.api.model.bone.type.HeldItem;
import com.ticxo.modelengine.api.model.bone.type.Segment;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.utils.data.PositionCodec;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import com.ticxo.modelengine.core.model.render.DisplayBoneImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import lombok.Generated;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SegmentRendererImpl extends AbstractBehaviorRenderer implements SegmentRenderer {
   private final EntityHandler entityHandler;
   private final Map<String, SegmentRenderer.Pivot> spawnQueue = new HashMap<>();
   private final Map<String, SegmentRenderer.Pivot> rendered = new HashMap<>();
   private final Map<String, SegmentRenderer.Pivot> destroyQueue = new HashMap<>();
   private boolean initialized;
   private final Map<String, ModelBone> unhandled = new HashMap<>();
   private final Map<String, SegmentRenderer.SegmentDisplayBone> bones = new HashMap<>();
   private BehaviorRendererParser<SegmentRenderer> cachedParser;
   private RenderParsers cachedParserOwner;

   public SegmentRendererImpl(ActiveModel activeModel) {
      super(activeModel);
      this.entityHandler = this.nmsHandler.getEntityHandler();
   }

   @Override
   public void initialize() {
      for (Entry<String, ModelBone> entry : this.activeModel.getBones().entrySet()) {
         this.createPivotOrBone(entry.getKey(), entry.getValue(), this.unhandled);
      }

      for (Entry<String, ModelBone> entry : this.unhandled.entrySet()) {
         this.createBone(entry.getKey(), entry.getValue(), null);
      }

      this.unhandled.clear();
      this.initialized = true;
   }

   private void createPivotOrBone(String boneId, ModelBone modelBone, Map<String, ModelBone> unhandled) {
      if (!this.createPivot(boneId, modelBone)) {
         this.createBone(boneId, modelBone, unhandled);
      }
   }

   private boolean createPivot(String boneId, ModelBone modelBone) {
      Optional maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.SEGMENT);
      if (maybeData.isEmpty()) {
         return false;
      }

      BoneBehavior segmentData = (BoneBehavior)maybeData.get();
      SegmentRendererImpl.PivotImpl pivot = new SegmentRendererImpl.PivotImpl(this.entityHandler.getNextEntityId());
      pivot.getPosition().set(((Segment)segmentData).getWorldLocation());
      pivot.getYaw().set(-modelBone.getYaw() * (float) (Math.PI / 180.0));
      this.spawnQueue.put(boneId, pivot);
      this.destroyQueue.remove(boneId);
      return true;
   }

   private void createBone(String boneId, ModelBone modelBone, @Nullable Map<String, ModelBone> unhandled) {
      if (modelBone.isRenderer() && modelBone.getPivot() != null) {
         SegmentRendererImpl.PivotImpl pivot = this.getPivot(modelBone.getPivot().getUniqueBoneId());
         if (pivot == null) {
            if (unhandled != null) {
               unhandled.put(boneId, modelBone);
            }
         } else {
            SafeTransform transform = modelBone.getGlobalTransform();
            BoneItems models = modelBone.getModels();
            SegmentRendererImpl.SegmentDisplayBoneImpl bone = new SegmentRendererImpl.SegmentDisplayBoneImpl(boneId, pivot);
            bone.getGlowing().set(modelBone.isGlowing());
            bone.getGlowColor().set(modelBone.getGlowColor());
            bone.getBrightness().set(modelBone.getBrightness());
            bone.getBillboard().set(modelBone.getBillboard());
            bone.getVisibility().set(modelBone.isVisible());
            bone.getPosition().set(transform.getPosition().rotateY(pivot.yaw.get()));
            bone.getLeftRotation().set(transform.getLeftQuaternion().rotateLocalY(pivot.yaw.get()));
            bone.getScale().set(modelBone.isScaleHidden() ? new Vector3f() : transform.getScale());
            bone.getRightRotation().set(transform.getRightQuaternion());
            bone.getDisplay().set(ItemDisplayTransform.HEAD);
            bone.getSnapshotHandler().recordSnapshot();
            models.forEach((uuid, stack) -> {
               DisplayBoneImpl.BoneDataImpl data = new DisplayBoneImpl.BoneDataImpl(this.entityHandler.getNextEntityId(), bone);
               data.getModel().set(stack.clone());
               bone.getModel().put(uuid, data);
               pivot.passengers.add(data.getId());
            });
            this.initializeSpecialBehaviorRender(modelBone, bone);
            this.bones.put(boneId, bone);
            pivot.spawnQueue.put(boneId, bone);
            pivot.destroyQueue.remove(boneId);
         }
      }
   }

   private void initializeSpecialBehaviorRender(ModelBone modelBone, DisplayBone bone) {
      modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM).ifPresent(item -> bone.getDisplay().set(((HeldItem)item).getDisplay()));
      modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(limb -> bone.getDisplay().set(ItemDisplayTransform.THIRDPERSON_RIGHTHAND));
   }

   @Nullable
   private SegmentRendererImpl.PivotImpl getPivot(String boneId) {
      SegmentRenderer.Pivot pivot = this.getQueued(boneId);
      return pivot instanceof SegmentRendererImpl.PivotImpl impl ? impl : null;
   }

   @Override
   public void readBoneData() {
      if (this.initialized) {
         this.destroyQueue.putAll(this.rendered);
         ArrayList<Runnable> boneReadQueue = new ArrayList<>();
         ArrayList<Runnable> createQueue = new ArrayList<>();

         for (Entry<String, ModelBone> entry : this.activeModel.getBones().entrySet()) {
            String boneId = entry.getKey();
            ModelBone modelBone = entry.getValue();
            SegmentRenderer.Pivot pivot = this.getQueued(boneId);
            if (pivot != null) {
               this.readPivotBone(boneId, pivot, modelBone);
            } else {
               SegmentRenderer.SegmentDisplayBone displayBone = this.bones.get(boneId);
               if (displayBone != null) {
                  boneReadQueue.add(() -> this.readDisplayBone(boneId, displayBone, modelBone));
               } else {
                  createQueue.add(() -> this.createPivotOrBone(boneId, modelBone, this.unhandled));
               }
            }
         }

         boneReadQueue.forEach(Runnable::run);
         createQueue.forEach(Runnable::run);

         for (Entry<String, ModelBone> entry : this.unhandled.entrySet()) {
            this.createBone(entry.getKey(), entry.getValue(), null);
         }

         this.unhandled.clear();
         this.spawnQueue
            .forEach(
               (pivotName, pivot2) -> pivot2.getDestroyQueue().forEach((s, bone) -> bone.getModel().keySet().forEach(id -> pivot2.getPassengers().remove(id)))
            );
         this.rendered
            .forEach(
               (pivotName, pivot3) -> pivot3.getDestroyQueue().forEach((s, bone) -> bone.getModel().keySet().forEach(id -> pivot3.getPassengers().remove(id)))
            );
      }
   }

   private void readPivotBone(String boneId, SegmentRenderer.Pivot pivot, ModelBone modelBone) {
      Optional maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.SEGMENT);
      maybeData.ifPresent(segmentData -> {
         pivot.getPosition().set(((Segment)segmentData).getWorldLocation());
         pivot.getYaw().set(-modelBone.getYaw() * (float) (Math.PI / 180.0));
         this.destroyQueue.remove(boneId);
      });
   }

   private void readDisplayBone(String boneId, SegmentRenderer.SegmentDisplayBone bone, ModelBone modelBone) {
      DataTracker<Boolean> shouldRender = bone.getRender();
      shouldRender.set(!modelBone.isEffectivelyInvisible());
      SegmentRenderer.Pivot pivot = bone.getPivot();
      if (shouldRender.get() || shouldRender.isDirty()) {
         SafeTransform transform = modelBone.getGlobalTransform();
         bone.getStep().set(modelBone.pollModelScaleChanged() || modelBone.shouldStep() || shouldRender.isDirty());
         bone.getPosition().set(transform.getPosition().rotateY(pivot.getYaw().get()));
         bone.getLeftRotation().set(transform.getLeftQuaternion().rotateLocalY(pivot.getYaw().get(), new Quaternionf()));
         bone.getScale().set(modelBone.isScaleHidden() ? new Vector3f() : transform.getScale());
         bone.getRightRotation().set(transform.getRightQuaternion());
         bone.getBillboard().set(modelBone.getBillboard());
         bone.getVisibility().set(modelBone.isVisible());
         bone.getGlowing().set(modelBone.isGlowing());
         bone.getGlowColor().set(modelBone.getGlowColor());
         bone.getBrightness().set(modelBone.getBrightness());
         bone.getSnapshotHandler().recordSnapshot();
         BoneItems models = modelBone.getModels();
         if (models.isDirty()) {
            models.clearDirty();
            ((SegmentRendererImpl.SegmentDisplayBoneImpl)bone).updateBoneData(this.entityHandler, pivot.getPassengers(), models);
         }

         this.updateSpecialBehaviorRender(modelBone, bone);
      }

      shouldRender.clearDirty();
      pivot.getDestroyQueue().remove(boneId);
   }

   private void updateSpecialBehaviorRender(ModelBone modelBone, DisplayBone bone) {
      modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM).ifPresent(item -> bone.getDisplay().set(((HeldItem)item).getDisplay()));
   }

   @Override
   public void sendToClient(RenderParsers parsers) {
      if (this.initialized) {
         this.destroyQueue.keySet().forEach(s -> {
            SegmentRenderer.Pivot pivot = this.rendered.remove(s);
            if (pivot != null) {
               pivot.getSpawnQueue().forEach((s1, bone) -> this.bones.remove(s1));
               pivot.getRendered().forEach((s1, bone) -> this.bones.remove(s1));
               pivot.getDestroyQueue().forEach((s1, bone) -> this.bones.remove(s1));
            }
         });
         this.parser(parsers).sendToClients(this);
         this.rendered.putAll(this.spawnQueue);
         this.spawnQueue.clear();
         this.destroyQueue.clear();
         this.rendered.forEach((s, pivot) -> {
            pivot.getDestroyQueue().forEach((s1, bone) -> {
               pivot.getRendered().remove(s1);
               this.bones.remove(s1);
            });
            pivot.getRendered().putAll(pivot.getSpawnQueue());
            pivot.getSpawnQueue().clear();
         });
      }
   }

   @Override
   public void destroy(RenderParsers parsers) {
      if (this.initialized) {
         parsers.getBehaviorParser(this).destroy(this);
      }
   }

   private BehaviorRendererParser<SegmentRenderer> parser(RenderParsers parsers) {
      if (this.cachedParser == null || this.cachedParserOwner != parsers) {
         this.cachedParserOwner = parsers;
         this.cachedParser = parsers.getBehaviorParser(this);
      }

      return this.cachedParser;
   }

   @Generated
   @Override
   public Map<String, SegmentRenderer.Pivot> getSpawnQueue() {
      return this.spawnQueue;
   }

   @Generated
   @Override
   public Map<String, SegmentRenderer.Pivot> getRendered() {
      return this.rendered;
   }

   @Generated
   @Override
   public Map<String, SegmentRenderer.Pivot> getDestroyQueue() {
      return this.destroyQueue;
   }

   public static class PivotImpl implements SegmentRenderer.Pivot {
      private final Map<String, SegmentRenderer.SegmentDisplayBone> spawnQueue = new HashMap<>();
      private final Map<String, SegmentRenderer.SegmentDisplayBone> rendered = new HashMap<>();
      private final Map<String, SegmentRenderer.SegmentDisplayBone> destroyQueue = new HashMap<>();
      private final int id;
      private final UUID uuid = UUID.randomUUID();
      private final DataTracker<Vector3f> position = new UpdateDataTracker<>(new Vector3f(), Vector3f::set);
      private final PositionCodec positionCodec = new PositionCodec();
      private final DataTracker<Float> yaw = new DataTracker<>();
      private final CollectionDataTracker<Integer> passengers = new CollectionDataTracker<>(new HashSet<>());

      @Override
      public void clearDirty() {
         this.yaw.clearDirty();
         this.passengers.clearDirty();
      }

      @Generated
      @Override
      public int getId() {
         return this.id;
      }

      @Generated
      @Override
      public UUID getUuid() {
         return this.uuid;
      }

      @Generated
      @Override
      public DataTracker<Vector3f> getPosition() {
         return this.position;
      }

      @Generated
      @Override
      public PositionCodec getPositionCodec() {
         return this.positionCodec;
      }

      @Generated
      @Override
      public DataTracker<Float> getYaw() {
         return this.yaw;
      }

      @Generated
      @Override
      public CollectionDataTracker<Integer> getPassengers() {
         return this.passengers;
      }

      @Generated
      public PivotImpl(int id) {
         this.id = id;
      }

      @Generated
      @Override
      public Map<String, SegmentRenderer.SegmentDisplayBone> getSpawnQueue() {
         return this.spawnQueue;
      }

      @Generated
      @Override
      public Map<String, SegmentRenderer.SegmentDisplayBone> getRendered() {
         return this.rendered;
      }

      @Generated
      @Override
      public Map<String, SegmentRenderer.SegmentDisplayBone> getDestroyQueue() {
         return this.destroyQueue;
      }
   }

   public static class SegmentDisplayBoneImpl extends DisplayBoneImpl implements SegmentRenderer.SegmentDisplayBone {
      private final SegmentRenderer.Pivot pivot;

      public SegmentDisplayBoneImpl(String boneId, SegmentRenderer.Pivot pivot) {
         super(boneId);
         this.pivot = pivot;
      }

      @Override
      protected void updateBoneData(EntityHandler entityHandler, CollectionDataTracker<Integer> passengerCollector, BoneItems boneItems) {
         super.updateBoneData(entityHandler, passengerCollector, boneItems);
      }

      @Generated
      @Override
      public SegmentRenderer.Pivot getPivot() {
         return this.pivot;
      }
   }
}
