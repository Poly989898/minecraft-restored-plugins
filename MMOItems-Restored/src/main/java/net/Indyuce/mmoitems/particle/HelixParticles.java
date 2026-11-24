package net.Indyuce.mmoitems.particle;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.particle.api.ParticleRunnable;
import net.Indyuce.mmoitems.stat.data.ParticleData;
import org.bukkit.Location;

public class HelixParticles extends ParticleRunnable {
   private final float speed;
   private final float height;
   private final float radius;
   private final float r_speed;
   private final float y_speed;
   private final int amount;
   private double j = 0.0;

   public HelixParticles(ParticleData particleData, PlayerData playerData) {
      super(particleData, playerData);
      this.speed = (float)particleData.getModifier("speed");
      this.height = (float)particleData.getModifier("height");
      this.radius = (float)particleData.getModifier("radius");
      this.r_speed = (float)particleData.getModifier("rotation-speed");
      this.y_speed = (float)particleData.getModifier("y-speed");
      this.amount = (int)particleData.getModifier("amount");
   }

   @Override
   public void createParticles() {
      Location location = this.player.getPlayer().getLocation();

      for (double value = 0.0; value < this.amount; value++) {
         double angle = this.j + value * Math.PI * 2.0 / this.amount;
         this.particle
            .display(
               location.clone()
                  .add(
                     Math.cos(angle) * Math.cos(this.j * this.y_speed) * this.radius,
                     1.0 + Math.sin(this.j * this.y_speed) * this.height,
                     Math.sin(angle) * Math.cos(this.j * this.y_speed) * this.radius
                  ),
               this.speed
            );
      }

      this.j = this.j + 0.1308996938995747 * this.r_speed;
      this.j = this.j - (this.j > (Math.PI * 2) / this.y_speed ? (Math.PI * 2) / this.y_speed : 0.0);
   }
}
