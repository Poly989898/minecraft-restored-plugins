package com.ticxo.modelengine.api.entity.data;

import com.google.common.collect.ImmutableSet;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.Hitbox;
import com.ticxo.modelengine.api.entity.cull.BasicCulling;
import com.ticxo.modelengine.api.entity.cull.ModelCuller;
import com.ticxo.modelengine.api.nms.entity.AttributeAccessor;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.entity.EntityState;
import com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity;
import com.ticxo.modelengine.api.nms.impl.TempTrackedEntity;
import com.ticxo.modelengine.api.utils.data.QueuedAtomic;
import com.ticxo.modelengine.api.utils.data.QueuedBoolean;
import com.ticxo.modelengine.api.utils.data.QueuedFloat;
import com.ticxo.modelengine.api.utils.data.QueuedInt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public class BukkitEntityData extends AbstractEntityData {
   protected final EntityHandler entityHandler = ModelEngineAPI.getEntityHandler();
   protected final Entity entity;
   @Getter
   protected final BukkitEntity baseEntity;
   protected final ModelCuller culler;
   protected final QueuedBoolean isEntityValid = new QueuedBoolean(false);
   protected final QueuedBoolean isDataValid = new QueuedBoolean(false);
   protected final QueuedBoolean isForcedAlive = new QueuedBoolean(true);
   protected final QueuedAtomic<Location> location = QueuedAtomic.<Location>builder()
      .value(() -> new Location(null, 0.0, 0.0, 0.0))
      .passer((a, b) -> b.set(a.get().clone()))
      .build();
   protected final QueuedAtomic<List<Entity>> passengers = QueuedAtomic.<List<Entity>>builder().value(ArrayList::new).setter((v, r) -> {
      r.get().clear();
      r.get().addAll(v);
   }).passer((a, b) -> {
      b.get().clear();
      b.get().addAll(a.get());
   }).build();
   protected final QueuedInt glowColor = new QueuedInt(-1);
   protected volatile Hitbox hitbox;
   protected final QueuedFloat yRot = new QueuedFloat(0.0F);
   protected final QueuedFloat yHeadRot = new QueuedFloat(0.0F);
   protected final QueuedFloat xHeadRot = new QueuedFloat(0.0F);
   protected final QueuedFloat yBodyRot = new QueuedFloat(0.0F);
   protected final QueuedBoolean isWalking = new QueuedBoolean(false);
   protected final QueuedBoolean isStrafing = new QueuedBoolean(false);
   protected final QueuedBoolean isJumping = new QueuedBoolean(false);
   protected final QueuedBoolean isFlying = new QueuedBoolean(false);
   protected final QueuedFloat health = new QueuedFloat(0.0F);
   protected final QueuedFloat maxHealth = new QueuedFloat(0.0F);
   protected final QueuedBoolean isRemoved = new QueuedBoolean(false);
   protected final QueuedBoolean isBaseGlowing = new QueuedBoolean(false);
   protected final QueuedBoolean isOnFire = new QueuedBoolean(false);
   protected final QueuedAtomic<BoundingBox> boundingBox = QueuedAtomic.<BoundingBox>builder()
      .value(() -> new BoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0))
      .build();
   protected final Set<UUID> syncTracking = new HashSet<>();
   private final TrackingMap trackingMap = new TrackingMap();
   protected final Map<UUID, CullType> asyncTracking = this.trackingMap;
   protected final Queue<Queue<UUID>> startTrackingQueue = new ConcurrentLinkedQueue<>();
   protected final Set<UUID> startTracking = new HashSet<>();
   protected final Queue<Queue<UUID>> stopTrackingQueue = new ConcurrentLinkedQueue<>();
   protected final Set<UUID> stopTracking = new HashSet<>();
   protected final Predicate<Player> trackingPredicate;
   protected volatile TrackedEntity tracked;
   protected final AttributeAccessor attributeAccessor;
   protected int syncTick;
   protected boolean wasValid;
   protected int foliaExecuteFailures;
   protected final Location reuseLoc = new Location(null, 0.0, 0.0, 0.0);
   protected final EntityState reuseState = new EntityState();
   protected final Set<UUID> reuseTracking = new HashSet<>();
   private long lastTrackingVersion = -1L;
   protected volatile Set<UUID> collidableExemptions;
   protected volatile double stepHeight = Double.NaN;
   private static final int SAVE_SNAPSHOT_INTERVAL = 20;
   private int saveSnapshotCooldown;

   public BukkitEntityData(Entity entity, BukkitEntity base) {
      this(entity, base, new BasicCulling());
   }

   public BukkitEntityData(Entity entity, BukkitEntity base, ModelCuller culler) {
      this.entity = entity;
      this.baseEntity = base;
      this.culler = culler;
      culler.setData(this);
      this.tracked = this.entityHandler.wrapTrackedEntity(entity);
      this.attributeAccessor = this.entityHandler.createAttributeAccessor(entity);
      this.trackingPredicate = player -> culler.getCulledCount() == 0 ? true : this.asyncTracking.get(player.getUniqueId()) != CullType.CULLED;
      boolean offThread = ServerInfo.IS_FOLIA && !this.isRegionThread();
      if (offThread) {
         this.entityHandler.getLocation(entity, this.reuseLoc);
      } else {
         entity.getLocation(this.reuseLoc);
      }

      this.location.set(this.reuseLoc.clone());
      if (offThread) {
         this.isEntityValid.set(true);
         this.isDataValid.set(true);
      } else {
         for (UUID uuid : this.tracked.getTrackedPlayer(this.trackingPredicate)) {
            this.asyncTracking.put(uuid, CullType.NO_CULL);
            this.syncTracking.add(uuid);
            this.startTracking.add(uuid);
            culler.put(uuid, CullType.NO_CULL);
         }

         if (ServerInfo.IS_FOLIA) {
            this.internalSyncUpdate();
         } else {
            this.syncUpdate();
         }
      }

      this.readEntityState();
      this.asyncUpdate();
   }

   private void readEntityState() {
      if (!ServerInfo.IS_FOLIA) {
         this.isRemoved.set(this.entityHandler.isRemoved(this.entity));
      } else {
         this.entityHandler.readEntityState(this.entity, this.reuseState);
         this.isRemoved.set(this.reuseState.removed);
         this.yRot.set(this.reuseState.yRot);
         this.yHeadRot.set(this.reuseState.yHeadRot);
         this.xHeadRot.set(this.reuseState.xHeadRot);
         this.yBodyRot.set(this.reuseState.yBodyRot);
         this.isWalking.set(this.reuseState.walking);
         this.isStrafing.set(this.reuseState.strafing);
         this.isJumping.set(this.reuseState.jumping);
         this.isFlying.set(this.reuseState.flying);
         this.health.set(this.reuseState.health);
         this.maxHealth.set(this.attributeAccessor.getMaxHealth());
         this.isBaseGlowing.set(this.reuseState.glowing);
         this.isOnFire.set(this.reuseState.onFire);
         if (!this.reuseState.boundingBox.equals(this.boundingBox.getUnsafe())) {
            this.boundingBox.set(this.reuseState.boundingBox.clone());
         }
      }
   }

   @Override
   public void asyncUpdate() {
      this.isEntityValid.pass();
      this.isDataValid.pass();
      this.isForcedAlive.pass();
      this.location.pass();
      this.passengers.pass();
      this.glowColor.pass();
      this.yRot.pass();
      this.yHeadRot.pass();
      this.xHeadRot.pass();
      this.yBodyRot.pass();
      this.isWalking.pass();
      this.isStrafing.pass();
      this.isJumping.pass();
      this.isFlying.pass();
      this.health.pass();
      this.maxHealth.pass();
      this.isRemoved.pass();
      this.isBaseGlowing.pass();
      this.isOnFire.pass();
      this.boundingBox.pass();

      while (!this.startTrackingQueue.isEmpty() || !this.stopTrackingQueue.isEmpty()) {
         if (!this.startTrackingQueue.isEmpty()) {
            Queue<UUID> playerQueue = this.startTrackingQueue.poll();
            if (playerQueue != null) {
               while (!playerQueue.isEmpty()) {
                  UUID player = playerQueue.poll();
                  this.startTracking.add(player);
                  this.stopTracking.remove(player);
                  this.culler.put(player, CullType.NO_CULL);
               }
            }
         }

         if (!this.stopTrackingQueue.isEmpty()) {
            Queue<UUID> playerQueue = this.stopTrackingQueue.poll();
            if (playerQueue != null) {
               while (!playerQueue.isEmpty()) {
                  UUID player = playerQueue.poll();
                  this.stopTracking.add(player);
                  this.startTracking.remove(player);
                  if (this.asyncTracking.get(player) != CullType.CULLED) {
                     this.culler.remove(player);
                  }
               }
            }
         }
      }
   }

   @Override
   public void syncUpdate() {
      if (ServerInfo.IS_FOLIA) {
         boolean scheduled = this.entity.getScheduler().execute(ModelEngineAPI.getAPI(), this::internalSyncUpdate, null, 1L);
         if (scheduled) {
            this.foliaExecuteFailures = 0;
         } else {
            Location loc = this.location.getUnsafe();
            if (loc != null && loc.getWorld() != null) {
               Bukkit.getRegionScheduler().execute(ModelEngineAPI.getAPI(), loc, this::internalSyncUpdate);
               this.foliaExecuteFailures = 0;
            } else {
               this.foliaExecuteFailures++;
               if (this.foliaExecuteFailures > 20) {
                  this.isEntityValid.set(false);
                  this.isDataValid.set(false);
               }
            }
         }
      } else {
         this.internalSyncUpdate();
      }
   }

   private void internalSyncUpdate() {
      this.syncTick++;
      boolean valid = this.entity.isValid();
      this.wasValid = this.wasValid | (valid || this.syncTick > 20);
      valid |= !this.wasValid;
      if (this.isEntityValid.getUnsafe() != valid) {
         this.isEntityValid.set(valid);
      }

      boolean removed = this.entityHandler.isRemoved(this.entity);
      if (this.isForcedAlive()) {
         this.entityHandler.setDeathTick(this.entity, 0);
      } else if (!this.isEntityValid()) {
         this.entityHandler.setDeathTick(this.entity, 20);
      }

      Location prevLoc = this.location.getUnsafe();
      World prevWorld = prevLoc == null ? null : prevLoc.getWorld();
      this.entity.getLocation(this.reuseLoc);
      boolean worldChanged = false;
      if (prevWorld != null) {
         World newWorld = this.reuseLoc.getWorld();
         worldChanged = newWorld != null && prevWorld != newWorld;
      }

      if (!this.reuseLoc.equals(prevLoc)) {
         this.location.set(this.reuseLoc.clone());
      }

      this.passengers.set(this.entity.getPassengers());
      this.safeSyncUpdate();
      if (this.isDataValid.getUnsafe() != !removed) {
         this.isDataValid.set(!removed);
      }

      this.updateTracking();
      if (worldChanged) {
         this.baseEntity.onWorldChanged();
      }
   }

   private void updateTracking() {
      boolean viewersUnchanged = this.getTracked().isViewerSetUnchanged();
      long trackingVersion = this.trackingMap.getVersion();
      if (!viewersUnchanged || trackingVersion != this.lastTrackingVersion) {
         this.lastTrackingVersion = trackingVersion;
         Set<UUID> updatedTracking = this.getTracked().getTrackedPlayer(this.trackingPredicate, this.reuseTracking);
         Queue<UUID> startTrack = null;
         Queue<UUID> stopTrack = null;
         int seen = 0;

         for (UUID player : updatedTracking) {
            seen++;
            if (this.syncTracking.add(player)) {
               if (startTrack == null) {
                  startTrack = new ConcurrentLinkedQueue<>();
               }

               startTrack.add(player);
            }
         }

         if (updatedTracking != this.reuseTracking || this.syncTracking.size() != seen) {
            Iterator<UUID> it = this.syncTracking.iterator();

            while (it.hasNext()) {
               UUID player = it.next();
               if (!updatedTracking.contains(player)) {
                  if (stopTrack == null) {
                     stopTrack = new ConcurrentLinkedQueue<>();
                  }

                  stopTrack.add(player);
                  it.remove();
               }
            }
         }

         if (stopTrack != null) {
            this.stopTrackingQueue.add(stopTrack);
         }

         if (startTrack != null) {
            this.startTrackingQueue.add(startTrack);
         }
      }
   }

   private boolean isRegionThread() {
      return ServerInfo.IS_FOLIA ? Bukkit.isOwnedByCurrentRegion(this.entity) : Bukkit.isPrimaryThread();
   }

   protected void safeSyncUpdate() {
      this.glowColor.set(this.entityHandler.getGlowColor(this.entity));
      Hitbox hitbox = this.hitbox;
      if (hitbox != null && ((float)this.entity.getWidth() != (float)hitbox.getMaxWidth() || (float)this.entity.getHeight() != (float)hitbox.getHeight())) {
         this.entityHandler.setHitbox(this.entity, hitbox);
      }

      this.readEntityState();
      if (ServerInfo.IS_FOLIA || --this.saveSnapshotCooldown <= 0) {
         this.saveSnapshotCooldown = 20;
         this.refreshSaveSnapshots();
      }
   }

   public void setHitbox(Hitbox hitbox) {
      this.entityHandler.setHitbox(this.entity, hitbox);
      if (!(this.entity instanceof Player)) {
         this.hitbox = hitbox;
      }
   }

   public void refreshSaveSnapshots() {
      Set<UUID> exemptions = this.entityHandler.getCollidableExemptions(this.entity);
      if (exemptions != null && !exemptions.equals(this.collidableExemptions)) {
         this.collidableExemptions = Set.copyOf(exemptions);
      }

      if (ServerInfo.IS_FOLIA) {
         this.stepHeight = this.entityHandler.getStepHeight(this.entity);
      }
   }

   public Set<UUID> getCollidableExemptions() {
      return this.collidableExemptions;
   }

   public double getStepHeight() {
      return ServerInfo.IS_FOLIA ? this.stepHeight : this.entityHandler.getStepHeight(this.entity);
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
      this.startTrackingQueue.forEach(Collection::clear);
      this.startTrackingQueue.clear();
      this.stopTrackingQueue.forEach(Collection::clear);
      this.stopTrackingQueue.clear();
   }

   @Override
   public boolean isDataValid() {
      return this.isDataValid.get();
   }

   public boolean isEntityValid() {
      return this.isEntityValid.get();
   }

   public boolean isForcedAlive() {
      return this.isForcedAlive.get();
   }

   public void setForcedAlive(boolean flag) {
      this.isForcedAlive.set(flag);
   }

   public int getGlowColor() {
      return this.glowColor.get();
   }

   @Override
   public Location getLocation() {
      return this.location.get().clone();
   }

   @Override
   public List<Entity> getPassengers() {
      return this.passengers.get();
   }

   @Override
   public void markViewersDirty() {
      TrackedEntity current = this.tracked;
      if (current != null) {
         current.markViewersDirty();
      }
   }

   @Override
   public Set<UUID> getStartTracking() {
      return ImmutableSet.copyOf(this.startTracking);
   }

   public void resendTracking(UUID player) {
      if (this.asyncTracking.containsKey(player)) {
         Queue<UUID> queue = new ConcurrentLinkedQueue<>();
         queue.add(player);
         this.startTrackingQueue.add(queue);
      }
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
      return !this.asyncTracking.isEmpty() && this.culler.getCulledCount() != this.asyncTracking.size();
   }

   @Override
   public boolean isBaseGlowing() {
      return ServerInfo.IS_FOLIA ? this.isBaseGlowing.get() : this.entityHandler.isGlowing(this.entity);
   }

   public boolean isOnFire() {
      return ServerInfo.IS_FOLIA ? this.isOnFire.get() : this.entityHandler.isVisualFire(this.entity) || this.entityHandler.getFireTicks(this.entity) > 0;
   }

   public TrackedEntity getTracked() {
      if (this.tracked instanceof TempTrackedEntity tempTracked) {
         TrackedEntity newTracked = this.entityHandler.wrapTrackedEntity(this.entity);
         if (!(newTracked instanceof TempTrackedEntity)) {
            if (tempTracked.getBaseRange() != -1) {
               newTracked.setBaseRange(tempTracked.getBaseRange());
            }

            newTracked.setPlayerPredicate(tempTracked.getPlayerPredicate());

            for (UUID player : tempTracked.getForcePaired()) {
               newTracked.addForcedPairing(player);
            }

            for (UUID player : tempTracked.getForceHidden()) {
               newTracked.addForcedHidden(player);
            }

            this.tracked = newTracked;
         }
      }

      return this.tracked;
   }

   public float getYRot() {
      return ServerInfo.IS_FOLIA ? this.yRot.get() : this.entityHandler.getYRot(this.entity);
   }

   public float getYHeadRot() {
      return ServerInfo.IS_FOLIA ? this.yHeadRot.get() : this.entityHandler.getYHeadRot(this.entity);
   }

   public float getXHeadRot() {
      return ServerInfo.IS_FOLIA ? this.xHeadRot.get() : this.entityHandler.getXHeadRot(this.entity);
   }

   public float getYBodyRot() {
      return ServerInfo.IS_FOLIA ? this.yBodyRot.get() : this.entityHandler.getYBodyRot(this.entity);
   }

   public boolean isWalking() {
      return ServerInfo.IS_FOLIA ? this.isWalking.get() : this.entityHandler.isWalking(this.entity);
   }

   public boolean isStrafing() {
      return ServerInfo.IS_FOLIA ? this.isStrafing.get() : this.entityHandler.isStrafing(this.entity);
   }

   public boolean isJumping() {
      return ServerInfo.IS_FOLIA ? this.isJumping.get() : this.entityHandler.isJumping(this.entity);
   }

   public boolean isFlying() {
      return ServerInfo.IS_FOLIA ? this.isFlying.get() : this.entityHandler.isFlying(this.entity);
   }

   public float getHealth() {
      return ServerInfo.IS_FOLIA ? this.health.get() : this.entityHandler.getHealth(this.entity);
   }

   public float getMaxHealth() {
      return ServerInfo.IS_FOLIA ? this.maxHealth.get() : this.attributeAccessor.getMaxHealth();
   }

   public boolean isRemoved() {
      return this.isRemoved.get();
   }

   public BoundingBox getBoundingBox() {
      return ServerInfo.IS_FOLIA ? this.boundingBox.get().clone() : this.entityHandler.getBoundingBox(this.entity);
   }
}
