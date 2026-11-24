package io.lumine.mythic.lib.player.particle.type;

import io.lumine.mythic.lib.player.particle.ParticleEffect;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HelixParticleEffect extends ParticleEffect {
   private final double speed;
   private final double height;
   private final double radius;
   private final double r_speed;
   private final double y_speed;
   private final int amount;
   private double j = 0.0;

   public HelixParticleEffect(ConfigObject obj) {
      super(obj);
      this.speed = obj.getDouble("speed");
      this.height = obj.getDouble("height");
      this.radius = obj.getDouble("radius");
      this.r_speed = obj.getDouble("rotation-speed");
      this.y_speed = obj.getDouble("y-speed");
      this.amount = obj.getInt("amount");
   }

   @Override
   public void tick(Player player) {
      Location loc = player.getLocation();

      for (double k = 0.0; k < this.amount; k++) {
         double a = this.j + k * Math.PI * 2.0 / this.amount;
         this.getParticle()
            .display(
               loc.clone()
                  .add(
                     Math.cos(a) * Math.cos(this.j * this.y_speed) * this.radius,
                     1.0 + Math.sin(this.j * this.y_speed) * this.height,
                     Math.sin(a) * Math.cos(this.j * this.y_speed) * this.radius
                  ),
               this.speed
            );
      }

      this.j = this.j + 0.1308996938995747 * this.r_speed;
      this.j = this.j - (this.j > (Math.PI * 2) / this.y_speed ? (Math.PI * 2) / this.y_speed : 0.0);
   }
}
