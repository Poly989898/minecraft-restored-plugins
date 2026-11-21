package io.lumine.mythic.bukkit.utils.cache;

import java.util.function.Consumer;

public class DataTrackerFloat extends DataTracker<Float> {
   protected float floatValue;

   public static DataTrackerFloat of(float value) {
      return new DataTrackerFloat(value);
   }

   public DataTrackerFloat(float value) {
      this.floatValue = value;
   }

   public void pokeFloat(float value) {
      this.floatValue = value;
   }

   public float peekFloat() {
      return this.floatValue;
   }

   public float getFloat() {
      this.isDirty = false;
      return this.floatValue;
   }

   public boolean setFloat(float value) {
      if (Float.floatToIntBits(this.floatValue) == Float.floatToIntBits(value)) {
         return false;
      } else {
         this.floatValue = value;
         this.isDirty = true;
         return true;
      }
   }

   public void poke(Float value) {
      this.floatValue = value == null ? 0.0F : value;
   }

   public Float peek() {
      return this.floatValue;
   }

   public Float get() {
      this.isDirty = false;
      return this.floatValue;
   }

   public boolean set(Float value) {
      return this.setFloat(value == null ? 0.0F : value);
   }

   @Override
   public void ifDirty(Consumer<Float> consumer) {
      if (this.isDirty) {
         consumer.accept(this.floatValue);
         this.isDirty = false;
      }
   }

   @Override
   public void ifDirty(Consumer<Float> consumer, boolean force) {
      if (this.isDirty || force) {
         consumer.accept(this.floatValue);
         this.isDirty = false;
      }
   }
}
