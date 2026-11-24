package net.Indyuce.mmoitems.particle;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.particle.api.ParticleRunnable;
import net.Indyuce.mmoitems.stat.data.ParticleData;
import org.bukkit.Location;

public class DoubleRingsParticles extends ParticleRunnable {
   private final float speed;
   private final float height;
   private final float radius;
   private final float r_speed;
   private final float y_offset;
   private double j = 0.0;

   public DoubleRingsParticles(ParticleData particleData, PlayerData playerData) {
      super(particleData, playerData);
      this.speed = (float)particleData.getModifier("speed");
      this.height = (float)particleData.getModifier("height");
      this.radius = (float)particleData.getModifier("radius");
      this.r_speed = (float)particleData.getModifier("rotation-speed");
      this.y_offset = (float)particleData.getModifier("y-offset");
   }

   @Override
   public void createParticles() {
      Location location = this.player.getPlayer().getLocation();

      for (double value = 0.0; value < 2.0; value++) {
         double angle = this.j + value * Math.PI;
         this.particle
            .display(location.clone().add(this.radius * Math.cos(angle), this.height + Math.sin(this.j) * this.y_offset, this.radius * Math.sin(angle)), this.speed);
      }

      this.j = this.j + (Math.PI / 16) * this.r_speed;
      this.j = this.j - (this.j > Math.PI * 2 ? Math.PI * 2 : 0.0);
   }
}
