package com.ticxo.modelengine.api.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.rootmotion.RootMotionHandler;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.lod.AnimationLODHandler;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.behavior.GlobalBehaviorData;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.utils.callback.Callback;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.meta.IMetaHolder;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A set of {@link ActiveModel}s bound to one {@link BaseEntity}: owns their lifecycle, ticking,
 * rotation smoothing, tick tasks and persistence.
 */
public interface ModeledEntity extends DataIO, IMetaHolder {
   /** The entity this model set is attached to (a Bukkit entity, a dummy or a custom base). */
   BaseEntity<?> getBase();

   /** Per-player level-of-detail handler: scales how often animation updates are sent depending on distance (see the animation LOD config options). */
   AnimationLODHandler getAnimationLodHandler();

   /** Number of ticks this modeled entity has been ticked. */
   int getTick();

   /**
    * Advances rotation smoothing and ticks every {@link ActiveModel}.
    *
    * @return {@code false} when this modeled entity should be removed (it was marked removed, its base entity
    *    is gone, or the base died and all death animations have finished); {@code true} to keep it ticking
    */
   boolean tick();

   /** Destroys every active model, clears the model map and runs the destroy callback. Not reversible. */
   void destroy();

   /** Whether post-init setup has finished; tasks queued by {@link #queuePostInitTask(Runnable)} run when it becomes true. */
   boolean isInitialized();

   /** Whether {@link #destroy()} has been called. */
   boolean isDestroyed();

   /** Flags this modeled entity for removal; the next {@link #tick()} returns {@code false}. Undone by {@link #restore()}. */
   void markRemoved();

   /** Clears the removed flag set by {@link #markRemoved()}. */
   void restore();

   /** Runs the task now if already initialized, otherwise once initialization finishes. */
   void queuePostInitTask(Runnable runnable);

   /** Whether the vanilla base entity is rendered next to its models. */
   boolean isBaseEntityVisible();

   /** Shows or hides the vanilla base entity (hide it to show only the model). */
   void setBaseEntityVisible(boolean baseEntityVisible);

   /** Starts the 10 tick hurt window reported by {@link #getHurtTick()}. */
   void markHurt();

   /** Remaining ticks of the hurt window started by {@link #markHurt()}; 0 when not hurt. */
   int getHurtTick();

   /** Whether this modeled entity is persisted with its base entity. */
   boolean shouldBeSaved();

   /** Sets whether this modeled entity is persisted with its base entity. */
   void setSaved(boolean saved);

   @Nullable
   String getCachedSaveJson();

   /** Re-serializes the current state into the cache returned by {@link #getCachedSaveJson()} (null if nothing is saved). */
   void updateSaveCache();

   /** Whether rotations follow the base entity's body rotation controller or are controlled manually. */
   boolean isModelRotationLocked();

   /** When locked, the body rotation controller is not read each tick; rotations are only changed by the setters. */
   void setModelRotationLocked(boolean isModelRotationLocked);

   boolean isGlowing();

   int getGlowColor();

   float getYHeadRot();

   float getXHeadRot();

   float getYBodyRot();

   /** Sets the target head yaw; the displayed value eases towards it over a few ticks. */
   void setYHeadRot(float rotation);

   /** Sets the target head pitch; the displayed value eases towards it over a few ticks. */
   void setXHeadRot(float rotation);

   /** Sets the target body yaw; the displayed value eases towards it over a few ticks. */
   void setYBodyRot(float rotation);

   /** Sets head yaw and skips the easing. */
   void setYHeadRotImmediately(float rotation);

   /** Sets head pitch and skips the easing. */
   void setXHeadRotImmediately(float rotation);

   /** Sets body yaw and skips the easing. */
   void setYBodyRotImmediately(float rotation);

   /** Sets the remaining easing ticks (-1 makes the next tick snap to the target rotations). */
   void setRotationTick(int rotationTick);

   /**
    * Attaches a model, replacing any model with the same blueprint name.
    *
    * @param model the model to attach; it must not belong to another modeled entity
    * @param overrideHitbox if true the model's main hitbox becomes this entity's hitbox
    * @return the model that was replaced, or empty if none was; also empty when an {@code AddModelEvent} listener cancelled the add
    */
   Optional<ActiveModel> addModel(@NotNull ActiveModel model, boolean overrideHitbox);

   /**
    * Detaches and returns the model with the given blueprint name (a {@code RemoveModelEvent} listener may cancel it).
    *
    * @param id blueprint name of the model
    * @return the removed model, or empty if there was none
    */
   Optional<ActiveModel> removeModel(String id);

   /** Returns the model with the given blueprint name, if attached. */
   Optional<ActiveModel> getModel(@Nullable String id);

   /** All attached models keyed by blueprint name. */
   Map<String, ActiveModel> getModels();

   <T extends BoneBehavior> GlobalBehaviorData getOrCreateGlobalBehaviorData(BoneBehaviorType<T> type, Supplier<GlobalBehaviorData> supplier);

   <T extends BoneBehavior> GlobalBehaviorData getGlobalBehaviorData(BoneBehaviorType<T> type);

   <T extends BoneBehavior> GlobalBehaviorData removeGlobalBehaviorData(BoneBehaviorType<T> type);

   Map<BoneBehaviorType<?>, GlobalBehaviorData> getAllGlobalBehaviorData(BoneBehaviorType<?> type);

   <T extends GlobalBehaviorData & MountData> T getMountData();

   RootMotionHandler getRootMotionHandler();

   /** Whether any attached model uses the pivot override with the given entity id. */
   boolean isPivotOverride(int id);

   /**
    * Registers a task for the given phase of the tick.
    *
    * @param function runs every tick in that phase; return {@code true} to unregister itself
    * @return an id for {@link #removeTickTask(UUID)}
    */
   UUID registerTickTask(ModeledEntity.Phase phase, Function<ModeledEntity, Boolean> function);

   /** Registers a task that runs every tick in the given phase until removed with {@link #removeTickTask(UUID)}. */
   UUID registerTickTask(ModeledEntity.Phase phase, Consumer<ModeledEntity> consumer);

   /** Unregisters a task registered with {@code registerTickTask}. */
   void removeTickTask(UUID uuid);

   /** Runs the tasks of one phase; tasks that return true are removed afterwards. */
   void runTickTasks(ModeledEntity.Phase phase);

   /** Callbacks run when this modeled entity is destroyed. */
   Callback<Runnable> getDestroyCallback();

   default void registerSelf() {
      ModelEngineAPI.getAPI().getModelUpdaters().registerModeledEntity(this.getBase(), this).ifPresent(ModeledEntity::destroy);
      this.getBase().registerData();
   }

   /** Points in the per-tick pipeline at which tick tasks can run. */
   enum Phase {
      PRE_DATA_SYNC,
      PRE_MODEL_TICK,
      PRE_MODEL_RENDER,
      POST_MODEL_RENDER;
   }
}
