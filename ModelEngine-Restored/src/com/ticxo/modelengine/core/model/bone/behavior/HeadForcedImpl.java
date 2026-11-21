package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

@Getter
@Setter
public class HeadForcedImpl extends HeadImpl {
   protected boolean inherited;

   public HeadForcedImpl(ModelBone bone, BoneBehaviorType<HeadImpl> type, BoneBehaviorData data) {
      super(bone, type, data);
      this.onParentSwap(bone.getParent());
   }

   @Override
   public void onParentSwap(@Nullable ModelBone parent) {
      this.shouldRotate = false;
      if (parent != null) {
         Optional<HeadImpl> maybeBehavior = parent.getBoneBehavior(this.type);
         maybeBehavior.ifPresent(head -> {
            this.inherited = head.inherited;
            if (!this.inherited && !(head instanceof HeadForcedImpl)) {
               this.shouldRotate = true;
               this.local = head.isLocal();
            }
         });
      }
   }

   @Override
   public void preGlobalRotate() {
      if (this.shouldRotate && !this.local) {
         this.rotate();
      }
   }

   @Override
   public void postGlobalRotate() {
      if (this.shouldRotate && this.local) {
         this.rotate();
      }
   }

   private void rotate() {
      ActiveModel activeModel = this.bone.getActiveModel();
      float yaw = -TMath.degreeDifference(activeModel.getYBodyRot(), activeModel.getYHeadRot());
      float pitch = -activeModel.getXHeadRot();
      Quaternionf q = new Quaternionf().rotateX(pitch * (float) (Math.PI / 180.0)).rotateY(-yaw * (float) (Math.PI / 180.0));
      this.bone.getTransformMatrix().rotate(q);
   }

   @Override
   public boolean isHidden() {
      return true;
   }
}
