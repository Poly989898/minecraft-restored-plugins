package com.ticxo.modelengine.api.entity.data;

import com.google.common.collect.ImmutableSet;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.Dummy;
import com.ticxo.modelengine.api.entity.cull.BasicCulling;
import com.ticxo.modelengine.api.entity.cull.ModelCuller;
import com.ticxo.modelengine.api.nms.impl.DummyTrackedEntity;
import com.ticxo.modelengine.api.utils.callback.Callback;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@Setter
public class DummyEntityData<T> extends AbstractEntityData {
   protected final Dummy<T> dummy;
   @Getter
   protected final DummyTrackedEntity tracked;
   protected final ModelCuller culler;
   protected final Set<UUID> syncTracking = new HashSet<>();
   private final TrackingMap trackingMap = new TrackingMap();
   protected final Map<UUID, CullType> asyncTracking = this.trackingMap;
   protected final Queue<UUID> startTrackingQueue = new ConcurrentLinkedQueue<>();
   protected final Set<UUID> startTracking = new HashSet<>();
   protected final Queue<UUID> stopTrackingQueue = new ConcurrentLinkedQueue<>();
   protected final Set<UUID> stopTracking = new HashSet<>();
   @Getter
   protected final Callback<Runnable> syncUpdateCallback = new Callback<>(runnables -> () -> runnables.forEach(Runnable::run));
   @Getter
   protected final Callback<Runnable> asyncUpdateCallback = new Callback<>(runnables -> () -> runnables.forEach(Runnable::run));
   @Getter
   protected Location location;
   @Getter
   protected boolean baseGlowing;

   public DummyEntityData(Dummy<T> dummy) {
      this(dummy, new BasicCulling());
   }

   public DummyEntityData(Dummy<T> dummy, ModelCuller culler) {
      this.dummy = dummy;
      this.culler = culler;
      culler.setData(this);
      this.tracked = new DummyTrackedEntity();
      this.syncUpdate();
      this.asyncUpdate();
   }

   @Override
   public void asyncUpdate() {
      while (!this.startTrackingQueue.isEmpty()) {
         UUID player = this.startTrackingQueue.poll();
         this.startTracking.add(player);
         this.asyncTracking.put(player, CullType.NO_CULL);
      }

      while (!this.stopTrackingQueue.isEmpty()) {
         UUID player = this.stopTrackingQueue.poll();
         this.stopTracking.add(player);
         if (this.asyncTracking.get(player) != CullType.CULLED) {
            this.asyncTracking.remove(player);
         }
      }

      this.asyncUpdateCallback.invoker().run();
   }

   @Override
   public void syncUpdate() {
      if (this.dummy.isDetectingPlayers() && this.location != null) {
         this.tracked.detectPlayers(this.location);
      }

      Set<UUID> updatedTracking = this.tracked.getTrackedPlayer(player2 -> this.asyncTracking.get(player2.getUniqueId()) != CullType.CULLED);

      for (UUID player : updatedTracking) {
         if (!this.syncTracking.contains(player)) {
            this.startTrackingQueue.add(player);
         }
      }

      for (UUID player : this.syncTracking) {
         if (!updatedTracking.contains(player)) {
            this.stopTrackingQueue.add(player);
         }
      }

      this.syncTracking.clear();
      this.syncTracking.addAll(updatedTracking);
      this.syncUpdateCallback.invoker().run();
   }

   @Override
   public void cullUpdate() {
      this.culler.updateCulledPlayer();
   }

   @Override
   public void cleanup() {
      this.startTracking.clear();
      this.stopTracking.clear();
   }

   @Override
   public void destroy() {
   }

   @Override
   public BaseEntity<?> getBaseEntity() {
      return this.dummy;
   }

   @Override
   public boolean isDataValid() {
      return this.dummy.isAlive();
   }

   @Override
   public List<Entity> getPassengers() {
      return List.of();
   }

   @Override
   public Set<UUID> getStartTracking() {
      return ImmutableSet.copyOf(this.startTracking);
   }

   @Override
   public Map<UUID, CullType> getMutableTracking() {
      return this.asyncTracking;
   }

   @Override
   public Map<UUID, CullType> getTracking() {
      return this.trackingMap.snapshot();
   }

   @Override
   public Set<UUID> getStopTracking() {
      return ImmutableSet.copyOf(this.stopTracking);
   }

   @Override
   public boolean hasTracking() {
      return !this.asyncTracking.isEmpty();
   }

   public int getRenderRadius() {
      return this.tracked.getBaseRange();
   }

   public void setRenderRadius(int radius) {
      this.tracked.setBaseRange(radius);
   }
}
