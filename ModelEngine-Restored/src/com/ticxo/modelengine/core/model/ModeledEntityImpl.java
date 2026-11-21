package com.ticxo.modelengine.core.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.rootmotion.RootMotionHandler;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.events.AddModelEvent;
import com.ticxo.modelengine.api.events.ModelSetupEvent;
import com.ticxo.modelengine.api.events.RemoveModelEvent;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.behavior.GlobalBehaviorData;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.utils.callback.Callback;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.data.tracker.FloatDataTracker;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.api.utils.meta.MetadataRegistry;
import com.ticxo.modelengine.core.model.bone.manager.MountDataImpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

public class ModeledEntityImpl implements ModeledEntity {
   @Getter
   private final BaseEntity<?> base;
   @Getter
   private final MetadataRegistry metadata = new MetadataRegistry();
   @Getter
   private final AnimationLODHandler animationLodHandler;
   private final Map<String, ActiveModel> models = Maps.newConcurrentMap();
   @Getter
   private final Map<BoneBehaviorType<?>, GlobalBehaviorData> data = Maps.newConcurrentMap();
   @Getter
   private final Map<ModeledEntity.Phase, Map<UUID, Function<ModeledEntity, Boolean>>> tickTasks = Maps.newConcurrentMap();
   @Getter
   private final Callback<Runnable> destroyCallback = new Callback<>(runs -> () -> runs.forEach(Runnable::run));
   @Getter
   private volatile Map<String, ActiveModel> cachedModels;
   @Getter
   private final boolean initialized;
   @Getter
   private final List<Runnable> queuedTask = new ArrayList<>();
   @Getter
   private final FloatDataTracker trueYHeadRot = new FloatDataTracker(0.0F);
   @Getter
   private final FloatDataTracker trueXHeadRot = new FloatDataTracker(0.0F);
   @Getter
   private final FloatDataTracker trueYBodyRot = new FloatDataTracker(0.0F);
   @Getter
   private final RootMotionHandler rootMotionHandler = new RootMotionHandler(this);
   @Getter
   private int tick;
   @Getter
   private boolean isBaseEntityVisible = true;
   @Getter
   private boolean destroyed;
   @Getter
   private boolean removed;
   @Getter
   private int hurtTick = 0;
   @Getter @Setter
   private boolean isModelRotationLocked;
   @Getter
   private int rotationTick = -1;
   @Getter
   private float yHeadRot = 0.0F;
   @Getter
   private float xHeadRot = 0.0F;
   @Getter
   private float yBodyRot = 0.0F;
   @Getter
   private boolean shouldSave = true;
   private volatile String cachedSaveJson;
   @Getter
   private ActiveModel lastHitboxOverride = null;

   public ModeledEntityImpl(@NotNull BaseEntity<?> base, @Nullable Consumer<ModeledEntity> consumer) {
      this.base = base;
      this.animationLodHandler = new AnimationLODHandler(this);
      if (consumer != null) {
         consumer.accept(this);
      }

      synchronized (this.queuedTask) {
         this.queuedTask.forEach(Runnable::run);
         this.initialized = true;
      }

      this.registerSelf();
   }

