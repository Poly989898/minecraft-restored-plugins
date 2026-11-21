package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.AbstractBoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.type.Head;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HeadImpl extends AbstractBoneBehavior<HeadImpl> implements Head {
   protected boolean shouldRotate;
   @Getter @Setter
   protected boolean inherited;
   @Getter @Setter
   protected boolean local;
   private final Vector3f pivot = new Vector3f();

   public HeadImpl(ModelBone bone, BoneBehaviorType<HeadImpl> type, BoneBehaviorData data) {
      super(bone, type, data);
      this.onParentSwap(bone.getParent());
      this.local = data.get("local", false);
      this.inherited = data.get("inherited", false);
   }

   @Override
   public void onParentSwap(@Nullable ModelBone parent) {
      this.shouldRotate = true;

      while (parent != null) {
         Optional<HeadImpl> maybeBehavior = parent.getBoneBehavior(this.type);
         this.shouldRotate = maybeBehavior.isEmpty() || maybeBehavior.get() instanceof HeadForcedImpl;
         if (!this.shouldRotate) {
            return;
         }

         parent = parent.getParent();
      }
   }

   @Override
   public void postGlobalTranslate() {
      this.bone.getTransformMatrix().getTranslation(this.pivot);
   }

   @Override
   public void postTransformDecompose() {
      if (this.shouldRotate && !this.local) {
         Quaternionf rotation = this.getRotation();
         this.bone.getGlobalTransform().mutatePosition(vector3f -> vector3f.sub(this.pivot).rotate(rotation).add(this.pivot));
         this.bone.getGlobalTransform().mutateLeftQuaternion(quaternionf -> quaternionf.premul(rotation));
      }
   }

   @Override
   public void postGlobalRotate() {
      if (this.shouldRotate && this.local) {
         this.bone.getTransformMatrix().rotate(this.getRotation());
      }
   }

   private Quaternionf getRotation() {
      ActiveModel activeModel = this.bone.getActiveModel();
      float yaw = TMath.degreeDifference(activeModel.getYBodyRot(), activeModel.getYHeadRot());
      float pitch = activeModel.getXHeadRot();
      return new Quaternionf().rotateY(-yaw * (float) (Math.PI / 180.0)).rotateX(pitch * (float) (Math.PI / 180.0));
   }
}
