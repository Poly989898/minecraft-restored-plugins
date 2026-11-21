package com.ticxo.modelengine.api.animation.rootmotion;

import org.bukkit.util.Vector;
import org.joml.Vector3f;

public record RootMotion(Vector3f velocity, float yaw, boolean boneOnGround) {
   public Vector delta() {
      return Vector.fromJOML(this.velocity).rotateAroundY(-this.yaw * (float) (Math.PI / 180.0));
   }
}
