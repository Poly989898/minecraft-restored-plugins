package net.Indyuce.mmoitems.stat.data;

import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.jetbrains.annotations.NotNull;

public class RestoreData implements StatData, Mergeable<RestoreData> {
   private double health;
   private double food;
   private double saturation;

   public RestoreData(double var1, double var3, double var5) {
      this.health = var1;
      this.food = var3;
      this.saturation = var5;
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

   public void setHealth(double var1) {
      this.health = var1;
   }

   public void setFood(double var1) {
      this.food = var1;
   }

   public void setSaturation(double var1) {
      this.saturation = var1;
   }

   @Override
   public boolean isEmpty() {
      return this.food == 0.0 && this.health == 0.0 && this.saturation == 0.0;
   }

   public void mergeWith(@NotNull RestoreData var1) {
      this.health = this.health + var1.health;
      this.food = this.food + var1.food;
      this.saturation = this.saturation + var1.saturation;
   }

   @NotNull
   public RestoreData clone() {
      return new RestoreData(this.health, this.food, this.saturation);
   }

   @Override
   public boolean equals(Object var1) {
      return !(var1 instanceof RestoreData)
         ? false
         : ((RestoreData)var1).getFood() == this.getFood()
            && ((RestoreData)var1).getHealth() == this.getHealth()
            && ((RestoreData)var1).getSaturation() == this.getSaturation();
   }
}
