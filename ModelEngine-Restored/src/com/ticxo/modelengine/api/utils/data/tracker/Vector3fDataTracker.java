package com.ticxo.modelengine.api.utils.data.tracker;

import com.ticxo.modelengine.api.utils.math.TMath;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class Vector3fDataTracker extends DataTracker<Vector3f> {
   public Vector3fDataTracker() {
      super(new Vector3f());
   }

   public void set(Vector3f value) {
      if (this.value == null || !TMath.isSimilar(this.value, value)) {
         this.value.set(value);
         this.isDirty = true;
      }
   }

   public void set(Vector3f value, Runnable ifDirty) {
      if (this.value == null || !TMath.isSimilar(this.value, value)) {
         this.value.set(value);
         this.isDirty = true;
         if (ifDirty != null) {
            ifDirty.run();
         }
      }
   }

   public Vector3f get() {
      return new Vector3f((Vector3fc)this.value);
   }
}
