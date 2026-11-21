package com.ticxo.modelengine.api.model.render;

import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

public interface DisplayFire {
   int getId();

   UUID getUuid();

   DataTracker<Vector3f> getPosition();

   DataTracker<Vector3f> getScale();

   DataTracker<ItemStack> getFireModel();

   DataTracker<Boolean> getVisible();

   void clearDirty();

   default boolean isDirty() {
      return this.getPosition().isDirty() || this.getScale().isDirty() || this.getFireModel().isDirty() || this.getVisible().isDirty();
   }
}
