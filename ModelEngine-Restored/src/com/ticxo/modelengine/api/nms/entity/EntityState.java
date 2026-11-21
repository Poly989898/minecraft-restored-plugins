package com.ticxo.modelengine.api.nms.entity;

import org.bukkit.util.BoundingBox;

public final class EntityState {
   public float yRot;
   public float yHeadRot;
   public float xHeadRot;
   public float yBodyRot;
   public float health;
   public boolean walking;
   public boolean strafing;
   public boolean jumping;
   public boolean flying;
   public boolean removed;
   public boolean glowing;
   public boolean onFire;
   public final BoundingBox boundingBox = new BoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
}