   @Override
   public boolean tick() {
      if (!this.initialized) {
         return true;
      }

      if (this.hurtTick > 0) {
         this.hurtTick--;
      }

      if (this.base.isAlive()) {
         if (!this.isModelRotationLocked) {
            BodyRotationController bodyRotationController = this.base.getBodyRotationController();
            bodyRotationController.tick();
            this.trueYHeadRot.set(bodyRotationController.getYHeadRot());
            this.trueXHeadRot.set(bodyRotationController.getXHeadRot());
            this.trueYBodyRot.set(bodyRotationController.getYBodyRot());
         }

         if (this.rotationTick == -1) {
            this.yHeadRot = this.trueYHeadRot.get();
            this.xHeadRot = this.trueXHeadRot.get();
            this.yBodyRot = this.trueYBodyRot.get();
            this.trueYHeadRot.clearDirty();
            this.trueXHeadRot.clearDirty();
            this.trueYBodyRot.clearDirty();
            this.rotationTick = 0;
         }

         if (!this.base.isWalking()) {
            this.yBodyRot = this.trueYBodyRot.get();
         }

         if (this.trueYHeadRot.isDirty() || this.trueXHeadRot.isDirty() || this.trueYBodyRot.isDirty()) {
            this.rotationTick = 3;
            this.trueYHeadRot.clearDirty();
            this.trueXHeadRot.clearDirty();
            this.trueYBodyRot.clearDirty();
         }

         if (this.rotationTick > 0) {
            this.yHeadRot = TMath.rotLerp(this.yHeadRot, this.trueYHeadRot.get(), 1.0F / this.rotationTick);
            this.xHeadRot = TMath.rotLerp(this.xHeadRot, this.trueXHeadRot.get(), 1.0F / this.rotationTick);
            this.yBodyRot = TMath.rotLerp(this.yBodyRot, this.trueYBodyRot.get(), 1.0F / this.rotationTick);
            this.rotationTick--;
         }
      }

      this.validateYaws();
      this.tick++;
      if (this.removed || this.base.isRemoved()) {
         return false;
      }

      if (this.base.isAlive()) {
         for (ActiveModel model : this.models.values()) {
            model.tick();
         }

         return true;
      } else {
         boolean hasFinished = true;

         for (ActiveModel model : this.models.values()) {
            model.tick();
            if (hasFinished) {
               hasFinished = model.getAnimationHandler().hasFinishedAllAnimations();
            }
         }

         this.base.setForcedAlive(!hasFinished);
         return !hasFinished && !this.base.getData().getTracking().isEmpty();
      }
   }

   private void validateYaws() {
      if (Float.isNaN(this.trueYHeadRot.get())) {
         this.trueYHeadRot.set(0.0F);
      }

      if (Float.isNaN(this.trueXHeadRot.get())) {
         this.trueXHeadRot.set(0.0F);
      }

      if (Float.isNaN(this.trueYBodyRot.get())) {
         this.trueYBodyRot.set(0.0F);
      }

      this.yHeadRot = Float.isNaN(this.yHeadRot) ? 0.0F : this.yHeadRot;
      this.xHeadRot = Float.isNaN(this.xHeadRot) ? 0.0F : this.xHeadRot;
      this.yBodyRot = Float.isNaN(this.yBodyRot) ? 0.0F : this.yBodyRot;
   }

   @Override
   public void destroy() {
      this.destroyCallback.invoker().run();
      this.destroyed = true;
      this.animationLodHandler.destroy();
      this.models.forEach((s, model) -> model.destroy());
      this.models.clear();
      this.cachedModels = null;
      if (this.base.getOriginal() instanceof Entity entity && !(entity instanceof Player)) {
         ModelEngineAPI.getPivotOverrideRegistry().remove(entity.getUniqueId());
      }
   }

   @Override
   public void markRemoved() {
      this.removed = true;
   }

   @Override
   public void restore() {
      this.removed = false;
   }

   @Override
   public void queuePostInitTask(Runnable runnable) {
      synchronized (this.queuedTask) {
         if (this.initialized) {
            runnable.run();
         } else {
            this.queuedTask.add(runnable);
         }
      }
   }

   @Override
   public void setBaseEntityVisible(boolean flag) {
      if (this.isBaseEntityVisible() != flag) {
         this.isBaseEntityVisible = flag;
         this.base.setVisible(this.isBaseEntityVisible);
      }
   }

   @Override
   public void markHurt() {
      this.hurtTick = 10;
   }

   @Override
   public boolean shouldBeSaved() {
      return this.shouldSave;
   }

   @Override
   public void setSaved(boolean flag) {
      this.shouldSave = flag;
   }

   @Override
   public String getCachedSaveJson() {
      return this.cachedSaveJson;
   }

   @Override
   public void updateSaveCache() {
      try {
         this.save().ifPresentOrElse(data -> this.cachedSaveJson = data.toString(), () -> this.cachedSaveJson = null);
      } catch (Throwable e) {
      }
   }

   @Override
   public boolean isGlowing() {
      return this.base.isGlowing();
   }

   @Override
   public int getGlowColor() {
      return this.base.getGlowColor();
   }

   @Override
   public void setYHeadRot(float rotation) {
      this.trueYHeadRot.set(rotation);
   }

   @Override
   public void setXHeadRot(float rotation) {
      this.trueXHeadRot.set(rotation);
   }

   @Override
   public void setYBodyRot(float rotation) {
      this.trueYBodyRot.set(rotation);
   }

