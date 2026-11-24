package io.lumine.mythic.lib.player.particle.type;

import io.lumine.mythic.lib.player.particle.ParticleEffect;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DoubleRingsParticleEffect extends ParticleEffect {
   private final double speed;
   private final double height;
   private final double radius;
   private final double r_speed;
   private final double y_offset;
   private double j = 0.0;

   public DoubleRingsParticleEffect(ConfigObject obj) {
      super(obj);
      this.speed = obj.getDouble("speed");
      this.height = obj.getDouble("height");
      this.radius = obj.getDouble("radius");
      this.r_speed = obj.getDouble("rotation-speed");
      this.y_offset = obj.getDouble("y-offset");
   }

   @Override
   public void tick(Player player) {
      Location loc = player.getLocation();

      for (double k = 0.0; k < 2.0; k++) {
         double a = this.j + k * Math.PI;
         this.getParticle()
            .display(loc.clone().add(this.radius * Math.cos(a), this.height + Math.sin(this.j) * this.y_offset, this.radius * Math.sin(a)), this.speed);
      }

      this.j = this.j + (Math.PI / 16) * this.r_speed;
      this.j = this.j - (this.j > Math.PI * 2 ? Math.PI * 2 : 0.0);
   }
}
