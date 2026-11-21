package com.ticxo.modelengine.api.utils.data;

public class QueuedInt {
   private volatile int writer;
   private volatile int reader;

   public QueuedInt(int initialValue) {
      this.writer = initialValue;
      this.reader = initialValue;
   }

   public void set(int value) {
      if (this.writer != value) {
         this.writer = value;
      }
   }

   public void setUnsafe(int value) {
      this.reader = value;
   }

   public void pass() {
      this.reader = this.writer;
   }

   public int get() {
      return this.reader;
   }

   public int getUnsafe() {
      return this.writer;
   }
}