   @Override
   public void setYHeadRotImmediately(float rotation) {
      this.trueYHeadRot.set(rotation);
      this.yHeadRot = rotation;
   }

   @Override
   public void setXHeadRotImmediately(float rotation) {
      this.trueXHeadRot.set(rotation);
      this.xHeadRot = rotation;
   }

   @Override
   public void setYBodyRotImmediately(float rotation) {
      this.trueYBodyRot.set(rotation);
      this.yBodyRot = rotation;
   }

   @Override
   public void setRotationTick(int rotationTick) {
      this.rotationTick = rotationTick;
   }

   @Override
   public Optional<ActiveModel> addModel(@NotNull ActiveModel model, boolean overrideHitbox) {
      assert !this.isDestroyed() : "Modeled Entity has been destroyed!";
      assert model.getModeledEntity() == null || model.isRemoved() : "Active Model already belongs to a different Modeled Entity";
      AddModelEvent event = new AddModelEvent(this, model);
      event.setOverrideHitbox(overrideHitbox);
      ModelEngineAPI.callEvent(event);
      if (event.isCancelled()) {
         return Optional.empty();
      }

      model.setRemoved(false);
      model.setModeledEntity(this);
      model.generateModel();
      Optional<ActiveModel> previous = this.removeModel(model.getBlueprint().getName());
      this.models.put(model.getBlueprint().getName(), model);
      this.cachedModels = null;
      model.getMountManager().ifPresent(mountManager -> {
         GlobalBehaviorData mountData = this.getMountData();
         if (((MountData)mountData).getMainMountManager() == null) {
            ((MountData)mountData).setMainMountManager(mountManager);
         }
      });
      if (overrideHitbox) {
         if (this.lastHitboxOverride != null) {
            this.lastHitboxOverride.setMainHitbox(false);
         }

         model.setMainHitbox(true);
         this.lastHitboxOverride = model;
         Hitbox mainHitbox = model.getBlueprint().getMainHitbox();
         Vector3fc scale = model.getScale();
         Hitbox scaledRenderHitbox = new Hitbox(
            mainHitbox.getWidth() * scale.x(), mainHitbox.getHeight() * scale.y(), mainHitbox.getDepth() * scale.z(), mainHitbox.getEyeHeight() * scale.y()
         );
         this.base.getData().setCullHitbox(scaledRenderHitbox);
         if (this.base.getData() instanceof BukkitEntityData data) {
            Vector3fc hitboxScale = model.getHitboxScale();
            Hitbox scaledHitbox = new Hitbox(
               mainHitbox.getWidth() * hitboxScale.x(),
               mainHitbox.getHeight() * hitboxScale.y(),
               mainHitbox.getDepth() * hitboxScale.z(),
               mainHitbox.getEyeHeight() * hitboxScale.y()
            );
            data.setHitbox(scaledHitbox);
         }
      }

      ModelEngineAPI.callEvent(new ModelSetupEvent(this, model));
      return previous;
   }

   @Override
   public Optional<ActiveModel> removeModel(String id) {
      assert !this.isDestroyed() : "Modeled Entity has been destroyed!";
      ActiveModel model = this.models.get(id);
      if (model == null) {
         return Optional.empty();
      }

      RemoveModelEvent event = new RemoveModelEvent(this, model);
      ModelEngineAPI.callEvent(event);
      if (event.isCancelled()) {
         return Optional.empty();
      }

      this.models.remove(id);
      this.cachedModels = null;
      model.setRemoved(true);
      return Optional.of(model);
   }

   @Override
   public Optional<ActiveModel> getModel(@Nullable String id) {
      return Optional.ofNullable(id == null ? null : this.models.get(id));
   }

   @Override
   public Map<String, ActiveModel> getModels() {
      if (this.cachedModels == null) {
         this.cachedModels = ImmutableMap.copyOf(this.models);
      }

      return this.cachedModels;
   }

   @Override
   public <T extends BoneBehavior> GlobalBehaviorData getOrCreateGlobalBehaviorData(BoneBehaviorType<T> type, Supplier<GlobalBehaviorData> supplier) {
      GlobalBehaviorData existing = this.data.get(type);
      return existing != null ? existing : this.data.computeIfAbsent(type, boneBehaviorType -> supplier.get());
   }

   @Override
   public <T extends BoneBehavior> GlobalBehaviorData getGlobalBehaviorData(BoneBehaviorType<T> type) {
      return this.data.get(type);
   }

