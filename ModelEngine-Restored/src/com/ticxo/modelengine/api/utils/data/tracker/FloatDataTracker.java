package com.ticxo.modelengine.api.utils.data.tracker;

public class FloatDataTracker {
   private float value;
   private boolean isDirty;

   public FloatDataTracker(float initialValue) {
      this.value = initialValue;
   }

   public void set(float value) {
      if (!(Math.abs(value - this.value) < 1.0E-5F)) {
         this.value = value;
         this.isDirty = true;
      }
   }

   public float get() {
      return this.value;
   }

   public boolean isDirty() {
      return this.isDirty;
   }

   public void clearDirty() {
      this.isDirty = false;
   }
}
