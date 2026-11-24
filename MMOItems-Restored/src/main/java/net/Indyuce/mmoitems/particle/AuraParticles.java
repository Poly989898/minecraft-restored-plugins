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

   public AuraParticles(ParticleData var1, PlayerData var2) {
      super(var1, var2);
      this.speed = (float)var1.getModifier("speed");
      this.height = (float)var1.getModifier("height");
      this.radius = (float)var1.getModifier("radius");
      this.r_speed = (float)var1.getModifier("rotation-speed");
      this.y_speed = (float)var1.getModifier("y-speed");
      this.y_offset = (float)var1.getModifier("y-offset");
      this.amount = (int)var1.getModifier("amount");
   }

   @Override
   public void createParticles() {
      Location var1 = this.player.getPlayer().getLocation();

      for (int var2 = 0; var2 < this.amount; var2++) {
         double var3 = this.j + (Math.PI * 2) * var2 / this.amount;
         this.particle
            .display(
               var1.clone()
                  .add(Math.cos(var3) * this.radius, Math.sin(this.j * this.y_speed * 3.0) * this.y_offset + this.height, Math.sin(var3) * this.radius),
               this.speed
            );
      }

      this.j = this.j + 0.06544984694978735 * this.r_speed;
      this.j = this.j - (this.j > (Math.PI * 2) / this.y_speed ? (Math.PI * 2) / this.y_speed : 0.0);
   }
}
