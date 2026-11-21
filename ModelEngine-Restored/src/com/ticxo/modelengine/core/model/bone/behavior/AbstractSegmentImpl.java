package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.Segment;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Getter
@Setter
public class AbstractSegmentImpl<T extends BoneBehavior> extends AbstractBoneBehavior<T> implements Segment {
   protected final boolean backPivot;
   protected boolean bounded;
   protected float minX;
   protected float maxX;
   protected float minY;
   protected float maxY;
   protected float minZ;
   protected float maxZ;
   protected float alignRate;
   protected float elasticity;
   protected final Vector3f delta = new Vector3f();
   protected final Vector3f deltaNorm = new Vector3f();
   protected final Vector3f worldLocation = new Vector3f();
   protected UUID scaleCallback;
   protected float boneLength;
   protected float modelScale = 1.0F;

   public AbstractSegmentImpl(ModelBone bone, BoneBehaviorType<T> type, BoneBehaviorData data) {
      super(bone, type, data);
      this.backPivot = data.get("back_pivot", true);
      this.bounded = data.get("bounded", true);
      this.minX = data.get("min_x", -50.0F);
      this.maxX = data.get("max_x", 50.0F);
      this.minY = data.get("min_y", -50.0F);
      this.maxY = data.get("max_y", 50.0F);
      this.minZ = data.get("min_z", -50.0F);
      this.maxZ = data.get("max_z", 50.0F);
      this.alignRate = data.get("align_rate", 0.25F);
      this.elasticity = data.get("elasticity", 0.0F);
   }

   @Override
   public void onApply() {
      if (this.backPivot) {
         this.delta.set(this.bone.getBlueprintBone().getLocalPosition());
         this.delta.normalize(this.deltaNorm);
         this.boneLength = this.delta.length();
         Vector3fc scale = this.bone.getActiveModel().getScale();
         this.modelScale = scale.get(scale.maxComponent());
         this.scaleCallback = this.bone.getActiveModel().getScaleCallback().subscribe((model, s) -> this.modelScale = (float)s);
         this.worldLocation.set(this.bone.getLocation().toVector().toVector3f());
      }
   }

   @Override
   public void onModelInitialized() {
      if (!this.backPivot) {
         Optional<ModelBone> maybeControlBone = this.getControlBone();
         if (maybeControlBone.isEmpty()) {
            throw new RuntimeException("Missing control bone for: " + this.bone.getBoneId());
         }

         ModelBone controlBone = maybeControlBone.get();
         this.delta.set(controlBone.getBlueprintBone().getLocalPosition());
         this.delta.normalize(this.deltaNorm);
         this.boneLength = this.delta.length();
         Vector3fc scale = this.bone.getActiveModel().getScale();
         this.modelScale = scale.get(scale.maxComponent());
         this.scaleCallback = this.bone.getActiveModel().getScaleCallback().subscribe((model, s) -> this.modelScale = (float)s);
         this.worldLocation.set(controlBone.getLocation().toVector().toVector3f());
      }
   }

   private Optional<ModelBone> getControlBone() {
      Map<String, ModelBone> children = this.bone.getChildren();
      if (children.isEmpty()) {
         return Optional.empty();
      } else {
         return children.size() == 1 ? children.values().stream().findFirst() : Optional.ofNullable(children.get(this.bone.getBoneId() + "_ctrl"));
      }
   }

   @Override
   public void onRemove() {
      this.bone.getActiveModel().getScaleCallback().unsubscribe(this.scaleCallback);
   }

   @Override
   public void postGlobalCalculation() {
      if (this.modelScale != 0.0F) {
         if (this.backPivot) {
            this.backPivotCalculation();
         } else {
            this.frontPivotCalculation();
         }
      }
   }

