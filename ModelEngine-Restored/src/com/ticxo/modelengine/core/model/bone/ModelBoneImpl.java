package com.ticxo.modelengine.core.model.bone;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.property.SubTickProperty;
import com.ticxo.modelengine.api.animation.rootmotion.RootMotion;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneItems;
import com.ticxo.modelengine.api.model.bone.ManualAnimator;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.behavior.ProceduralType;
import com.ticxo.modelengine.api.utils.OffsetMode;
import com.ticxo.modelengine.api.utils.StepFlag;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.MapDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.api.utils.math.Transform;
import com.ticxo.modelengine.core.misc.Cardinal;
import com.ticxo.modelengine.core.model.ActiveModelImpl;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ModelBoneImpl implements ModelBone {
   @Getter
   @NotNull
   private final ActiveModel activeModel;
   @Getter
   @NotNull
   private final BlueprintBone blueprintBone;
   @Getter
   private final Map<String, ModelBone> children = Maps.newConcurrentMap();
   private final Map<BoneBehaviorType<?>, BoneBehavior> boneBehaviors = new LinkedHashMap<>();
   @Getter
   private BoneBehavior[] boneBehaviorsArray = new BoneBehavior[0];
   @Getter
   private final BoneItems itemModels = new BoneItems();
   @Getter
   private final DataTracker<Vector3f> modelScale = new UpdateDataTracker<>(new Vector3f(), Vector3f::set);
   @Getter
   private final Set<ProceduralType> proceduralTypes = new HashSet<>();
   @Getter
   private boolean proceduralAnimation;
   @Getter
   private boolean proceduralTransform;
   @Getter @Setter
   private ManualAnimator manualAnimator;
   @Getter
   private final SafeTransform localTransform = new SafeTransform();
   @Getter
   private final SafeTransform globalTransform = new SafeTransform();
   @Getter
   private final Matrix4f transformMatrix = new Matrix4f();
   @Getter
   private final Map<String, StepFlag> stepFlags = new HashMap<>();
   @Getter
   private final DataTracker<Map<String, StepFlag>> trackedStepFlags = new MapDataTracker<>(new HashMap<>());
   @Getter
   private final Set<String> tickDependencies = new HashSet<>();
   private volatile ModelBoneImpl.SubTicks subTicks;
   @Getter @Setter
   private String customId = null;
   @Getter
   @Nullable
   private ModelBone parent;
   @Getter @Setter
   @Nullable
   private ModelBone pivot;
   @Getter
   private boolean pivotOverride;
   @Getter @Setter
   private Location pivotLocation;
   @Getter @Setter
   private boolean isRenderer;
   @Getter @Setter
   private float yaw;
   @Getter @Setter
   private boolean hasGlobalRotation;
   @Getter
   private Color lastColor;
   @Setter
   private Color defaultTint;
   @Setter
   private Color damageTint;
   @Getter
   private boolean enchanted;
   private Boolean glowing;
   @Setter
   private Integer glowColor;
   @Setter
   private Set<UUID> glowAudience;
   @Setter
   private int blockLight = -1;
   @Setter
   private int skyLight = -1;
   @Setter
   private Billboard billboard;
   @Setter
   private boolean visible = true;
   @Getter
   private boolean scaleHidden;
   @Getter
   private boolean scaleStepped;
   @Getter @Setter
   private boolean markedDestroy;
   @Getter @Setter
   private boolean isBoneOnGround;

   public ModelBoneImpl(@NotNull ActiveModel activeModel, @NotNull BlueprintBone blueprintBone) {
      this.activeModel = activeModel;
      this.blueprintBone = blueprintBone;
      this.updateYaw();
      this.globalTransform.setPosition(blueprintBone.getRotatedGlobalPosition());
      this.globalTransform.setLeftQuaternion(blueprintBone.getGlobalQuaternion());
      this.modelScale.set(new Vector3f(blueprintBone.getModelScale()).mul(blueprintBone.getScale()));
      this.globalTransform.setScale(this.modelScale.get());
      this.isRenderer = blueprintBone.isRenderer();
      if (this.isRenderer) {
         this.setModel(blueprintBone);
      } else {
         BlueprintBone dupeTarget = blueprintBone.getDupeTarget();
         if (dupeTarget != null && dupeTarget.isRenderer()) {
            this.setModel(dupeTarget);
            this.isRenderer = true;
         }
      }

      this.setVisible(blueprintBone.isRenderByDefault());
      this.globalTransform.recordLast();
      this.globalTransform.recordSafe();
      this.localTransform.setPosition(blueprintBone.getLocalPosition());
      this.localTransform.setLeftEuler(blueprintBone.getLocalRotation());
      this.localTransform.recordLast();
      this.localTransform.recordSafe();
   }

   @Override
   public String getUniqueBoneId() {
      return this.getCustomId() == null ? this.getBoneId() : this.getCustomId();
   }

   @Override
   public String getBoneId() {
      return this.blueprintBone.getName();
   }

   @Override
   public void setParent(@Nullable ModelBone bone) {
      if (this.parent != null) {
         this.parent.getChildren().remove(this.getUniqueBoneId());
      }

      this.parent = bone;
      if (this.parent != null) {
         this.parent.getChildren().put(this.getUniqueBoneId(), this);
      }

      this.forBehaviors(boneBehavior -> boneBehavior.onParentSwap(this.parent));
      if (this.activeModel instanceof ActiveModelImpl impl) {
         impl.getBoneSequencer().setDirty(true);
      }
   }

   @Override
   public Location calculatePivotLocation() {
      if (this.pivot != null) {
         Location loc = this.pivot.getPivotLocation();
         if (loc != null) {
            return loc.clone();
         }
      }

      return this.getBaseLocation();
   }

   @Override
   public Location getBaseLocation() {
      return this.activeModel.getModeledEntity().getBase().getLocation().clone();
   }

   @Override
   public void addTickDependencies(String... boneIds) {
      this.tickDependencies.addAll(Arrays.asList(boneIds));
      if (this.activeModel instanceof ActiveModelImpl impl) {
         impl.getBoneSequencer().setDirty(true);
      }
   }

   @Override
   public void removeTickDependencies(String... boneIds) {
      for (String id : boneIds) {
         this.tickDependencies.remove(id);
      }

      if (this.activeModel instanceof ActiveModelImpl impl) {
         impl.getBoneSequencer().setDirty(true);
      }
   }

   @Override
   public void clearTickDependencies() {
      this.tickDependencies.clear();
      if (this.activeModel instanceof ActiveModelImpl impl) {
         impl.getBoneSequencer().setDirty(true);
      }
   }

   @Override
   public void tick() {
      this.stepFlags.clear();
      this.trackedStepFlags.clearDirty();
      this.scaleStepped = false;
      this.localTransform.recordLast();
      this.globalTransform.recordLast();
      this.tryTintModel();
      this.localTransform.identity();
      if (this.manualAnimator == null || this.manualAnimator.applyBoneDefaultLocal()) {
         this.localTransform.setPosition(this.blueprintBone.getLocalPosition());
         this.localTransform.setLeftEuler(this.blueprintBone.getLocalRotation());
      }

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.preAnimation();
      }

      if (this.manualAnimator == null) {
         if (this.proceduralAnimation) {
            for (BoneBehavior behavior : this.boneBehaviorsArray) {
               behavior.onAnimation();
            }
         } else {
            this.activeModel.getAnimationHandler().updateBone(this);
         }
      } else {
         this.manualAnimator.animate(this);
      }

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.postAnimation();
      }

      boolean ownScaleHidden = this.scaleStepped && this.localTransform.getScale().lengthSquared() < 1.0E-5F;
      this.scaleHidden = ownScaleHidden || this.parent != null && this.parent.isScaleHidden();
      if (this.scaleHidden) {
         this.localTransform.setScale(this.localTransform.getLastScale());
      }

      this.localTransform.recordSafe();
      this.updateYaw();
      if (this.proceduralTransform) {
         for (BoneBehavior behavior : this.boneBehaviorsArray) {
            behavior.onGlobalCalculation();
         }
      } else {
         this.calculateGlobalTransform();
      }

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.preTransformDecompose();
      }

      this.decomposeTransform();

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.postTransformDecompose();
      }

      this.recomposeTransform();

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.postTransformRecompose();
      }

      this.globalTransform.mutateScale(scale -> scale.mul((Vector3fc)this.modelScale.get()).mul(this.activeModel.getScale()));
      this.globalTransform.mutatePosition(position -> position.mul(this.activeModel.getScale()));

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.onFinalize();
      }

      if (this.isRootBone()) {
         Vector3f pos = this.localTransform.getSafePosition();
         if (!TMath.isSimilar(pos.lengthSquared(), 0.0F)) {
            this.queueVelocity(new RootMotion(pos, this.activeModel.getYHeadRot(), this.isBoneOnGround));
         }

         this.globalTransform.mutatePosition(Vector3f::zero);
      }

      this.trackedStepFlags.set(this.stepFlags);
      this.globalTransform.recordSafe();
      this.captureSubTicks();
   }

   @Override
   public void lazyTick() {
      this.updateYaw();
      if (!this.children.isEmpty()) {
         this.children.values().forEach(ModelBone::lazyTick);
      }
   }

   private void updateYaw() {
      this.yaw = this.pivot == null ? this.activeModel.getYBodyRot() : this.pivot.getYaw();

      for (int i = 0; i < this.boneBehaviorsArray.length; i++) {
         this.yaw = this.boneBehaviorsArray[i].onUpdateYaw(this.yaw);
      }

      this.yaw = Float.isNaN(this.yaw) ? 0.0F : TMath.wrapDegree(this.yaw);
   }

   @Override
   public void destroy() {
      this.markedDestroy = true;
      this.boneBehaviors.values().forEach(BoneBehavior::onRemove);
      this.children.values().forEach(ModelBone::destroy);
      this.children.clear();
      this.getData().markBoneGlowing(this, false);
      if (this.parent != null && !this.parent.isMarkedDestroy()) {
         this.parent.getChildren().remove(this.getUniqueBoneId());
      }

      this.activeModel.removeBone(this.getUniqueBoneId());
   }

   @Override
   public void setModelScale(int scale) {
      this.modelScale.set(new Vector3f(scale));
   }

   @Override
   public void calculateGlobalTransform() {
      this.transformMatrix.identity();
      if (this.parent != null) {
         this.transformMatrix.set(this.parent.getTransformMatrix());
      }

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.preGlobalCalculation();
      }

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.preGlobalTranslate();
      }

      this.transformMatrix.translate(this.localTransform.getPosition());

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.postGlobalTranslate();
      }

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.preGlobalRotate();
      }

      if (this.parent != null && this.hasGlobalRotation) {
         this.transformMatrix.rotate(this.parent.getGlobalTransform().getLeftQuaternion().invert(new Quaternionf()));
      }

      this.transformMatrix.rotate(this.localTransform.getLeftQuaternion());

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.postGlobalRotate();
      }

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.preGlobalScale();
      }

      TMath.clampedScale(this.transformMatrix, this.localTransform.getScale());

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.postGlobalScale();
      }

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.preGlobalShear();
      }

      this.transformMatrix.rotate(this.localTransform.getRightQuaternion());

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.postGlobalShear();
      }

      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         behavior.postGlobalCalculation();
      }
   }

   @Override
   public boolean hasGlobalRotation() {
      return this.hasGlobalRotation;
   }

   private void decomposeTransform() {
      decomposeInto(this.transformMatrix, this.globalTransform);
   }

   private static void decomposeInto(Matrix4f matrix, Transform target) {
      Transformation t = ModelEngineAPI.getNMSHandler().decompose(matrix);
      Triple<Quaternionf, Vector3f, Quaternionf> correct = Triple.of(t.getLeftRotation(), t.getScale(), t.getRightRotation());
      Vector3f delta = target.getRightQuaternion().difference(t.getRightRotation()).getEulerAnglesXYZ(new Vector3f());
      if (Math.abs(delta.x) >= (float) (Math.PI / 4) || Math.abs(delta.y) >= (float) (Math.PI / 4) || Math.abs(delta.z) >= (float) (Math.PI / 4)) {
         float len = Float.MAX_VALUE;

         for (Cardinal cardinal : Cardinal.cardinals()) {
            Triple<Quaternionf, Vector3f, Quaternionf> wrapped = cardinal.warpTransform(t.getLeftRotation(), t.getScale(), t.getRightRotation());
            delta = target.getRightQuaternion().difference(wrapped.getRight()).getEulerAnglesXYZ(new Vector3f());
            float l = delta.lengthSquared();
            if (len > l) {
               correct = wrapped;
               len = l;
            }
         }
      }

      target.setPosition(t.getTranslation());
      target.setLeftQuaternion(correct.getLeft());
      target.setScale(correct.getMiddle());
      target.setRightQuaternion(correct.getRight());
   }

   private void recomposeTransform() {
      this.transformMatrix
         .identity()
         .translate(this.globalTransform.getPosition())
         .rotate(this.globalTransform.getLeftQuaternion())
         .scale(this.globalTransform.getScale())
         .rotate(this.globalTransform.getRightQuaternion());
   }

   @Override
   public ItemStack getModel() {
      return this.itemModels.getFirst();
   }

   @Override
   public BoneItems getModels() {
      return this.itemModels;
   }

   @Override
   public void setModel(BlueprintBone bone) {
      this.itemModels.update(bone, this.getColor());
   }

   @Override
   public void setModel(ItemStack stack) {
      if (!this.itemModels.isEqual(Set.of(stack))) {
         this.itemModels.clear();
         this.itemModels.add(stack);
      }
   }

   @Override
   public void clearModel() {
      this.itemModels.clear();
   }

   @Override
   public DataTracker<BoneItems> getModelTracker() {
      return this.itemModels.getTracker();
   }

   @Override
   public Color getDefaultTint() {
      return this.defaultTint == null ? this.activeModel.getDefaultTint() : this.defaultTint;
   }

   @Override
   public Color getDamageTint() {
      return this.damageTint == null ? this.activeModel.getDamageTint() : this.damageTint;
   }

   @Override
   public void setEnchanted(boolean flag) {
      if (this.isEnchanted() != flag) {
         this.enchanted = flag;
         this.itemModels.forEach(itemStack -> {
            if (flag) {
               itemStack.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
            } else {
               itemStack.removeEnchantment(Enchantment.VANISHING_CURSE);
            }
         }, true);
      }
   }

   @Override
   public boolean isGlowing() {
      return this.glowing == null ? this.activeModel.isGlowing() : this.glowing;
   }

   @Override
   public void setGlowing(@Nullable Boolean flag) {
      this.glowing = flag;
      this.getData().markBoneGlowing(this, this.glowing != null && this.glowing);
   }

   @Override
   public int getGlowColor() {
      return this.glowColor == null ? this.activeModel.getGlowColor() : this.glowColor;
   }

   @Override
   public Set<UUID> getGlowAudience() {
      return this.glowing == null ? this.activeModel.getGlowAudience() : this.glowAudience;
   }

   @Override
   public int getBlockLight() {
      return this.blockLight == -1 ? this.activeModel.getBlockLight() : this.blockLight;
   }

   @Override
   public int getSkyLight() {
      return this.skyLight == -1 ? this.activeModel.getSkyLight() : this.skyLight;
   }

   @Override
   public Billboard getBillboard() {
      return this.billboard == null ? this.activeModel.getBillboard() : this.billboard;
   }

   @Override
   public boolean isVisible() {
      return this.visible && !this.scaleHidden;
   }

   @Override
   public boolean isEffectivelyInvisible() {
      return !this.isRenderer
         || !this.activeModel.isInvisUpdate()
            && (this.scaleHidden || this.modelScale.get().lengthSquared() < 1.0E-5F || this.globalTransform.getScale().lengthSquared() < 1.0E-5F);
   }

   @Override
   public boolean shouldStep() {
      return this.trackedStepFlags.isDirty();
   }

   @Override
   public void markStep(StepFlag flag, String animation, float key) {
      this.stepFlags.put(animation + ":" + key, flag);
      if (flag == StepFlag.SCALE) {
         this.scaleStepped = true;
      }
   }

   @Override
   public int getSubTickSamples() {
      ModelBoneImpl.SubTicks buffer = this.subTicks;
      return buffer == null ? 0 : buffer.samples;
   }

   @Override
   public void setSubTickSamples(int samples) {
      samples = Math.max(0, samples);
      if (samples > 0 && this.parent != null && this.parent.getSubTickSamples() < samples) {
         this.parent.setSubTickSamples(samples);
      }

      if (samples != this.getSubTickSamples()) {
         this.subTicks = samples == 0 ? null : new ModelBoneImpl.SubTicks(samples);
      }
   }

   @Nullable
   @Override
   public Transform getSubTickTransform(int index) {
      ModelBoneImpl.SubTicks buffer = this.subTicks;
      return buffer != null && buffer.filled && index >= 0 && index <= buffer.samples ? buffer.transforms[index] : null;
   }

   @Nullable
   @Override
   public Matrix4f getSubTickMatrix(int index) {
      ModelBoneImpl.SubTicks buffer = this.subTicks;
      return buffer != null && buffer.filled && index >= 0 && index <= buffer.samples ? buffer.matrices[index] : null;
   }

   @Nullable
   @Override
   public Location getSubTickLocation(int index) {
      Transform transform = this.getSubTickTransform(index);
      return transform == null ? null : this.getLocation(transform.getPosition());
   }

   @Nullable
   @Override
   public Location getSubTickLocation(int index, OffsetMode mode, Vector3f offset, boolean scale) {
      Transform transform = this.getSubTickTransform(index);
      if (transform == null) {
         return null;
      }

      float angle = -this.getYaw() * (float) (Math.PI / 180.0);
      Vector3f bonePosition = transform.getPosition();
      Quaternionf boneLeftRotation = transform.getLeftQuaternion();
      Quaternionf boneRightRotation = transform.getRightQuaternion();
      Quaternionf boneRotation = boneLeftRotation.mul(boneRightRotation, new Quaternionf());
      Vector3f localOffset = new Vector3f(offset);
      if (scale) {
         localOffset.mul(this.activeModel.getScale());
      }
      bonePosition = switch (mode) {
         case LOCAL -> bonePosition.add(localOffset.mul(-1.0F, 1.0F, -1.0F).rotate(boneRotation)).rotateY(angle);
         case MODEL -> bonePosition.add(localOffset.mul(-1.0F, 1.0F, -1.0F)).rotateY(angle);
         case GLOBAL -> bonePosition.rotateY(angle).add(localOffset);
      };
      Location pivot = this.isPivotOverride() ? this.getPivotLocation() : this.calculatePivotLocation();
      return pivot.clone().add(bonePosition.x, bonePosition.y, bonePosition.z);
   }

   @Override
   public boolean isSubTickStep(int index) {
      ModelBoneImpl.SubTicks buffer = this.subTicks;
      return buffer != null && buffer.filled && index >= 0 && index <= buffer.samples ? buffer.steps[index] : false;
   }

   private void captureSubTicks() {
      ModelBoneImpl.SubTicks buffer = this.subTicks;
      if (buffer != null) {
         if (this.parent != null) {
            int parentSamples = this.parent.getSubTickSamples();
            if (parentSamples > buffer.samples) {
               buffer = new ModelBoneImpl.SubTicks(parentSamples);
               this.subTicks = buffer;
            } else if (parentSamples < buffer.samples) {
               this.parent.setSubTickSamples(buffer.samples);
            }
         }

         int samples = buffer.samples;
         buffer.matrices[samples].set(this.transformMatrix);
         buffer.transforms[samples].set(this.globalTransform);
         boolean hasLast = buffer.hasLastGlobal;
         buffer.lastGlobal.set(buffer.tickGlobal);
         decomposeInto(this.transformMatrix, buffer.tickGlobal);
         buffer.hasLastGlobal = true;
         boolean rootBone = this.isRootBone();
         boolean onGround = this.isBoneOnGround;
         boolean globalRotation = this.hasGlobalRotation;
         boolean stepped = this.scaleStepped;
         buffer.tickLocal.set(this.localTransform);
         buffer.tickSteps.clear();
         buffer.tickSteps.putAll(this.stepFlags);
         buffer.lastSteps.clear();
         boolean sampled = !this.proceduralTransform && this.animationDrivesTransform(buffer);

         for (int i = 0; i < samples; i++) {
            double alpha = (double)i / samples;
            Matrix4f matrix = buffer.matrices[i];
            Transform transform = buffer.transforms[i];
            if (sampled) {
               this.applyAnimationSample(buffer, alpha);
               this.composeSubTickMatrix(matrix, i);
               decomposeInto(matrix, transform);
               buffer.steps[i] = i > 0 && !this.stepFlags.equals(buffer.lastSteps);
               buffer.lastSteps.clear();
               buffer.lastSteps.putAll(this.stepFlags);
            } else {
               interpolateTickGlobal(buffer, transform, hasLast ? (float)alpha : 1.0F);
               matrix.set(transform.createMatrix());
               buffer.steps[i] = false;
            }

            transform.setScale(transform.getScale().mul((Vector3fc)this.modelScale.get()).mul(this.activeModel.getScale()));
            transform.setPosition(rootBone ? new Vector3f() : transform.getPosition().mul(this.activeModel.getScale()));
         }

         buffer.steps[samples] = sampled && !buffer.tickSteps.equals(buffer.lastSteps);
         this.localTransform.set(buffer.tickLocal);
         this.stepFlags.clear();
         this.isBoneOnGround = onGround;
         this.hasGlobalRotation = globalRotation;
         this.scaleStepped = stepped;
         buffer.filled = true;
      }
   }

   private boolean animationDrivesTransform(ModelBoneImpl.SubTicks buffer) {
      this.applyAnimationSample(buffer, 1.0);
      this.composeSubTickMatrix(buffer.probe, buffer.samples);
      return buffer.probe.equals(this.transformMatrix, 0.001F);
   }

   private void applyAnimationSample(ModelBoneImpl.SubTicks buffer, double alpha) {
      this.localTransform.identity();
      this.localTransform.setPosition(this.blueprintBone.getLocalPosition());
      this.localTransform.setLeftEuler(this.blueprintBone.getLocalRotation());
      this.stepFlags.clear();
      this.activeModel.getAnimationHandler().updateBone(this, property -> SubTickProperty.at(property, alpha));
      if (this.scaleHidden) {
         this.localTransform.setScale(buffer.tickLocal.getScale());
      }
   }

   private void composeSubTickMatrix(Matrix4f target, int index) {
      target.identity();
      if (this.parent != null) {
         Matrix4f parentMatrix = this.parent.getSubTickMatrix(index);
         target.set(parentMatrix == null ? this.parent.getTransformMatrix() : parentMatrix);
      }

      target.translate(this.localTransform.getPosition());
      if (this.parent != null && this.hasGlobalRotation) {
         Transform parentTransform = this.parent.getSubTickTransform(index);
         Quaternionf parentLeft = parentTransform == null ? this.parent.getGlobalTransform().getLeftQuaternion() : parentTransform.getLeftQuaternion();
         target.rotate(parentLeft.invert());
      }

      target.rotate(this.localTransform.getLeftQuaternion());
      TMath.clampedScale(target, this.localTransform.getScale());
      target.rotate(this.localTransform.getRightQuaternion());
   }

   private static void interpolateTickGlobal(ModelBoneImpl.SubTicks buffer, Transform target, float alpha) {
      target.setPosition(TMath.lerp(buffer.lastGlobal.getPosition(), buffer.tickGlobal.getPosition(), alpha));
      target.setLeftQuaternion(buffer.lastGlobal.getLeftQuaternion().slerp(buffer.tickGlobal.getLeftQuaternion(), alpha));
      target.setScale(TMath.lerp(buffer.lastGlobal.getScale(), buffer.tickGlobal.getScale(), alpha));
      target.setRightQuaternion(buffer.lastGlobal.getRightQuaternion().slerp(buffer.tickGlobal.getRightQuaternion(), alpha));
   }

   @Override
   public boolean pollModelScaleChanged() {
      if (this.modelScale.isDirty()) {
         this.modelScale.clearDirty();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean isRootBone() {
      return this.activeModel.getModeledEntity().getRootMotionHandler().getRootBone() == this;
   }

   @Override
   public Location getLocationUnsafe() {
      return this.getLocation(this.globalTransform.getPosition());
   }

   @Override
   public Location getLocation() {
      return this.getLocation(this.globalTransform.getSafePosition());
   }

   private Location getLocation(Vector3f bonePosition) {
      float angle = -this.getYaw() * (float) (Math.PI / 180.0);
      bonePosition = bonePosition.rotateY(angle, new Vector3f());
      Location pivot = this.isPivotOverride() ? this.getPivotLocation() : this.calculatePivotLocation();
      return pivot.clone().add(bonePosition.x, bonePosition.y, bonePosition.z);
   }

   @Override
   public Location getLocation(OffsetMode mode, Vector3f offset, boolean scale, boolean safe) {
      float angle = -this.getYaw() * (float) (Math.PI / 180.0);
      Vector3f bonePosition = safe ? this.globalTransform.getSafePosition() : this.globalTransform.getPosition();
      Quaternionf boneLeftRotation = safe ? this.globalTransform.getSafeLeftQuaternion() : this.globalTransform.getLeftQuaternion();
      Quaternionf boneRightRotation = safe ? this.globalTransform.getSafeRightQuaternion() : this.globalTransform.getRightQuaternion();
      Quaternionf boneRotation = boneLeftRotation.mul(boneRightRotation, new Quaternionf());
      if (scale) {
         offset.mul(this.activeModel.getScale());
      }
      bonePosition = switch (mode) {
         case LOCAL -> bonePosition.add(offset.mul(-1.0F, 1.0F, -1.0F).rotate(boneRotation)).rotateY(angle);
         case MODEL -> bonePosition.add(offset.mul(-1.0F, 1.0F, -1.0F)).rotateY(angle);
         case GLOBAL -> bonePosition.rotateY(angle).add(offset);
      };
      Location pivot = this.isPivotOverride() ? this.getPivotLocation() : this.calculatePivotLocation();
      return pivot.clone().add(bonePosition.x, bonePosition.y, bonePosition.z);
   }

   @Override
   public void addBoneBehavior(BoneBehavior boneBehavior) {
      this.boneBehaviors.put(boneBehavior.getType(), boneBehavior);
      this.boneBehaviorsArray = this.boneBehaviors.values().toArray(new BoneBehavior[0]);
      boneBehavior.onApply();
      this.proceduralTypes.addAll(boneBehavior.getType().getProceduralTypes());
      this.proceduralAnimation = this.proceduralTypes.contains(ProceduralType.ANIMATION);
      this.proceduralTransform = this.proceduralTypes.contains(ProceduralType.TRANSFORM);
      this.pivotOverride = this.pivotOverride | boneBehavior.getType().isPivot();
   }

   @Override
   public boolean hasBoneBehavior(BoneBehaviorType<?> type) {
      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         if (behavior.getType() == type) {
            return true;
         }
      }

      return false;
   }

   @Override
   public <T extends BoneBehavior> Optional<T> getBoneBehavior(BoneBehaviorType<T> type) {
      BoneBehavior behavior = null;

      for (BoneBehavior b : this.boneBehaviorsArray) {
         if (b.getType() == type) {
            behavior = b;
            break;
         }
      }

      return Optional.ofNullable((T)behavior);
   }

   @Override
   public <T extends BoneBehavior> Optional<T> removeBoneBehavior(BoneBehaviorType<T> type) {
      BoneBehavior behavior = this.boneBehaviors.remove(type);
      if (behavior != null) {
         this.boneBehaviorsArray = this.boneBehaviors.values().toArray(new BoneBehavior[0]);
         behavior.onRemove();
         this.proceduralTypes.clear();

         for (BoneBehavior b : this.boneBehaviorsArray) {
            this.proceduralTypes.addAll(b.getType().getProceduralTypes());
         }

         this.proceduralAnimation = this.proceduralTypes.contains(ProceduralType.ANIMATION);
         this.proceduralTransform = this.proceduralTypes.contains(ProceduralType.TRANSFORM);
      }

      return Optional.ofNullable((T)behavior);
   }

   @Override
   public Map<BoneBehaviorType<?>, BoneBehavior> getImmutableBoneBehaviors() {
      return ImmutableMap.copyOf(this.boneBehaviors);
   }

   @Override
   public boolean hasBoneBehaviors() {
      return this.boneBehaviorsArray.length > 0;
   }

   @Override
   public void forBehaviors(Consumer<BoneBehavior> consumer) {
      for (BoneBehavior behavior : this.boneBehaviorsArray) {
         consumer.accept(behavior);
      }
   }

   private void tryTintModel() {
      Color color = this.getColor();
      if (color != this.lastColor) {
         this.lastColor = color;
         this.itemModels.forEachMutating(itemStack -> ModelEngineAPI.getNMSHandler().colorStack(itemStack, color));
      }
   }

   private Color getColor() {
      return this.activeModel.isMarkedHurt() ? this.getDamageTint() : this.getDefaultTint();
   }

   @Override
   public void save(SavedData data) {
      if (this.defaultTint != null) {
         data.putInt("default_tint", this.defaultTint.asRGB());
      }

      if (this.damageTint != null) {
         data.putInt("damage_tint", this.damageTint.asRGB());
      }

      if (this.blueprintBone.isRenderByDefault() != this.visible) {
         data.putBoolean("visible", this.visible);
      }

      if (this.isEnchanted()) {
         data.putBoolean("enchant", true);
      }

      if (this.glowing != null) {
         data.putBoolean("glowing", this.glowing);
      }

      if (this.glowColor != null) {
         data.putInt("glow_color", this.glowColor);
      }

      if (this.blockLight > 0) {
         data.putInt("block_light", this.blockLight);
      }

      if (this.skyLight > 0) {
         data.putInt("sky_light", this.skyLight);
      }

      if (!this.tickDependencies.isEmpty()) {
         data.putList("tick_dependencies", this.tickDependencies);
      }

      this.forBehaviors(behavior -> behavior.save().ifPresent(data1 -> data.putData(behavior.getType().getId(), data1)));
   }

   @Override
   public void load(SavedData data) {
      Integer defaultTint = data.getInt("default_tint");
      if (defaultTint != null) {
         this.setDefaultTint(Color.fromRGB(defaultTint));
      }

      Integer damageTint = data.getInt("damage_tint");
      if (damageTint != null) {
         this.setDamageTint(Color.fromRGB(damageTint));
      }

      Boolean visible = data.getBoolean("visible");
      if (visible != null) {
         this.setVisible(visible);
      }

      if (data.getBoolean("enchant", false)) {
         this.setEnchanted(true);
      }

      this.setGlowing(data.getBoolean("glowing"));
      this.setGlowColor(data.getInt("glow_color"));
      this.setBlockLight(data.getInt("block_light", -1));
      this.setSkyLight(data.getInt("sky_light", -1));
      List<String> tickDep = data.getList("tick_dependencies", String.class);
      if (tickDep != null) {
         this.tickDependencies.addAll(tickDep);
      }

      this.forBehaviors(behavior -> data.getData(behavior.getType().getId()).ifPresent(behavior::load));
   }

   private IEntityData getData() {
      return this.activeModel.getModeledEntity().getBase().getData();
   }

   private void queueVelocity(RootMotion rootMotion) {
      this.activeModel.getModeledEntity().getRootMotionHandler().queueVelocity(rootMotion);
   }

   private static class SubTicks {
      private final int samples;
      private final Matrix4f[] matrices;
      private final Transform[] transforms;
      private final boolean[] steps;
      private final Matrix4f probe = new Matrix4f();
      private final Transform tickLocal = new Transform();
      private final Transform tickGlobal = new Transform();
      private final Transform lastGlobal = new Transform();
      private boolean hasLastGlobal;
      private volatile boolean filled;
      private final Map<String, StepFlag> tickSteps = new HashMap<>();
      private final Map<String, StepFlag> lastSteps = new HashMap<>();

      private SubTicks(int samples) {
         this.samples = samples;
         this.matrices = new Matrix4f[samples + 1];
         this.transforms = new Transform[samples + 1];
         this.steps = new boolean[samples + 1];

         for (int i = 0; i <= samples; i++) {
            this.matrices[i] = new Matrix4f();
            this.transforms[i] = new Transform();
         }
      }
   }
}
