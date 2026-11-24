package io.lumine.mythic.lib.player.particle.type;

import io.lumine.mythic.lib.player.particle.ParticleEffect;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GalaxyParticleEffect extends ParticleEffect {
   private final double speed;
   private final double height;
   private final double r_speed;
   private final double y_coord;
   private final int amount;
   private double j = 0.0;

   public GalaxyParticleEffect(ConfigObject obj) {
      super(obj);
      this.speed = obj.getDouble("speed") * 0.2;
      this.height = obj.getDouble("height");
      this.r_speed = obj.getDouble("rotation-speed");
      this.y_coord = obj.getDouble("y-coord");
      this.amount = obj.getInt("amount");
   }

   @Override
   public void tick(Player player) {
      Location loc = player.getLocation();

      for (int k = 0; k < this.amount; k++) {
         double a = this.j + (Math.PI * 2) * k / this.amount;
         this.getParticle().display(loc.clone().add(0.0, this.height, 0.0), 0, (float)Math.cos(a), this.y_coord, (float)Math.sin(a), this.speed);
      }

      this.j = this.j + 0.1308996938995747 * this.r_speed;
      this.j = this.j - (this.j > Math.PI * 2 ? Math.PI * 2 : 0.0);
   }
}