   private void frontPivotCalculation() {
      Matrix4f parentTransform = this.getParent(parent -> new Matrix4f(parent.getTransformMatrix()), new Matrix4f());
      if (parentTransform.determinant() != 0.0F) {
         parentTransform.translate(this.bone.getLocalTransform().getPosition()).rotate(this.bone.getBlueprintBone().getLocalQuaternion());
         Matrix4f invParentTransform = parentTransform.invert(new Matrix4f());
         Vector3f pivotLocation = this.bone.calculatePivotLocation().toVector().toVector3f();
         Vector3f parentLocation = TMath.transform(this.bone.getTransformMatrix(), new Vector3f())
            .rotateY(-this.bone.getYaw() * (float) (Math.PI / 180.0))
            .mul(this.modelScale)
            .add(pivotLocation);
         Matrix4f transformMatrix = this.bone.getTransformMatrix();
         Vector3f originPosition = TMath.transform(transformMatrix, this.delta)
            .rotateY(-this.bone.getYaw() * (float) (Math.PI / 180.0))
            .mul(this.modelScale)
            .add(pivotLocation);
         float animatedBoneLength = originPosition.distance(parentLocation);
         float finalBoneLength = TMath.lerp(this.boneLength * this.modelScale, animatedBoneLength, this.elasticity);
         this.worldLocation.lerp(originPosition, this.alignRate).sub(parentLocation);
         if (this.worldLocation.lengthSquared() > 1.0E-6F) {
            this.worldLocation.normalize(finalBoneLength);
         }

         this.worldLocation.add(parentLocation);
         Vector3f globalPosition = this.worldLocation
            .sub(pivotLocation, new Vector3f())
            .mul(1.0F / this.modelScale)
            .rotateY(this.bone.getYaw() * (float) (Math.PI / 180.0));
         Vector3f localPosition = TMath.transform(invParentTransform, globalPosition);
         Quaternionf q = new Quaternionf();
         if (this.bounded) {
            Vector3f normLocalPosition = localPosition.normalize(new Vector3f());
            Vector3f axis = this.deltaNorm.cross(normLocalPosition, new Vector3f()).normalize();
            float angle = (float)Math.acos(this.deltaNorm.dot(normLocalPosition));
            q.set(new AxisAngle4f(angle, axis));
            if (!q.isFinite()) {
               this.deltaNorm.rotationTo(localPosition, q);
            } else {
               Vector3f euler = TMath.toEulerZYX(q, new Vector3f());
               euler.x = TMath.clamp(euler.x, this.minX, this.maxX) * (float) (Math.PI / 180.0);
               euler.y = TMath.clamp(euler.y, this.minY, this.maxY) * (float) (Math.PI / 180.0);
               euler.z = TMath.clamp(euler.z, this.minZ, this.maxZ) * (float) (Math.PI / 180.0);
               TMath.toQuaternion(euler, q);
               this.deltaNorm.rotate(q, localPosition).mul(finalBoneLength / this.modelScale);
            }
         } else {
            this.deltaNorm.rotationTo(localPosition, q);
         }

         transformMatrix.set(parentTransform);
         this.worldLocation
            .set(TMath.transform(transformMatrix, localPosition))
            .rotateY(-this.bone.getYaw() * (float) (Math.PI / 180.0))
            .mul(this.modelScale)
            .add(pivotLocation);
         transformMatrix.rotate(q);
      }
   }

   private void backPivotCalculation() {
      Matrix4f parentTransform = this.getParent(ModelBone::getTransformMatrix, new Matrix4f());
      Matrix4f invParentTransform = parentTransform.invert(new Matrix4f());
      Vector3f pivotLocation = this.bone.calculatePivotLocation().toVector().toVector3f();
      Vector3f parentLocation = this.getParent(parent -> parent.getLocationUnsafe().toVector().toVector3f(), pivotLocation);
      Matrix4f transformMatrix = this.bone.getTransformMatrix();
      Vector3f originPosition = TMath.transform(transformMatrix, new Vector3f())
         .rotateY(-this.bone.getYaw() * (float) (Math.PI / 180.0))
         .mul(this.modelScale)
         .add(pivotLocation);
      float animatedBoneLength = originPosition.distance(parentLocation);
      float finalBoneLength = TMath.lerp(this.boneLength * this.modelScale, animatedBoneLength, this.elasticity);
      this.worldLocation.lerp(originPosition, this.alignRate).sub(parentLocation);
      if (this.worldLocation.lengthSquared() > 1.0E-6F) {
         this.worldLocation.normalize(finalBoneLength);
      }

      this.worldLocation.add(parentLocation);
      Vector3f globalPosition = this.worldLocation
         .sub(pivotLocation, new Vector3f())
         .mul(1.0F / this.modelScale)
         .rotateY(this.bone.getYaw() * (float) (Math.PI / 180.0));
      Vector3f localPosition = TMath.transform(invParentTransform, globalPosition);
      Quaternionf q = new Quaternionf();
      if (this.bounded) {
         Vector3f normLocalPosition = localPosition.normalize(new Vector3f());
         Vector3f axis = this.deltaNorm.cross(normLocalPosition, new Vector3f()).normalize();
         float angle = (float)Math.acos(this.deltaNorm.dot(normLocalPosition));
         q.set(new AxisAngle4f(angle, axis));
         if (!q.isFinite()) {
            this.deltaNorm.rotationTo(localPosition, q);
         } else {
            Vector3f euler = TMath.toEulerZYX(q, new Vector3f());
            euler.x = TMath.clamp(euler.x, this.minX, this.maxX) * (float) (Math.PI / 180.0);
            euler.y = TMath.clamp(euler.y, this.minY, this.maxY) * (float) (Math.PI / 180.0);
            euler.z = TMath.clamp(euler.z, this.minZ, this.maxZ) * (float) (Math.PI / 180.0);
            TMath.toQuaternion(euler, q);
            this.deltaNorm.rotate(q, localPosition).mul(finalBoneLength / this.modelScale);
         }
      } else {
         this.deltaNorm.rotationTo(localPosition, q);
      }

      transformMatrix.set(parentTransform).translate(localPosition);
      this.worldLocation
         .set(TMath.transform(transformMatrix, new Vector3f()))
         .rotateY(-this.bone.getYaw() * (float) (Math.PI / 180.0))
         .mul(this.modelScale)
         .add(pivotLocation);
      transformMatrix.rotate(q).rotate(this.bone.getBlueprintBone().getLocalQuaternion());
   }

   private <R> R getParent(Function<ModelBone, R> function, R def) {
      return this.bone.getParent() == null ? def : function.apply(this.bone.getParent());
   }
}
