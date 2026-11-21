package com.ticxo.modelengine.core.model.bone.behavior;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorData;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import org.bukkit.Location;
import org.joml.Vector3f;

public class SegmentImpl extends AbstractSegmentImpl<SegmentImpl> {
   public SegmentImpl(ModelBone bone, BoneBehaviorType<SegmentImpl> type, BoneBehaviorData data) {
      super(bone, type, data);
   }

   @Override
   public void onApply() {
      super.onApply();
      if (this.backPivot) {
         this.resetPivot();
      }
   }

   @Override
   public void onModelInitialized() {
      super.onModelInitialized();
      if (!this.backPivot) {
         this.resetPivot();
      }
   }

   @Override
   public void postGlobalCalculation() {
      super.postGlobalCalculation();
      this.resetPivot();
   }

   @Override
   public void postTransformDecompose() {
      SafeTransform global = this.bone.getGlobalTransform();
      this.bone.getTransformMatrix().identity().rotate(global.getLeftQuaternion()).scale(global.getScale()).rotate(global.getRightQuaternion());
      global.mutatePosition(Vector3f::zero);
   }

   private void resetPivot() {
      this.bone.setPivotLocation(this.toLocation(this.worldLocation));
   }

   private Location toLocation(Vector3f vec) {
      return this.bone.getBaseLocation().clone().set(vec.x, vec.y, vec.z);
   }
}
