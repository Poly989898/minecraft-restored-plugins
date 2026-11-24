package net.Indyuce.mmoitems.particle;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.particle.api.ParticleRunnable;
import net.Indyuce.mmoitems.stat.data.ParticleData;
import org.bukkit.Location;

public class VortexParticles extends ParticleRunnable {
   private final float speed;
   private final float height;
   private final float radius;
   private final float r_speed;
   private final float y_speed;
   private final int amount;
   private double j = 0.0;

   public VortexParticles(ParticleData var1, PlayerData var2) {
      super(var1, var2);
      this.speed = (float)var1.getModifier("speed");
      this.height = (float)var1.getModifier("height");
      this.radius = (float)var1.getModifier("radius");
      this.y_speed = (float)var1.getModifier("y-speed");
      this.r_speed = (float)var1.getModifier("rotation-speed");
      this.amount = (int)var1.getModifier("amount");
   }

   @Override
   public void createParticles() {
      Location var1 = this.player.getPlayer().getLocation();
      double var2 = this.j / Math.PI / 2.0;

      for (int var4 = 0; var4 < this.amount; var4++) {
         double var5 = this.j + (Math.PI * 2) * var4 / this.amount;
         this.particle
            .display(
               var1.clone()
                  .add(
                     Math.cos(var5) * this.radius * (1.0 - var2 * this.y_speed),
                     var2 * this.y_speed * this.height,
                     Math.sin(var5) * this.radius * (1.0 - var2 * this.y_speed)
                  ),
               this.speed
            );
      }

      this.j = this.j + 0.1308996938995747 * this.r_speed;
      this.j = this.j - (this.j > (Math.PI * 2) / this.y_speed ? (Math.PI * 2) / this.y_speed : 0.0);
   }
}
