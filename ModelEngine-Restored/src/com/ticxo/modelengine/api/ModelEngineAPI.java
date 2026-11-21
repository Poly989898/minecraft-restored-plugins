package com.ticxo.modelengine.api;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.ticxo.modelengine.api.animation.AnimationHandlerRegistry;
import com.ticxo.modelengine.api.animation.AnimationPropertyRegistry;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeTypeRegistry;
import com.ticxo.modelengine.api.animation.keyframe.data.KeyframeReaderRegistry;
import com.ticxo.modelengine.api.animation.script.ScriptReaderRegistry;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.entity.BukkitPlayer;
import com.ticxo.modelengine.api.entity.EntityDataTrackers;
import com.ticxo.modelengine.api.generator.ModelGenerator;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.generator.skin.SkinGeneratorService;
import com.ticxo.modelengine.api.generator.skin.SkinSplitter;
import com.ticxo.modelengine.api.interaction.InteractionTracker;
import com.ticxo.modelengine.api.menu.ScreenManager;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModelRegistry;
import com.ticxo.modelengine.api.model.ModelUpdaters;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverrideRegistry;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorRegistry;
import com.ticxo.modelengine.api.model.limb.UserLimbRegistry;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.mount.MountControllerTypeRegistry;
import com.ticxo.modelengine.api.mount.MountPairManager;
import com.ticxo.modelengine.api.nms.NMSHandler;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.network.CancelType;
import com.ticxo.modelengine.api.nms.network.NetworkHandler;
import com.ticxo.modelengine.api.nms.ui.AnvilHandler;
import com.ticxo.modelengine.api.utils.CompatibilityManager;
import com.ticxo.modelengine.api.utils.config.ConfigCache;
import com.ticxo.modelengine.api.utils.config.ConfigManager;
import com.ticxo.modelengine.api.utils.scheduling.PlatformScheduler;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import com.ticxo.modelengine.api.vfx.VFX;
import com.ticxo.modelengine.api.vfx.VFXUpdater;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Generated;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Entry point of the ModelEngine API.
 *
 * Typical use: create an {@link ActiveModel} from a blueprint id, create (or look up) the {@link ModeledEntity} for
 * an entity, and add the model to it:
 *
 * <pre>{@code
 * ModeledEntity modeled = ModelEngineAPI.getOrCreateModeledEntity(entity);
 * ActiveModel model = ModelEngineAPI.createActiveModel("my_model");
 * modeled.addModel(model, true);
 * }</pre>
 *
 * Registries and managers are reached through the instance returned by {@link #getAPI()}; most helpers are static.
 */
public abstract class ModelEngineAPI extends JavaPlugin {
   protected static ModelEngineAPI API;
   protected final Set<Integer> renderCanceled = Sets.newConcurrentHashSet();
   protected ConfigManager configManager;
   protected ConfigCache configCache;
   protected Gson gson;
   protected PlatformScheduler scheduler;
   protected ModelRegistry modelRegistry;
   protected ModelGenerator modelGenerator;
   protected KeyframeTypeRegistry keyframeTypeRegistry;
   protected KeyframeReaderRegistry keyframeReaderRegistry;
   protected ScriptReaderRegistry scriptReaderRegistry;
   protected BoneBehaviorRegistry boneBehaviorRegistry;
   protected AnimationHandlerRegistry animationHandlerRegistry;
   protected AnimationPropertyRegistry animationPropertyRegistry;
   protected DualTicker ticker;
   protected ModelUpdaters modelUpdaters;
   protected VFXUpdater vfxUpdater;
   protected EntityDataTrackers dataTrackers;
   protected InteractionTracker interactionTracker;
   protected MountPairManager mountPairManager;
   protected MountControllerTypeRegistry mountControllerTypeRegistry;
   protected NMSHandler nmsHandler;
   protected CompatibilityManager compatibilityManager;
   protected ScreenManager screenManager;
   protected SkinSplitter skinSplitter;
   protected SkinGeneratorService skinGenerator;
   protected UserLimbRegistry userLimbRegistry;
   protected PivotOverrideRegistry pivotOverrideRegistry;

   public static ConfigCache getConfigCache() {
      return getAPI().configCache;
   }

   public static NMSHandler getNMSHandler() {
      return getAPI().nmsHandler;
   }

   public static EntityHandler getEntityHandler() {
      return getNMSHandler().getEntityHandler();
   }

   public static AnvilHandler getAnvilHandler() {
      return getNMSHandler().getAnvilHandler();
   }

   public static NetworkHandler getNetworkHandler() {
      return getNMSHandler().getNetworkHandler();
   }

   public static InteractionTracker getInteractionTracker() {
      return getAPI().interactionTracker;
   }

   public static MountPairManager getMountPairManager() {
      return getAPI().mountPairManager;
   }

   public static MountControllerTypeRegistry getMountControllerTypeRegistry() {
      return getAPI().mountControllerTypeRegistry;
   }

   public static AnimationHandlerRegistry getAnimationHandlerRegistry() {
      return getAPI().animationHandlerRegistry;
   }

   public static AnimationPropertyRegistry getAnimationPropertyRegistry() {
      return getAPI().animationPropertyRegistry;
   }

   public static UserLimbRegistry getUserLimbRegistry() {
      return getAPI().userLimbRegistry;
   }

   public static SkinGeneratorService getSkinGeneratorService() {
      return getAPI().skinGenerator;
   }

   public static PivotOverrideRegistry getPivotOverrideRegistry() {
      return getAPI().pivotOverrideRegistry;
   }

   public VFXUpdater getVFXUpdater() {
      return this.vfxUpdater;
   }

   public static ModeledEntity createModeledEntity(Entity base) {
      return createModeledEntity(base, null);
   }

   /**
    * Creates a modeled entity for a Bukkit entity (players get a player-specific base).
    *
    * @param consumer called with the new modeled entity before it starts ticking; may be null
    */
   public static ModeledEntity createModeledEntity(Entity base, Consumer<ModeledEntity> consumer) {
      return createModeledEntity(base instanceof Player player ? new BukkitPlayer(player) : new BukkitEntity(base), consumer);
   }

   public static ModeledEntity createModeledEntity(BaseEntity<?> base) {
      return createModeledEntity(base, null);
   }

   /** Creates a modeled entity for any base entity, including dummies and other non-Bukkit bases. The consumer may be null. */
   public static ModeledEntity createModeledEntity(BaseEntity<?> base, Consumer<ModeledEntity> consumer) {
      return getAPI().createModeledEntityImpl(base, consumer);
   }

   /** The modeled entity registered for this entity, or null. */
   public static ModeledEntity getModeledEntity(Entity entity) {
      return getModeledEntity(entity.getUniqueId());
   }

   /** The modeled entity registered for this entity id, or null. */
   public static ModeledEntity getModeledEntity(int id) {
      return getAPI().getModelUpdaters().getModeledEntity(id);
   }

   /** The modeled entity registered for this entity UUID, or null. */
   public static ModeledEntity getModeledEntity(UUID uuid) {
      return getAPI().getModelUpdaters().getModeledEntity(uuid);
   }

   /** Returns the entity's modeled entity, creating one if it has none. */
   public static ModeledEntity getOrCreateModeledEntity(Entity base) {
      ModeledEntity model = getModeledEntity(base);
      return model != null ? model : createModeledEntity(base, null);
   }

   public static ModeledEntity getOrCreateModeledEntity(Entity base, Consumer<ModeledEntity> consumer) {
      ModeledEntity model = getModeledEntity(base);
      return model != null ? model : createModeledEntity(base instanceof Player player ? new BukkitPlayer(player) : new BukkitEntity(base), consumer);
   }

   /** Returns the modeled entity registered for the UUID; if there is none, creates one on the base entity the supplier returns (the supplier is only called in that case). */
   public static ModeledEntity getOrCreateModeledEntity(UUID uuid, Supplier<BaseEntity<?>> baseEntitySupplier) {
      ModeledEntity model = getModeledEntity(uuid);
      return model != null ? model : createModeledEntity(baseEntitySupplier.get(), null);
   }

   public static ModeledEntity getOrCreateModeledEntity(UUID uuid, Supplier<BaseEntity<?>> baseEntitySupplier, Consumer<ModeledEntity> consumer) {
      ModeledEntity model = getModeledEntity(uuid);
      return model != null ? model : getAPI().createModeledEntityImpl(baseEntitySupplier.get(), consumer);
   }

   /** Marks the entity's modeled entity as removed (it is cleaned up on its next tick) and returns it, or null if it had none. */
   public static ModeledEntity removeModeledEntity(Entity entity) {
      return removeModeledEntity(entity.getUniqueId());
   }

   public static ModeledEntity removeModeledEntity(int id) {
      return getAPI().getModelUpdaters().removeModeledEntity(id);
   }

   public static ModeledEntity removeModeledEntity(UUID uuid) {
      return getAPI().getModelUpdaters().removeModeledEntity(uuid);
   }

   /** Whether a modeled entity is registered for this UUID. */
   public static boolean isModeledEntity(UUID uuid) {
      return getModeledEntity(uuid) != null;
   }

   /** Creates a model from the blueprint with this id. Throws {@code RuntimeException} if no such blueprint is loaded. */
   public static ActiveModel createActiveModel(String modelId) {
      return createActiveModel(modelId, null, null);
   }

   public static ActiveModel createActiveModel(
      String modelId, Function<ActiveModel, ModelRenderer> rendererSupplier, Function<ActiveModel, AnimationHandler> handlerSupplier
   ) {
      ModelBlueprint blueprint = getAPI().getModelRegistry().get(modelId);
      if (blueprint == null) {
         throw new RuntimeException("Error while creating ActiveModel. Unknown model: " + modelId);
      } else {
         return createActiveModel(blueprint, rendererSupplier, handlerSupplier);
      }
   }

   /** Creates a model from a blueprint with the default renderer and animation handler. */
   public static ActiveModel createActiveModel(ModelBlueprint blueprint) {
      return createActiveModel(blueprint, null, null);
   }

   public static ActiveModel createActiveModel(
      ModelBlueprint blueprint, Function<ActiveModel, ModelRenderer> rendererSupplier, Function<ActiveModel, AnimationHandler> handlerSupplier
   ) {
      return getAPI().createActiveModelImpl(blueprint, rendererSupplier, handlerSupplier);
   }

   /** Creates a priority based animation handler for the model. */
   public static AnimationHandler createPriorityHandler(ActiveModel activeModel) {
      return getAPI().getPriorityHandler(activeModel);
   }

   /** Creates a state machine animation handler for the model. */
   public static AnimationHandler createStateMachineHandler(ActiveModel activeModel) {
      return getAPI().getStateMachineHandler(activeModel);
   }

   public static VFX createVFX(Entity base) {
      return createVFX(base, null, null);
   }

   public static VFX createVFX(Entity base, Consumer<VFX> consumer) {
      return createVFX(base, consumer, null);
   }

   /** Creates a VFX (a visual effect drawn by a {@link VFXRenderer}) on a Bukkit entity. Consumer and renderer supplier may be null. */
   public static VFX createVFX(Entity base, Consumer<VFX> consumer, Function<VFX, VFXRenderer> rendererSupplier) {
      return createVFX(base instanceof Player player ? new BukkitPlayer(player) : new BukkitEntity(base), consumer, rendererSupplier);
   }

   public static VFX createVFX(BaseEntity<?> base) {
      return createVFX(base, null, null);
   }

   public static VFX createVFX(BaseEntity<?> base, Consumer<VFX> consumer) {
      return createVFX(base, consumer, null);
   }

   public static VFX createVFX(BaseEntity<?> base, Consumer<VFX> consumer, Function<VFX, VFXRenderer> rendererSupplier) {
      return getAPI().createVFXImpl(base, rendererSupplier, consumer);
   }

   public static VFX getVFX(Entity entity) {
      return getVFX(entity.getUniqueId());
   }

   public static VFX getVFX(int id) {
      return getAPI().getVFXUpdater().getVFX(id);
   }

   /** The VFX registered for this UUID, or null. */
   public static VFX getVFX(UUID uuid) {
      return getAPI().getVFXUpdater().getVFX(uuid);
   }

   public static boolean isVFX(UUID uuid) {
      return getVFX(uuid) != null;
   }

   /** The loaded blueprint with this id, or null. */
   public static ModelBlueprint getBlueprint(String id) {
      return getAPI().getModelRegistry().get(id);
   }

   /** Marks an entity id so ModelEngine hides it from players (used for the helper entities it spawns itself). */
   public static void setRenderCanceled(int id, boolean flag) {
      if (flag) {
         getAPI().renderCanceled.add(id);
      } else {
         getAPI().renderCanceled.remove(id);
      }
   }

   public static boolean isRenderCanceled(int id) {
      return getAPI().renderCanceled.contains(id);
   }

   /**
    * Decides how the entity with this id is shown to the player: players always see themselves; an entity whose
    * base is hidden gives {@code INVIS} for its pivot override and {@code HIDE} otherwise; render-cancelled ids are hidden.
    */
   public static CancelType shouldShow(Player player, int id) {
      if (player.getEntityId() == id) {
         return CancelType.SHOW;
      } else {
         ModeledEntity entity = getAPI().modelUpdaters.getModeledEntity(id);
         if (entity != null && !entity.isBaseEntityVisible()) {
            return entity.isPivotOverride(id) ? CancelType.INVIS : CancelType.HIDE;
         } else {
            return isRenderCanceled(id) ? CancelType.HIDE : CancelType.SHOW;
         }
      }
   }

   /** Calls a Bukkit event. */
   public static void callEvent(Event event) {
      Bukkit.getPluginManager().callEvent(event);
   }

   public static int getPlayerProtocolVersion(UUID uuid) {
      return getAPI().playerProtocolVersion(uuid);
   }

   public abstract ModeledEntity createModeledEntityImpl(BaseEntity<?> base, Consumer<ModeledEntity> consumer);

   public abstract ActiveModel createActiveModelImpl(
      ModelBlueprint blueprint, Function<ActiveModel, ModelRenderer> rendererSupplier, Function<ActiveModel, AnimationHandler> handlerSupplier
   );

   public abstract VFX createVFXImpl(BaseEntity<?> base, Function<VFX, VFXRenderer> rendererSupplier, Consumer<VFX> consumer);

   public abstract UUID getDisguiseRelayOrDefault(UUID uuid);

   public abstract AnimationHandler getPriorityHandler(ActiveModel activeModel);

   public abstract AnimationHandler getStateMachineHandler(ActiveModel activeModel);

   public abstract int playerProtocolVersion(UUID uuid);

   @Generated
   public Set<Integer> getRenderCanceled() {
      return this.renderCanceled;
   }

   @Generated
   public ConfigManager getConfigManager() {
      return this.configManager;
   }

   @Generated
   public Gson getGson() {
      return this.gson;
   }

   @Generated
   public PlatformScheduler getScheduler() {
      return this.scheduler;
   }

   @Generated
   public ModelRegistry getModelRegistry() {
      return this.modelRegistry;
   }

   @Generated
   public ModelGenerator getModelGenerator() {
      return this.modelGenerator;
   }

   @Generated
   public KeyframeTypeRegistry getKeyframeTypeRegistry() {
      return this.keyframeTypeRegistry;
   }

   @Generated
   public KeyframeReaderRegistry getKeyframeReaderRegistry() {
      return this.keyframeReaderRegistry;
   }

   @Generated
   public ScriptReaderRegistry getScriptReaderRegistry() {
      return this.scriptReaderRegistry;
   }

   @Generated
   public BoneBehaviorRegistry getBoneBehaviorRegistry() {
      return this.boneBehaviorRegistry;
   }

   @Generated
   public DualTicker getTicker() {
      return this.ticker;
   }

   @Generated
   public ModelUpdaters getModelUpdaters() {
      return this.modelUpdaters;
   }

   @Generated
   public EntityDataTrackers getDataTrackers() {
      return this.dataTrackers;
   }

   @Generated
   public CompatibilityManager getCompatibilityManager() {
      return this.compatibilityManager;
   }

   @Generated
   public ScreenManager getScreenManager() {
      return this.screenManager;
   }

   @Generated
   public SkinSplitter getSkinSplitter() {
      return this.skinSplitter;
   }

   @Generated
   public SkinGeneratorService getSkinGenerator() {
      return this.skinGenerator;
   }

   @Generated
   public static ModelEngineAPI getAPI() {
      return API;
   }
}
