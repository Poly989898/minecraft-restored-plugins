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

   public ArrowParticlesData(Particle particle, int count, double value, double value2) {
      this.particle = particle;
      this.amount = count;
      this.offset = value;
      this.speed = value2;
      this.colored = false;
      this.red = 0;
      this.blue = 0;
      this.green = 0;
   }

   public ArrowParticlesData(Particle particle, int count, double value, int count2, int count3, int count4) {
      this.particle = particle;
      this.amount = count;
      this.offset = value;
      this.speed = 0.0;
      this.colored = true;
      this.red = count2;
      this.green = count3;
      this.blue = count4;
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
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("Particle", this.particle.name());
      jsonObject.addProperty("Amount", this.amount);
      jsonObject.addProperty("Offset", this.offset);
      jsonObject.addProperty("Colored", this.colored);
      if (this.colored) {
         jsonObject.addProperty("Red", this.red);
         jsonObject.addProperty("Green", this.green);
         jsonObject.addProperty("Blue", this.blue);
      } else {
         jsonObject.addProperty("Speed", this.speed);
      }

      return jsonObject.toString();
   }

   public ArrowParticlesData randomize(MMOItemBuilder mmoItemBuilder) {
      return this;
   }
}
