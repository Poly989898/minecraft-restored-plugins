package com.ticxo.modelengine.core.model;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverride;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.utils.callback.Callback;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.CachedImmutableMap;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.meta.MetadataRegistry;
import com.ticxo.modelengine.core.animation.handler.PriorityHandler;
import com.ticxo.modelengine.core.animation.handler.StateMachineHandler;
import com.ticxo.modelengine.core.model.bone.ModelBoneImpl;
import com.ticxo.modelengine.core.model.render.DisplayRendererImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ActiveModelImpl implements ActiveModel {
   @Getter
   private final ModelBlueprint blueprint;
   @Getter
   private final MetadataRegistry metadata = new MetadataRegistry();
   @Getter
   private final ModelRenderer modelRenderer;
   @Getter
   private final AnimationHandler animationHandler;
   private final Map<String, ModelBone> bones = Maps.newConcurrentMap();
   private final Map<String, ModelBone> roots = Maps.newConcurrentMap();
   private final Map<BoneBehaviorType<?>, BehaviorManager<?>> behaviorManagers = new LinkedHashMap<>();
   private final Map<BoneBehaviorType<?>, BehaviorRenderer> behaviorRenderers = new LinkedHashMap<>();
   @Getter
   private final CachedImmutableMap<String, ModelBone> boneCache = new CachedImmutableMap<>(this.bones);
   @Getter
   private final CachedImmutableMap<BoneBehaviorType<?>, BehaviorManager<?>> behaviorManagerCache = new CachedImmutableMap<>(this.behaviorManagers);
   @Getter
   private final CachedImmutableMap<BoneBehaviorType<?>, BehaviorRenderer> behaviorRendererCache = new CachedImmutableMap<>(this.behaviorRenderers);
   private final Vector3f scale = new Vector3f(1.0F, 1.0F, 1.0F);
   private final Vector3f hitboxScale = new Vector3f(1.0F, 1.0F, 1.0F);
   private UUID pivotOverride;
   @Getter @Setter
   private ModeledEntity modeledEntity;
   @Getter @Setter
   private boolean mainHitbox;
   @Getter
   private boolean generated;
   @Getter
   private boolean destroyed;
   @Getter @Setter
   private boolean removed;
   @Getter @Setter
   private boolean autoRendererInitialization = true;
   @Getter @Setter
   private boolean hitboxVisible = true;
   @Getter @Setter
   private boolean shadowVisible = true;
   @Getter @Setter
   private float viewRange = 4096.0F;
   @Getter @Setter
   private boolean canHurt = true;
   @Getter @Setter
   private Color defaultTint = Color.fromRGB(16777215);
   @Getter @Setter
   private Color damageTint = Color.fromRGB(16737894);
   @Getter
   private boolean wasMarkedHurt;
   private Boolean glowing;
   @Setter
   private Integer glowColor;
   @Setter
   private Set<UUID> glowAudience;
   @Getter @Setter
   private int blockLight = -1;
   @Getter @Setter
   private int skyLight = -1;
   @Getter @Setter
   private Billboard billboard = Billboard.FIXED;
   @Setter
   private Boolean onFire;
   @Setter
   private Boolean renderFire;
   @Getter @Setter
   private boolean lockPitch;
   @Getter @Setter
   private boolean lockYaw;
   @Getter @Setter
   private float lockedYHeadRot = 0.0F;
   @Getter @Setter
   private float lockedXHeadRot = 0.0F;
   @Getter @Setter
   private float lockedYBodyRot = 0.0F;
   private Boolean modelRotationLocked;
   @Getter @Setter
   private boolean invisUpdate;
   @Getter @Setter
   private boolean cameraPivot;
   @Getter
   private final BoneSequencer boneSequencer = new BoneSequencer();
   private ActiveModelImpl.ResolvedBoneOrder resolvedBoneOrder;
   @Getter
   private final Callback<ActiveModel.Scale> scaleCallback = new Callback<>(
      scales -> (model, scale) -> scales.forEach(callback -> callback.onScale(model, scale))
   );
   @Getter
   private final Callback<Runnable> preSendCallback = new Callback<>(preSends -> () -> preSends.forEach(Runnable::run));
   @Getter
   private final Callback<Runnable> postSendCallback = new Callback<>(postSends -> () -> postSends.forEach(Runnable::run));

   public ActiveModelImpl(
      @NotNull ModelBlueprint blueprint,
      @Nullable Function<ActiveModel, ModelRenderer> rendererSupplier,
      @Nullable Function<ActiveModel, AnimationHandler> handlerSupplier
   ) {
      this.blueprint = blueprint;
      ModelRenderer renderer = rendererSupplier == null ? new DisplayRendererImpl(this) : rendererSupplier.apply(this);
      this.modelRenderer = renderer == null ? new DisplayRendererImpl(this) : renderer;
      AnimationHandler handler = handlerSupplier == null ? createDefaultHandler(this) : handlerSupplier.apply(this);
      this.animationHandler = handler == null ? createDefaultHandler(this) : handler;
   }

   private static AnimationHandler createDefaultHandler(ActiveModel activeModel) {
      return ConfigProperty.USE_STATE_MACHINE.getBoolean() ? new StateMachineHandler(activeModel) : new PriorityHandler(activeModel);
   }

   public static ActiveModel fromData(SavedData data) {
      try {
         return ModelEngineAPI.createActiveModel(
            data.getString("blueprint"),
            null,
            activeModel -> {
               activeModel.setMainHitbox(data.getBoolean("main_hitbox"));
               return data.getData("animation_handler")
                  .map(handlerData -> ModelEngineAPI.getAnimationHandlerRegistry().createHandler(activeModel, handlerData))
                  .orElse(null);
            }
         );
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   @Override
   public Optional<PivotOverride> getPivotOverride() {
      return ModelEngineAPI.getPivotOverrideRegistry().get(this.pivotOverride);
   }

   @Override
   public void setPivotOverride(PivotOverride override) {
      this.modelRenderer.updatePivotOverride(override);
      this.pivotOverride = override.getUuid();
   }

   @Override
   public Map<String, ModelBone> getBones() {
      return this.boneCache.get();
   }

   @Override
   public Map<BoneBehaviorType<?>, BehaviorManager<?>> getBehaviorManagers() {
      Map<BoneBehaviorType<?>, BehaviorManager<?>> cached = this.behaviorManagerCache.getIfCached();
      if (cached != null) {
         return cached;
      }

      synchronized (this.behaviorManagers) {
         return this.behaviorManagerCache.get();
      }
   }

   @Override
   public Map<BoneBehaviorType<?>, BehaviorRenderer> getBehaviorRenderers() {
      Map<BoneBehaviorType<?>, BehaviorRenderer> cached = this.behaviorRendererCache.getIfCached();
      if (cached != null) {
         return cached;
      }

      synchronized (this.behaviorRenderers) {
         return this.behaviorRendererCache.get();
      }
   }

   @Override
   public Vector3fc getScale() {
      return this.scale;
   }

   @Override
   public void setScale(double scale) {
      this.setScale(new Vector3f((float)scale, (float)scale, (float)scale));
   }

   @Override
   public void setScale(Vector3fc scale) {
      this.scale.set(scale);
      this.scaleCallback.invoker().onScale(this, scale.get(scale.maxComponent()));
      if (this.mainHitbox && this.modeledEntity != null) {
         Vector3fc scaleVec = this.getScale();
         Hitbox mainHitbox = this.blueprint.getMainHitbox();
         Hitbox scaledRenderHitbox = new Hitbox(
            mainHitbox.getWidth() * scaleVec.x(),
            mainHitbox.getHeight() * scaleVec.y(),
            mainHitbox.getDepth() * scaleVec.z(),
            mainHitbox.getEyeHeight() * scaleVec.y()
         );
         this.modeledEntity.getBase().getData().setCullHitbox(scaledRenderHitbox);
      }
   }

   @Override
   public Vector3fc getHitboxScale() {
      return this.hitboxScale;
   }

   @Override
   public void setHitboxScale(double scale) {
      this.setHitboxScale(new Vector3f((float)scale, (float)scale, (float)scale));
   }

   @Override
   public void setHitboxScale(Vector3fc scale) {
      this.hitboxScale.set(scale);
      if (this.mainHitbox && this.modeledEntity != null) {
         if (this.modeledEntity.getBase().getData() instanceof BukkitEntityData data) {
            Vector3fc hitboxScale = this.getHitboxScale();
            Hitbox mainHitbox = this.blueprint.getMainHitbox();
            Hitbox scaledHitbox = new Hitbox(
               mainHitbox.getWidth() * hitboxScale.x(),
               mainHitbox.getHeight() * hitboxScale.y(),
               mainHitbox.getDepth() * hitboxScale.z(),
               mainHitbox.getEyeHeight() * hitboxScale.y()
            );
            data.setHitbox(scaledHitbox);
         }
      }
   }

   @Override
   public void tick() {
      if (!this.isDestroyed()) {
         if (this.modeledEntity.getBase().getData().hasTracking()) {
            this.animationHandler.prepare();
            this.forManagers(BehaviorManager::preScriptTick);
            this.animationHandler.tickGlobal();
            this.forManagers(BehaviorManager::postScriptTick);
            this.forManagers(BehaviorManager::preBoneTick);
            this.boneSequencer.reorder(this.roots);
            List<String> boneOrders = this.boneSequencer.getBoneOrders();
            int index = 0;

            for (String boneId : boneOrders) {
               Map<String, ModelBone> boneMap = this.getBones();
               ActiveModelImpl.ResolvedBoneOrder order = this.resolvedBoneOrder;
               if (order == null || order.source() != boneMap || index >= order.ids().length || order.ids()[index] != boneId) {
                  this.resolvedBoneOrder = order = ActiveModelImpl.ResolvedBoneOrder.resolve(boneMap, boneOrders);
               }

               ModelBone bone = order.source() == boneMap && index < order.ids().length && order.ids()[index] == boneId
                  ? order.bones()[index]
                  : boneMap.get(boneId);
               index++;
               if (bone != null) {
                  bone.tick();
               }
            }

            this.forManagers(BehaviorManager::postBoneTick);
            this.modelRenderer.readModelData();
            this.wasMarkedHurt = this.isMarkedHurt();
         } else {
            this.animationHandler.prepare();
            this.forBones(ModelBone::lazyTick);
            this.animationHandler.tickGlobal();
         }
      }
   }

   @Override
   public void destroy() {
      this.forBones(ModelBone::destroy);
      this.forManagers(BehaviorManager::onDestroy);
      this.bones.clear();
      this.boneCache.invalidate();
      this.behaviorManagerCache.invalidate();
      this.behaviorRendererCache.invalidate();
      this.boneSequencer.setDirty(true);
      this.getData().markModelGlowing(this, false);
      this.modelRenderer.destroy(ModelEngineAPI.getNMSHandler().getGlobalParsers());
      this.destroyed = true;
   }

   @Override
   public void initializeRenderer() {
      if (!this.modelRenderer.isInitialized()) {
         this.modelRenderer.initialize();
      }
   }

   @Override
   public void generateModel() {
      if (!this.generated) {
         this.generated = true;

         for (Entry<String, BlueprintBone> entry : this.blueprint.getFlatMap().entrySet()) {
            BlueprintBone blueprint = entry.getValue();
            ModelBone parent = blueprint.getParent() == null ? null : this.bones.get(blueprint.getParent().getName());
            ModelBoneImpl bone = new ModelBoneImpl(this, blueprint);
            this.configureChildBone(blueprint, parent, bone);
            this.bones.put(bone.getUniqueBoneId(), bone);
            this.boneCache.invalidate();
            this.behaviorManagerCache.invalidate();
            this.behaviorRendererCache.invalidate();
            this.boneSequencer.setDirty(true);
         }

         this.bones.forEach((s, modelBone) -> modelBone.forBehaviors(BoneBehavior::onModelInitialized));
         if (this.autoRendererInitialization) {
            this.modelRenderer.initialize();
         }
      }
   }

   @Override
   public void forceGenerateBone(String parentId, String prefix, final BlueprintBone blueprintBone) {
      ModelBone parentBone = parentId == null ? null : this.getBone(parentId).orElse(null);
      HashMap<String, ModelBone> forceBoneMap = new HashMap<>();
      var queue = new LinkedList<BlueprintBone>() {
         {
            this.add(blueprintBone);
         }
      };

      while (!queue.isEmpty()) {
         BlueprintBone blueprint = queue.pop();
         queue.addAll(blueprint.getChildren().values());
         String name = blueprint.getName();
         String customId = prefix + name;
         if (this.bones.containsKey(customId)) {
            TLogger.error("Unable to force generate custom bone: ID " + customId + " already exists.");
         } else {
            ModelBone parent = blueprint.getParent() == null ? parentBone : forceBoneMap.getOrDefault(blueprint.getParent().getName(), parentBone);
            ModelBoneImpl bone = new ModelBoneImpl(this, blueprint);
            bone.setCustomId(customId);
            this.configureChildBone(blueprint, parent, bone);
            forceBoneMap.put(name, bone);
            this.bones.put(bone.getUniqueBoneId(), bone);
            this.boneCache.invalidate();
            this.behaviorManagerCache.invalidate();
            this.behaviorRendererCache.invalidate();
            this.boneSequencer.setDirty(true);
         }
      }
   }

   private void configureChildBone(BlueprintBone blueprint, ModelBone parent, ModelBoneImpl bone) {
      if (parent != null) {
         bone.setParent(parent);
         if (parent.isPivotOverride()) {
            bone.setPivot(parent);
         } else if (parent.getPivot() != null) {
            bone.setPivot(parent.getPivot());
         }
      } else {
         this.roots.put(bone.getUniqueBoneId(), bone);
      }

      for (Entry<BoneBehaviorType<?>, BoneBehaviorType.CachedProvider<?>> behaviorEntry : blueprint.getCachedBehaviorProvider().entrySet()) {
         BoneBehaviorType<?> type = behaviorEntry.getKey();
         BoneBehaviorType.CachedProvider<?> provider = behaviorEntry.getValue();
         this.getBehaviorManager(type);
         this.getBehaviorRenderer(type);
         bone.addBoneBehavior(provider.create(bone));
      }
   }

   @Override
   public void removeBone(String bone) {
      ModelBone removed = this.bones.remove(bone);
      if (removed != null) {
         removed.setParent(null);
         this.roots.remove(bone);
         this.boneCache.invalidate();
         this.boneSequencer.setDirty(true);
      }
   }

   @Override
   public boolean canHurt() {
      return this.canHurt;
   }

   @Override
   public boolean wasMarkedHurt() {
      return this.wasMarkedHurt;
   }

   @Override
   public boolean isMarkedHurt() {
      return this.canHurt && this.modeledEntity != null && this.modeledEntity.getHurtTick() > 0;
   }

   @Override
   public boolean isGlowing() {
      return this.glowing == null ? this.modeledEntity.isGlowing() : this.glowing;
   }

   @Override
   public void setGlowing(@Nullable Boolean flag) {
      this.glowing = flag;
      this.getData().markModelGlowing(this, this.glowing != null && this.glowing);
   }

   @Override
   public int getGlowColor() {
      return this.glowColor == null ? this.modeledEntity.getGlowColor() : this.glowColor;
   }

   @Override
   public Set<UUID> getGlowAudience() {
      return this.glowAudience;
   }

   @Override
   public boolean isOnFire() {
      return this.onFire == null ? this.modeledEntity.getBase().isOnFire() : this.onFire;
   }

   @Override
   public boolean canRenderFire() {
      return this.renderFire == null ? ModelEngineAPI.getConfigCache().isRenderFire() : this.renderFire;
   }

   @Override
   public float getXHeadRot() {
      return this.useLock() ? this.lockedXHeadRot : (this.lockPitch ? 0.0F : this.modeledEntity.getXHeadRot());
   }

   @Override
   public float getYHeadRot() {
      return this.useLock() ? this.lockedYHeadRot : (this.lockYaw ? this.modeledEntity.getYBodyRot() : this.modeledEntity.getYHeadRot());
   }

   @Override
   public float getYBodyRot() {
      return this.useLock() ? this.lockedYBodyRot : this.modeledEntity.getYBodyRot();
   }

   private boolean useLock() {
      return this.modelRotationLocked != null && this.modelRotationLocked;
   }

   @Override
   public boolean isModelRotationLocked() {
      return this.modelRotationLocked == null ? this.modeledEntity.isModelRotationLocked() : this.modelRotationLocked;
   }

   @Override
   public void setModelRotationLocked(Boolean flag) {
      if (this.modelRotationLocked != flag) {
         this.modelRotationLocked = flag;
         if (this.modelRotationLocked) {
            this.lockedYHeadRot = this.modeledEntity.getYHeadRot();
            this.lockedXHeadRot = this.modeledEntity.getXHeadRot();
            this.lockedYBodyRot = this.modeledEntity.getYBodyRot();
         }
      }
   }

   @Override
   public <T extends BoneBehavior> Optional<BehaviorManager<T>> getBehaviorManager(BoneBehaviorType<T> type) {
      BoneBehaviorType.BehaviorManagerProvider<T> provider = type.getBehaviorManagerProvider();
      if (provider == null) {
         return Optional.empty();
      }

      synchronized (this.behaviorManagers) {
         BehaviorManager<T> existing = (BehaviorManager<T>)this.behaviorManagers.get(type);
         return existing != null
            ? Optional.of(existing)
            : Optional.ofNullable((BehaviorManager<T>)this.behaviorManagers.computeIfAbsent(type, boneBehaviorType -> {
               BehaviorManager<T> manager = provider.create(this, type);
               if (manager == null) {
                  return null;
               }

               manager.onCreate();
               return manager;
            }));
      }
   }

   @Override
   public Optional<BehaviorRenderer> getBehaviorRenderer(BoneBehaviorType<?> type) {
      synchronized (this.behaviorRenderers) {
         BehaviorRenderer renderer = this.behaviorRenderers.get(type);
         if (renderer != null) {
            return Optional.of(renderer);
         }

         renderer = type.getRenderType().createBehaviorRenderer(this);
         if (renderer != null) {
            this.behaviorRenderers.put(type, renderer);
         }

         return Optional.ofNullable(renderer);
      }
   }

   private void forBones(Consumer<ModelBone> consumer) {
      for (ModelBone bone : this.roots.values()) {
         consumer.accept(bone);
      }
   }

   private void forManagers(Consumer<BehaviorManager<?>> consumer) {
      synchronized (this.behaviorManagers) {
         for (BehaviorManager<?> manager : this.behaviorManagers.values()) {
            consumer.accept(manager);
         }
      }
   }

   @Override
   public void save(SavedData data) {
      data.putString("blueprint", this.blueprint.getName());
      data.putFloat("render_scale", this.getScale().x());
      data.putFloat("hitbox_scale", this.getHitboxScale().x());
      data.putBoolean("can_hurt", this.canHurt());
      data.putInt("default_tint", this.defaultTint.asRGB());
      data.putInt("damage_tint", this.damageTint.asRGB());
      data.putBoolean("lock_pitch", this.lockPitch);
      data.putBoolean("lock_yaw", this.lockYaw);
      data.putBoolean("hitbox_visible", this.hitboxVisible);
      data.putBoolean("shadow_visible", this.shadowVisible);
      data.putBoolean("main_hitbox", this.mainHitbox);
      data.putBoolean("glowing", this.glowing);
      data.putInt("glow_color", this.glowColor);
      data.putInt("block_light", this.blockLight);
      data.putInt("sky_light", this.skyLight);
      HashSet<String> removed = new HashSet<>();
      SavedData boneDataMap = new SavedData();

      for (String boneId : this.blueprint.getFlatMap().keySet()) {
         ModelBone bone = this.bones.get(boneId);
         if (bone == null) {
            removed.add(boneId);
         } else {
            bone.save().ifPresent(boneData -> boneDataMap.putData(boneId, boneData));
         }
      }

      data.putList("removed", removed);
      data.putData("default_bones", boneDataMap);
      this.animationHandler.save().ifPresent(animationData -> data.putData("animation_handler", animationData));
      Map<BoneBehaviorType<?>, BehaviorManager<?>> managers = this.getBehaviorManagers();
      if (!managers.isEmpty()) {
         SavedData managerData = new SavedData();
         managers.forEach((boneBehaviorType, manager) -> manager.save().ifPresent(savedData -> managerData.putData(boneBehaviorType.getId(), savedData)));
         data.putData("managers", managerData);
      }

      Map<BoneBehaviorType<?>, BehaviorRenderer> renderers = this.getBehaviorRenderers();
      if (!renderers.isEmpty()) {
         SavedData rendererData = new SavedData();
         renderers.forEach((boneBehaviorType, renderer) -> renderer.save().ifPresent(savedData -> rendererData.putData(boneBehaviorType.getId(), savedData)));
         data.putData("renderers", rendererData);
      }
   }

   @Override
   public void load(SavedData data) {
      this.setScale(data.getFloat("render_scale").floatValue());
      this.setHitboxScale(data.getFloat("hitbox_scale").floatValue());
      this.setCanHurt(data.getBoolean("can_hurt"));
      this.setDefaultTint(Color.fromRGB(data.getInt("default_tint")));
      this.setDamageTint(Color.fromRGB(data.getInt("damage_tint")));
      this.setLockPitch(data.getBoolean("lock_pitch"));
      this.setLockYaw(data.getBoolean("lock_yaw"));
      this.setHitboxVisible(data.getBoolean("hitbox_visible"));
      this.setShadowVisible(data.getBoolean("shadow_visible"));
      this.setGlowing(data.getBoolean("glowing"));
      this.setGlowColor(data.getInt("glow_color"));
      this.setBlockLight(data.getInt("block_light", -1));
      this.setSkyLight(data.getInt("sky_light", -1));

      for (Object remove : data.getList("removed")) {
         this.removeBone((String)remove);
      }

      data.getData("default_bones").ifPresent(boneDataMap -> {
         for (String key : boneDataMap.keySet()) {
            this.getBone(key).ifPresent(modelBone -> boneDataMap.getData(key).ifPresent(modelBone::load));
         }
      });
      Optional<SavedData> managerData = data.getData("managers");
      managerData.ifPresent(
         savedData -> this.getBehaviorManagers().forEach((boneBehaviorType, manager) -> savedData.getData(boneBehaviorType.getId()).ifPresent(manager::load))
      );
      Optional<SavedData> renderersData = data.getData("renderers");
      renderersData.ifPresent(
         savedData -> this.getBehaviorRenderers()
            .forEach((boneBehaviorType, renderer) -> savedData.getData(boneBehaviorType.getId()).ifPresent(renderer::load))
      );
   }

   private IEntityData getData() {
      return this.modeledEntity.getBase().getData();
   }

   private record ResolvedBoneOrder(Map<String, ModelBone> source, String[] ids, ModelBone[] bones) {
      private static ActiveModelImpl.ResolvedBoneOrder resolve(Map<String, ModelBone> source, List<String> boneOrders) {
         String[] ids = boneOrders.toArray(new String[0]);
         ModelBone[] bones = new ModelBone[ids.length];

         for (int i = 0; i < ids.length; i++) {
            bones[i] = source.get(ids[i]);
         }

         return new ActiveModelImpl.ResolvedBoneOrder(source, ids, bones);
      }
   }
}
