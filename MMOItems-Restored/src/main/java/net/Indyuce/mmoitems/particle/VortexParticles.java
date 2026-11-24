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

   public VortexParticles(ParticleData particleData, PlayerData playerData) {
      super(particleData, playerData);
      this.speed = (float)particleData.getModifier("speed");
      this.height = (float)particleData.getModifier("height");
      this.radius = (float)particleData.getModifier("radius");
      this.y_speed = (float)particleData.getModifier("y-speed");
      this.r_speed = (float)particleData.getModifier("rotation-speed");
      this.amount = (int)particleData.getModifier("amount");
   }

   @Override
   public void createParticles() {
      Location location = this.player.getPlayer().getLocation();
      double value = this.j / Math.PI / 2.0;

      for (int i = 0; i < this.amount; i++) {
         double angle = this.j + (Math.PI * 2) * i / this.amount;
         this.particle
            .display(
               location.clone()
                  .add(
                     Math.cos(angle) * this.radius * (1.0 - value * this.y_speed),
                     value * this.y_speed * this.height,
                     Math.sin(angle) * this.radius * (1.0 - value * this.y_speed)
                  ),
               this.speed
            );
      }

      this.j = this.j + 0.1308996938995747 * this.r_speed;
      this.j = this.j - (this.j > (Math.PI * 2) / this.y_speed ? (Math.PI * 2) / this.y_speed : 0.0);
   }
}
