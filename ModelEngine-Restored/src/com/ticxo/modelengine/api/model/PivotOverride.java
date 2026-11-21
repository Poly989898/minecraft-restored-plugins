package com.ticxo.modelengine.api.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@Getter
@Setter
@RequiredArgsConstructor
public class PivotOverride {
   private final UUID uuid;
   private final int id;
   private final Supplier<Vector3f> mountOffsetSupplier;
   private final CollectionDataTracker<Integer> passengers = new CollectionDataTracker<>(new HashSet<>());
   private volatile int[] basePassengerSnapshot = new int[0];

   public void addPassenger(int id) {
      this.passengers.add(id);
   }

   public void removePassenger(int id) {
      this.passengers.remove(id);
   }

   public Vector3f getMountOffset() {
      return this.mountOffsetSupplier.get();
   }

   public static PivotOverride create(@NotNull Entity entity) {
      return new PivotOverride(entity.getUniqueId(), entity.getEntityId(), () -> ModelEngineAPI.getEntityHandler().getPivotOffset(entity));
   }
}
