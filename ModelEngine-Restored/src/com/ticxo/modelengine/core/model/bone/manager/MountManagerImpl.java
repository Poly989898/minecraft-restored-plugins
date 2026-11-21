package com.ticxo.modelengine.core.model.bone.manager;

import com.google.common.collect.ImmutableMap;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.events.ModelDismountEvent;
import com.ticxo.modelengine.api.events.ModelMountEvent;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.manager.AbstractBehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.render.renderer.MountRenderer;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.mount.controller.MountControllerSupplier;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.core.model.bone.behavior.MountImpl;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MountManagerImpl extends AbstractBehaviorManager<MountImpl> implements MountManager {
   private final Map<String, ?> seats = new LinkedHashMap<>();
   private final Map<Entity, Mount> passengerSeatMap = new HashMap<>();
   @Setter
   boolean canDrive;
   @Setter
   boolean canRide;
   @Getter
   private Entity driver;
   private Mount driverBone;

   public MountManagerImpl(ActiveModel activeModel, BoneBehaviorType<MountImpl> supplier) {
      super(activeModel, supplier);
   }

   @Override
   public void onDestroy() {
      this.dismountDriver();
      this.dismountAll();
   }

   @Override
   public void load(SavedData data) {
      this.canDrive = data.getBoolean("drive");
      this.canRide = data.getBoolean("ride");
   }

   @Override
   public void save(SavedData data) {
      data.putBoolean("drive", this.canDrive);
      data.putBoolean("ride", this.canRide);
   }

   @Override
   public boolean canDrive() {
      return this.canDrive;
   }

   @Override
   public boolean canRide() {
      return this.canRide;
   }

   @Override
   public boolean isControlled() {
      return this.driver != null && this.driverBone != null;
   }

   @Override
   public boolean hasPassengers() {
      return !this.passengerSeatMap.isEmpty();
   }

   @Override
   public boolean hasRiders() {
      return this.driver != null || !this.passengerSeatMap.isEmpty();
   }

   @Nullable
   @Override
   public <T extends Mount & BoneBehavior> T getDriverBone() {
      return (T)this.driverBone;
   }

   @Override
   public <T extends Mount & BoneBehavior> void setDriverBone(@Nullable T mount) {
      this.driverBone = mount;
   }

   @Override
   public <T extends Mount & BoneBehavior> void registerSeat(T mount) {
      this.<T>getSeats().put(mount.getBone().getUniqueBoneId(), mount);
   }

   @Override
   public <T extends Mount & BoneBehavior> Map<String, T> getSeats() {
      return (Map<String, T>)this.seats;
   }

   @Override
   public <T extends Mount & BoneBehavior> Optional<T> getSeat(String boneId) {
      if (boneId == null) {
         return Optional.empty();
      }

      Map seats = this.getSeats();
      BoneBehavior seat = (BoneBehavior)seats.get(boneId);
      if (seat == null && boneId.startsWith("p_")) {
         seat = (BoneBehavior)seats.get(boneId.substring(2));
      }

      return Optional.ofNullable((T)seat);
   }

   @Override
   public <T extends Mount & BoneBehavior> Optional<T> getMount(Entity entity) {
      if (entity == this.driver) {
         return Optional.ofNullable(this.getDriverBone());
      }

      Mount mount = this.passengerSeatMap.get(entity);
      return Optional.ofNullable((T)mount);
   }

   @Override
   public Map<Entity, Mount> getPassengerSeatMap() {
      return ImmutableMap.copyOf(this.passengerSeatMap);
   }

   @Override
   public boolean mountDriver(Entity entity, MountControllerSupplier supplier) {
      return this.mountDriver(entity, supplier, null);
   }

   @Override
   public boolean mountDriver(Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
      if (this.driverBone != null && this.driverBone.getPassengers().isEmpty() && this.canDrive()) {
         ModelMountEvent event = new ModelMountEvent(this.activeModel, entity, true, this.driverBone);
         ModelEngineAPI.callEvent(event);
         if (event.isCancelled()) {
            return false;
         }

         boolean success = this.driverBone.addPassenger(entity);
         if (success) {
            this.driver = entity;
            this.registerMountPair(entity, this.driverBone, supplier, consumer);
         }

         return success;
      } else {
         return false;
      }
   }

   @Override
   public boolean mountPassenger(String boneId, Entity entity, MountControllerSupplier supplier) {
      return this.mountPassenger(boneId, entity, supplier, null);
   }

   @Override
   public boolean mountPassenger(String boneId, Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
      return this.canRide() && this.getSeat(boneId).map(mount -> this.mountPassenger((Mount)mount, entity, supplier, consumer)).orElse(false);
   }

   @Override
   public boolean mountPassenger(Mount mount, Entity entity, MountControllerSupplier supplier) {
      return this.mountPassenger(mount, entity, supplier, null);
   }

   @Override
   public boolean mountPassenger(Mount mount, Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
      ModelMountEvent event = new ModelMountEvent(this.activeModel, entity, false, mount);
      ModelEngineAPI.callEvent(event);
      if (event.isCancelled()) {
         return false;
      }

      boolean success = mount.addPassenger(entity);
      if (success) {
         this.passengerSeatMap.put(entity, mount);
         this.registerMountPair(entity, mount, supplier, consumer);
      }

      return success;
   }

   @Override
   public boolean mountAvailable(Entity entity, MountControllerSupplier supplier) {
      return this.mountAvailable(entity, supplier, null);
   }

   @Override
   public boolean mountAvailable(Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
      return this.mountAvailable(entity, this.seats.keySet(), supplier, consumer);
   }

   @Override
   public Set<Entity> mountAvailable(Collection<Entity> entities, MountControllerSupplier supplier) {
      return this.mountAvailable(entities, supplier, null);
   }

   @Override
   public Set<Entity> mountAvailable(Collection<Entity> entities, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
      return this.mountAvailable(entities, this.seats.keySet(), supplier, consumer);
   }

   @Override
   public boolean mountAvailable(Entity entity, Collection<String> seats, MountControllerSupplier supplier) {
      return this.mountAvailable(entity, seats, supplier, null);
   }

   @Override
   public boolean mountAvailable(Entity entity, Collection<String> seats, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
      for (String seatId : seats) {
         Optional maybeSeat = this.getSeat(seatId);
         if (!maybeSeat.isEmpty()) {
            BoneBehavior seat = (BoneBehavior)maybeSeat.get();
            if (((Mount)seat).getPassengers().isEmpty()) {
               this.mountPassenger((Mount)seat, entity, supplier, consumer);
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public Set<Entity> mountAvailable(Collection<Entity> entities, Collection<String> seats, MountControllerSupplier supplier) {
      return this.mountAvailable(entities, seats, supplier, null);
   }

   @Override
   public Set<Entity> mountAvailable(
      Collection<Entity> entities, Collection<String> seats, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer
   ) {
      HashSet<Entity> unseated = new HashSet<>();
      boolean noSeats = false;

      outer:
      for (Entity entity : entities) {
         if (!noSeats) {
            noSeats = true;

            for (String seatId : seats) {
               Optional maybeSeat = this.getSeat(seatId);
               if (!maybeSeat.isEmpty()) {
                  BoneBehavior seat = (BoneBehavior)maybeSeat.get();
                  if (((Mount)seat).getPassengers().isEmpty()) {
                     this.mountPassenger((Mount)seat, entity, supplier, consumer);
                     noSeats = false;
                     continue outer;
                  }
               }
            }
         }

         unseated.add(entity);
      }

      return unseated;
   }

   @Override
   public boolean mountLeastOccupied(Entity entity, MountControllerSupplier supplier) {
      return this.mountLeastOccupied(entity, supplier, null);
   }

   @Override
   public boolean mountLeastOccupied(Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
      return this.mountLeastOccupied(entity, this.seats.keySet(), supplier, consumer);
   }

   @Override
   public Set<Entity> mountLeastOccupied(Collection<Entity> entities, MountControllerSupplier supplier) {
      return this.mountLeastOccupied(entities, supplier, null);
   }

   @Override
   public Set<Entity> mountLeastOccupied(Collection<Entity> entities, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
      return this.mountLeastOccupied(entities, this.seats.keySet(), supplier, consumer);
   }

   @Override
   public boolean mountLeastOccupied(Entity entity, Collection<String> seats, MountControllerSupplier supplier) {
      return this.mountLeastOccupied(entity, seats, supplier, null);
   }

   @Override
   public boolean mountLeastOccupied(Entity entity, Collection<String> seats, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
      int leastCount = Integer.MAX_VALUE;
      Mount leastOccupied = null;

      for (String seatId : seats) {
         Optional maybeSeat = this.getSeat(seatId);
         if (!maybeSeat.isEmpty()) {
            BoneBehavior seat = (BoneBehavior)maybeSeat.get();
            if (((Mount)seat).canMountMore()) {
               int count = ((Mount)seat).getPassengers().size();
               if (count == 0) {
                  return this.mountPassenger((Mount)seat, entity, supplier, consumer);
               }

               if (leastCount > count) {
                  leastCount = count;
                  leastOccupied = (Mount)seat;
               }
            }
         }
      }

      return leastOccupied != null ? this.mountPassenger(leastOccupied, entity, supplier, consumer) : false;
   }

   @Override
   public Set<Entity> mountLeastOccupied(Collection<Entity> entities, Collection<String> seats, MountControllerSupplier supplier) {
      return this.mountLeastOccupied(entities, seats, supplier, null);
   }

   @Override
   public Set<Entity> mountLeastOccupied(
      Collection<Entity> entities, Collection<String> seats, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer
   ) {
      HashSet<Entity> set = new HashSet<>();

      for (Entity entity : entities) {
         if (!this.mountLeastOccupied(entity, seats, supplier, consumer)) {
            set.add(entity);
         }
      }

      return set;
   }

   @Override
   public Entity dismountDriver() {
      if (this.driverBone != null && this.driver != null) {
         ModelDismountEvent event = new ModelDismountEvent(this.activeModel, this.driver, true, this.driverBone);
         ModelEngineAPI.callEvent(event);
         if (event.isCancelled()) {
            return null;
         }

         this.driverBone.removePassenger(this.driver);
         this.flushMountPassengerUpdate(this.driverBone);
         this.removeMountPair(this.driver);
         Entity t = this.driver;
         this.driver = null;
         return t;
      } else {
         return null;
      }
   }

   @Override
   public void dismountPassenger(@NotNull Entity entity) {
      Mount mount = this.passengerSeatMap.remove(entity);
      if (mount != null) {
         ModelDismountEvent event = new ModelDismountEvent(this.activeModel, entity, false, mount);
         ModelEngineAPI.callEvent(event);
         if (event.isCancelled()) {
            return;
         }

         mount.removePassenger(entity);
         this.flushMountPassengerUpdate(mount);
         this.removeMountPair(entity);
      }
   }

   @Override
   public void dismountRider(@NotNull Entity entity) {
      if (entity == this.driver) {
         this.dismountDriver();
      } else {
         this.dismountPassenger(entity);
      }
   }

   @Override
   public Set<Entity> dismountPassengers(String boneId) {
      HashSet<Entity> removed = new HashSet<>();
      this.getSeat(boneId).ifPresent(mount -> {
         for (Entity entity : ((Mount)mount).getPassengers()) {
            ModelDismountEvent event = new ModelDismountEvent(this.activeModel, entity, false, (Mount)mount);
            ModelEngineAPI.callEvent(event);
            if (!event.isCancelled()) {
               ((Mount)mount).removePassenger(entity);
               this.removeMountPair(entity);
               removed.add(entity);
            }
         }

         if (!removed.isEmpty()) {
            this.flushMountPassengerUpdate((Mount)mount);
         }
      });
      return removed;
   }

   @Override
   public Set<Entity> dismountAll() {
      HashSet<Entity> set = new HashSet<>();

      for (BoneBehavior val : this.getSeats().values()) {
         Set<Entity> passengers = ((Mount)val).clearPassengers();
         boolean anyDismounted = false;

         for (Entity entity : passengers) {
            ModelDismountEvent event = new ModelDismountEvent(this.activeModel, entity, false, (Mount)val);
            ModelEngineAPI.callEvent(event);
            if (!event.isCancelled()) {
               this.passengerSeatMap.remove(entity);
               this.removeMountPair(entity);
               set.add(entity);
               anyDismounted = true;
            }
         }

         if (anyDismounted) {
            this.flushMountPassengerUpdate((Mount)val);
         }
      }

      return set;
   }

   private void registerMountPair(Entity entity, Mount mount, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer) {
      MountController controller = supplier.createController(entity, mount);
      if (consumer != null) {
         consumer.accept(controller);
      }

      ModelEngineAPI.getMountPairManager().registerMountedPair(entity, this.activeModel, controller);
      this.activeModel.getModeledEntity().getBase().setCollidableWith(entity, false);
   }

   private void removeMountPair(Entity entity) {
      ModelEngineAPI.getMountPairManager().unregisterMountedPair(entity.getUniqueId());
      this.activeModel.getModeledEntity().getBase().setCollidableWith(entity, true);
   }

   private void flushMountPassengerUpdate(Mount mount) {
      if (mount instanceof BoneBehavior behavior) {
         String boneId = behavior.getBone().getUniqueBoneId();
         this.activeModel.getBehaviorRenderer(BoneBehaviorTypes.MOUNT).ifPresent(renderer -> {
            if (renderer instanceof MountRenderer mountRenderer) {
               MountRenderer.Mount renderedMount = mountRenderer.getRendered().get(boneId);
               if (renderedMount == null) {
                  renderedMount = mountRenderer.getSpawnQueue().get(boneId);
               }

               if (renderedMount != null) {
                  HashSet<Integer> newIds = new HashSet<>();
                  if (ServerInfo.IS_FOLIA && mount instanceof MountImpl mountImpl) {
                     newIds.addAll(mountImpl.getPassengerEntityIds());
                  } else {
                     for (Entity entity : mount.getPassengers()) {
                        newIds.add(entity.getEntityId());
                     }
                  }

                  renderedMount.getPassengers().retainAll(newIds);
                  renderedMount.getPassengers().addAll(newIds);
                  Set<UUID> targets = this.activeModel.getModeledEntity().getBase().getData().getTracking().keySet();
                  ModelEngineAPI.getNMSHandler().sendMountPassengerUpdate(targets, renderedMount.getMountId(), renderedMount.getPassengers().get());
               }
            }
         });
      }
   }
}
