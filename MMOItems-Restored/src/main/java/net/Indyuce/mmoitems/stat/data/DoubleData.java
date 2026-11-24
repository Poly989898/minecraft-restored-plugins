package net.Indyuce.mmoitems.stat.data;

import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;

public class DoubleData implements StatData, Mergeable<DoubleData> {
   private double value;

   public DoubleData(double value) {
      this.value = value;
   }

   public double getValue() {
      return this.value;
   }

   public void setValue(double value) {
      this.value = value;
   }

   public void add(double value) {
      this.value += value;
   }

   public void addRelative(double value) {
      this.value *= 1.0 + value;
   }

   public void mergeWith(DoubleData doubleData) {
      this.value = this.value + doubleData.value;
   }

   public DoubleData clone() {
      return new DoubleData(this.value);
   }

   @Override
   public boolean isEmpty() {
      return this.value == 0.0;
   }

   @Override
   public String toString() {
      return String.valueOf(this.getValue());
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof DoubleData) ? false : ((DoubleData)obj).getValue() == this.getValue();
   }
}
