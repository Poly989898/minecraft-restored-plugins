package com.ticxo.modelengine.api.utils.data.tracker;

import com.ticxo.modelengine.api.utils.math.TMath;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public final class QuaternionfDataTracker extends DataTracker<Quaternionf> {
   public QuaternionfDataTracker() {
      super(new Quaternionf());
   }

   public void set(Quaternionf value) {
      if (this.value == null || !TMath.isSimilar(this.value, value)) {
         this.value.set(value);
         this.isDirty = true;
      }
   }

   public void set(Quaternionf value, Runnable ifDirty) {
      if (this.value == null || !TMath.isSimilar(this.value, value)) {
         this.value.set(value);
         this.isDirty = true;
         if (ifDirty != null) {
            ifDirty.run();
         }
      }
   }

   public Quaternionf get() {
      return new Quaternionf((Quaternionfc)this.value);
   }
}
