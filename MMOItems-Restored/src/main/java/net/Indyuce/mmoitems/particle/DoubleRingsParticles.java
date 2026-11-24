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

   public DoubleRingsParticles(ParticleData var1, PlayerData var2) {
      super(var1, var2);
      this.speed = (float)var1.getModifier("speed");
      this.height = (float)var1.getModifier("height");
      this.radius = (float)var1.getModifier("radius");
      this.r_speed = (float)var1.getModifier("rotation-speed");
      this.y_offset = (float)var1.getModifier("y-offset");
   }

   @Override
   public void createParticles() {
      Location var1 = this.player.getPlayer().getLocation();

      for (double var2 = 0.0; var2 < 2.0; var2++) {
         double var4 = this.j + var2 * Math.PI;
         this.particle
            .display(var1.clone().add(this.radius * Math.cos(var4), this.height + Math.sin(this.j) * this.y_offset, this.radius * Math.sin(var4)), this.speed);
      }

      this.j = this.j + (Math.PI / 16) * this.r_speed;
      this.j = this.j - (this.j > Math.PI * 2 ? Math.PI * 2 : 0.0);
   }
}
