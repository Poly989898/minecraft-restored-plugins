package com.ticxo.modelengine.api.entity;

import com.ticxo.modelengine.api.utils.math.Box;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class DynamicBox implements Box {
   private final BoundingBox box = new BoundingBox();

   public void reset() {
      this.box.resize(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   }

   public void expand(@NotNull BoundingBox other) {
      this.box.union(other);
   }

   @NotNull
   @Override
   public BoundingBox createBoundingBox(Vector vector) {
      return this.box.clone().shift(vector);
   }
}
