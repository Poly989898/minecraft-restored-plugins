package net.Indyuce.mmoitems.stat.data;

import io.lumine.mythic.lib.gson.JsonObject;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.bukkit.Particle;

public class ArrowParticlesData implements StatData, RandomStatData<ArrowParticlesData> {
   private final Particle particle;
   private final int amount;
   private final int red;
   private final int green;
   private final int blue;
   private final double speed;
   private final double offset;
   private final boolean colored;

   public ArrowParticlesData(Particle var1, int var2, double var3, double var5) {
      this.particle = var1;
      this.amount = var2;
      this.offset = var3;
      this.speed = var5;
      this.colored = false;
      this.red = 0;
      this.blue = 0;
      this.green = 0;
   }

   public ArrowParticlesData(Particle var1, int var2, double var3, int var5, int var6, int var7) {
      this.particle = var1;
      this.amount = var2;
      this.offset = var3;
      this.speed = 0.0;
      this.colored = true;
      this.red = var5;
      this.green = var6;
      this.blue = var7;
   }

   public Particle getParticle() {
      return this.particle;
   }

   public boolean isColored() {
      return this.colored;
   }

   public int getAmount() {
      return this.amount;
   }

   public double getOffset() {
      return this.offset;
   }

   public double getSpeed() {
      return this.speed;
   }

   public int getRed() {
      return this.red;
   }

   public int getGreen() {
      return this.green;
   }

   public int getBlue() {
      return this.blue;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   @Override
   public String toString() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("Particle", this.particle.name());
      var1.addProperty("Amount", this.amount);
      var1.addProperty("Offset", this.offset);
      var1.addProperty("Colored", this.colored);
      if (this.colored) {
         var1.addProperty("Red", this.red);
         var1.addProperty("Green", this.green);
         var1.addProperty("Blue", this.blue);
      } else {
         var1.addProperty("Speed", this.speed);
      }

      return var1.toString();
   }

   public ArrowParticlesData randomize(MMOItemBuilder var1) {
      return this;
   }
}
