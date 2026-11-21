package com.ticxo.modelengine.api.utils.math;

import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface Box {
   @NotNull
   BoundingBox createBoundingBox(Vector vector);
}
