package net.Indyuce.mmoitems.stat.data;

import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.jetbrains.annotations.NotNull;

public class RestoreData implements StatData, Mergeable<RestoreData> {
   private double health;
   private double food;
   private double saturation;

   public RestoreData(double value, double value2, double value3) {
      this.health = value;
      this.food = value2;
      this.saturation = value3;
   }

   public double getHealth() {
      return this.health;
   }

   public double getFood() {
      return this.food;
   }

   public double getSaturation() {
      return this.saturation;
   }

   public void setHealth(double value) {
      this.health = value;
   }

   public void setFood(double value) {
      this.food = value;
   }

   public void setSaturation(double value) {
      this.saturation = value;
   }

   @Override
   public boolean isEmpty() {
      return this.food == 0.0 && this.health == 0.0 && this.saturation == 0.0;
   }

   public void mergeWith(@NotNull RestoreData restoreData) {
      this.health = this.health + restoreData.health;
      this.food = this.food + restoreData.food;
      this.saturation = this.saturation + restoreData.saturation;
   }

   @NotNull
   public RestoreData clone() {
      return new RestoreData(this.health, this.food, this.saturation);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof RestoreData)
         ? false
         : ((RestoreData)obj).getFood() == this.getFood()
            && ((RestoreData)obj).getHealth() == this.getHealth()
            && ((RestoreData)obj).getSaturation() == this.getSaturation();
   }
}
