package com.ticxo.modelengine.api.utils.data;

public class QueuedBoolean {
   private volatile boolean writer;
   private volatile boolean reader;

   public QueuedBoolean(boolean initialValue) {
      this.writer = initialValue;
      this.reader = initialValue;
   }

   public void set(boolean value) {
      if (this.writer != value) {
         this.writer = value;
      }
   }

   public void setUnsafe(boolean value) {
      this.reader = value;
   }

   public void pass() {
      this.reader = this.writer;
   }

   public boolean get() {
      return this.reader;
   }

   public boolean getUnsafe() {
      return this.writer;
   }
}
