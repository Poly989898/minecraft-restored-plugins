package com.ticxo.modelengine.api.utils.math;

import java.lang.invoke.VarHandle;
import java.util.function.Consumer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SafeTransform extends Transform {
   private final Transform lastTransform = new Transform();
   private final Transform safe = new Transform();

   public void recordLast() {
      this.lastTransform.position.set(this.position);
      this.lastTransform.leftEuler.set(this.leftEuler);
      this.lastTransform.scale.set(this.scale);
      this.lastTransform.rightEuler.set(this.rightEuler);
      this.lastTransform.leftQuaternion.set(this.leftQuaternion);
      this.lastTransform.rightQuaternion.set(this.rightQuaternion);
   }

   public void recordSafe() {
      this.safe.position.set(this.position);
      this.safe.leftEuler.set(this.leftEuler);
      this.safe.scale.set(this.scale);
      this.safe.rightEuler.set(this.rightEuler);
      this.safe.leftQuaternion.set(this.leftQuaternion);
      this.safe.rightQuaternion.set(this.rightQuaternion);
      VarHandle.releaseFence();
   }

   public Vector3f getSafePosition() {
      VarHandle.acquireFence();
      return this.safe.getPosition();
   }

   public Vector3f getSafeLeftEuler() {
      VarHandle.acquireFence();
      return this.safe.getLeftEuler();
   }

   public Vector3f getSafeScale() {
      VarHandle.acquireFence();
      return this.safe.getScale();
   }

   public Vector3f getSafeRightEuler() {
      VarHandle.acquireFence();
      return this.safe.getRightEuler();
   }

   public Quaternionf getSafeLeftQuaternion() {
      VarHandle.acquireFence();
      return this.safe.getLeftQuaternion();
   }

   public Quaternionf getSafeRightQuaternion() {
      VarHandle.acquireFence();
      return this.safe.getRightQuaternion();
   }

   public void mutateSafe(Consumer<Transform> consumer) {
      VarHandle.acquireFence();
      consumer.accept(this.safe);
      VarHandle.releaseFence();
   }

   public Vector3f getLastPosition() {
      return this.lastTransform.getPosition();
   }

   public Vector3f getLastLeftEuler() {
      return this.lastTransform.getLeftEuler();
   }

   public Vector3f getLastScale() {
      return this.lastTransform.getScale();
   }

   public Vector3f getLastRightEuler() {
      return this.lastTransform.getRightEuler();
   }

   public Quaternionf getLastLeftQuaternion() {
      return this.lastTransform.getLeftQuaternion();
   }

   public Quaternionf getLastRightQuaternion() {
      return this.lastTransform.getRightQuaternion();
   }
}
