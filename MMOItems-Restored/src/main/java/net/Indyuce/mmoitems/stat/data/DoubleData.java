package net.Indyuce.mmoitems.stat.data;

import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;

public class DoubleData implements StatData, Mergeable<DoubleData> {
   private double value;

   public DoubleData(double var1) {
      this.value = var1;
   }

   public double getValue() {
      return this.value;
   }

   public void setValue(double var1) {
      this.value = var1;
   }

   public void add(double var1) {
      this.value += var1;
   }

   public void addRelative(double var1) {
      this.value *= 1.0 + var1;
   }

   public void mergeWith(DoubleData var1) {
      this.value = this.value + var1.value;
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
   public boolean equals(Object var1) {
      return !(var1 instanceof DoubleData) ? false : ((DoubleData)var1).getValue() == this.getValue();
   }
}
