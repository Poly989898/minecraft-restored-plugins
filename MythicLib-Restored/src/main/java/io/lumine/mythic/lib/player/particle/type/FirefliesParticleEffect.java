package io.lumine.mythic.lib.player.particle.type;

import io.lumine.mythic.lib.player.particle.ParticleEffect;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FirefliesParticleEffect extends ParticleEffect {
   private final double speed;
   private final double height;
   private final double radius;
   private final double r_speed;
   private final int amount;
   private double j = 0.0;

   public FirefliesParticleEffect(ConfigObject obj) {
      super(obj);
      this.speed = obj.getDouble("speed");
      this.height = obj.getDouble("height");
      this.radius = obj.getDouble("radius");
      this.r_speed = obj.getDouble("rotation-speed");
      this.amount = obj.getInt("amount");
   }

   @Override
   public void tick(Player player) {
      Location loc = player.getLocation();

      for (int k = 0; k < this.amount; k++) {
         double a = this.j + (Math.PI * 2) * k / this.amount;
         this.getParticle().display(loc.clone().add(Math.cos(a) * this.radius, this.height, Math.sin(a) * this.radius), this.speed);
      }

      this.j = this.j + 0.06544984694978735 * this.r_speed;
      this.j = this.j - (this.j > Math.PI * 2 ? Math.PI * 2 : 0.0);
   }
}
