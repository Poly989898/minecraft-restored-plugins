package com.ticxo.modelengine.core.model.render;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.events.BoneTransformReadEvent;
import com.ticxo.modelengine.api.generator.assets.ItemModelData;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverride;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.BoneItems;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.renderer.HeldItemRenderer;
import com.ticxo.modelengine.api.model.bone.type.HeldItem;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.model.render.DisplayFire;
import com.ticxo.modelengine.api.model.render.DisplayRenderer;
import com.ticxo.modelengine.api.model.render.ModelRendererParser;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.utils.data.PooledCollection;
import com.ticxo.modelengine.api.utils.data.PositionCodec;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class DisplayRendererImpl implements DisplayRenderer {
   @Getter
   private final ActiveModel activeModel;
   private final EntityHandler entityHandler;
   @Getter
   private final Map<String, DisplayBone> spawnQueue = new HashMap<>();
   @Getter
   private final Map<String, DisplayBone> rendered = new HashMap<>();
   @Getter
   private final Map<String, DisplayBone> destroyQueue = new HashMap<>();
   private final Set<UUID> fullUpdate = new HashSet<>();
   private final AtomicInteger glowAudienceBones = new AtomicInteger();
   private final Vector3f scratchPosition = new Vector3f();
   private final Vector3f scratchScale = new Vector3f();
   private final Quaternionf scratchLeft = new Quaternionf();
   private final Quaternionf scratchRight = new Quaternionf();
   private final Quaternionf scratchRotated = new Quaternionf();
   private boolean trigValid;
   private int trigYawBits;
   private float yawSin;
   private float yawCos;
   private float halfYawSin;
   private float halfYawCos;
   @Getter
   private DisplayRendererImpl.PivotImpl pivot;
   @Getter
   private DisplayRendererImpl.HitboxImpl hitbox;
   @Getter
   private boolean initialized;
   private boolean firstSpawned;
   private final Queue<Runnable> queuedTasks = new ConcurrentLinkedQueue<>();
   private ModelRendererParser<DisplayRenderer> cachedParser;
   private RenderParsers cachedParserOwner;

   public DisplayRendererImpl(ActiveModel activeModel) {
      this.activeModel = activeModel;
      this.entityHandler = ModelEngineAPI.getEntityHandler();
   }

   @Override
   public void updatePivotOverride(PivotOverride override) {
      if (this.initialized) {
         this.updatePivotOverride0(override);
      } else {
         this.queuedTasks.add(() -> this.updatePivotOverride0(override));
      }
   }

   private void updatePivotOverride0(PivotOverride override) {
      this.activeModel.getPivotOverride().ifPresent(old -> old.removePassenger(this.pivot.id));
      if (override != null) {
         override.addPassenger(this.pivot.id);
      }
   }

   @Override
   public void initialize() {
      ModeledEntity model = this.activeModel.getModeledEntity();
      BaseEntity<?> base = model.getBase();
      Location location = base.getLocation();
      ModelBlueprint blueprint = this.activeModel.getBlueprint();
      Vector3fc scale = this.activeModel.getScale();
      Vector3fc hitboxScale = this.activeModel.getHitboxScale();
      com.ticxo.modelengine.api.entity.Hitbox mainHitbox = blueprint.getMainHitbox();
      float scaledEyeHeight = (float)mainHitbox.getEyeHeight() * scale.y();
      float scaledHeight = (float)mainHitbox.getHeight() * hitboxScale.y();
      float scaledWidth = (float)mainHitbox.getMaxWidth() * hitboxScale.x();
      this.pivot = new DisplayRendererImpl.PivotImpl(this.entityHandler.getNextEntityId());
      this.pivot.pivotOverride = this.activeModel.getPivotOverride().orElse(null);
      this.updateCameraPivot();
      this.pivot.updatePosition(location, scaledEyeHeight);
      this.pivot.getYaw().set(this.pivot.cameraEye != null ? 0.0F : -this.activeModel.getYBodyRot() * (float) (Math.PI / 180.0));
      Vector3f initialOffset = this.pivot.getMountOffset(scaledEyeHeight);

      for (Entry<String, ModelBone> entry : this.activeModel.getBones().entrySet()) {
         this.create(entry.getKey(), entry.getValue(), initialOffset);
      }

      this.forBehaviorRenderer(behaviorRenderer -> {
         behaviorRenderer.setModelRenderer(this);
         behaviorRenderer.initialize();
      });
      this.hitbox = new DisplayRendererImpl.HitboxImpl(
         this.entityHandler.getNextEntityId(), this.entityHandler.getNextEntityId(), this.entityHandler.getNextEntityId()
      );
      this.hitbox.updatePosition(location);
      this.hitbox.getHeight().set(scaledHeight);
      this.hitbox.getWidth().set(scaledWidth);
      this.hitbox.getShadowRadius().set(blueprint.getShadowRadius() * scale.x());
      this.hitbox.getHitboxVisible().set(this.activeModel.isHitboxVisible());
      this.hitbox.getShadowVisible().set(this.activeModel.isShadowVisible());
      this.hitbox.getFireVisible().set(this.activeModel.canRenderFire());
      if (this.hitbox.isHitboxDirty() || this.hitbox.getFireVisible().isDirty()) {
         this.hitbox.updateFireDisplay();
      }

      this.hitbox.setOnFire(this.activeModel.isOnFire());
      ModelEngineAPI.getInteractionTracker().setModelRelay(this.hitbox.hitboxId, this.activeModel);
      this.initialized = true;
      this.firstSpawned = true;

      while (!this.queuedTasks.isEmpty()) {
         this.queuedTasks.poll().run();
      }
   }

   private void updateCameraPivot() {
      Float eye = null;
      Float height = null;
      if (this.activeModel.isCameraPivot()
         && this.pivot.pivotOverride != null
         && this.activeModel.getModeledEntity().getBase().getOriginal() instanceof Entity entity) {
         eye = this.entityHandler.getCameraEyeHeight(entity);
         height = this.entityHandler.getCameraPivotHeight(entity);
         if (eye == null || height == null) {
            eye = null;
            height = null;
         }
      }

      this.pivot.cameraEye = eye;
      if (height != null || this.pivot.cameraHeight.get() != null) {
         this.pivot.cameraHeight.set(height);
      }
   }

   private void create(String boneId, ModelBone modelBone, Vector3f offset) {
      if (modelBone.isRenderer() && modelBone.getPivot() == null) {
         BoneItems models = modelBone.getModels();
         DisplayBoneImpl bone = new DisplayBoneImpl(boneId);
         this.applyGlow(bone, modelBone);
         bone.getBrightness().set(modelBone.getBrightness());
         bone.getBillboard().set(modelBone.getBillboard());
         bone.getVisibility().set(modelBone.isVisible());
         SafeTransform transform = modelBone.getGlobalTransform();
         BoneTransformReadEvent event = new BoneTransformReadEvent(
            modelBone, transform.getPosition(), transform.getLeftQuaternion(), transform.getScale(), transform.getRightQuaternion()
         );
         Bukkit.getPluginManager().callEvent(event);
         modelBone.forBehaviors(boneBehavior -> boneBehavior.modifyTransform(event));
         bone.getPosition().set(event.getPosition().rotateY(this.pivot.yaw.get()).sub(offset));
         bone.getLeftRotation().set(event.getLeftRotation().rotateLocalY(this.pivot.yaw.get()));
         bone.getScale().set(modelBone.isScaleHidden() ? new Vector3f() : TMath.zerofyScale(event.getScale()));
         bone.getRightRotation().set(event.getRightRotation());
         bone.getDisplay().set(ItemDisplayTransform.HEAD);
         bone.getSnapshotHandler().recordSnapshot();
         models.forEach((uuid, stack) -> {
            DisplayBoneImpl.BoneDataImpl data = new DisplayBoneImpl.BoneDataImpl(this.entityHandler.getNextEntityId(), bone);
            data.getModel().set(stack.clone());
            bone.getModel().put(uuid, data);
            this.pivot.passengers.add(data.getId());
         });
         this.initializeSpecialBehaviorRender(modelBone, bone);
         this.spawnQueue.put(boneId, bone);
         this.destroyQueue.remove(boneId);
      }
   }

   private void initializeSpecialBehaviorRender(ModelBone modelBone, DisplayBone bone) {
      modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM).ifPresent(item -> bone.getDisplay().set(((HeldItem)item).getDisplay()));
      modelBone.getBoneBehavior(BoneBehaviorTypes.PLAYER_LIMB).ifPresent(limb -> bone.getDisplay().set(ItemDisplayTransform.THIRDPERSON_RIGHTHAND));
   }

   @Override
   public void readModelData() {
      if (this.initialized) {
         ModeledEntity model = this.activeModel.getModeledEntity();
         BaseEntity<?> base = model.getBase();
         Location location = base.getLocation();
         ModelBlueprint blueprint = this.activeModel.getBlueprint();
         Vector3fc scale = this.activeModel.getScale();
         Vector3fc hitboxScale = this.activeModel.getHitboxScale();
         com.ticxo.modelengine.api.entity.Hitbox mainHitbox = blueprint.getMainHitbox();
         float scaledEyeHeight = (float)mainHitbox.getEyeHeight() * scale.y();
         float scaledHeight = (float)mainHitbox.getHeight() * hitboxScale.y();
         float scaledWidth = (float)mainHitbox.getMaxWidth() * hitboxScale.x();
         this.pivot.pivotOverride = this.activeModel.getPivotOverride().orElse(null);
         this.updateCameraPivot();
         this.pivot.updatePosition(location, scaledEyeHeight);
         this.pivot.getYaw().set(this.pivot.cameraEye != null ? 0.0F : -this.activeModel.getYBodyRot() * (float) (Math.PI / 180.0));
         Vector3f offset = this.pivot.getMountOffset(scaledEyeHeight);
         boolean hasTransformListeners = BoneTransformReadEvent.getHandlerList().getRegisteredListeners().length > 0;
         Map<String, ModelBone> bones = this.activeModel.getBones();

         for (Entry<String, ModelBone> entry : bones.entrySet()) {
            DisplayBone bone = this.getQueued(entry.getKey());
            if (bone == null) {
               this.create(entry.getKey(), entry.getValue(), offset);
            } else {
               this.read(entry.getKey(), bone, entry.getValue(), offset, hasTransformListeners);
            }
         }

         for (Entry<String, DisplayBone> entry : this.rendered.entrySet()) {
            if (!bones.containsKey(entry.getKey()) && !this.spawnQueue.containsKey(entry.getKey())) {
               this.destroyQueue.put(entry.getKey(), entry.getValue());
            }
         }

         for (DisplayBone bone : this.destroyQueue.values()) {
            for (DisplayBone.BoneData boneData : bone.getModel().values()) {
               this.pivot.passengers.remove(boneData.getId());
            }
         }

         this.forBehaviorRenderer(BehaviorRenderer::readBoneData);
         this.hitbox.updatePosition(location);
         this.hitbox.getHeight().set(scaledHeight);
         this.hitbox.getWidth().set(scaledWidth);
         this.hitbox.getShadowRadius().set(blueprint.getShadowRadius() * scale.x());
         this.hitbox.getHitboxVisible().set(this.activeModel.isHitboxVisible());
         this.hitbox.getShadowVisible().set(this.activeModel.isShadowVisible());
         this.hitbox.getFireVisible().set(this.activeModel.canRenderFire());
         if (this.hitbox.isHitboxDirty() || this.hitbox.getFireVisible().isDirty()) {
            this.hitbox.updateFireDisplay();
         }

         this.hitbox.setOnFire(this.activeModel.isOnFire());
      }
   }

   private void read(String boneId, DisplayBone bone, ModelBone modelBone, Vector3f offset, boolean hasTransformListeners) {
      DataTracker<Boolean> shouldRender = bone.getRender();
      shouldRender.set(!modelBone.isEffectivelyInvisible());
      if (shouldRender.get() || shouldRender.isDirty()) {
         bone.getStep().set(modelBone.pollModelScaleChanged() || modelBone.shouldStep() || shouldRender.isDirty());
         SafeTransform transform = modelBone.getGlobalTransform();
         float yaw = this.pivot.yaw.get();
         int yawBits = Float.floatToRawIntBits(yaw);
         if (!this.trigValid || yawBits != this.trigYawBits) {
            this.trigYawBits = yawBits;
            this.trigValid = true;
            this.yawSin = org.joml.Math.sin(yaw);
            this.yawCos = org.joml.Math.cosFromSin(this.yawSin, yaw);
            float halfYaw = yaw * 0.5F;
            this.halfYawSin = org.joml.Math.sin(halfYaw);
            this.halfYawCos = org.joml.Math.cosFromSin(this.halfYawSin, halfYaw);
         }

         if (!hasTransformListeners && !modelBone.hasBoneBehaviors()) {
            bone.getPosition().set(rotateY(transform.getPosition(this.scratchPosition), this.yawSin, this.yawCos).sub(offset));
            bone.getLeftRotation().set(rotateLocalY(transform.getLeftQuaternion(this.scratchLeft), this.halfYawSin, this.halfYawCos, this.scratchRotated));
            bone.getScale().set(TMath.zerofyScale(transform.getScale(this.scratchScale), this.scratchScale));
            bone.getRightRotation().set(transform.getRightQuaternion(this.scratchRight));
         } else {
            BoneTransformReadEvent event = new BoneTransformReadEvent(
               modelBone, transform.getPosition(), transform.getLeftQuaternion(), transform.getScale(), transform.getRightQuaternion()
            );
            if (hasTransformListeners) {
               Bukkit.getPluginManager().callEvent(event);
            }

            modelBone.forBehaviors(boneBehavior -> boneBehavior.modifyTransform(event));
            bone.getPosition().set(rotateY(event.getPosition(), this.yawSin, this.yawCos).sub(offset));
            bone.getLeftRotation().set(rotateLocalY(event.getLeftRotation(), this.halfYawSin, this.halfYawCos, this.scratchRotated));
            bone.getScale().set(TMath.zerofyScale(event.getScale()));
            bone.getRightRotation().set(event.getRightRotation());
         }

         if (modelBone.isScaleHidden()) {
            bone.getScale().set(this.scratchScale.zero());
         }

         bone.getBillboard().set(modelBone.getBillboard());
         bone.getVisibility().set(modelBone.isVisible());
         this.applyGlow(bone, modelBone);
         bone.getBrightness().set(modelBone.getBrightness());
         bone.getSnapshotHandler().recordSnapshot();
         BoneItems models = modelBone.getModels();
         if (models.isDirty()) {
            models.clearDirty();
            ((DisplayBoneImpl)bone).updateBoneData(this.entityHandler, this.pivot.getPassengers(), models);
         }

         this.updateSpecialBehaviorRender(modelBone, bone);
      }

      shouldRender.clearDirty();
   }

   private static Vector3f rotateY(Vector3f vector, float sin, float cos) {
      float x = vector.x * cos + vector.z * sin;
      float z = -vector.x * sin + vector.z * cos;
      vector.x = x;
      vector.z = z;
      return vector;
   }

   private static Quaternionf rotateLocalY(Quaternionf quaternion, float sin, float cos, Quaternionf dest) {
      return dest.set(
         cos * quaternion.x + sin * quaternion.z,
         cos * quaternion.y + sin * quaternion.w,
         cos * quaternion.z - sin * quaternion.x,
         cos * quaternion.w - sin * quaternion.y
      );
   }

   private void updateSpecialBehaviorRender(ModelBone modelBone, DisplayBone bone) {
      modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM).ifPresent(item -> bone.getDisplay().set(((HeldItem)item).getDisplay()));
   }

   private void applyGlow(DisplayBone bone, ModelBone modelBone) {
      Set<UUID> audience = modelBone.getGlowAudience();
      DataTracker<Set<UUID>> audienceTracker = bone.getGlowAudience();
      Set<UUID> previousAudience = audienceTracker.get();
      if (!Objects.equals(previousAudience, audience)) {
         if (previousAudience == null) {
            this.glowAudienceBones.incrementAndGet();
         } else if (audience == null) {
            this.glowAudienceBones.decrementAndGet();
         }

         audienceTracker.set(audience);
      }

      if (audience == null) {
         bone.getGlowing().set(modelBone.isGlowing());
         bone.getGlowColor().set(modelBone.getGlowColor());
         bone.getAudienceGlowing().set(false);
      } else {
         bone.getGlowing().set(false);
         bone.getAudienceGlowing().set(modelBone.isGlowing());
         bone.getAudienceGlowColor().set(modelBone.getGlowColor());
      }
   }

   @Override
   public void sendToClient(RenderParsers parsers) {
      if (this.initialized) {
         this.forManagers(BehaviorManager::preBoneRender);
         this.forBehavior(BoneBehavior::preRender);

         for (String boneId : this.destroyQueue.keySet()) {
            DisplayBone removed = this.rendered.remove(boneId);
            if (removed != null && removed.getGlowAudience().get() != null) {
               this.glowAudienceBones.decrementAndGet();
            }
         }

         this.activeModel.getPreSendCallback().invoker().run();
         this.parser(parsers).sendToClients(this);
         this.activeModel.getPostSendCallback().invoker().run();
         this.rendered.putAll(this.spawnQueue);
         this.spawnQueue.clear();
         this.destroyQueue.clear();
         this.forBehaviorRenderer(behaviorRenderer -> behaviorRenderer.sendToClient(parsers));
         this.forBehavior(BoneBehavior::onRender);
         this.forBehavior(BoneBehavior::postRender);
         this.forManagers(BehaviorManager::postBoneRender);
      }
   }

   @Override
   public void destroy(RenderParsers parsers) {
      if (this.initialized) {
         this.forBehaviorRenderer(behaviorRenderer -> behaviorRenderer.destroy(parsers));
         parsers.getModelParser(this).destroy(this);
         ModelEngineAPI.getInteractionTracker().removeModelRelay(this.hitbox.hitboxId);
         this.activeModel.getPivotOverride().ifPresent(override -> {
            ModeledEntity model = this.activeModel.getModeledEntity();
            if (model != null) {
               List<Entity> basePassengers = model.getBase().getPassengers();
               int[] snapshot = new int[basePassengers.size()];
               int i = 0;

               for (Entity passenger : basePassengers) {
                  snapshot[i++] = passenger.getEntityId();
               }

               override.setBasePassengerSnapshot(snapshot);
            }

            override.removePassenger(this.pivot.id);
         });
      }
   }

   @Override
   public void createRealEntities() {
      World world = this.activeModel.getModeledEntity().getBase().getLocation().getWorld();
      if (world != null) {
         Vector3f pos = this.pivot.getPosition().get();
         Location location = new Location(world, pos.x, pos.y, pos.z);

         for (DisplayBone bone : this.rendered.values()) {
            for (DisplayBone.BoneData boneData : bone.getModel().values()) {
               world.spawn(location, ItemDisplay.class, itemDisplay -> {
                  Quaternionf rotation = bone.getLeftRotation().get();
                  if (ServerInfo.VERSION_NUMBER <= 19) {
                     rotation = rotation.rotateY((float) Math.PI, new Quaternionf());
                  }

                  itemDisplay.setTransformation(new Transformation(bone.getPosition().get(), rotation, bone.getScale().get(), new Quaternionf()));
                  itemDisplay.setItemStack(boneData.getModel().get().clone());
                  itemDisplay.setItemDisplayTransform(bone.getDisplay().get());
               });
            }
         }
      }
   }

   @Override
   public boolean pollFirstSpawn() {
      if (!this.firstSpawned) {
         return false;
      }

      this.firstSpawned = false;
      return true;
   }

   @Override
   public boolean hasGlowAudience() {
      return this.glowAudienceBones.get() > 0;
   }

   private void forManagers(Consumer<BehaviorManager<?>> consumer) {
      for (BehaviorManager<?> manager : this.activeModel.getBehaviorManagers().values()) {
         consumer.accept(manager);
      }
   }

   private void forBehavior(Consumer<BoneBehavior> consumer) {
      Map<String, ModelBone> bones = this.activeModel.getBones();

      for (String boneId : this.activeModel.getBlueprint().getBones().keySet()) {
         ModelBone bone = bones.get(boneId);
         if (bone != null) {
            bone.forBehaviors(consumer);
         }
      }
   }

   private void forBehaviorRenderer(Consumer<BehaviorRenderer> consumer) {
      for (BehaviorRenderer renderer : this.activeModel.getBehaviorRenderers().values()) {
         if (!(renderer instanceof HeldItemRenderer)) {
            consumer.accept(renderer);
         }
      }
   }

   @Override
   public int getTick() {
      return this.activeModel.getModeledEntity().getTick();
   }

   @Override
   public void pushFullUpdate(UUID uuid) {
      this.fullUpdate.add(uuid);
   }

   @Override
   public boolean pollFullUpdate(UUID uuid) {
      return this.fullUpdate.remove(uuid);
   }

   private ModelRendererParser<DisplayRenderer> parser(RenderParsers parsers) {
      if (this.cachedParser == null || this.cachedParserOwner != parsers) {
         this.cachedParserOwner = parsers;
         this.cachedParser = parsers.getModelParser(this);
      }

      return this.cachedParser;
   }

   @Getter
   @AllArgsConstructor
   public class HitboxImpl implements DisplayRenderer.Hitbox {
      private final int pivotId;
      private final UUID pivotUuid = UUID.randomUUID();
      private final int hitboxId;
      private final UUID hitboxUuid = UUID.randomUUID();
      private final int shadowId;
      private final UUID shadowUuid = UUID.randomUUID();
      private final DataTracker<Vector3f> position = new UpdateDataTracker<>(new Vector3f(), Vector3f::set);
      private final PositionCodec positionCodec = new PositionCodec();
      private final DataTracker<Float> width = new DataTracker<>();
      private final DataTracker<Float> height = new DataTracker<>();
      private final DataTracker<Float> shadowRadius = new DataTracker<>();
      private final DataTracker<Boolean> hitboxVisible = new DataTracker<>();
      private final DataTracker<Boolean> shadowVisible = new DataTracker<>();
      private final DataTracker<Boolean> fireVisible = new DataTracker<>();
      private final DataTracker<Boolean> onFire = new DataTracker<>();
      private final PooledCollection<DisplayFire> fireDisplay = new PooledCollection<>(
         () -> new DisplayFireImpl(DisplayRendererImpl.this.entityHandler.getNextEntityId())
      );

      public void updatePosition(Location location) {
         this.position.set(new Vector3f().set(location.getX(), location.getY(), location.getZ()));
      }

      public void updateFireDisplay() {
         this.fireDisplay.releaseAll();
         if (this.isFireVisible()) {
            Map<String, BlueprintBone> fireBones = ModelEngineAPI.getBlueprint("internal_fire").getBones();
            float fireScale = this.width.get() * 1.4F;
            if (!TMath.isSimilar(fireScale, 0.0F)) {
               float fireWidth = 1.0F;
               float fireHeight = this.height.get() / fireScale;
               float fireYOffset = 0.0F;
               float fireZOffset = 0.3F - (int)fireHeight * 0.02F;

               for (int modelType = 0; fireHeight > 0.0F; modelType++) {
                  ItemModelData modelData = fireBones.get("fire_" + modelType % 2).getModelData();
                  float flip = fireHeight / 2.0F % 2.0F == 0.0F ? -1.0F : 1.0F;
                  Optional<ItemStack> maybeStack = modelData.createItemStack().stream().findFirst();
                  if (maybeStack.isPresent()) {
                     DisplayFire fire = this.fireDisplay.getOrCreate();
                     fire.getFireModel().set(maybeStack.get());
                     fire.getPosition().set(new Vector3f(0.0F, fireYOffset, fireZOffset).mul(fireScale));
                     fire.getScale().set(new Vector3f(flip * fireWidth, 1.4F, 1.0F).mul(fireScale));
                  }

                  fireHeight -= 0.45F;
                  fireYOffset += 0.45F;
                  fireWidth *= 0.9F;
                  fireZOffset -= 0.03F;
               }
            }
         }
      }

      public void setOnFire(boolean flag) {
         this.onFire.set(flag, () -> this.fireDisplay.getInUse().forEach(displayFire -> displayFire.getVisible().set(flag)));
         this.onFire.clearDirty();
      }

      @Override
      public void clearDirty() {
         this.width.clearDirty();
         this.height.clearDirty();
         this.shadowRadius.clearDirty();
         this.hitboxVisible.clearDirty();
         this.shadowVisible.clearDirty();
         this.fireVisible.clearDirty();
         this.fireDisplay.getInUse().forEach(DisplayFire::clearDirty);
      }
   }

   @Getter
   @RequiredArgsConstructor
   public static class PivotImpl implements DisplayRenderer.Pivot {
      private PivotOverride pivotOverride;
      private Float cameraEye;
      private final int id;
      private final UUID uuid = UUID.randomUUID();
      private final DataTracker<Vector3f> position = new UpdateDataTracker<>(new Vector3f(), Vector3f::set);
      private final PositionCodec positionCodec = new PositionCodec();
      private final DataTracker<Float> yaw = new DataTracker<>();
      private final DataTracker<Float> cameraHeight = new DataTracker<>();
      private final CollectionDataTracker<Integer> passengers = new CollectionDataTracker<>(new HashSet<>());

      public Vector3f getMountOffset(float eyeHeight) {
         if (this.pivotOverride == null) {
            return new Vector3f(0.0F, eyeHeight, 0.0F);
         }

         if (this.cameraEye != null) {
            return new Vector3f(0.0F, this.cameraEye, 0.0F);
         }

         Vector3f offset = this.pivotOverride.getMountOffset();
         return offset == null ? new Vector3f(0.0F, eyeHeight, 0.0F) : offset;
      }

      public void updatePosition(Location location, float eyeHeight) {
         Vector3f offset = this.getMountOffset(eyeHeight);
         this.position.set(new Vector3f().set(location.getX(), location.getY(), location.getZ()).add(offset));
      }

      @Override
      public void clearDirty() {
         this.yaw.clearDirty();
         this.position.clearDirty();
         this.passengers.clearDirty();
         this.cameraHeight.clearDirty();
      }

      @Override
      public boolean isOverridden() {
         return this.pivotOverride != null;
      }
   }
}
