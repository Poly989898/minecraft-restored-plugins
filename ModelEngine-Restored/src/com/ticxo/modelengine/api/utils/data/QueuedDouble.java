package com.ticxo.modelengine.api.utils.data;

public class QueuedDouble {
   private volatile long writer;
   private volatile long reader;

   public QueuedDouble(double initialValue) {
      long bits = Double.doubleToRawLongBits(initialValue);
      this.writer = bits;
      this.reader = bits;
   }

   public void set(double value) {
      this.writer = Double.doubleToRawLongBits(value);
   }

   public void setUnsafe(double value) {
      this.reader = Double.doubleToRawLongBits(value);
   }

   public void pass() {
      this.reader = this.writer;
   }

   public double get() {
      return Double.longBitsToDouble(this.reader);
   }

   public double getUnsafe() {
      return Double.longBitsToDouble(this.writer);
   }
}
