package com.ticxo.modelengine.api.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.nms.NMSHandler;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.nms.network.NetworkHandler;
import com.ticxo.modelengine.api.utils.Profiler;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class ModelUpdaters {
   private final ExecutorService executors = Executors.newWorkStealingPool(
      ConfigProperty.MAX_ENGINE_THREADS.getIntOrIfNeg(() -> Runtime.getRuntime().availableProcessors())
   );
   private final Map<UUID, ModeledEntity> uuidLookup = Maps.newConcurrentMap();
   private final Map<Integer, ModeledEntity> idLookup = Maps.newConcurrentMap();
   private final RenderParsers parsers;
   private final NetworkHandler networkHandler;
   private final NMSHandler nmsHandler;
   private final Set<UUID> desyncMonitored = Sets.newConcurrentHashSet();
   private final AtomicBoolean ticking = new AtomicBoolean(false);
   private final AtomicLong removalEpoch = new AtomicLong();
   private final AtomicLong membershipEpoch = new AtomicLong();
   private static final ModeledEntity[] EMPTY_SNAPSHOT = new ModeledEntity[0];
   private ModeledEntity[] tickSnapshot = EMPTY_SNAPSHOT;
   private long tickSnapshotEpoch = -1L;
   @Getter
   private final Profiler profiler = new Profiler(200);
   private volatile boolean bundleEverything;
   private volatile boolean batching;

   public ModelUpdaters() {
      this.parsers = ModelEngineAPI.getNMSHandler().createParsers();
      this.networkHandler = ModelEngineAPI.getNetworkHandler();
      this.nmsHandler = ModelEngineAPI.getNMSHandler();
      ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(() -> this.bundleEverything = ConfigProperty.BUNDLE_EVERYTHING.getBoolean());
   }

   public void start() {
      DualTicker.queueRepeatingSyncTask(this::tick, 0, 0);
   }

   public void tick() {
      if (this.ticking.compareAndSet(false, true)) {
         this.profiler.startProfiling();
         long epoch = this.membershipEpoch.get();
         if (epoch != this.tickSnapshotEpoch) {
            this.tickSnapshot = this.uuidLookup.values().toArray(EMPTY_SNAPSHOT);
            this.tickSnapshotEpoch = epoch;
         }

         ModeledEntity[] snapshot = this.tickSnapshot;
         if (snapshot.length == 0) {
            this.profiler.stopProfiling();
            this.ticking.set(false);
         } else {
            this.batching = this.bundleEverything;
            if (this.batching) {
               this.networkHandler.startBatch();
            }

            AtomicInteger remaining = new AtomicInteger(1);

            try {
               for (ModeledEntity entity : snapshot) {
                  BaseEntity<?> base = entity.getBase();
                  remaining.incrementAndGet();

                  try {
                     this.executors.execute(() -> {
                        try {
                           entity.runTickTasks(ModeledEntity.Phase.PRE_DATA_SYNC);
                           base.getData().asyncUpdate();
                           entity.runTickTasks(ModeledEntity.Phase.PRE_MODEL_TICK);
                           if (!entity.tick()) {
                              this.forceRemoveModeledEntity(entity);
                           } else {
                              entity.runTickTasks(ModeledEntity.Phase.PRE_MODEL_RENDER);
                              this.forRenderers(entity, modelRenderer -> modelRenderer.sendToClient(this.parsers));
                              entity.runTickTasks(ModeledEntity.Phase.POST_MODEL_RENDER);
                              if (entity.shouldBeSaved() && entity.getTick() % 200 == 0) {
                                 entity.updateSaveCache();
                              }
                           }
                        } catch (Throwable ex) {
                           ex.printStackTrace();
                        } finally {
                           if (remaining.decrementAndGet() == 0) {
                              this.finishTick();
                           }
                        }
                     });
                  } catch (Throwable submitFail) {
                     submitFail.printStackTrace();
                     remaining.decrementAndGet();
                  }
               }
            } catch (Throwable e) {
               e.printStackTrace();
            } finally {
               if (remaining.decrementAndGet() == 0) {
                  this.finishTick();
               }
            }
         }

         try {
            for (UUID uuid : this.desyncMonitored) {
               this.networkHandler.ping(uuid);
            }
         } catch (Throwable e) {
            e.printStackTrace();
         }
      }
   }

   private void finishTick() {
      try {
         this.nmsHandler.sendPivotOverrides();
      } catch (Throwable e) {
         e.printStackTrace();
      } finally {
         if (this.batching) {
            this.batching = false;

            try {
               this.networkHandler.endBatch();
            } catch (Throwable e) {
               e.printStackTrace();
            }
         }

         this.profiler.stopProfiling();
         this.ticking.set(false);
      }
   }

   public void startDesyncMonitor(UUID uuid) {
      this.desyncMonitored.add(uuid);
   }

   public void stopDesyncMonitor(UUID uuid) {
      this.desyncMonitored.remove(uuid);
   }

   private void forRenderers(ModeledEntity modeledEntity, Consumer<ModelRenderer> rendererConsumer) {
      IEntityData data = modeledEntity.getBase().getData();
      Map<String, ActiveModel> models = modeledEntity.getModels();
      if (!models.isEmpty()) {
         for (ActiveModel model : models.values()) {
            if (!model.getModelRenderer().isInitialized()) {
               return;
            }
         }

         for (ActiveModel model : models.values()) {
            rendererConsumer.accept(model.getModelRenderer());
         }

         data.cleanup();
      }
   }

   public void end() {
      this.executors.shutdown();

      try {
         if (!this.executors.awaitTermination(10L, TimeUnit.SECONDS)) {
            this.executors.shutdownNow();
         }
      } catch (InterruptedException e) {
         this.executors.shutdownNow();
      }
   }

   public Optional<ModeledEntity> registerModeledEntity(BaseEntity<?> base, ModeledEntity modeledEntity) {
      this.idLookup.put(base.getEntityId(), modeledEntity);
      ModeledEntity previous = this.uuidLookup.put(base.getUUID(), modeledEntity);
      this.membershipEpoch.incrementAndGet();
      return Optional.ofNullable(previous);
   }

   public ModeledEntity getModeledEntity(int id) {
      return this.idLookup.get(id);
   }

   public void rekeyEntityId(UUID uuid, int oldId, int newId) {
      if (oldId != newId) {
         ModeledEntity model = this.uuidLookup.get(uuid);
         if (model != null) {
            this.idLookup.remove(oldId, model);
            this.idLookup.put(newId, model);
         }
      }
   }

   public ModeledEntity getModeledEntity(UUID uuid) {
      return uuid == null ? null : this.uuidLookup.get(uuid);
   }

   public ModeledEntity removeModeledEntity(int id) {
      ModeledEntity model = this.idLookup.get(id);
      if (model != null) {
         model.markRemoved();
      }

      return model;
   }

   public ModeledEntity removeModeledEntity(UUID uuid) {
      if (uuid == null) {
         return null;
      }

      ModeledEntity model = this.uuidLookup.get(uuid);
      if (model != null) {
         model.markRemoved();
      }

      return model;
   }

   public long getRemovalEpoch() {
      return this.removalEpoch.get();
   }

   public void forceRemoveModeledEntity(ModeledEntity model) {
      this.uuidLookup.remove(model.getBase().getUUID(), model);
      this.idLookup.remove(model.getBase().getEntityId(), model);
      this.removalEpoch.incrementAndGet();
      this.membershipEpoch.incrementAndGet();
      model.getBase().setForcedAlive(false);
      if (ServerInfo.IS_FOLIA) {
         DualTicker.queueSyncTask(model::destroy);
      } else {
         model.destroy();
      }
   }

   public void saveAllModels() {
      for (ModeledEntity model : this.uuidLookup.values()) {
         if (model.shouldBeSaved() && model.getBase().getOriginal() instanceof Entity entity && !(entity instanceof Player)) {
            String cachedJson = model.getCachedSaveJson();
            if (cachedJson != null) {
               entity.getPersistentDataContainer().set(SavedData.DATA_KEY, PersistentDataType.STRING, cachedJson);
            } else {
               model.save().ifPresent(data -> entity.getPersistentDataContainer().set(SavedData.DATA_KEY, PersistentDataType.STRING, data.toString()));
            }
         }
      }
   }
}
