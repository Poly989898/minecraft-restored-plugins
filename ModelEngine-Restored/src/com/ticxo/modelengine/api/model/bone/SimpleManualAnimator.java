package com.ticxo.modelengine.api.model.bone;

import com.ticxo.modelengine.api.utils.math.SafeTransform;
import lombok.Getter;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SimpleManualAnimator implements ManualAnimator {
   @Getter
   private final Vector3f position = new Vector3f();
   @Getter
   private final Quaternionf rotation = new Quaternionf();
   @Getter
   private final Vector3f scale = new Vector3f(1.0F);
   private final boolean copied;

   public SimpleManualAnimator() {
      this.copied = false;
   }

   public SimpleManualAnimator(ModelBone bone) {
      SafeTransform transform = bone.getLocalTransform();
      this.position.set(transform.getSafePosition());
      this.rotation.set(transform.getSafeLeftQuaternion());
      this.scale.set(transform.getSafeScale());
      this.copied = true;
   }

   @Override
   public boolean applyBoneDefaultLocal() {
      return !this.copied;
   }

   @Override
   public void animate(ModelBone bone) {
      SafeTransform transform = bone.getLocalTransform();
      transform.mutatePosition(vector3f -> vector3f.add(this.position));
      transform.mutateLeftQuaternion(quaternionf -> quaternionf.mul(this.rotation));
      transform.mutateScale(vector3f -> vector3f.mul(this.scale));
   }
}
