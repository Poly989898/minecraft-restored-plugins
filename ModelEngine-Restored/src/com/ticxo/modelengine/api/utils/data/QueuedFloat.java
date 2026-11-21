package com.ticxo.modelengine.api.utils.data;

public class QueuedFloat {
   private volatile int writer;
   private volatile int reader;

   public QueuedFloat(float initialValue) {
      int bits = Float.floatToRawIntBits(initialValue);
      this.writer = bits;
      this.reader = bits;
   }

   public void set(float value) {
      int bits = Float.floatToRawIntBits(value);
      if (this.writer != bits) {
         this.writer = bits;
      }
   }

   public void setUnsafe(float value) {
      this.reader = Float.floatToRawIntBits(value);
   }

   public void pass() {
      this.reader = this.writer;
   }

   public float get() {
      return Float.intBitsToFloat(this.reader);
   }

   public float getUnsafe() {
      return Float.intBitsToFloat(this.writer);
   }
}
