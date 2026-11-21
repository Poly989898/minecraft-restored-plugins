package com.ticxo.modelengine.api.utils.math;

import java.util.function.Consumer;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
   protected final Vector3f position = new Vector3f();
   protected final Vector3f leftEuler = new Vector3f();
   protected final Vector3f scale = new Vector3f(1.0F);
   protected final Vector3f rightEuler = new Vector3f();
   protected final Quaternionf leftQuaternion = new Quaternionf();
   protected final Quaternionf rightQuaternion = new Quaternionf();

   public void identity() {
      this.position.set(0.0F);
      this.leftEuler.set(0.0F);
      this.scale.set(1.0F);
      this.rightEuler.set(0.0F);
      this.leftQuaternion.identity();
      this.rightQuaternion.identity();
   }

   public void set(Transform other) {
      this.position.set(other.position);
      this.leftEuler.set(other.leftEuler);
      this.scale.set(other.scale);
      this.rightEuler.set(other.rightEuler);
      this.leftQuaternion.set(other.leftQuaternion);
      this.rightQuaternion.set(other.rightQuaternion);
   }

   public Vector3f getPosition() {
      return new Vector3f(this.position);
   }

   public Vector3f getLeftEuler() {
      return new Vector3f(this.leftEuler);
   }

   public Vector3f getScale() {
      return new Vector3f(this.scale);
   }

   public Vector3f getRightEuler() {
      return new Vector3f(this.rightEuler);
   }

   public Quaternionf getLeftQuaternion() {
      return new Quaternionf(this.leftQuaternion);
   }

   public Quaternionf getRightQuaternion() {
      return new Quaternionf(this.rightQuaternion);
   }

   public Vector3f getPosition(Vector3f dest) {
      return dest.set(this.position);
   }

   public Vector3f getScale(Vector3f dest) {
      return dest.set(this.scale);
   }

   public Quaternionf getLeftQuaternion(Quaternionf dest) {
      return dest.set(this.leftQuaternion);
   }

   public Quaternionf getRightQuaternion(Quaternionf dest) {
      return dest.set(this.rightQuaternion);
   }

   public Vector3f setPosition(Vector3f value) {
      return this.position.set(value);
   }

   public Vector3f setLeftEuler(Vector3f value) {
      this.leftEuler.set(value);
      TMath.toQuaternion(this.leftEuler, this.leftQuaternion);
      return this.leftEuler;
   }

   public Vector3f setScale(Vector3f value) {
      return this.scale.set(value);
   }

   public Vector3f setRightEuler(Vector3f value) {
      this.rightEuler.set(value);
      TMath.toQuaternion(this.rightEuler, this.rightQuaternion);
      return this.rightEuler;
   }

   public Quaternionf setLeftQuaternion(Quaternionf value) {
      this.leftQuaternion.set(value);
      TMath.getEulerAnglesZYX(this.leftQuaternion, this.leftEuler);
      return this.leftQuaternion;
   }

   public Quaternionf setRightQuaternion(Quaternionf value) {
      this.rightQuaternion.set(value);
      TMath.getEulerAnglesZYX(this.rightQuaternion, this.rightEuler);
      return this.rightQuaternion;
   }

   public void mutatePosition(Consumer<Vector3f> mutator) {
      mutator.accept(this.position);
   }

   public void mutateLeftEuler(Consumer<Vector3f> mutator) {
      mutator.accept(this.leftEuler);
      TMath.toQuaternion(this.leftEuler, this.leftQuaternion);
   }

   public void mutateScale(Consumer<Vector3f> mutator) {
      mutator.accept(this.scale);
   }

   public void mutateRightEuler(Consumer<Vector3f> mutator) {
      mutator.accept(this.rightEuler);
      TMath.toQuaternion(this.rightEuler, this.rightQuaternion);
   }

   public void mutateLeftQuaternion(Consumer<Quaternionf> mutator) {
      mutator.accept(this.leftQuaternion);
      TMath.toEulerZYX(this.leftQuaternion, this.leftEuler);
   }

   public void mutateRightQuaternion(Consumer<Quaternionf> mutator) {
      mutator.accept(this.rightQuaternion);
      TMath.toEulerZYX(this.rightQuaternion, this.rightEuler);
   }

   public Matrix4f createMatrix() {
      return new Matrix4f().translate(this.position).rotate(this.leftQuaternion).scale(this.scale).rotate(this.rightQuaternion);
   }
}
