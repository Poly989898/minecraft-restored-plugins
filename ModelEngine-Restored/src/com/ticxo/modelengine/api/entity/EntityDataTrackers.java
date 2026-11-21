package com.ticxo.modelengine.api.entity;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.scheduling.PlatformScheduler;
import com.ticxo.modelengine.api.utils.ticker.AbstractLoadBalancer;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import com.ticxo.modelengine.api.utils.ticker.LoadBalancer;
import com.ticxo.modelengine.api.utils.ticker.PseudoThread;
import com.ticxo.modelengine.api.utils.ticker.Task;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityDataTrackers extends AbstractLoadBalancer<UUID, EntityDataTrackers.Tracker> {
   private final Map<EntityDataTrackers.Tracker, PseudoThread> threads = new ConcurrentHashMap<>();
   private final JavaPlugin plugin;
   private final PlatformScheduler scheduler;
   private final int maximumThreads;
   private int trackerCount = 0;
   private volatile boolean started;

   public EntityDataTrackers(JavaPlugin plugin, PlatformScheduler scheduler) {
      super(ConfigProperty.CULLING_THREADS.getInt());
      this.plugin = plugin;
      this.scheduler = scheduler;
      this.maximumThreads = Math.max(ConfigProperty.MAX_CULLING_THREADS.getInt(), this.available.size());
      this.available.forEach(this::setup);
   }

   public EntityDataTrackers.Tracker supply() {
      return new EntityDataTrackers.Tracker();
   }

   public void start() {
      this.started = true;

      for (Entry<EntityDataTrackers.Tracker, PseudoThread> entry : this.threads.entrySet()) {
         entry.getValue().start();
      }
   }

   public void end() {
      for (Entry<EntityDataTrackers.Tracker, PseudoThread> entry : this.threads.entrySet()) {
         entry.getValue().end();
      }
   }

   private void setup(EntityDataTrackers.Tracker tracker) {
      tracker.setId("data_tracker_" + this.trackerCount);
      PseudoThread thread = new PseudoThread(tracker.getId(), this.scheduler, this.plugin, true, 0, 0, true, false);
      thread.registerOverloadCallback(skipped -> {
         TLogger.debug(tracker.getId() + " is overloaded with " + tracker.getLoad() + " targets.");
         if (this.available.size() < this.maximumThreads) {
            this.growAndBalance(tracker, skipped);
         }
      });
      thread.queueTask(new Task(task -> tracker.asyncFetchEntityData(), 0, 0, true));
      DualTicker.queueRepeatingSyncTask(tracker::fetchEntityData, 0, 0);
      this.threads.put(tracker, thread);
      if (this.started) {
         thread.start();
      }

      this.trackerCount++;
   }

   private void growAndBalance(EntityDataTrackers.Tracker request, int skipped) {
      TLogger.debug(request.id + " has skipped " + skipped + " ticks. Requested a new server.");
      EntityDataTrackers.Tracker handle = this.supply();
      int moved = request.dataTrackers.size() / 2;

      for (Entry<UUID, IEntityData> entry : request.dataTrackers.entrySet()) {
         if (moved <= 0) {
            break;
         }

         handle.putEntityData(entry.getKey(), entry.getValue());
         this.reference.put(entry.getKey(), handle);
         moved--;
      }

      handle.dataTrackers.keySet().forEach(uuid -> {
         request.dataTrackers.remove(uuid);
         request.unverified.remove(uuid);
      });
      this.available.add(handle);
      this.setup(handle);
      TLogger.debug("- Created " + handle.id + ".");
   }

   public class Tracker implements LoadBalancer.Server {
      @Getter
      private final Map<UUID, IEntityData> dataTrackers = Maps.newConcurrentMap();
      @Getter @Setter
      private String id = "Unknown";
      private volatile int lastSize = 0;
      @Getter
      private long timings;
      private long verifiedEpoch = -1L;
      private final Set<UUID> unverified = ConcurrentHashMap.newKeySet();

      public void fetchEntityData() {
         long epoch = ModelEngineAPI.getAPI().getModelUpdaters().getRemovalEpoch() + ModelEngineAPI.getAPI().getVFXUpdater().getRemovalEpoch();
         boolean recheckAll = epoch != this.verifiedEpoch;
         boolean hasUnverified = !this.unverified.isEmpty();

         for (Entry<UUID, IEntityData> entry : this.dataTrackers.entrySet()) {
            IEntityData data = entry.getValue();
            boolean checkRegistry = (hasUnverified && this.unverified.remove(entry.getKey())) | recheckAll;
            if (data.isDataValid() && (!checkRegistry || ModelEngineAPI.isModeledEntity(entry.getKey()) || ModelEngineAPI.isVFX(entry.getKey()))) {
               data.syncUpdate();
            } else {
               data.destroy();
               this.dataTrackers.remove(entry.getKey());
               this.unverified.remove(entry.getKey());
               EntityDataTrackers.this.unregister(entry.getKey());
            }
         }

         this.verifiedEpoch = epoch;
      }

      public void asyncFetchEntityData() {
         long time = System.currentTimeMillis();

         for (IEntityData data : this.dataTrackers.values()) {
            data.cullUpdate();
         }

         this.timings = System.currentTimeMillis() - time;
         if (this.lastSize != this.dataTrackers.size()) {
            this.lastSize = this.dataTrackers.size();
            TLogger.debug(this.id + ": " + this.lastSize + " - " + this.timings);
         }
      }

      public void putEntityData(UUID uuid, IEntityData dataTracker) {
         this.unverified.add(uuid);
         this.dataTrackers.put(uuid, dataTracker);
      }

      public IEntityData getEntityData(UUID uuid) {
         return this.dataTrackers.get(uuid);
      }

      public IEntityData removeEntityData(UUID uuid) {
         this.unverified.remove(uuid);
         EntityDataTrackers.this.unregister(uuid);
         return this.dataTrackers.remove(uuid);
      }

      @Override
      public int getLoad() {
         return this.lastSize;
      }
   }
}
