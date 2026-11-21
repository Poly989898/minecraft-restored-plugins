package com.ticxo.modelengine.api.model.bone.manager;

import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.mount.controller.MountControllerSupplier;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MountManager {
   void setCanDrive(boolean canDrive);

   boolean canDrive();

   void setCanRide(boolean canRide);

   boolean canRide();

   Entity getDriver();

   boolean isControlled();

   boolean hasPassengers();

   boolean hasRiders();

   @Nullable
   <T extends Mount & BoneBehavior> T getDriverBone();

   <T extends Mount & BoneBehavior> void setDriverBone(@Nullable T mount);

   <T extends Mount & BoneBehavior> void registerSeat(T mount);

   <T extends Mount & BoneBehavior> Map<String, T> getSeats();

   <T extends Mount & BoneBehavior> Optional<T> getSeat(String boneId);

   <T extends Mount & BoneBehavior> Optional<T> getMount(Entity entity);

   Map<Entity, Mount> getPassengerSeatMap();

   boolean mountDriver(Entity entity, MountControllerSupplier supplier);

   boolean mountDriver(Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer);

   boolean mountPassenger(String boneId, Entity entity, MountControllerSupplier supplier);

   boolean mountPassenger(String boneId, Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer);

   boolean mountPassenger(Mount mount, Entity entity, MountControllerSupplier supplier);

   boolean mountPassenger(Mount mount, Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer);

   boolean mountAvailable(Entity entity, MountControllerSupplier supplier);

   boolean mountAvailable(Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer);

   Set<Entity> mountAvailable(Collection<Entity> entities, MountControllerSupplier supplier);

   Set<Entity> mountAvailable(Collection<Entity> entities, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer);

   boolean mountAvailable(Entity entity, Collection<String> seats, MountControllerSupplier supplier);

   boolean mountAvailable(Entity entity, Collection<String> seats, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer);

   Set<Entity> mountAvailable(Collection<Entity> entities, Collection<String> seats, MountControllerSupplier supplier);

   Set<Entity> mountAvailable(Collection<Entity> entities, Collection<String> seats, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer);

   boolean mountLeastOccupied(Entity entity, MountControllerSupplier supplier);

   boolean mountLeastOccupied(Entity entity, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer);

   Set<Entity> mountLeastOccupied(Collection<Entity> entities, MountControllerSupplier supplier);

   Set<Entity> mountLeastOccupied(Collection<Entity> entities, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer);

   boolean mountLeastOccupied(Entity entity, Collection<String> seats, MountControllerSupplier supplier);

   boolean mountLeastOccupied(Entity entity, Collection<String> seats, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer);

   Set<Entity> mountLeastOccupied(Collection<Entity> entities, Collection<String> seats, MountControllerSupplier supplier);

   Set<Entity> mountLeastOccupied(Collection<Entity> entities, Collection<String> seats, MountControllerSupplier supplier, @Nullable Consumer<MountController> consumer);

   Entity dismountDriver();

   void dismountPassenger(@NotNull Entity entity);

   void dismountRider(@NotNull Entity entity);

   Set<Entity> dismountPassengers(String boneId);

   Set<Entity> dismountAll();
}
