package net.Indyuce.mmoitems.particle;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.particle.api.ParticleRunnable;
import net.Indyuce.mmoitems.stat.data.ParticleData;
import org.bukkit.Location;

public class AuraParticles extends ParticleRunnable {
   private final float speed;
   private final float height;
   private final float radius;
   private final float r_speed;
   private final float y_offset;
   private final float y_speed;
   private final int amount;
   private double j = 0.0;

   public AuraParticles(ParticleData particleData, PlayerData playerData) {
      super(particleData, playerData);
      this.speed = (float)particleData.getModifier("speed");
      this.height = (float)particleData.getModifier("height");
      this.radius = (float)particleData.getModifier("radius");
      this.r_speed = (float)particleData.getModifier("rotation-speed");
      this.y_speed = (float)particleData.getModifier("y-speed");
      this.y_offset = (float)particleData.getModifier("y-offset");
      this.amount = (int)particleData.getModifier("amount");
   }

   @Override
   public void createParticles() {
      Location location = this.player.getPlayer().getLocation();

      for (int i = 0; i < this.amount; i++) {
         double value = this.j + (Math.PI * 2) * i / this.amount;
         this.particle
            .display(
               location.clone()
                  .add(Math.cos(value) * this.radius, Math.sin(this.j * this.y_speed * 3.0) * this.y_offset + this.height, Math.sin(value) * this.radius),
               this.speed
            );
      }

      this.j = this.j + 0.06544984694978735 * this.r_speed;
      this.j = this.j - (this.j > (Math.PI * 2) / this.y_speed ? (Math.PI * 2) / this.y_speed : 0.0);
   }
}