   @Override
   public <T extends BoneBehavior> GlobalBehaviorData removeGlobalBehaviorData(BoneBehaviorType<T> type) {
      return this.data.remove(type);
   }

   @Override
   public Map<BoneBehaviorType<?>, GlobalBehaviorData> getAllGlobalBehaviorData(BoneBehaviorType<?> type) {
      return ImmutableMap.copyOf(this.data);
   }

   @Override
   public <T extends GlobalBehaviorData & MountData> T getMountData() {
      return (T)this.getOrCreateGlobalBehaviorData((BoneBehaviorType)BoneBehaviorTypes.MOUNT, MountDataImpl::new);
   }

   @Override
   public boolean isPivotOverride(int id) {
      return this.models.values().stream().anyMatch(model -> model.getPivotOverride().map(override -> override.getId() == id).orElse(false));
   }

   @Override
   public UUID registerTickTask(ModeledEntity.Phase phase, Function<ModeledEntity, Boolean> function) {
      UUID uuid = UUID.randomUUID();
      this.tickTasks.computeIfAbsent(phase, phase1 -> Maps.newConcurrentMap()).put(uuid, function);
      return uuid;
   }

   @Override
   public UUID registerTickTask(ModeledEntity.Phase phase, Consumer<ModeledEntity> consumer) {
      return this.registerTickTask(phase, modeledEntity -> {
         consumer.accept(modeledEntity);
         return false;
      });
   }

   @Override
   public void removeTickTask(UUID uuid) {
      for (Entry<ModeledEntity.Phase, Map<UUID, Function<ModeledEntity, Boolean>>> entry : this.tickTasks.entrySet()) {
         if (entry.getValue().remove(uuid) != null) {
            return;
         }
      }
   }

   @Override
   public void runTickTasks(ModeledEntity.Phase phase) {
      Map<UUID, Function<ModeledEntity, Boolean>> tasks = this.tickTasks.get(phase);
      if (tasks != null) {
         HashSet<UUID> remover = null;

         for (Entry<UUID, Function<ModeledEntity, Boolean>> entry : tasks.entrySet()) {
            if (entry.getValue().apply(this)) {
               if (remover == null) {
                  remover = new HashSet<>();
               }

               remover.add(entry.getKey());
            }
         }

         if (remover != null) {
            remover.forEach(tasks::remove);
         }
      }
   }

   @Override
   public void save(SavedData data) {
      data.putString("version", "R4.0.3");
      data.putBoolean("base_visible", this.isBaseEntityVisible());
      data.putBoolean("rotation_locked", this.isModelRotationLocked());
      data.putFloat("head_y", this.trueYHeadRot.get());
      data.putFloat("head_x", this.trueXHeadRot.get());
      data.putFloat("body_y", this.trueYBodyRot.get());
      ArrayList<SavedData> list = new ArrayList<>();

      for (ActiveModel activeModel : this.models.values()) {
         activeModel.save().ifPresent(list::add);
      }

      data.putList("models", list);
      this.base.save().ifPresent(entityData -> data.putData("base_entity", entityData));
   }

   @Override
   public void load(SavedData data) {
      this.setBaseEntityVisible(data.getBoolean("base_visible"));
      this.setModelRotationLocked(data.getBoolean("rotation_locked"));
      this.trueYHeadRot.set(this.yHeadRot = data.getFloat("head_y", 0.0F));
      this.trueXHeadRot.set(this.xHeadRot = data.getFloat("head_x", 0.0F));
      this.trueYBodyRot.set(this.yBodyRot = data.getFloat("body_y", 0.0F));
      this.trueYHeadRot.clearDirty();
      this.trueXHeadRot.clearDirty();
      this.trueYBodyRot.clearDirty();
      this.base.getBodyRotationController().setYBodyRot(this.yBodyRot);

      for (SavedData modelData : data.getList("models", SavedData.class)) {
         ActiveModel model = ActiveModelImpl.fromData(modelData);
         if (model != null) {
            model.setAutoRendererInitialization(false);
            this.addModel(model, model.isMainHitbox()).ifPresent(ActiveModel::destroy);
            model.load(modelData);
            model.initializeRenderer();
         }
      }

      data.getData("base_entity").ifPresent(this.base::load);
   }
}
